package net.sf.okapi.common.resource;

import java.util.List;

public interface IPart {

	public String toString ();
	
	public boolean isSegment ();
	
	public void setIsSegment (boolean value);
	
	public boolean isEmpty ();
	
	public boolean hasText (boolean whiteSpacesAreText);

	public void reset ();
	
	public void append (String text);

	public void append (char ch);
	
	public void append (IFragment fragment);

	public void setContent (String codedText);

	public void setContent (String codedText,
		List<IFragment> codes);
	
	public void setContent (IFragment fragment);
	
	public String getCodedText ();
	
	public List<IFragment> getCodes ();

	public List<IFragment> getFragments ();

	public String getCodeForID (int id,
		int type);

	public String toXML ();

	public IPart copy (int start);

	public IPart copy (int start,
		int end);

	/**
	 * Changes a section of text into an in-line code.
	 * @param start The position of the first character to change to code,
	 * in the coded text string.
	 * @param end The position of the first character after the new code,
	 * in the coded text string.
	 * @param type The type of the new code. This must be one of the values
	 * define as IContainer2.CODE_*.
	 * @param id The identifier of the new code. 
	 */
	public void changeToCode (int start,
		int end,
		int type,
		int id);

}
