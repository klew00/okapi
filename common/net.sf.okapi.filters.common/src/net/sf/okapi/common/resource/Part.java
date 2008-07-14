package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;

public class Part implements IPart {

	ArrayList<IFragment>     list;
	IFragment                lastFrag;
	boolean                  isSegment;


	public Part (boolean isSegment) {
		reset();
		this.isSegment = isSegment;
	}
	
	public Part (String text,
		boolean isSegment)
	{
		reset();
		append(text);
		this.isSegment = isSegment;
	}
	
	public Part (IFragment fragment,
		boolean isSegment)
	{
		reset();
		append(fragment);
		this.isSegment = isSegment;
	}
	
	@Override
	public String toString () {
		// Same as getText()
		return getText(false);
	}
	
	public boolean isSegment () {
		return isSegment;
	}
	
	public void setIsSegment (boolean value) {
		isSegment = value;
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
	
	public boolean hasText (boolean whiteSpacesAreText) {
		if ( lastFrag == null ) return false;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				if ( whiteSpacesAreText ) {
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

	public void reset () {
		list = new ArrayList<IFragment>();
		lastFrag = null;
		isSegment = false;
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

	public void setContent (String codedText) {
		resetContent(codedText, null);
	}

	public void setContent (String codedText,
		List<IFragment> codes)
	{
		resetContent(codedText, codes);
	}

	public void setContent (IFragment fragment) {
		reset();
		append(fragment);
	}

	public String getCodedText () {
		return getText(true);
	}

	public List<IFragment> getCodes () {
		ArrayList<IFragment> codes = new ArrayList<IFragment>();
		for ( IFragment frag : list ) {
			if ( !frag.isText() ) {
				codes.add(frag);
			}
		}
		return codes;
	}

	public List<IFragment> getFragments () {
		return list;
	}

	public String getCodeForID (int id,
		int type)
	{
		if ( lastFrag == null ) return null;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) continue;
			if ( ((CodeFragment)frag).id == id ) {
				if ( ((CodeFragment)frag).type == type ) 
					return frag.toString();
			}
		}
		return null;
	}
	
	public String toXML () {
		// Shortcut for empty
		if ( lastFrag == null ) return "";

		// Shortcut for only one text fragment
		if (( list.size() == 1 ) && ( lastFrag.isText() )) {
			if ( isSegment ) {
				return "<s>" + Util.escapeToXML(lastFrag.toString(), 0, false) + "</s>";
			}
			else {
				return Util.escapeToXML(lastFrag.toString(), 0, false);
			}
		}
		
		// Otherwise
		StringBuilder text = new StringBuilder();
		if ( isSegment ) text.append("<s>");
		for ( IFragment frag : list ) {
			text.append(frag.toXML(this));
		}
		if ( isSegment ) text.append("</s>");
		return text.toString();
	}

	public IPart copy (int start) {
		return copy(start, -1);
	}

	//TODO: Test this method extensively, especially with in-line codes
	public IPart copy (int start,
		int end)
	{
		// Nothing in the container
		if ( lastFrag == null )
			throw new IndexOutOfBoundsException("No fragment available.");
		
		// Get all the fragments of all parts
		List<IFragment> list = getFragments();

		// Set auto-end boundary if needed
		if ( end < 0 ) {
			end = getCodedText().length();
		}
		
		// Detect which fragments hold the starting and ending positions
		int len = 0; // Total length scanned
		int fragLen; // Length of the fragment
		int startIndex = -1; // Index of the fragment where to start
		int startSplit = -1; // Location where to split the start
		int endIndex = -1; // Index of the fragment where to end
		int endSplit = -1; // Location where to split the end
		int i = 0;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) fragLen = frag.toString().length();
			else fragLen = 2;
			len += fragLen;
			if (( startIndex == -1 ) && ( start <= len )) {
				startIndex = i;
				startSplit = start-(len-fragLen);
			}
			if (( startIndex > -1 ) && ( end <= (len+1) )) {
				endIndex = i;
				endSplit = end-(len-fragLen);
				break;
			}
			i++;
		}
		if (( startIndex == -1 ) || ( endIndex == -1 )) {
			throw new IndexOutOfBoundsException("Invalid start or end position.");
		}

		// Copy the relevant fragments to the new part
		// (make sure we use clones not references)
		Part newPart = new Part(true);
		IFragment frag;
		for ( i=startIndex; i<=endIndex; i++ ) {
			if (( i > startIndex ) && ( i < endIndex )) {
				newPart.list.add(list.get(i).clone());
			}
			else if (( i == startIndex ) && ( i == endIndex )) {
				frag = list.get(i);
				if ( frag.isText() ) {
					newPart.list.add(new TextFragment(
						frag.toString().substring(startSplit, endSplit)));
				}
				else {
					// Not possible
					throw new RuntimeException("You cannot split a code fragment.");
				}
			}
			else if ( i == startIndex ) {
				frag = list.get(i);
				if ( frag.isText() ) {
					if ( startSplit == 0 ) {
						newPart.list.add(frag.clone());
					}
					else {
						newPart.list.add(new TextFragment(
							frag.toString().substring(startSplit)));
					}
				}
				else {
					newPart.list.add(frag.clone());
				}
			}
			else if ( i == endIndex ) {
				frag = list.get(i);
				if ( frag.isText() ) {
					newPart.list.add(new TextFragment(
						frag.toString().substring(0, endSplit)));
				}
				else {
					newPart.list.add(frag.clone());
				}
			}
		}
		
		// Adjust the last fragment for the new object
		// Needs to be done before the next step!
		if ( newPart.list.size() > 0 )
			newPart.lastFrag = newPart.list.get(newPart.list.size()-1);
		else
			newPart.lastFrag = null;
		
		// Adjust the types of the opening/closing in-line codes now orphans
		for ( IFragment newFrag : newPart.list ) {
			if ( !newFrag.isText() ) {
				CodeFragment cf = (CodeFragment)newFrag;
				if ( cf.getType() == IContainer.CODE_OPENING ) {
					if ( newPart.getCodeForID(cf.id, IContainer.CODE_CLOSING) == null ) {
						cf.type = IContainer.CODE_ISOLATED;
					}
				}
				else if ( cf.getType() == IContainer.CODE_CLOSING ) {
					if ( newPart.getCodeForID(cf.id, IContainer.CODE_OPENING) == null ) {
						cf.type = IContainer.CODE_ISOLATED;
					}
				}
			}
		}

		// Return the new part
		return newPart;
	}

