package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;


public class TextContainer implements ITextContent {
	
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

	public void append (ITextContent content) {
		// TODO Auto-generated method stub
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

	public void append (int markerType,
		String layerType)
	{
		// TODO Auto-generated method stub
	}

	public void clear () {
		text = null;
		codes = null;
		lastCodeID = 0;
		isBalanced = true;
	}

	public String getCodedText () {
		if ( text == null ) return "";
		else {
			if ( !isBalanced ) balanceMarkers();
			return text.toString();
		}
	}

	public String getCodedText (int start,
		int end)
	{
		if ( text == null ) return "";
		else {
			//TODO: check that the start and end don't split markers
			if ( !isBalanced ) balanceMarkers();
			return text.substring(start, end);
		}
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
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEmpty () {
		if ( text == null ) return true;
		else return (text.length()>0);
	}

	public void remove (int start,
		int end)
	{
		//TODO: Check if the start/end points split markers
		for ( int i=start; i<end; i++ ) {
			switch ( text.charAt(i) ) {
			case ITextContent.MARKER_OPENING:
			case ITextContent.MARKER_CLOSING:
			case ITextContent.MARKER_ISOLATED:
				codes.remove(toIndex(text.charAt(++i)));
				//TODO: need to update the index point in markers!!!
				// and the index is not necessarily sequential
				break;
			}
		}
		text.replace(start, end, "");
		isBalanced = false;
	}

	public void setCodedText (String codedText) {
		// TODO Auto-generated method stub
	}

	public void setCodedText (String codedText, List<Code> codes) {
		// TODO Auto-generated method stub
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
			case ITextContent.MARKER_OPENING:
			case ITextContent.MARKER_CLOSING:
			case ITextContent.MARKER_ISOLATED:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(checkForSubflows(code));
				tmp.append(String.format("[id=%d:%s]", code.id, checkForSubflows(code))); // Debug
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}

	private String checkForSubflows (Code code) {
		if ( !code.hasSubflow ) return code.data;
		if ( parent == null ) {
			return code.data;
			//TODO: log a warning
		}
		// Else: look for place-holders
		StringBuilder tmp = new StringBuilder(code.data);
		int start;
		int pos = 0;
		while ( (start = tmp.indexOf(ITextContent.SFMARKER_START, pos)) > -1 ) {
			int end = tmp.indexOf(ITextContent.SFMARKER_END, start);
			if ( end != -1 ) {
				String id = tmp.substring(
					start+ITextContent.SFMARKER_START.length(), end);
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
					text.setCharAt(i, (char)ITextContent.MARKER_ISOLATED);
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
					if ( found ) text.setCharAt(i, (char)ITextContent.MARKER_OPENING);
					else text.setCharAt(i, (char)ITextContent.MARKER_ISOLATED);
					break;
				case CLOSING:
					// If Id is -1, this closing code has no corresponding opening
					// otherwise its ID is already set
					if ( code.id == -1 ) text.setCharAt(i, (char)ITextContent.MARKER_ISOLATED);
					else text.setCharAt(i, (char)ITextContent.MARKER_CLOSING);
				}
				i++; // Skip index part of the index
				break;
			}
		}
		isBalanced = true;
	}

	public TextUnit getParent() {
		return parent;
	}

	public void setParent (TextUnit value) {
		parent = value;
	}
	
	/*
	private void balanceMarkers () {
		//TODO: REDO this based on the markers
		if ( codes == null ) return;
		Code code;
		int lastID = 0;
		//TODO: change isolated back to opening/closing
		for ( int i=0; i<codes.size(); i++ ) {
			switch ( codes.get(i).tagType ) {
			case OPENING:
				code = codes.get(i);
				code.id = ++lastID;
				boolean found = false;
				int stack = 1;
				for ( int j=i+1; j<codes.size(); j++ ) {
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
				if ( !found ) {
					changeMarkerType(i, MARKER_ISOLATED);
				}
				break;
				
			case CLOSING:
				code = codes.get(i);
				if ( code.id == -1 ) {
					changeMarkerType(i, MARKER_ISOLATED);
					code.id = ++lastID;
				}
				break;
			}
		}
		isBalanced = true;
	}

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
