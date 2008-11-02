package net.sf.okapi.lib.translation;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.okapi.common.resource.TextFragment;

public class QueryManager implements IQuery {

//	ArrayList<ResourceItem> list;
	Hashtable<Integer, ResourceItem> resList;
	ArrayList<QueryResult> results;
	int current = -1;
	int lastId = 0;
	String srcLang;
	String trgLang;
	String contextKey;
	String contextValue;
	
	public QueryManager () {
		resList = new Hashtable<Integer, ResourceItem>();
		results = new ArrayList<QueryResult>();
	}
	
	public int addResource (IQuery connector) {
		assert(connector!=null);
		ResourceItem ri = new ResourceItem();
		ri.query = connector;
		resList.put(++lastId, ri);
		return lastId;
	}
	
	public int addAndInitializeResource (IQuery connector,
		String connectionString)
	{
		// Add the resource
		int id = addResource(connector);
		// open it and set the current options
		connector.open(connectionString);
		if (( srcLang != null ) && ( trgLang != null )) {
			connector.setLanguages(srcLang, trgLang);
		}
		connector.setContext(contextKey, contextValue);
		// Set the connection string
		ResourceItem ri = resList.get(id);
		ri.connectionString = connectionString;
		return id;
	}
	
	public void setEnabled (int connectorId,
		boolean enabled)
	{
		resList.get(connectorId).enabled = enabled;
	}
	
	public void remove (int connectorId) {
		resList.remove(connectorId);
	}
	
	public IQuery getResource (int connectorId) {
		return resList.get(connectorId).query;
	}
	
	public void close () {
		for ( ResourceItem ri : resList.values() ) {
			ri.query.close();
		}
	}

	public boolean hasNext() {
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	public QueryResult next() {
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	public void open (String connectionString) {
		for ( ResourceItem ri : resList.values() ) {
			ri.query.open(ri.connectionString);
		}
	}

	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}

	public int query (TextFragment text) {
		results.clear();
		ResourceItem ri;
		for ( int id : resList.keySet() ) {
			ri = resList.get(id);
			if ( ri.query.query(text) > 0 ) {
				QueryResult res;
				while ( ri.query.hasNext() ) {
					res = ri.query.next();
					res.connectorId = id;
					results.add(res);
				}
			}
		}
		if ( results.size() > 0 ) current = 0;
		return results.size();
	}

	public void setContext (String key,
		String value)
	{
		contextKey = key;
		contextValue = value;
		for ( ResourceItem ri : resList.values() ) {
			ri.query.setContext(contextKey, contextValue);
		}
	}
	
	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = sourceLang;
		trgLang = targetLang;
		for ( ResourceItem ri : resList.values() ) {
			ri.query.setLanguages(srcLang, trgLang);
		}
	}

}
