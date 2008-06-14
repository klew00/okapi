package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.Util;

/**
 * Reference implementation of the IContainer interface.
 * <p>In this implementation the codes are encoded into 2-char sequence:
 * the type of code, then its index in the list of codes. Note that the map 
 * returned by getCodes() is mapped on indices not IDs (it could be an array).
 */
public class Container implements IContainer {

	private ArrayList<IFragment>  list;
	private IFragment             lastFrag;
	private Map<String, Object>   props;


	static public char ItoC (int index) {
		return (char)(index+CHARBASE);
	}

	static public int CtoI (char index) {
		return ((int)index)-CHARBASE;
	}

	public Container () {
		reset();
	}
	
	/**
	 * Creates a Container object and initializes it with a given plain text string.
	 * @param text The text to set.
	 */
	public Container (String text) {
		reset();
		append(text);
	}
	
	@Override
	public String toString () {
		return getText(false);
	}
	
	public boolean isEmpty () {
		if ( lastFrag == null ) return true;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				if ( ((TextFragment)frag).text.length() > 0 ) return false;
			}
			else return false; // At least one code
		}
		return true; // Found only empty text fragments
	}
	
	public boolean hasText (boolean includingWhiteSpaces) {
		if ( lastFrag == null ) return false;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				if ( includingWhiteSpaces ) {
					if ( ((TextFragment)frag).text.length() > 0 ) return true;
				}
				else { // Check for any non-whitespace chars
					String tmp = ((TextFragment)frag).text.toString();
					for ( int i=0; i<tmp.length(); i++ )
						if ( !Character.isWhitespace(tmp.charAt(i)) ) return true;
				}
			}
		}
		return false;
	}
	
	private void reset () {
		list = new ArrayList<IFragment>();
		lastFrag = null;
	}
	
	public void append (String text) {
		if (( lastFrag == null ) || ( !lastFrag.isText() )) {
			lastFrag = new TextFragment();
			((TextFragment)lastFrag).text.append(text);
			list.add(lastFrag);
		}
		else {
			((TextFragment)lastFrag).text.append(text);
		}
	}

	public void append (char ch) {
		if (( lastFrag == null ) || ( !lastFrag.isText() )) {
			lastFrag = new TextFragment();
			((TextFragment)lastFrag).text.append(ch);
			list.add(lastFrag);
		}
		else {
			((TextFragment)lastFrag).text.append(ch);
		}
	}

	public void append (IFragment fragment) {
		list.add(fragment);
		lastFrag = fragment;
	}

	public void append (IContainer content) {
		List<IFragment> fragList = content.getFragments();
		for ( IFragment frag : fragList ) {
			list.add(frag);
		}
		if ( list.size() > 0 )
			lastFrag = list.get(list.size()-1);
	}

	/**
	 * Gets the text of the object in original or coded format. 
	 * @param coded Indicates if the returned text should be coded. Use true 
	 * for coded, false for original.
	 * @return The coded or original text of the object.
	 */
	private String getText (boolean coded) {
		// Empty
		if ( lastFrag == null ) return "";

		// Only one text fragment
		if (( list.size() == 1 ) && ( lastFrag.isText() )) {
			return lastFrag.toString();
		}
		
		// Multiple segments or one code fragment
		StringBuilder text = new StringBuilder();
		int index = 0;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				text.append(frag.toString());
			}
			else {
				if ( coded ) {
					text.append((char)((CodeFragment)frag).type);
					text.append(ItoC(index));
					index++; // Not used if not coded
				}
				else {
					text.append(frag.toString());
				}
			}
		}
		return text.toString();
	}

	public String getCodedText () {
		return getText(true);
	}

	public List<IFragment> getCodes () {
		ArrayList<IFragment> codes = new ArrayList<IFragment>();
		int index = 0;
		for ( IFragment frag : list ) {
			if ( !frag.isText() ) {
				codes.add(frag);
				index++;
			}
		}
		return codes;
	}

	public List<IFragment> getFragments () {
		return list;
	}

	public boolean isCodePrefix (int codePoint) {
		switch ( codePoint ) {
		case CODE_ISOLATED:
		case CODE_OPENING:
		case CODE_CLOSING:
			return true;
		}
		return false;
	}
	
	// Specific to this class for now
