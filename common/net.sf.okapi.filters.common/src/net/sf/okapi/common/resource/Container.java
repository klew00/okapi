package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of the IContainer interface. 
 */
public class Container implements IContainer {

	private ArrayList<IFragment>       list;
	private IFragment                  lastFrag;
	private Map<String, Object>        props;

	static public char ItoC (int id) {
		return (char)(id+CHARBASE);
	}

	static public int CtoI (char id) {
		return ((int)id)-CHARBASE;
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
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				text.append(frag.toString());
			}
			else {
				if ( coded ) {
					text.append((char)((CodeFragment)frag).type);
					text.append(ItoC(((CodeFragment)frag).id));
				}
				else {
					text.append(((CodeFragment)frag).data);
				}
			}
		}
		return text.toString();
	}

	public String getCodedText () {
		return getText(true);
	}

	public Map<Integer, IFragment> getCodes () {
		HashMap<Integer, IFragment> map = new HashMap<Integer, IFragment>();
		for ( IFragment frag : list ) {
			if ( !frag.isText() ) {
				map.put(((CodeFragment)frag).id, frag);
			}
		}
		return map;
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

	/**
	 * Resets the content of the object based on a coded text string and a map of 
	 * codes.
	 * @param codedText The coded text to use to create the new content.
	 * @param codes The codes to use along with the coded text. Use null to use the
	 * existing codes.
	 * @throws Exception
	 */
	private void resetContent (String codedText,
		Map<Integer, IFragment> codes)
		throws Exception //TODO: Maybe a specific exception
	{
		// Store code fragments temporarily
		Map<Integer, IFragment> tmpMap = codes;
		if ( tmpMap == null ) tmpMap = getCodes();
		reset(); // Reset all

		if ( codedText == null ) {
			if ( tmpMap.size() > 0 ) //TODO: Maybe null just reset all instead of giving an error?
				throw new Exception("One missing code or more in the coded text.");
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
					throw new Exception("Missing id after code prefix.");
				int id = CtoI(codedText.charAt(i));
				if ( !tmpMap.containsKey(id) )
					throw new Exception(String.format("Code id '%d' is not in the object.", id));
				list.add(tmpMap.get(id));
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
		
		if ( codeCount < tmpMap.size() )
			throw new Exception("One missing code or more in the coded text.");
		
		// Update latFrag once at the end
		lastFrag = list.get(list.size()-1);
	}

	public void setContent (String codedText)
		throws Exception //TODO: Maybe a specific exception
	{
		resetContent(codedText, null);
	}

	public void setContent (String codedText,
		Map<Integer, IFragment> codes)
		throws Exception //TODO: Maybe a specific exception
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
}
