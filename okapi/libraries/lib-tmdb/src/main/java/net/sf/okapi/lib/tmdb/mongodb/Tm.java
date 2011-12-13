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

package net.sf.okapi.lib.tmdb.mongodb;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.DbUtil.PageMode;
import net.sf.okapi.lib.tmdb.mongodb.Repository;

public class Tm implements ITm {
	
	private Repository store;
	private String name;
	private String uuid;
	
	private int limit = 500;
	private boolean needPagingRefresh = true; // Must be set to true anytime we change the row count
	private long totalRows;
	private long pageCount;
	private long currentPage = -1; // 0-based
	private PageMode pageMode = PageMode.EDITOR;
	
	BasicDBObject sortObject = new BasicDBObject("_id",1);
	
	@SuppressWarnings("unused")
	private boolean pagingWithMethod = true;

	private List<String> recordFields = new ArrayList<String>();
	
	private static int segIndex=0;

	private List<String> existingTuFields;
	private List<String> existingSegFields;
	private List<String> existingLocales;
	
	//List<String> cachedTuFields;
	//List<String> cachedSegFields;
	//List<String> cachedLocales;
	//int cachedSegmentCount;
	
	public Tm (Repository store,
			String uuid,
			String name)
		{
			this.store = store;
			this.name = name;
			this.uuid = uuid;
		}
	
	@Override
	public String getUUID() {
		return uuid;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return store.getTmDescription(name);
	}
	
	@Override
	public List<String> getAvailableFields() {
		return store.getAvailableFields(name);
	}

	@Override
	public void rename(String newName) {
		store.renameTm(name, newName);
		name = newName;
	}

	@Override
	public void setRecordFields(List<String> names) {
		recordFields.clear();
		recordFields.add(Repository.SEG_COL_SEGKEY);
		recordFields.add(Repository.SEG_COL_FLAG);
		recordFields.addAll(names);
	}

	@Override
	public void startImport() {
		existingTuFields = store.getTuFields(name);
		existingSegFields = store.getSegFields(name);
		existingLocales = store.getTmLocales(name);
	}
	
	@Override
	public void finishImport() {
		existingTuFields = null;
		existingSegFields = null;
		existingLocales = null;
	}
	
	@Override
	public long addRecord (long tuKey,
		Map<String, Object> tuFields,
		Map<String, Object> segFields)
	{
		DBCollection segColl = store.getDb().getCollection(name+"_SEG");
		
		calculateAndUpdateTuFields(tuFields);
		calculateAndUpdateSegFields(segFields);
		updateTMlocales();
		
		BasicDBObject doc = new BasicDBObject();
		segIndex++;
		doc.put(Repository.SEG_COL_SEGKEY, segIndex);
		doc.put(Repository.SEG_COL_FLAG, true);
		for (Entry<String, Object> entry : segFields.entrySet()) {
			doc.put(entry.getKey(), entry.getValue());
		}
		if(tuFields != null){
			for (Entry<String, Object> entry : tuFields.entrySet()) {
				doc.put(entry.getKey(), entry.getValue());
			}
		}
		segColl.insert(doc);
		return tuKey;
	}

	/**
	 * the provided fields check which ones need to be added to the tuFields
	 * @param tuFields
	 * @return null if no new fields
	 */
	@SuppressWarnings("unused")
	private List<String> getNewTuFields(Map<String, Object> tuFields) {
		if(tuFields != null){
			List<String> availTuProps = store.getTuFields(name);
			if(availTuProps.size()==0){
				return new ArrayList<String>(tuFields.keySet());
			}else{
			    Collection<String> result = new ArrayList<String>(availTuProps);
		    	result.removeAll(tuFields.keySet());
		    	return new ArrayList<String>(result);
			}
		}
		return null;
	}
	
	
	private void calculateAndUpdateTuFields(Map<String, Object> intputFields) {
		if(intputFields != null){
			if(existingTuFields.size()==0){
				updateTuFields(new ArrayList<String>(intputFields.keySet()));
			}else{
			    Collection<String> newFields = new ArrayList<String>(intputFields.keySet());
			    newFields.removeAll(existingTuFields);
				if(newFields.size() > 0){
				    existingTuFields.addAll(newFields);
					updateTuFields(existingTuFields);				    
		    		//return newFields.size();
		    	}
			}
		}
	}
	
