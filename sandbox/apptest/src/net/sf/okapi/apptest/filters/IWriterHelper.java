package net.sf.okapi.apptest.filters;

import net.sf.okapi.apptest.common.IReferenceable;

/**
 * The IWriterHelper interface provides the methods to access the various data
 * related to writing out the original extracted format.
 */
public interface IWriterHelper {

	/**
	 * Gets the reference for a given id.
	 * @param id The id of the reference to fetch.
	 * @return The referenced object, or null if it is not in the reference list.
	 */
	public IReferenceable getReference (String id);
	
	/**
	 * Indicates if the current output should use the target data (vs. source).
	 * @return True if the output is the target.
	 */
	public boolean useTarget ();
	
	/**
	 * Encodes/escapes a text to conform to the original format.
	 * For example a '<' would be escaped to '&lt;' for an encoder that would
	 * implements an XML/HTML original format.
	 * @param text The text to encode.
	 * @return The resulting encoded text.
	 */
	public String encode (String text);
	
	/**
	 * Encodes/escapes a character to conform to the original format.
	 * For example a '<' would be escaped to '&lt;' for an encoder that would
	 * implements an XML/HTML original format.
	 * @param value The character to encode.
	 * @return The resulting encoded text.
	 */
	public String encode (char value);
	
	public String getLanguage ();
	
	public String getLayerBeforeCode ();
	public String getLayerAfterCode ();
	public String getLayerBeforeInline ();
	public String getLayerAfterInline ();

}
