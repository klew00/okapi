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
	 * Page mode for editors.
	 * See {@link #setPageMode(int)} for details. 
	 */
	public static final int PAGEMODE_EDITOR = 0;

	//public static final int PAGEMODE_ITERATOR = 1;
	
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

	/**
	 * Rename this TM.
	 * @param newName the name name of the TM.
	 */
	public void rename (String newName);
	
	/**
	 * Sets the list of fields to be returned by {@link #getRecords()}.
	 * @param names list of fields to be returned. 
	 */
	public void setRecordFields (List<String> names);

	/**
	 * Prepares the system to import a set of new entries.
	 * This method must be called before calling {@link #addRecord(long, Map, Map)}.
	 * You must call {@link #finishImport()} to terminate the batch of import.
	 * @see #finishImport()
	 * @see #addRecord(long, Map, Map)
	 */
	public void startImport ();
	
	/**
	 * Finishes a batch of {@link #addRecord(long, Map, Map)}.
	 * @see #startImport()
	 * @see #addRecord(long, Map, Map)
	 */
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
	 * The minimum size of a page is 2. If a smaller value is given, the value is
	 * silently changed to 2.
	 */
	public void setPageSize (long size);
	
	/**
	 * Gets the current number of records per page.
	 * @return the current number of records per page.
	 */
	public long getPageSize ();
	
	/**
	 * Sets the type of pages the system returns.
	 * @param pageMode the type of pages the system should return.
	 * Currently: {@link #PAGEMODE_EDITOR}.
	 * <p>In {@link #PAGEMODE_EDITOR} mode the last record of the previous page
	 * is the first record of the next one, and the first record of the next one 
	 * is the last of the previous. In other words: there is one record that is 
	 * common to each adjacent pages.
	 * <p>For example, if the database has 6 records numbered from 1 to 6 and 
	 * the page size is set to 3: There are 3 pages (not 2). The first record of 
	 * the first page is 1, the one of the second page is 3 (not 4), and the one
	 * of the last page is 5.
	 */
	public void setPageMode (int pageMode);
	
	/**
	 * Gets the type of page the system returns.
	 * @return the type of page the system returns.
	 * See {@link #setPageMode(int)} for details.
	 */
	public int getPageMode ();
	
	/**
	 * Moves the page cursor before the first page.
	 * See {@link #setPageMode(int)} for details.
	 */
	public void moveBeforeFirstPage ();
	
	/**
	 * Gets the first page of records.
	 * This method is the same has calling {@link #moveBeforeFirstPage()} and then {@link #getNextPage()}.
	 * See {@link #setPageMode(int)} for details.
	 * @return the results for the first page, or null if there is no first page.
	 */
	public ResultSet getFirstPage ();
	
	/**
	 * Gets the last page of records.
	 * @return the results for the last page, or null if the is no last page.
	 * Note that the number of records in the last page may be smaller than the 
	 * current page size.
	 * See {@link #setPageMode(int)} for details.
	 * @param the results for the last page, or null if there is no last page.
	 */
	public ResultSet getLastPage ();
	
	public ResultSet getNextPage ();
	
	public ResultSet getPreviousPage ();
	
//	public void addLocale (String LocaleId);
//	
//	public void deleteLocale (String localeId);

	/**
	 * Gets the list of the locales in this TM.
	 * @return the list of the locales in this TM.
	 */
	public List<String> getLocales ();
	
	/**
	 * Gets the zero-based index of the current page.
	 * @return the index of the current page, or -1 if no page is active.
	 * 0 is the first page.
	 */
	public long getCurrentPage ();
	
	/**
	 * Gets the number of pages available.
	 * @return the number of pages available.
	 */
	public long getPageCount ();
	
}