	private void calculateAndUpdateSegFields(Map<String, Object> intputFields) {
		if(intputFields != null){
			if(existingSegFields.size()==0){
				updateSegFields(new ArrayList<String>(intputFields.keySet()));
			}else{
			    Collection<String> newFields = new ArrayList<String>(intputFields.keySet());
			    newFields.removeAll(existingSegFields);
				if(newFields.size() > 0){
				    existingSegFields.addAll(newFields);
					updateSegFields(existingSegFields);
		    		//return newFields.size();
		    	}
			}
		}
	}
	
	private int updateTMlocales() {
		List<String> segFields = store.getSegFields(name);
		
		ArrayList<String> langs = new ArrayList<String>();
		
		for (String field : segFields) {
			if ( field.startsWith(DbUtil.TEXT_PREFIX) ) {
				int n = field.lastIndexOf(DbUtil.LOC_SEP);
				if ( n > -1 ) {
					langs.add(field.substring(n+1));
				}
			}
		}

	    Collection<String> result = new ArrayList<String>(langs);
    	result.removeAll(existingLocales);
    	if(result.size() > 0){
    		existingLocales.addAll(result);
        	updateLocales(existingLocales);
    		return result.size();
    	}
		return 0;
	}
	
	@Override
	public void setPageSize(long size) {
		if ( size < 2 ) this.limit = 2;
		else this.limit = (int) size;
		// We changed the number of rows
		needPagingRefresh = true;
	}
	
	@Override
	public long getPageSize() {
		return limit;
	}

	@Override
	public void moveBeforeFirstPage() {
		currentPage = -1;
	}
	
	@Override
	public ResultSet getFirstPage() {
		checkPagingVariables();
		currentPage = 0;
		return getPage();
	}

	@Override
	public ResultSet getLastPage() {	
		checkPagingVariables();
		currentPage = pageCount-1;
		//int pageCount = calculatePageCount(); 
		//if (pageCount >= 2){
		//	currentPage = pageCount-1;			
		//} 
		return getPage();
	}

	@Override
	public ResultSet getNextPage() {
		checkPagingVariables();
		if ( currentPage >= pageCount-1 ) return null; // Last page reached
		currentPage++;
		//int pageCount = calculatePageCount();
		//if(currentPage < pageCount){
		//	currentPage++;
		//}
		return getPage();
	}
	
	@Override
	public ResultSet getPreviousPage() {
		checkPagingVariables();
		if ( currentPage <= 0 ) return null; // First page reached
		currentPage--;
		//if(currentPage > 0){
		//	currentPage--;
		//}
		return getPage();
	}

	@Override
	public List<String> getLocales() {
		return store.getTmLocales(name);
	}
	
	@Override
	public long getCurrentPage() {
		return currentPage;
	}
	
	@Override
	public long getPageCount() {
		return pageCount;
	}
	
	/**
	 * Return the ResultSet from the current page. Should all Close() to release the Mongo DBCursor.
	 * @return
	 */
	private ResultSet getPage(){
		DBCollection segColl = store.getDb().getCollection(name+"_SEG");
		DBCursor cur;
		if (pageMode == PageMode.EDITOR ) {
			cur = segColl.find().sort(sortObject).limit(limit).skip((int)((limit-1)*currentPage));
		}
		else {
//TOFIX: YS: Not sure if it's the right code for this case (no overlap)
			cur = segColl.find().sort(sortObject).limit(limit).skip((int)(limit*currentPage));
		}

		return new MongodbResultSet(cur, recordFields, limit);
	}

	/**
	 * Calculate the total page count in the tm
	 * @return pages
	 */
	public int calculatePageCount(){
		DBCollection segColl = store.getDb().getCollection(name+"_SEG");

		//TODO make it a long
		int count = (int) segColl.count();
		
		if(count > 0){
			return count/limit;
		}else{
			return 0;
		}
	}
	
	/**
	 * Update the tuFields field
	 * @param fields
	 */
	void updateTuFields (List<String> fields){
        updateCommaSeparatedValues(fields, Repository.TM_COL_TU_FIELDS);
	}
	
	/**
	 * Update the segFields field
	 * @param fields
	 */
	void updateSegFields (List<String> fields){
		updateCommaSeparatedValues(fields, Repository.TM_COL_SEG_FIELDS);
	}
	
