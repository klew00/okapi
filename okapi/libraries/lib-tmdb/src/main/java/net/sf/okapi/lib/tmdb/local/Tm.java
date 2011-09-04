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

package net.sf.okapi.lib.tmdb.local;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.tmdb.IRecord;
import net.sf.okapi.lib.tmdb.ITm;

public class Tm implements ITm {

	private Repository store;
	private String name;
	private String uuid;
	private PreparedStatement pstmGet;
	private long pageTop = 1;
	private int pageSize = 100;

	private PreparedStatement pstmAdd;
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

	public void close () {
		try {
			if ( pstmGet != null ) {
				pstmGet.close();
				pstmGet = null;
			}
			if ( pstmAdd != null ) {
				pstmAdd.close();
				pstmAdd = null;
			}
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
		String res[] = store.getTmData(uuid);
		return res[0];
	}

	@Override
	public List<IRecord> getRecords () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUUID () {
		return uuid;
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
				if ( name.indexOf('_') == -1 ) {
					if ( !name.equals("FLAG") ) {
						hasTUField = true;
						break;
					}
				}
			}
			
			if ( hasTUField ) {
			//todo	
			}
			else { // Simple select in the segment table
				StringBuilder tmp = new StringBuilder("SELECT ID, FLAG");
				for ( String name : names ) {
					tmp.append(", "+name);
				}
				tmp.append(" FROM "+name+"_SEG "); //LIMIT ? OFFSET ?;");
				pstmGet = store.getConnection().prepareStatement(tmp.toString());
			}
			
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	private ResultSet getPage (long start) {
		ResultSet result = null;
		try {
//			pstmGet.setLong(1, pageSize);
//			pstmGet.setLong(2, start);
			result = pstmGet.executeQuery();
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	@Override
	public ResultSet getFirstPage () {
		return getPage(1);
	}

	@Override
	public ResultSet getLastPage () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getNextPage () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getPreviousPage () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPageSize (int size) {
		if ( size < 1 ) size = 10;
		this.pageSize = size;
	}
	
	private boolean isSegmentField (String name) {
		return (( name.indexOf('_') != -1 ) || ( name.equals("FLAG") )); 
	}

	@Override
	public void startImport () {
		try {
			if ( pstmAdd != null ) {
				pstmAdd.close();
			}
			pstmAdd = null;
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
			if ( pstmAdd != null ) {
				pstmAdd.close();
			}
			pstmAdd = null;
			existingTuFields = null;
			existingSegFields = null;
			fieldsToImport = null;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private void verifyFieldsToImport (LinkedHashMap<String, Object> fields,
		boolean useValuesAsDefault)
	{
		try {
			LinkedHashMap<String, String> tuFieldsToCreate = null;
			LinkedHashMap<String, String> segFieldsToCreate = null;
			LinkedHashMap<String, String> currentFieldsToCreate;
			String type = null;
			// Go through the list of fields
			// and add the ones that do not exist to the list of fields to create
			for ( String name : fields.keySet() ) {
				// Check where the field belongs, and if it exists yet
				// currentFieldsToCreate will be null if the field is not to add
				currentFieldsToCreate = null;
				if ( isSegmentField(name) ) {
					// This is a segment-level field
					if ( !existingSegFields.contains(name) ) {
						if ( segFieldsToCreate == null ) {
							segFieldsToCreate = new LinkedHashMap<String, String>();
						}
						currentFieldsToCreate = segFieldsToCreate;
					}
				}
				else {
					// This is a TU-level field
					if ( !existingTuFields.contains(name) ) {
						if ( tuFieldsToCreate == null ) {
							tuFieldsToCreate = new LinkedHashMap<String, String>();
						}
						currentFieldsToCreate = tuFieldsToCreate;
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
				if ( currentFieldsToCreate != null ) {
					// Nothing to add, move on to the next field
					currentFieldsToCreate.put(name, type);
				}
			}
			
			// Create the new fields as needed, and update the lists
			// The lists can be null or empty in this call
			store.createNewFields(name, true, segFieldsToCreate, existingSegFields);
			store.createNewFields(name, false, tuFieldsToCreate, existingTuFields);

			// Create or re-create the statement to insert the entry
			if (( pstmAdd == null ) || Util.isEmpty(segFieldsToCreate) || Util.isEmpty(tuFieldsToCreate) ) {
				if ( pstmAdd != null ) {
					pstmAdd.close();
				}
				boolean first = true;
				StringBuilder tmp = new StringBuilder("INSERT INTO "+name+"_SEG (");
				for ( String name : fieldsToImport.keySet() ) {
					if ( first ) {
						first = false;
						tmp.append(name);
					}
					else {
						tmp.append(", "+name);
					}
				}
				tmp.append(") VALUES (");
				for ( int i=0; i<fieldsToImport.size(); i++ ) {
					tmp.append((i==0) ? "?" : ", ?");
				}
				tmp.append(");");
				pstmAdd = store.getConnection().prepareStatement(tmp.toString());
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public IRecord addRecord (Map<String, String> fields) {
		return null;//todo
	}

	@Override
	public void addNewRecord (LinkedHashMap<String, Object> fields) {
		if ( Util.isEmpty(fields) ) {
			return;
		}
		// Check if any field is new and create the columns as needed
		// And create a new pre-defined statement if needed
		verifyFieldsToImport(fields, false);
		
		try {
			// Do the addition using the pre-defined statement
			// Fill the parameters
			int n = 1;
			Object value;
			for ( String name : fieldsToImport.keySet() ) {
				// Get the value from the provided map or from the defaults
				if ( fields.containsKey(name) ) {
					value = fields.get(name);
				}
				else {
					// Not in the current set of fields provided
					// Set the value to its default
					value = fieldsToImport.get(name);
				}
	
				if ( value instanceof String ) {
					pstmAdd.setString(n, (String)value);
				}
				else if ( value instanceof Boolean ) {
					pstmAdd.setBoolean(n, (Boolean)value);
				}
				else if ( value instanceof Integer ) {
					pstmAdd.setLong(n, ((Integer)value).longValue());
				}
				else if ( value instanceof Long ) {
					pstmAdd.setLong(n, (Long)value);
				}
				n++;
			}
			// Execute the addition
			pstmAdd.executeUpdate();
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

}
