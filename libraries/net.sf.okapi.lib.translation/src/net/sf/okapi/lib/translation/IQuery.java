package net.sf.okapi.lib.translation;

import net.sf.okapi.common.resource.TextFragment;

public interface IQuery {

	public void setLanguages (String sourceLang, String targetLang);
	
	public String getSourceLanguage ();
	
	public String getTargetLanguage ();
	
	public void setContext (String key, String value);

	public void open (String connectionString);
	
	public void close ();
	
	public int query (String plainText);
	
	/**
	 * Starts a query for a given text.
	 * @param text The text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (TextFragment text);
	
	public boolean hasNext ();

	/**
	 * Gets the next hit for the current query.
	 * @return The source and target text of the hit,
	 * or null if there is no more hit. 
	 */
	public QueryResult next ();
	
}
