package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;


public class TextContainer implements Comparable {
	
	public static final int MARKER_OPENING  = 0xE101;
	public static final int MARKER_CLOSING  = 0xE102;
	public static final int MARKER_ISOLATED = 0xE103;
	public static final int CHARBASE        = 0xE110;

	public static final String SFMARKER_START    = "{@#$";
	public static final String SFMARKER_END      = "}";
	
	public static enum TagType {
		OPENING,
		CLOSING,
		PLACEHOLDER
	};
	
	protected StringBuilder       text;
	protected ArrayList<Code>     codes;
	protected boolean             isBalanced;
	protected String              id;
	protected int                 lastCodeID;
	protected TextUnit            parent;
	

	/**
	 * Converts a marker index to its character value in the
	 * coded text string.
	 * @param index The index value to encode.
	 * @return The corresponding character value.
	 */
	static char toChar (int index) {
		return (char)(index+CHARBASE);
	}

	/**
	 * Converts the index-coded-as-character part of a marker into its index value.
	 * @param index The character to decode.
	 * @return The corresponding index value.
	 */
	static int toIndex (char index) {
		return ((int)index)-CHARBASE;
	}

	public TextContainer () {
	}

	public TextContainer (String text) {
		append(text);
	}
	
	public String getID () {
		return id;
	}
	
	public void setID (String value) {
		id = value;
	}
	
	public void append (char value) {
		if ( text == null ) text = new StringBuilder(""+value);
		else text.append(value);
	}

	public void append (String text) {
		if ( this.text == null ) this.text = new StringBuilder(text);
		else this.text.append(text);
	}

	public void append (TextContainer container) {
		String tmp = container.getCodedText();
		List<Code> newCodes = container.getCodes();
		for ( int i=0; i<tmp.length(); i++ ) {
			switch ( tmp.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				append(""+tmp.charAt(i)+toChar(codes.size()));
				codes.add(newCodes.get(toIndex(tmp.charAt(++i))).clone());
				break;
			default:
				text.append(tmp.charAt(i));
				break;
			}
		}
		if ( codes.size() > 0 ) isBalanced = false;
	}

	public Code append (TagType tagType,
		String type,
		String data)
	{
		// Create the list of codes if needed
		if ( codes == null ) codes = new ArrayList<Code>();
		// Append the code marker
		switch ( tagType ) {
		case OPENING:
			append(""+((char)MARKER_OPENING)+toChar(codes.size()));
			break;
		case CLOSING:
			append(""+((char)MARKER_CLOSING)+toChar(codes.size()));
			break;
		case PLACEHOLDER:
			append(""+((char)MARKER_ISOLATED)+toChar(codes.size()));
			break;
		}
		// Create the code
		codes.add(new Code(tagType, type, data));
		if ( tagType != TagType.CLOSING ) codes.get(codes.size()-1).id = ++lastCodeID;
		if ( tagType != TagType.PLACEHOLDER ) isBalanced = false;
		return codes.get(codes.size()-1);
	}

	public void clear () {
		text = null;
		codes = null;
		lastCodeID = 0;
		isBalanced = true;
	}

	public String getCodedText () {
		if ( text == null ) return "";
		if ( !isBalanced ) balanceMarkers();
		return text.toString();
	}

	public String getCodedText (int start,
		int end)
	{
		if ( text == null ) return "";
		checkPositionForMarker(start);
		checkPositionForMarker(end);
		if ( !isBalanced ) balanceMarkers();
		return text.substring(start, end);
	}

	public List<Code> getCodes () {
		//TODO: Should they be copies or self?
		if ( codes == null ) codes = new ArrayList<Code>();
		if ( !isBalanced ) balanceMarkers();
		return codes;
	}

	public List<Code> getCodes (int start,
		int end)
	{
		if ( codes == null ) return null;
		ArrayList<Code> tmpCodes = new ArrayList<Code>();
		if ( codes.isEmpty() ) return tmpCodes;
		checkPositionForMarker(start);
		checkPositionForMarker(end);

		for ( int i=start; i<end; i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				Code ori = codes.get(toIndex(text.charAt(++i)));
				tmpCodes.add(ori.clone());
				break;
			}
		}
	