	/**
	 * Update the locales field
	 * @param fields
	 */
	void updateLocales (List<String> fields){
		updateCommaSeparatedValues(fields, Repository.TM_COL_LOCALES);
	}
	
	/**
	 * Updates list as comma separated values
	 * @param values
	 * @param columnName
	 */
	void updateCommaSeparatedValues (List<String> values, String columnName){
		DBCollection tmList = store.getDb().getCollection(Repository.TM_COLL);
		
		BasicDBObject query = new BasicDBObject();
        query.put(Repository.TM_COL_NAME, name);

        DBObject obj = tmList.findOne(query);
        
        if(obj == null){
        	throw new RuntimeException(String.format("TM '%s' does not exists.", name));
        }
        tmList.update(query, new BasicDBObject("$set", new BasicDBObject(columnName, buildCommaList(values))));
	}
	
	/**
	 * Build comma separated list of values
	 * @param items
	 * @return
	 */
	private String buildCommaList(List<String> items){
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String item : items) {
			i++;
			if(i == 1){
				sb.append(item);	
			}else{
				sb.append(","+item);
			}
		}		
		return sb.toString();
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
				if ( (totalRows-1) % (limit-1) > 0 ) pageCount++; // Last page
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
	}

	@Override
	public void addLocale(String localeCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteLocale(String localeCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PageMode getPageMode() {
		return pageMode;
	}

	@Override
	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
	@Override
	public void renameLocale (String currentCode,
		String newCode)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRecord (long segKey,
		Map<String, Object> tuFields,
		Map<String, Object> segFields)
	{
		DBCollection segColl = store.getDb().getCollection(name+"_SEG");
		
		BasicDBObject doc = new BasicDBObject();
		for (Entry<String, Object> entry : segFields.entrySet()) {
			doc.put(entry.getKey(), entry.getValue());
		}
		if(tuFields != null){
			for (Entry<String, Object> entry : tuFields.entrySet()) {
				doc.put(entry.getKey(), entry.getValue());
			}
		}
		
		BasicDBObject query = new BasicDBObject();
        query.put(Repository.SEG_COL_SEGKEY, segKey);

        BasicDBObject set = new BasicDBObject("$set", doc);
        
		segColl.update(query, set, false, false);
	}

	@Override
	public void addField (String fullName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteField (String fullName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renameField (String currentFullName, String newFiiullName) {

		boolean found = false;
		
		//--check segfields-
		List<String> segFields = store.getSegFields(name);
		if(segFields.contains(currentFullName)){
			segFields.remove(currentFullName);
			segFields.add(newFiiullName);
			updateSegFields(segFields);
			found=true;
		}
		
		//--check tufields-
		List<String> tuFields = store.getTuFields(name);
		if(tuFields.contains(currentFullName)){
			tuFields.remove(currentFullName);
			tuFields.add(newFiiullName);
			updateTuFields(tuFields);
			found=true;
		}
		
		if(found){
			DBCollection segColl = store.getDb().getCollection(name+"_SEG");

			BasicDBObject doc = new BasicDBObject();
			doc.put(currentFullName, newFiiullName);

			BasicDBObject query = new BasicDBObject();

			BasicDBObject set = new BasicDBObject("$rename", doc);

			segColl.update(query, set, false, true);
		}
	}

	@Override
	public void setSortOrder (LinkedHashMap<String, Boolean> fields) {
		
		BasicDBObject sortObject = new BasicDBObject();
		for (Entry<String, Boolean> field : fields.entrySet()) {
			sortObject.put(field.getKey(),field.getValue());
		}
	}

	@Override
	public long getTotalSegmentCount () {
		return store.getTotalSegmentCount(name);
	}

	@Override
	public void deleteSegments (List<Long> segKeys) {
		// TODO Auto-generated method stub
		throw new RuntimeException("deleteSegments() not implemented yet");
	}

	@Override
	public ResultSet refreshCurrentPage () {
		long oldPage = currentPage;
		needPagingRefresh = true;
		checkPagingVariables();
		if ( pageCount > oldPage ) currentPage = oldPage;
		else if ( pageCount > 0 ) currentPage = pageCount-1; 
		return getPage();
	}

	@Override
	public IRepository getRepository () {
		return store;
	}

}
