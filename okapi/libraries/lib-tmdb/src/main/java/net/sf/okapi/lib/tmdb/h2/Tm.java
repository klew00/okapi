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

public class Tm implements ITm {

	private Repository store;
	private String name;
	private String uuid;
	private PreparedStatement pstmGet;

	private int pageMode = ITm.PAGEMODE_EDITOR;
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

	public void close () {
		try {
			if ( pstmGet != null ) {
				pstmGet.close();
				pstmGet = null;
			}
			closeAddStatements();
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
				if ( !isSegmentField(name) ) {
					hasTUField = true;
					break;
				}
			}
			
			StringBuilder tmp;
			if ( hasTUField ) {
				String tuTable = "\""+name+"_TU\"";
				String segTable = "\""+name+"_SEG\"";
				tmp = new StringBuilder("SELECT "+segTable+".SEGKEY, "+segTable+".FLAG");
				for ( String name : names ) {
					if ( isSegmentField(name) ) tmp.append(", "+segTable+".\""+name+"\"");
					else tmp.append(", "+tuTable+".\""+name+"\"");
				}
				tmp.append(" FROM "+segTable+" LEFT JOIN "+tuTable+" ON "+segTable+".TUREF="+tuTable+".TUKEY");
				
			}
			else { // Simple select in the segment table
				tmp = new StringBuilder("SELECT SEGKEY, FLAG");
				for ( String name : names ) {
					tmp.append(", \""+name+"\"");
				}
				tmp.append(" FROM \""+name+"_SEG\"");
			}
			
			//old tmp.append(" ORDER BY SegKey LIMIT ? OFFSET ?");
			tmp.append(" WHERE SegKey>=? ORDER BY SegKey LIMIT ?");

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
	
	private boolean isSegmentField (String name) {
		return (( name.indexOf(DbUtil.LANG_SEP) != -1 )
			|| name.equals("TUREF")
			|| name.equals("FLAG")
		); 
	}

	@Override
	public void startImport () {
		try {
			closeAddStatements();
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
				if ( !isSegmentField(name) ) continue;
			}
			else {
				if ( isSegmentField(name) ) continue;
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
			
			if ( !Util.isEmpty(fields) ) {

				String type = null;
				// Go through the list of fields
				// and add the ones that do not exist to the list of fields to create
				for ( String name : fields.keySet() ) {
					// This is a TU-level field
					boolean add = false;
					if ( !existingFields.contains(name) ) {
						add = true;
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
						}
						else if (( value instanceof Long ) || ( value instanceof Integer )) {
							type = "INTEGER";
							if ( useValuesAsDefault ) fieldsToImport.put(name, value);
							else fieldsToImport.put(name, 0);
						}
						else if ( value instanceof Boolean ) {
							type = "BOOLEAN";
							if ( useValuesAsDefault ) fieldsToImport.put(name, value);
							else fieldsToImport.put(name, false);
						}
						else throw new RuntimeException("Invalid field type to add.");
					}
					
					// If the field is to create type should be set because
					// it was also a filed that wasn't in the fieldsToImport list
					if ( add ) {
						// Nothing to add, move on to the next field
						fieldsToCreate.put(name, type);
					}
				}
				
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
						if ( !isSegmentField(name) ) continue;  // Skip over TU level fields
						if ( first ) {
							first = false;
							tmp.append("\""+name+"\"");
						}
						else {
							tmp.append(", \""+name+"\"");
						}
						count++;
					}
					tmp.append(", TUREF) VALUES (?"); // Always include TUREF at the end
					for ( int i=0; i<count; i++ ) {
						tmp.append(", ?");
					}
					tmp.append(");");
					pstmAddSeg = store.getConnection().prepareStatement(tmp.toString());
				}
			}
			else {
				// Create or re-create the statement to insert the entry
				if (( pstmAddTu == null ) || !Util.isEmpty(fieldsToCreate) ) {
					if ( pstmAddTu != null ) {
						pstmAddTu.close();
					}
					StringBuilder tmp;
					if ( !Util.isEmpty(fieldsToImport) ) {
						boolean first = true;
						int count = 0;
						tmp = new StringBuilder("INSERT INTO \""+name+"_TU\" (");
						for ( String name : fieldsToImport.keySet() ) {
							if ( isSegmentField(name) ) continue; // Skip over segment-level fields
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
			pageCount = (totalRows-1) / (limit-1); // -1 for overlap
			if ( (totalRows-1) % (limit-1) > 0 ) pageCount++; // Last page
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
			return (page * (limit-1)) + 1;
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
	public int getPageMode () {
		return pageMode;
	}

	@Override
	public void setPageMode (int pageMode) {
		this.pageMode = pageMode;
	}
}
