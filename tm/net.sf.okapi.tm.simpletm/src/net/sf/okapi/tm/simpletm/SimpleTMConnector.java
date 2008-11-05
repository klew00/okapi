package net.sf.okapi.tm.simpletm;

import java.util.List;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class SimpleTMConnector implements ITMQuery {
	
	private Database db;
	private int maxHits = 5;
	private List<QueryResult> results;
	private int current = -1;
	private String resName;
	private String srcLang;
	private String trgLang;

	public SimpleTMConnector () {
		db = new Database();
	}
	
	public void setMaximumHits (int max) {
		if ( max < 1 ) maxHits = 1;
		else maxHits = max;
	}

	public void setThreshold (int threshold) {
		// Threshold not used in this TM
	}

	public void close() {
		db.close();
	}

	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}
	
	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	/**
	 * opens a TM.
	 * @param connectionString The full path of the database name to open.
	 */
	public void open (String connectionString) {
		db.open(connectionString);
	}

	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}
	
	public int query (TextFragment text) {
		current = -1;
		results = db.query(text, resName, maxHits);
		if ( results == null ) return 0;
		current = 0;
		return results.size();
	}
	
	public void setContext (String key,
		String value)
	{
		// the only context available with this TM is resname
		resName = value;
	}

	public void setLanguages (String sourceLang, String targetLang) {
		srcLang = sourceLang;
		trgLang = targetLang;
	}

	public String getSourceLanguage () {
		return srcLang;
	}
	
	public String getTargetLanguage () {
		return trgLang;
	}
	
}
