package net.sf.okapi.tm.simpletm;

import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class SimpleTMConnector implements ITMQuery {
	
	private Database db;
	private int maxHits = 5;
	private List<QueryResult> results;
	private int current = -1;
	private String srcLang;
	private String trgLang;
	private LinkedHashMap<String, String> attributes;

	public SimpleTMConnector () {
		db = new Database();
		attributes = new LinkedHashMap<String, String>();
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
		results = db.query(text, attributes, maxHits);
		if ( results == null ) return 0;
		current = 0;
		return results.size();
	}
	
	public void setAttribute (String name,
		String value)
	{
		assert(value!=null);
		if ( "resname".equals(name) ) name = Database.NNAME;
		if ( "restype".equals(name) ) name = Database.NTYPE;
		if ( attributes.put(name, value) == null ) {
			// Update the query if this attribute did not exist yet
			db.setAttributes(attributes);
		}
	}
	
	public void removeAttribute (String name) {
		if ( attributes.containsKey(name) ) {
			attributes.remove(name);
			db.setAttributes(attributes);
		}
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = sourceLang;
		trgLang = targetLang;
	}

	public String getSourceLanguage () {
		return srcLang;
	}
	
	public String getTargetLanguage () {
		return trgLang;
	}

	public boolean hasOption (int option) {
		switch ( option ) {
		case HAS_FILEPATH:
		case SUPPORT_EXPORT:
			return true;
		default:
			return false;
		}
	}

	public void export (String outputPath) {
		db.exportToTMX(outputPath, srcLang, trgLang);
	}

}