		return tmpCodes;
	}

	public boolean isEmpty () {
		if ( text == null ) return true;
		else return (text.length()==0);
	}

	public void remove (int start,
		int end)
	{
		checkPositionForMarker(start);
		checkPositionForMarker(end);
		for ( int i=start; i<end; i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				codes.remove(toIndex(text.charAt(++i)));
				//TODO: need to update the index point in markers!!!
				// and the index is not necessarily sequential
				break;
			}
		}
		text.replace(start, end, "");
		isBalanced = false;
	}

	/**
	 * Gets a copy of a sub-sequence of this object.
	 * @param start The start of the sequence (in the coded text representation).
	 * @param end The position just after the last character to include in the 
	 * sub-sequence (in the coded text representation).
	 * @return A new TextContainer object with a copy of the given sub-sequence.
	 */
	public TextContainer subSequence (int start,
		int end)
	{
		TextContainer sub = new TextContainer();
		sub.parent = this.parent;
		if ( isEmpty() ) return sub;
		StringBuilder tmpText = new StringBuilder(getCodedText(start, end));
		ArrayList<Code> tmpCodes = null;
	
		// Get the codes and adjust indices if needed
		if ( codes.size() > 0 ) {
			tmpCodes = new ArrayList<Code>(); 
			for ( int i=0; i<tmpText.length(); i++ ) {
				switch ( tmpText.charAt(i) ) {
				case MARKER_OPENING:
				case MARKER_CLOSING:
				case MARKER_ISOLATED:
					tmpCodes.add(codes.get(toIndex(tmpText.charAt(++i))).clone());
					tmpText.setCharAt(i, toChar(tmpCodes.size()-1));
					break;
				}
			}
		}
		sub.setCodedText(tmpText.toString(), tmpCodes);
		return sub;
	}
	
	public void setCodedText (String codedText) {
		setCodedText(codedText, codes);
	}

	public void setCodedText (String codedText,
		List<Code> codes)
	{
		isBalanced = false;
		this.text = new StringBuilder(codedText);
		if ( codes == null ) codes = null;
		else this.codes = new ArrayList<Code>(codes);
		//TODO: do we need to reset the lastCodeID?
	}
	
	@Override
	public String toString () {
		if ( text == null ) return "";
		if (( codes == null ) || ( codes.size() == 0 )) return text.toString();
		if ( !isBalanced ) balanceMarkers();
		StringBuilder tmp = new StringBuilder();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(checkForSubflows(code));
				tmp.append(String.format("[O:id=%d:%s]", code.id, expendSubflows(code))); // Debug
				break;
			case MARKER_CLOSING:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(checkForSubflows(code));
				tmp.append(String.format("[C:id=%d:%s]", code.id, expendSubflows(code))); // Debug
				break;
			case MARKER_ISOLATED:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(checkForSubflows(code));
				tmp.append(String.format("[I:id=%d:%s]", code.id, expendSubflows(code))); // Debug
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}

	public TextUnit getParent() {
		return parent;
	}

	public void setParent (TextUnit value) {
		parent = value;
	}
	
	public int compareTo (Object object) {
		if ( object == null ) return -1;
		if ( object instanceof TextContainer ) {
			return getCodedText().compareTo(((TextContainer)object).getCodedText());
		}
		// Else, compare string representation
		return toString().compareTo(object.toString());
	}

	@Override
	public boolean equals (Object object) {
		if ( object == null ) return false;
		return (compareTo(object)==0);
	}

	private void checkPositionForMarker (int position) {
		if ( position > 0 ) {
			switch ( text.charAt(position-1) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				throw new RuntimeException(
					String.format("Position %d is inside a marker.", position));
			}
		}
	}
	
	private String expendSubflows (Code code) {
		if ( !code.hasSubflow ) return code.data;
		if ( parent == null ) {
			return code.data;
			//TODO: log a warning
		}
		// Else: look for place-holders
		StringBuilder tmp = new StringBuilder(code.data);
		int start;
		int pos = 0;
		while ( (start = tmp.indexOf(SFMARKER_START, pos)) > -1 ) {
			int end = tmp.indexOf(SFMARKER_END, start);
			if ( end != -1 ) {
				String id = tmp.substring(start+SFMARKER_START.length(), end);
				ITranslatable res = parent.getChild(id);
				if ( res == null ) {
					tmp.replace(start, end+1, "-Subflow not found-");
				}
				else tmp.replace(start, end+1, res.toString());
				pos = end;
			}
			else pos = start;
		}
		return tmp.toString();
	}
	
	private void balanceMarkers () {
		if (( codes == null ) || ( text == null )) return;
		for ( Code item : codes ) {
			// Void all IDs of closing codes
			if ( item.tagType == TagType.CLOSING ) item.id = -1;
		}
		// Process the markers
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				int index = toIndex(text.charAt(i+1));
				Code code = codes.get(index);
				switch ( code.tagType ) {
				case PLACEHOLDER:
					text.setCharAt(i, (char)MARKER_ISOLATED);
					break;
				case OPENING:
					// Search for corresponding closing code
					boolean found = false;
					int stack = 1;
					for ( int j=index+1; j<codes.size(); j++ ) {
						if ( codes.get(j).type.equals(code.type) ) {
							if ( codes.get(j).tagType == TagType.OPENING ) {
								stack++;
							}
							else if ( codes.get(j).tagType == TagType.CLOSING ) {
								if ( --stack == 0 ) {
									codes.get(j).id = code.id;
									found = true;
									break;
								}
							}
						}
					}
					if ( found ) text.setCharAt(i, (char)MARKER_OPENING);
					else text.setCharAt(i, (char)MARKER_ISOLATED);
					break;
				case CLOSING:
					// If Id is -1, this closing code has no corresponding opening
					// otherwise its ID is already set
					if ( code.id == -1 ) text.setCharAt(i, (char)MARKER_ISOLATED);
					else text.setCharAt(i, (char)MARKER_CLOSING);
				}
				i++; // Skip index part of the index
				break;
			}
		}
		isBalanced = true;
	}

	/*
	private void changeMarkerType (int index,
		int newMarkerType)
	{
		// Update the coded text marker
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				if ( toIndex(text.charAt(++i)) == index ) {
					text.setCharAt(i-1, (char)newMarkerType);
					return; // Done
				}
			}
		}
	}*/

}