/*	public int getIDFromIndex (int index) {
		int codeIndex = 0;
		for ( IFragment frag : list ) {
			if ( !frag.isText() ) {
				if ( codeIndex == index ) return ((CodeFragment)frag).id;
				codeIndex++;
			}
		}
		return -1; // Not found
	}
*/
	/**
	 * Resets the content of the object based on a coded text string and a map of 
	 * codes.
	 * @param codedText The coded text to use to create the new content.
	 * @param codes The codes to use along with the coded text. Use null to use the
	 * existing codes.
	 * @throws InvalidContentException (runtime)
	 */
	private void resetContent (String codedText,
		List<IFragment> codes)
	{
		// Store code fragments temporarily
		List<IFragment> tmpList = codes;
		if ( tmpList == null ) tmpList = getCodes();
		reset(); // Reset all

		if ( codedText == null ) {
			if ( tmpList.size() > 0 ) //TODO: Maybe null just reset all instead of giving an error?
				throw new InvalidContentException("One missing code or more in the coded text.");
			else return;
		}

		int codeCount = 0;
		int start = 0;
		int len = 0;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case CODE_ISOLATED:
			case CODE_OPENING:
			case CODE_CLOSING:
				// Add a text fragment if needed
				if ( len > 0 ) {
					lastFrag = new TextFragment();
					((TextFragment)lastFrag).text.append(codedText.subSequence(start, start+len));
					list.add(lastFrag);
					len = 0;
				}
				// Then map to existing codes
				if ( ++i >= codedText.length() )
					throw new InvalidContentException("Missing id after code prefix.");
				int codeIndex = CtoI(codedText.charAt(i));
				if ( codeIndex >= tmpList.size() )
					throw new InvalidContentException(String.format("Code index '%d' is not in the object.", codeIndex));
				list.add(tmpList.get(codeIndex));
				codeCount++;
				start = i+1;
				break;
				
			default:
				len++;
				break;
			}
		}
		// Add any remaining text
		if ( len > 0 ) {
			lastFrag = new TextFragment();
			((TextFragment)lastFrag).text.append(codedText.subSequence(start, start+len));
			list.add(lastFrag);
		}
		
		if ( codeCount < tmpList.size() )
			throw new InvalidContentException("One missing code or more in the coded text.");
		
		// Update lastFrag once at the end (if needed)
		if ( len > 0 ) {
			lastFrag = list.get(list.size()-1);
		}
	}

	public void setContent (String codedText) {
		resetContent(codedText, null);
	}

	public void setContent (String codedText,
		List<IFragment> codes)
	{
		resetContent(codedText, codes);
	}

	public Object getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}

	public void setProperty (String name,
		Object value)
	{
		if ( props == null ) {
			props = new HashMap<String, Object>();
		}
		props.put(name, value);
	}

	public void clearProperties () {
		if ( props != null ) {
			props.clear();
		}
	}

	/**
	 * Gets the underlying data of the closing code for a given id. 
	 * @param id The id to look for.
	 * @return The data or empty if not found.
	 */
	private String getClosingCodeForID (int id) {
		for ( IFragment frag : list ) {
			if ( frag.isText() ) continue;
			if ( ((CodeFragment)frag).id == id ) {
				if ( ((CodeFragment)frag).type == CODE_CLOSING ) 
					return frag.toString();
			}
		}
		return "";
	}
	
	public String toXML () {
		// Empty
		if ( lastFrag == null ) return "";

		// Only one text fragment
		if (( list.size() == 1 ) && ( lastFrag.isText() )) {
			return Util.escapeToXML(lastFrag.toString(), 0, false);
		}
		
		// Multiple segments or one code fragment
		StringBuilder text = new StringBuilder();
		int index = 0;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				text.append(Util.escapeToXML(frag.toString(), 0, false));
			}
			else { // Else, it's a code fragment
				CodeFragment cf = (CodeFragment)frag;
				switch ( cf.type ) {
				case IContainer.CODE_ISOLATED:
					text.append(String.format("<ic id=\"%d\" data=\"%s\"/>",
						cf.id, Util.escapeToXML(cf.toString(), 3, false)));
					break;
				case IContainer.CODE_OPENING:
					text.append(String.format("<pc id=\"%d\" start=\"%s\" end=\"%s\">",
						cf.id, Util.escapeToXML(cf.toString(), 3, false),
						Util.escapeToXML(getClosingCodeForID(cf.id), 3, false)));
					break;
				case IContainer.CODE_CLOSING:
					text.append("</pc>");
					break;
				}
				index++;
			}
		}
		return text.toString();
	}

}
