/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.tmdb.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.DbUtil.PageMode;

public class Tm implements ITm {

	private Repository store;
	private String name;
	private String uuid;
	private PreparedStatement pstmGet;

	private DbUtil.PageMode pageMode = PageMode.EDITOR;
	private long limit = 500;
	private boolean needPagingRefresh = true; // Must be set to true anytime we change the row count
	private long totalRows;
	private long pageCount;
	private long currentPage = -1; // 0-based
	private boolean pagingWithMethod = true;

	private PreparedStatement pstmAddSeg;
	private PreparedStatement pstmAddTu;
	private ArrayList<String> existingTuFields;
	private ArrayList<String> existingSegFields;
	private LinkedHashMap<String, Object> fieldsToImport;
	
	private PreparedStatement pstmUpdSeg;
	private ArrayList<String> updSegFields;

	
	public Tm (Repository store,
		String uuid,
		String name)
	{
		this.store = store;
		this.name = name;
		this.uuid = uuid;
	}
	
	@Override
	protected void finalize() throws Throwable {
        close();
        super.finalize();
	}

	private void closeAddStatements ()
		throws SQLException
	{
		if ( pstmAddSeg != null ) {
			pstmAddSeg.close();
			pstmAddSeg = null;
		}
		if ( pstmAddTu != null ) {
			pstmAddTu.close();
			pstmAddTu = null;
		}
	}
	
	private void closeUpdateStatements ()
		throws SQLException
	{
		if ( pstmUpdSeg != null ) {
			pstmUpdSeg.close();
			pstmUpdSeg = null;
			updSegFields = null;
		}
	}