	public void changeToCode (int start,
		int end,
		int type,
		int id)
	{
		//TODO: changeToCode
	}
	
	private void resetContent (String codedText,
		List<IFragment> codes)
	{
		// Store new fragments temporarily
		List<IFragment> tmpList = codes;
		// If we don't have new ones, use the current list
		if ( tmpList == null ) tmpList = getCodes();
		// Reset all
		reset();

		if ( codedText == null ) {
			if ( tmpList.size() > 0 ) //TODO: Maybe null just reset all instead of giving an error?
				throw new InvalidContentException("No coded text for the list of codes.");
			else return;
		}

		String oldMap = null;
		int codeCount = 0;
		int start = 0;
		int len = 0;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case IContainer.CODE_ISOLATED:
			case IContainer.CODE_OPENING:
			case IContainer.CODE_CLOSING:
				// Add a text fragment if needed
				if ( len > 0 ) {
					lastFrag = new TextFragment();
					((TextFragment)lastFrag).text.append(codedText.subSequence(start, start+len));
					list.add(lastFrag);
					len = 0;
				}

				if ( oldMap == null ) {
					StringBuilder tmp = new StringBuilder();
					for ( IFragment frag : tmpList ) {
						if ( !frag.isText() ) {
							// Each old type+id set at position oldIndex*2
							tmp.append((char)((CodeFragment)frag).type);
							tmp.append((char)((CodeFragment)frag).id);
						}
					}
					oldMap = tmp.toString();
				}
				
				// Then map to existing codes
				if ( ++i >= codedText.length() )
					throw new InvalidContentException("Missing index after code prefix.");
				// Get the index
				int codeIndex = Container.CtoI(codedText.charAt(i));
				// Get the id for the code at codeIndex
				int newCodeIndex = -1;
				try {
					int type = oldMap.codePointAt(codeIndex*2);
					int id = oldMap.codePointAt((codeIndex*2)+1);
					// Search for the same id+codeType in the new codes
					int j = 0;
					for ( IFragment frag : tmpList ) {
						if ( frag.isText() ) continue;
						// All fragments here are codes
						if (( ((CodeFragment)frag).id == id )
							&& ( ((CodeFragment)frag).type == type )) {
							newCodeIndex = j;
						}
						j++;
					}
				}
				catch ( Exception e ) {
					throw new InvalidContentException(e);
				}
				if ( newCodeIndex == -1 ) {
					throw new InvalidContentException(String.format("Code index %d is not in the new set of codes.",
						codeIndex));
				}
				list.add(tmpList.get(newCodeIndex));
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
			throw new InvalidContentException(String.format("Number of codes found in the text: %d, number of codes in the code list: %d.",
				codeCount, tmpList.size()));
		
		// Update lastFrag once at the end (if needed)
		if ( list.size() > 0 ) {
			lastFrag = list.get(list.size()-1);
		}
	}
		
	private String getText (boolean coded) {
		// Shortcut for empty
		if ( lastFrag == null ) return "";

		// Shortcut for one text fragment
		if (( list.size() == 1 ) && ( lastFrag.isText() )) {
			return lastFrag.toString();
		}
		
		// Otherwise
		StringBuilder text = new StringBuilder();
		int index = 0;
		for ( IFragment frag : list ) {
			if ( frag.isText() ) {
				text.append(frag.toString());
			}
			else {
				if ( coded ) {
					text.append((char)((CodeFragment)frag).type);
					text.append(Container.ItoC(index));
					index++; // Not used if not coded
				}
				else {
					text.append(frag.toString());
				}
			}
		}
		return text.toString();
	}

}
