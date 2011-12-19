package net.sf.okapi.lib.tmdb.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoHelper {
	
	/**
	 * Find a specific coll entry
	 * @return
	 */
	static DBObject findCollEntry(DB db, String collName, String key, Object value){
		DBCollection coll = db.getCollection(collName);
		return findCollEntry(coll, key, value);
	}
	
	/**
	 * Find a specific coll entry
	 * @return
	 */
	static DBObject findCollEntry(DBCollection coll, String key, Object value){
		BasicDBObject query = new BasicDBObject();
	    query.put(key, value);
	    return coll.findOne(query);
	}
	
	/**
	 * Find a specific coll entry value
	 * @return
	 */
	static String findCollEntryValue(DB db, String collName, String key, Object value, String field){
		DBCollection coll = db.getCollection(collName);
		return findCollEntryValue(coll, key, value, field);
	}
	
	/**
	 * Find a specific coll entry value
	 * @return
	 */
	static String findCollEntryValue(DBCollection coll, String key, Object value, String field){
		BasicDBObject query = new BasicDBObject();
	    query.put(key, value);
	    
	    DBObject obj = coll.findOne(query); 
	    
	    if(obj != null){
	    	return (String)obj.get(field);
	    }else{
	    	return null;
	    }
	}	
}