	public void close () {
		try {
			if ( pstmGet != null ) {
				pstmGet.close();
				pstmGet = null;
			}
			closeAddStatements();
			closeUpdateStatements();
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getAvailableFields () {
		return store.getAvailableFields(name);
	}

	@Override
	public String getDescription () {
		String res[] = store.getTmData(uuid);
		return res[1];
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public String getUUID () {
		return uuid;
	}

	public void rename (String newName) {
		store.renameTm(name, newName);
		name = newName;
	}
	
	@Override
	public void setRecordFields (List<String> names) {
		try {
			// Create a prepared statement to use to retrieve the selection
			if ( pstmGet != null ) {
				pstmGet.close();
			}
			// Check if we have at least one field that is TU-level
			boolean hasTUField = false;
			for ( String name : names ) {
				if ( !DbUtil.isSegmentField(name) ) {
					hasTUField = true;
					break;
				}
			}
			
			StringBuilder tmp;
			if ( hasTUField ) {
				String tuTable = "\""+name+"_TU\"";
				String segTable = "\""+name+"_SEG\"";
				tmp = new StringBuilder(String.format("SELECT %s.\"%s\", %s.\"%s\"", segTable, DbUtil.SEGKEY_NAME, segTable, DbUtil.FLAG_NAME));
				for ( String name : names ) {
					if ( DbUtil.isSegmentField(name) ) tmp.append(", "+segTable+".\""+name+"\"");
					else tmp.append(", "+tuTable+".\""+name+"\"");
				}
				tmp.append(" FROM "+segTable+" LEFT JOIN "+tuTable+" ON "+segTable+".\""+DbUtil.TUREF_NAME+"\"="+tuTable+".TUKEY");
				
			}
			else { // Simple select in the segment table
				tmp = new StringBuilder(String.format("SELECT \"%s\", \"%s\"", DbUtil.SEGKEY_NAME, DbUtil.FLAG_NAME));
				for ( String name : names ) {
					tmp.append(", \""+name+"\"");
				}
				tmp.append(" FROM \""+name+"_SEG\"");
			}
			
			//old tmp.append(" ORDER BY SegKey LIMIT ? OFFSET ?");
			tmp.append(String.format(" WHERE \"%s\">=? ORDER BY \"%s\" LIMIT ?", DbUtil.SEGKEY_NAME, DbUtil.SEGKEY_NAME));

			pstmGet = store.getConnection().prepareStatement( tmp.toString(),
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	public void moveBeforeFirstPage () {
		currentPage = -1;
	}
	
	@Override
	public void setPageSize (long size) {
		if ( size < 2 ) this.limit = 2;
		else this.limit = size;
		// We changed the number of rows
		needPagingRefresh = true;
	}
	
	@Override
	public long getPageSize () {
		return limit;
	}
	
	@Override
	public void startImport () {
		try {
			closeAddStatements();
			closeUpdateStatements();
			// Get the list of the original existing fields
			// This list will be use until the end of the import
			// It will be update with any added field from the API, not from the database
			existingTuFields = store.getFields(name, false);
			existingSegFields = store.getFields(name, true);
			// Create the list of the fields to import (to use with the pre-defined statement
			fieldsToImport = new LinkedHashMap<String, Object>();
			
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void finishImport () {
		try {
			closeAddStatements();
			existingTuFields = null;
			existingSegFields = null;
			fieldsToImport = null;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long addRecord (long tuKey,
		Map<String, Object> tuFields,
		Map<String, Object> segFields)
	{
		//TODO: need to wrap this into a transaction
		try {
			verifyFieldsToImport(false, tuFields, false);
			verifyFieldsToImport(true, segFields, false);
		
			fillStatement(false, tuFields, 0); // tuKey not used here
			pstmAddTu.executeUpdate();
			if ( tuKey == -1 ) {
				ResultSet keys = pstmAddTu.getGeneratedKeys();
				if ( keys.next() ) {
					tuKey = keys.getLong(1);
				}
			}

			// It's unlikely there are no segment-level fields but it could happens 
			if ( pstmAddSeg != null ) {
				fillStatement(true, segFields, tuKey);
				pstmAddSeg.executeUpdate();
			}
			
			// We changed the number of rows
			needPagingRefresh = true;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		return tuKey;
	}

	private void fillStatement (boolean segmentLevel,
		Map<String, Object> fields,
		long tuKey)
		throws SQLException
	{
		PreparedStatement pstm;
		if ( segmentLevel ) {
			pstm = pstmAddSeg;
		}
		else {
			pstm = pstmAddTu;
		}
		if ( pstm == null ) {
			return; // Nothing to do
		}
		
		int n = 1;
		Object value;
		for ( String name : fieldsToImport.keySet() ) {
			// Skip fields for the other statement
			if ( segmentLevel ) {
				if ( !DbUtil.isSegmentField(name) ) continue;
			}
			else {
				if ( DbUtil.isSegmentField(name) ) continue;
			}
			// Get the value from the provided map or from the defaults
			// Empty list is treat like it's all defaults
			if ( fields.containsKey(name) ) {
				value = fields.get(name);
			}
			else {
				// Not in the current set of fields provided
				// Set the value to its default
				value = fieldsToImport.get(name);
			}

			if ( value instanceof String ) {
				pstm.setString(n, (String)value);
			}
			else if ( value instanceof Boolean ) {
				pstm.setBoolean(n, (Boolean)value);
			}
			else if ( value instanceof Integer ) {
				pstm.setLong(n, ((Integer)value).longValue());
			}
			else if ( value instanceof Long ) {
				pstm.setLong(n, (Long)value);
			}
			else if ( value == null ) {
				pstm.setString(n, null);
			}
			n++;
		}

		// If this is the segment table: set TUREF, which should be the last field
		if ( segmentLevel ) {
			pstm.setLong(n, tuKey);
		}
	}
	
	private void verifyFieldsToImport (boolean segmentLevel,
		Map<String, Object> fields,
		boolean useValuesAsDefault)
	{
		try {
			ArrayList<String> existingFields;
			if ( segmentLevel ) existingFields = existingSegFields;
			else existingFields = existingTuFields;
			
			LinkedHashMap<String, String> fieldsToCreate = null;
			boolean hasNewFieldToImport = false;
			
			if ( !Util.isEmpty(fields) ) {

				String type = null;
				// Go through the list of fields
				// and add the ones that do not exist to the list of fields to create
				for ( String name : fields.keySet() ) {
					// This is a TU-level field
					boolean hasFieldToCreate = false;
					if ( !existingFields.contains(name) ) {
						hasFieldToCreate = true;
						if ( fieldsToCreate == null ) {
							fieldsToCreate = new LinkedHashMap<String, String>();
						}
					}
	
					if ( !fieldsToImport.containsKey(name) ) {
						Object value = fields.get(name);
						if ( value instanceof String ) {
							type = "VARCHAR";
							if ( useValuesAsDefault ) fieldsToImport.put(name, value);
							else fieldsToImport.put(name, null);
							hasNewFieldToImport = true;
						}
						else if (( value instanceof Long ) || ( value instanceof Integer )) {
							type = "INTEGER";
							if ( useValuesAsDefault ) fieldsToImport.put(name, value);
							else fieldsToImport.put(name, 0);
							hasNewFieldToImport = true;
						}
						else if ( value instanceof Boolean ) {
							type = "BOOLEAN";
							if ( useValuesAsDefault ) fieldsToImport.put(name, value);
							else fieldsToImport.put(name, false);
							hasNewFieldToImport = true;
						}
						else {
							throw new RuntimeException("Invalid field type to add.");
						}
					}
					
					// If the field is to create type should be set because
					// it was also a field that wasn't in the fieldsToImport list
					if ( hasFieldToCreate ) {
						// Nothing to add, move on to the next field
						fieldsToCreate.put(name, type);
					}
				}
//TODO: detect new locale and make sure all fields for the locale are added (e.g. codes not just text)

				// Create the new fields as needed, and update the lists
				// The lists can be null or empty in this call
				store.createNewFields(name, segmentLevel, fieldsToCreate, existingFields);
			}
			
			// Create or re-create the statement to insert the entry
			if ( segmentLevel ) {
				if (( pstmAddSeg == null ) || !Util.isEmpty(fieldsToCreate) ) {
					if ( pstmAddSeg != null ) {
						pstmAddSeg.close();
					}
					boolean first = true;
					int count = 0;
					StringBuilder tmp = new StringBuilder("INSERT INTO \""+name+"_SEG\" (");
					for ( String name : fieldsToImport.keySet() ) {
						if ( !DbUtil.isSegmentField(name) ) continue;  // Skip over TU level fields
						if ( first ) {
							first = false;
							tmp.append("\""+name+"\"");
						}
						else {
							tmp.append(", \""+name+"\"");
						}
						count++;
					}
					tmp.append((first ? "" : ", ")+"\""+DbUtil.TUREF_NAME+"\") VALUES (?"); // Always include TUREF at the end
					for ( int i=0; i<count; i++ ) {
						tmp.append(", ?");
					}
					tmp.append(");");
					pstmAddSeg = store.getConnection().prepareStatement(tmp.toString());
				}
			}
			else {
				// Create or re-create the statement to insert the entry
				if (( pstmAddTu == null ) || !Util.isEmpty(fieldsToCreate) || hasNewFieldToImport ) {
					if ( pstmAddTu != null ) {
						pstmAddTu.close();
					}
					StringBuilder tmp;
					if ( !Util.isEmpty(fieldsToImport) ) {
						boolean first = true;
						int count = 0;
						tmp = new StringBuilder("INSERT INTO \""+name+"_TU\" (");
						for ( String name : fieldsToImport.keySet() ) {
							if ( DbUtil.isSegmentField(name) ) continue; // Skip over segment-level fields
							if ( first ) {
								first = false;
								tmp.append("\""+name+"\"");
							}
							else {
								tmp.append(", \""+name+"\"");
							}
							count++;
						}
						tmp.append(") VALUES (");
						for ( int i=0; i<count; i++ ) {
							tmp.append((i==0) ? "?" : ", ?");
						}
						tmp.append(");");
					}
					else { // We need to create the statement but have no fields
						// In that case we just pass the TUKey with NULL
						tmp = new StringBuilder("INSERT INTO \""+name+"_TU\" (TUKEY) VALUES (NULL)");
					}
					pstmAddTu = store.getConnection().prepareStatement(tmp.toString(), Statement.RETURN_GENERATED_KEYS);
				}
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getLocales () {
		return store.getTmLocales(name);
	}

	@Override	
	public ResultSet refreshCurrentPage () {
		long oldPage = currentPage;
		needPagingRefresh = true;
		checkPagingVariables();
		if ( pageCount > oldPage ) currentPage = oldPage;
		else if ( pageCount > 0 ) currentPage = pageCount-1; 
		return getPage(getFirstKeySegValueForPage(currentPage));
	}
	
	@Override
	public ResultSet getFirstPage () {
		checkPagingVariables();
		currentPage = 0;
		return getPage(getFirstKeySegValueForPage(currentPage));
	}

	@Override
	public ResultSet getLastPage () {
		checkPagingVariables();
		currentPage = pageCount-1;
		return getPage(getFirstKeySegValueForPage(currentPage));
	}

	@Override
	public ResultSet getNextPage () {
		checkPagingVariables();
		if ( currentPage >= pageCount-1 ) return null; // Last page reached
		currentPage++;
		return getPage(getFirstKeySegValueForPage(currentPage));
	}

	@Override
	public ResultSet getPreviousPage () {
		checkPagingVariables();
		if ( currentPage <= 0 ) return null; // First page reached
		currentPage--;
		return getPage(getFirstKeySegValueForPage(currentPage));
	}

	private void checkPagingVariables () {
		// Do we need to re-compute the paging variables
		if ( !needPagingRefresh ) return;
		
		totalRows = store.getTotalSegmentCount(name);
		if ( totalRows < 1 ) {
			pageCount = 0;
		}
		else {
			if ( pageMode == PageMode.EDITOR ) {
				pageCount = (totalRows-1) / (limit-1); // -1 for overlap
				if ( totalRows == 1 ) pageCount++;
				else if ( (totalRows-1) % (limit-1) > 0 ) pageCount++; // Last page
			}
			else {
				pageCount = totalRows / limit; // -1 for overlap
				if ( totalRows % limit > 0 ) pageCount++; // Last page
			}
		}
		pagingWithMethod = true;
		
		currentPage = -1;
		needPagingRefresh = false; // Stable until we add or delete rows or change the page-size
		//TODO: handle sort on other fields
		
//		Statement stm = null;
//		long count = 0;
//		try {
//			stm = store.getConnection().createStatement();
//			ResultSet result = stm.executeQuery("SELECT COUNT(*) FROM \""+nme+"_SEG\""); // Optimized call for H2
//			if ( result.first() ) {
//				count = result.getLong(1);
//			}
//		}		
//		catch ( SQLException e ) {
//			throw new RuntimeException(e);
//		}
//		finally {
//			try {
//				if ( stm != null ) {
//					stm.close();
//					stm = null;
//				}
//			}
//			catch ( SQLException e ) {
//				throw new RuntimeException(e);
//			}
//		}
		
	}
	
	private ResultSet getPage (long topSegKey) {
		if ( topSegKey < 1 ) return null;
		ResultSet result = null;
		try {
			pstmGet.setLong(1, topSegKey);
			pstmGet.setLong(2, limit);
			result = pstmGet.executeQuery();
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private long getFirstKeySegValueForPage (long page) {
		if ( pagingWithMethod ) {
			// Used if the sort is on the SegKey
			if ( page == 0 ) return 1;
			if ( pageMode == PageMode.EDITOR ) {
				return (page * (limit-1)) + 1;
			}
			else {
				return (page * limit) + 1;
			}
		}
		else {
			// Get the key from a pre-computed list
			//TODO
			return 1;
		}
	}

	@Override
	public long getCurrentPage () {
		return currentPage;
	}

	@Override
	public long getPageCount () {
		return pageCount;
	}

	@Override
	public PageMode getPageMode () {
		return pageMode;
	}

	@Override
	public void setPageMode (PageMode pageMode) {
		this.pageMode = pageMode;
	}

	@Override
	public void addLocale (String localeId) {
		localeId = localeId.toUpperCase();
		List<String> existing = getLocales();
		if ( existing.contains(localeId) ) {
			return; // This locale exists already
		}

		// Locale does not exists we can add it
		Statement stm = null;
		try {
			StringBuilder tmp = new StringBuilder();
			tmp.append(String.format("ALTER TABLE \"%s%s\" ADD \"%s\" VARCHAR; ",
				name, "_SEG", DbUtil.TEXT_PREFIX+localeId));
			tmp.append(String.format("ALTER TABLE \"%s%s\" ADD \"%s\" VARCHAR;",
				name, "_SEG", DbUtil.CODES_PREFIX+localeId));
			stm = store.getConnection().createStatement();
			stm.execute(tmp.toString());
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void deleteLocale (String localeId) {
		List<String> existing = getLocales();
		if ( existing.size() < 2 ) {
			return; // Must keep at least one locale
		}
		localeId = localeId.toUpperCase();
		if ( !existing.contains(localeId) ) {
			return; // This locale does not exist
		}

		// Locale does not exists and we need to remove all it fields
		Statement stm = null;
		try {
			StringBuilder tmp = new StringBuilder();
			stm = store.getConnection().createStatement();
			ResultSet result = stm.executeQuery("SHOW COLUMNS FROM \""+name+"_SEG\"");
			while ( result.next() ) {
				String fn = result.getString(1);
				int n = fn.lastIndexOf(DbUtil.LOC_SEP);
				if ( n > -1 ) {
					if ( fn.substring(n+1).equals(localeId) ) {
						// This field is to be removed
						tmp.append(String.format("ALTER TABLE \"%s%s\" DROP COLUMN \"%s\"; ",
							name, "_SEG", fn));
					}
				}
			}
			if ( tmp.length() > 0 ) {
				stm.execute(tmp.toString());
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void renameLocale (String currentCode,
		String newCode)
	{
		List<String> existing = getLocales();
		if ( !existing.contains(currentCode) ) {
			return; // There is not a locale with that name
		}
		if ( existing.contains(newCode) ) {
			return; // The name/code is already used
		}

		Statement stm = null;
		try {
			StringBuilder tmp = new StringBuilder();
			stm = store.getConnection().createStatement();
			ResultSet result = stm.executeQuery("SHOW COLUMNS FROM \""+name+"_SEG\"");
			while ( result.next() ) {
				String fn = result.getString(1);
				int n = fn.lastIndexOf(DbUtil.LOC_SEP);
				if ( n > -1 ) {
					if ( fn.substring(n+1).equals(currentCode) ) {
						// This field is to be renamed
						String fnRoot = fn.substring(0, n+1);
						tmp.append(String.format("ALTER TABLE \"%s%s\" ALTER COLUMN \"%s\" RENAME TO \"%s\"; ",
							name, "_SEG", fn, fnRoot+newCode));
					}
				}
			}
			if ( tmp.length() > 0 ) {
				stm.execute(tmp.toString());
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void updateRecord (long segKey,
		Map<String, Object> tuFields,
		Map<String, Object> segFields)
	{
		try {
			if ( segKey < 0 ) {
				throw new IllegalArgumentException("Illegal SegKey value.");
			}

			boolean changed = (pstmUpdSeg == null);
			if ( updSegFields == null ) {
				updSegFields = new ArrayList<String>(segFields.keySet());
			}
			else { // Update the existing list
				// Use brute force for now
				updSegFields = new ArrayList<String>(segFields.keySet());
				changed = true;
			}
			
			if ( changed ) {
				if ( pstmUpdSeg != null ) {
					pstmUpdSeg.close();
					pstmUpdSeg = null;
				}
				
				StringBuilder tmp = new StringBuilder("UPDATE \""+name+"_SEG\"");
				for ( int i=0; i<updSegFields.size(); i++ ) {
					tmp.append(String.format("%s%s \"%s\"=?", (i==0 ? "" : ","), (i==0 ? " SET" : ""), updSegFields.get(i)));
				}
				tmp.append(String.format(" WHERE \"%s\"=?", DbUtil.SEGKEY_NAME));
				
				pstmUpdSeg = store.getConnection().prepareStatement(tmp.toString());
			}
//TODO: support the various filed types!
			// Fill the statement
			int i = 1;
			for ( String fn : segFields.keySet() ) {
				if ( fn.equals(DbUtil.FLAG_NAME) ) {
					pstmUpdSeg.setBoolean(i, (Boolean)segFields.get(fn));
				}
				else {
					pstmUpdSeg.setString(i, (String)segFields.get(fn));
				}
				i++;
			}
			// Fill the SegKey value
			pstmUpdSeg.setLong(i, segKey);
			pstmUpdSeg.execute();
			
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addField (String fullName) {
		// Cannot add pre-defined fields
		if ( DbUtil.isPreDefinedField(fullName) ) {
			return;
		}
		
		String suffix = "_TU";
		
		// Check the locale
		String loc = DbUtil.getFieldLocale(fullName);
		if ( loc != null ) {
			suffix = "_SEG";
			if ( !getLocales().contains(loc) ) {
				// Not an existing locale
				return;
			}
		}

		Statement stm = null;
		try {
			stm = store.getConnection().createStatement();
			String tmp = String.format("ALTER TABLE \"%s%s\" ADD \"%s\" VARCHAR",
				name, suffix, fullName);
			stm.execute(tmp);
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void deleteField (String fullName) {
		// Block deletion of system fields
		if ( DbUtil.isPreDefinedField(fullName) ) {
			return;
		}
		String suffix = "_TU";
		if ( DbUtil.isSegmentField(fullName) ) suffix = "_SEG";
		
		Statement stm = null;
		try {
			stm = store.getConnection().createStatement();
			String tmp = String.format("ALTER TABLE \"%s%s\" DROP COLUMN \"%s\"",
				name, suffix, fullName);
			stm.execute(tmp);
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void renameField (String currentFullName,
		String newFullName)
	{
		// Block renaming of system fields
		if ( DbUtil.isPreDefinedField(currentFullName) ) {
			// Cannot rename those fields
			return;
		}
		List<String> existing = getAvailableFields();
		if ( !existing.contains(currentFullName) ) {
			// This field does not exists: do nothing
			return;
		}
		if ( existing.contains(newFullName) ) {
			// This field does exists already: do nothing
			return;
		}

		// Check if the locale part changes
		String loc1 = DbUtil.getFieldLocale(currentFullName);
		String loc2 = DbUtil.getFieldLocale(newFullName);
		if ( loc1 != null ) {
			if ( loc2 != null ) {
				if ( !loc1.equals(loc2) ) {
					// Check if it goes to an existing locale
					if ( !getLocales().contains(loc2) ) {
						return;
					}
				}
			}
			// It is allowed to make a segment level field into a unit level one
		}

		boolean sameTable = (( loc1 == null ) && ( loc2 == null)) || (( loc1 != null ) && ( loc2 != null ));
		if ( !sameTable ) {
			throw new RuntimeException("Changing level of field not implemented yet");
		}
		
		String suffix = "_SEG";
		if ( loc1 == null ) suffix = "_TU";
		
		Statement stm = null;
		try {
			stm = store.getConnection().createStatement();
			StringBuilder tmp = new StringBuilder();
			tmp.append(String.format("ALTER TABLE \"%s%s\" ALTER COLUMN \"%s\" RENAME TO \"%s\"; ",
				name, suffix, currentFullName, newFullName));
			stm.execute(tmp.toString());
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setSortOrder (LinkedHashMap<String, Boolean> fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTotalSegmentCount () {
		return store.getTotalSegmentCount(name);
	}

	@Override
	public void deleteSegments (List<Long> segKeys) {
		store.deleteSegments(name, segKeys);
	}

}
