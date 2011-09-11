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

package net.sf.okapi.lib.tmdb;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface ITm {

	// 1-Based index of the given fields (in result set)
	public static final int SEGKEY_FIELD = 1;
	public static final int FLAG_FIELD = 2;
	
	/**
	 * Gets the UUID of this TM.
	 * The UUID is a global universal unique identifier. It can be used for example
	 * to link display property with a specific TM.
	 * @return the UUID of this TM.
	 */
	public String getUUID ();
	
	/**
	 * Gets the name of this TM.
	 * The name of the TM is unique per repository.
	 * @return the name of this TM.
	 */
	public String getName ();
	
	/**
	 * Gets the description of this TM.
	 * @return the description of this TM.
	 */
	public String getDescription ();

	/**
	 * Gets a list of all available fields in this TM.
	 * @return the list of all available fields in this TM.
	 */
	public List<String> getAvailableFields ();
	
	public void rename (String newName);
	
	/**
	 * Sets the list of fields to be returned by {@link #getRecords()}.
	 * @param names list of fields to be returned. 
	 */
	public void setRecordFields (List<String> names);

	public void startImport ();
	
	public void finishImport();
	
	/**
	 * Adds a record to the repository.
	 * @param tuKey the key for the text unit this record belongs.
	 * <b>You must use -1 when adding the first entry of this text unit</b> the call returns
	 * the text unit key that you can use for the subsequent call to add other records to
	 * that given text unit.
	 * @param tuFields the list of the text unit level fields.
	 * @param segFields the list of the segment level fields.
	 * @return the key of text unit of the added record. 
	 */
	public long addRecord (long tuKey,
		Map<String, Object> tuFields,
		Map<String, Object> segFields);

	/**
	 * Sets the number of records a call to a paging method should return.
	 * @param size the number of records a call to a paging method should return.
	 */
	public void setPageSize (int size);
	
	public ResultSet getFirstPage ();
	
	public ResultSet getLastPage ();
	
	public ResultSet getNextPage ();
	
	public ResultSet getPreviousPage ();
	
//	public void addLocale (String LocaleId);
//	
//	public void deleteLocale (String localeId);
//	
//	public List<String> getLocales ();
}
