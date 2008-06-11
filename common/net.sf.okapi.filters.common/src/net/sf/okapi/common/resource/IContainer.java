package net.sf.okapi.common.resource;

import java.util.List;

public interface IContainer {

	public static final int CODE_OPENING    = 0xE101;
	public static final int CODE_CLOSING    = 0xE102;
	public static final int CODE_ISOLATED   = 0xE103;

	public static final int CHARBASE        = 0xE200;

	/**
	 * Gets A string representation of the content in a format close to
	 * its original format. There is no guarantee that this representation will
	 * be a valid representation of the original format.
	 * @return A string representation of the content.
	 */
	String toString ();
	
	/**
	 * Indicates if the object is empty or not.
	 * @return True if the object is empty, false otherwise.
	 */
	boolean isEmpty ();
	
	/**
	 * Indicates if the object contains at least one character.
	 * @param includingWhiteSpaces True to count whitespace characters as text,
	 * false to exclude them. Note that codes do not count as text.
	 * @return True if the object contains some text.
	 */
	boolean hasText (boolean includingWhiteSpaces);
	
	/**
	 * Appends a text (without codes) to the object. 
	 * @param text The text to append.
	 */
	void append (String text);
	
	/**
	 * Appends a character to the object.
	 * @param ch The character to append.
	 */
	void append (char ch);
	
	/**
	 * Appends a IFragment object to the object.
	 * @param fragment The fragment to append.
	 */
	void append (IFragment fragment);
	
	/**
	 * Appends a IContainer object to the object.
	 * @param content The data to append.
	 */
	void append (IContainer content);
	
	/**
	 * Sets the content of the object, using the codes currently set.
	 * @param codedText The coded text to set. This string must match the
	 * codes already set in the object.
	 * @throws InvalidContentException (runtime)
	 */
	void setContent (String codedText);
	
	/**
	 * Sets the content of the object, using a new set of codes, and a
	 * matching coded text. Any existing codes are removed.
	 * @param codedText The coded text to set. This string must match the
	 * codes passed as parameter.
	 * @param codes The codes to set. They must correspond to the codes
	 * passed as parameter.
	 * @throws InvalidContentException (runtime)
	 */
	void setContent (String codedText,
		List<IFragment> codes);
	
	/**
	 * Gets a list of all the fragments that compose the object.
	 * @return The list of text and code fragments of the object.
	 */
	List<IFragment> getFragments ();
	
	/**
	 * Gets the coded text string of the object. The coded text representation 
	 * of the object is a string-based encoding of the object where in-line codes
	 * are represented by special compressed runs of text more easily and
	 * quickly parsed that a tag-based representation. The characters
	 * representing the codes should be chosen so they do not break 
	 * character-based manipulation like find/replace, etc. 
	 * @return The coded text of the object.
	 */
	String getCodedText ();

	/**
	 * Gets the codes of the object.
	 * @return A list of the codes of the object.
	 */
	List<IFragment> getCodes ();

	/**
	 * Helper function: Indicates if a given code point in a coded string
	 * is the prefix of a code section.
	 * @param codePoint The code point to look at.
	 * @return True if the code-point is the start of a code section.
	 */
	boolean isCodePrefix (int codePoint);

	/**
	 * Sets the property value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @param value The new value to set.
	 */
	void setProperty (String name,
		Object value);
	
	/**
	 * Gets the value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @return The current object associated with the given property name, this
	 * can be null. Null is also return if there is no property for the given name.
	 */
	Object getProperty (String name);
	
	/**
	 * Removes the list of properties associated with the object.
	 */
	void clearProperties ();
}
