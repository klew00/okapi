package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container implements IContainer {

	static private final int CHARBASE  = 0xE200;

	private ArrayList<IFragment>       list;
	private IFragment                  lastFrag;

	static private char ItoC (int id) {
		return (char)(id+CHARBASE);
	}

	static private int CtoI (char id) {
		return ((int)id)-CHARBASE;
	}

	public Container () {
		reset();
	}
	
	@Override
	public String toString () {
		return getText(false);
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

	public String getText (boolean coded) {
		if ( lastFrag == null ) return "";
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

	private void resetContent (String codedText,
		Map<Integer, IFragment> codes)
		throws Exception
	{
		// Store code fragments temporarily
		Map<Integer, IFragment> tmpMap = codes;
		if ( tmpMap == null ) tmpMap = getCodes();
		reset(); // Reset all

		if ( codedText == null ) {
			if ( tmpMap.size() > 0 ) //TODO: Maybe null just reset all?
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
		throws Exception
	{
		resetContent(codedText, null);
	}

	public void setContent (String codedText,
		Map<Integer, IFragment> codes)
		throws Exception
	{
		resetContent(codedText, codes);
	}

}
