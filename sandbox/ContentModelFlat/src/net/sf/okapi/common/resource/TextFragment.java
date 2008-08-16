/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TextFragment implements Comparable<Object> {
	
	public static final int MARKER_OPENING  = 0xE101;
	public static final int MARKER_CLOSING  = 0xE102;
	public static final int MARKER_ISOLATED = 0xE103;
	public static final int CHARBASE        = 0xE110;

	public static final String SFMARKER_START    = "{@#$";
	public static final String SFMARKER_END      = "}";

	/**
	 * List of the types of tag useable for in-line codes.
	 */
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
	public static char toChar (int index) {
		return (char)(index+CHARBASE);
	}

	/**
	 * Converts the index-coded-as-character part of a marker into its index value.
	 * @param index The character to decode.
	 * @return The corresponding index value.
	 */
	public static int toIndex (char index) {
		return ((int)index)-CHARBASE;
	}

	/**
	 * Creates an empty TextFragment.
	 */
	public TextFragment () {
		text = new StringBuilder();
	}

	/**
	 * Creates a TextFragment with a given text.
	 * @param text The text to use.
	 */
	public TextFragment (String text) {
		append(text);
	}
	
	/**
	 * Gets the ID of the fragment.
	 * @return The ID of the fragment.
	 */
	public String getID () {
		return id;
	}
	
	/**
	 * sets the ID of the fragment.
	 * @param value The new value to apply (can be null).
	 */
	public void setID (String value) {
		id = value;
	}
	
	/**
	 * Appends a character to the fragment.
	 * @param value The character to append.
	 */
	public void append (char value) {
		text.append(value);
	}

	/**
	 * Appends a string to the fragment.
	 * @param text The string to append.
	 */
	public void append (String text) {
		this.text.append(text);
	}

	/**
	 * Appends a TextFragment object to this fragment.
	 * @param container The TextFragment to append.
	 */
	public void append (TextFragment container) {
		//TODO: maybe a smarter way to implement this would
		// be to do an insert() and treat this as insert(afterlast);
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
	
	/**
	 * Appends a new code to the text.
	 * @param tagType The tag type of the code (e.g. TagType.OPENING).
	 * @param type The type of the code (e.g. "bold").
	 * @param data The raw code itself. (e.g. "<b>").
	 * @return The new code that was added to the text.
	 */
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

	/**
	 * Clears the fragment of all content. The parent is not modified.
	 */
	public void clear () {
		text = new StringBuilder();
		codes = null;
		lastCodeID = 0;
		isBalanced = true;
	}

	/**
	 * Gets the coded text representation of the fragment.
	 * @return The coded text for the fragment.
	 */
	public String getCodedText () {
		if ( !isBalanced ) balanceMarkers();
		return text.toString();
	}

	/**
	 * Gets the portion of coded text for a given section of the coded text.
	 * @param start The position of the first character or marker of the section
	 * (in the coded text representation).
	 * @param end The position just after the last character or marker of the section
	 * (in the coded text representation).
	 * You can use -1 for ending the section at the end of the fragment.
	 * @return The portion of coded text for the given range. It can be 
	 * empty but never null.
	 * @throws InvalidPositionException
	 */
	public String getCodedText (int start,
		int end)
	{
		if ( end == -1 ) end = text.length();
		checkPositionForMarker(start);
		checkPositionForMarker(end);
		if ( !isBalanced ) balanceMarkers();
		return text.substring(start, end);
	}

	/**
	 * Gets the code for a given index formatted as character (the second
	 * special character in a marker in a coded text string).
	 * @param indexAsChar The index value coded as character.
	 * @return The corresponding code.
	 */
	public Code getCode (char indexAsChar) {
		return codes.get(toIndex(indexAsChar)); 
	}
	
	/**
	 * Gets the code for a given index.
	 * @param index the index of the code.
	 * @return The code for the given index.
	 * @throws IndexOutOfBoundsException
	 */
	public Code getCode (int index) {
		return codes.get(index);
	}
	
	/**
	 * Gets the list of all codes for the fragment.
	 * @return The list of all codes for the fragment. If there is no code, an empty
	 * list is returned.
	 */
	public List<Code> getCodes () {
		if ( codes == null ) codes = new ArrayList<Code>();
		if ( !isBalanced ) balanceMarkers();
		return Collections.unmodifiableList(codes);
	}

	/**
	 * Gets a copy of the list of the codes that are within a given section of
	 * coded text.
	 * @param start The position of the first character or marker of the section
	 * (in the coded text representation).
	 * @param end The position just after the last character or marker of the section
	 * (in the coded text representation).
	 * @return A new list of all codes within the given range.
	 * @throws InvalidPositionException
	 */
	public List<Code> getCodes (int start,
		int end)
	{
		ArrayList<Code> tmpCodes = new ArrayList<Code>();
		if ( codes == null ) return tmpCodes;
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

	/**
	 * Indicates if the fragment is empty (no text and no codes).
	 * @return True if the fragment is empty.
	 */
	public boolean isEmpty () {
		return (text.length()==0);
	}
	
	/**
	 * Indicates if the fragment contains at least one character (markers do not
	 * count as characters).
	 * @param whiteSpacesAreText Indicates if white-spaces should be considered 
	 * characters or not for the purpose of checking if this fragment is empty.
	 * @return True if the fragment contains at least one character.
	 */
	public boolean hasText (boolean whiteSpacesAreText) {
		for ( int i=0; i<text.length(); i++ ) {
			switch (text.charAt(i)) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip over the marker, they are not text
				continue;
			}
			// Not a marker
			// If we count ws as text, then we have text
			if ( whiteSpacesAreText ) return true;
			// Otherwise we have text if it's not a whitespace
			if ( !Character.isWhitespace(text.charAt(i)) ) return true;
		}
		return false;
	}
	
	/**
	 * Indicates if the fragment contains at least one code.
	 * @return True if the fragment contains at least one code.
	 */
	public boolean hasCode () {
		if ( codes == null ) return false;
		return (codes.size()>0);
	}

	/**
	 * Removes a portion of the fragment (including its codes).
	 * @param start The position of the first character or marker of the section
	 * (in the coded text representation).
	 * @param end The position just after the last character or marker of the section
	 * (in the coded text representation).
	 * @throws InvalidPositionException
	 */
	public void remove (int start,
		int end)
	{
		// TODO: Check if there is a better way to do this,
		// as this is quite expensive.
		checkPositionForMarker(start);
		checkPositionForMarker(end);
		// Remove the coded text to delete
		text.replace(start, end, "");
		if (( codes == null ) || ( codes.size()==0 )) return;
		// Make a list of all remaining codes
		ArrayList<Code> remaining = new ArrayList<Code>();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				// Copy the remaining codes into the new list
				remaining.add(codes.get(toIndex(text.charAt(++i))));
				// And update the index in the coded text
				text.setCharAt(i, toChar(remaining.size()-1));
				break;
			}
		}
		codes.clear();
		codes = remaining; // The new list is the remaining codes
		isBalanced = false;
	}

	/**
	 * Gets a copy of a sub-sequence of this object.
	 * @param start The position of the first character or marker of the section
	 * (in the coded text representation).
	 * @param end The position just after the last character or marker of the section
	 * (in the coded text representation).
	 * You can use -1 for ending the section at the end of the fragment.
	 * @return A new TextContainer object with a copy of the given sub-sequence.
	 */
	public TextFragment subSequence (int start,
		int end)
	{
		TextFragment sub = new TextFragment();
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
		sub.lastCodeID = lastCodeID;
		return sub;
	}
	
	/**
	 * Sets the coded text of the fragment, using its the existing codes. The coded
	 * text must be valid for the existing codes.
	 * @param newCodedText The coded text to apply.
	 * @throws InvalidContentException
	 */
	public void setCodedText (String newCodedText) {
		setCodedText(newCodedText, codes);
	}

	/**
	 * Sets the coded text of the fragment and its corresponding codes.
	 * @param newCodedText The coded text to apply.
	 * @param newCodes the list of the corresponding codes.
	 * @throws InvalidContentException
	 */
	public void setCodedText (String newCodedText,
		List<Code> newCodes)
	{
		isBalanced = false;
		text = new StringBuilder(newCodedText);
		if ( newCodes == null ) codes = null;
		else codes = new ArrayList<Code>(newCodes);
		//TODO: do we need to reset the lastCodeID?
		
		// Validate the codes and coded text
		int j = 0;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				j++;
				if ( codes == null ) {
					throw new InvalidContentException("Invalid index for marker "+j);
				}
				try {
					// Just try to access the code
					codes.get(toIndex(text.charAt(++i)));
				}
				catch ( IndexOutOfBoundsException e ) {
					throw new InvalidContentException("Invalid index for marker "+j);
				}
				break;
			}
		}
		if ( j < codes.size() ) {
			throw new InvalidContentException(
				String.format("Markers in coded text: %d. Listed codes: %d ", j, codes.size()));
		}
	}
	
	@Override
	public String toString () {
		if (( codes == null ) || ( codes.size() == 0 )) return text.toString();
		if ( !isBalanced ) balanceMarkers();
		StringBuilder tmp = new StringBuilder();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(expendSubflows(code));
				tmp.append(String.format("[%d:%s]", code.id, expendSubflows(code))); // Debug
				break;
			case MARKER_CLOSING:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(expendSubflows(code));
				tmp.append(String.format("[/%d:%s]", code.id, expendSubflows(code))); // Debug
				break;
			case MARKER_ISOLATED:
				code = codes.get(toIndex(text.charAt(++i)));
				//tmp.append(expendSubflows(code));
				tmp.append(String.format("[%d:%s/]", code.id, expendSubflows(code))); // Debug
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}

	/**
	 * Gets the parent of the fragment.
	 * @return the parent of the fragment or null if none is assigned.
	 */
	public TextUnit getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the fragment.
	 * @param value The new parent to assign (can be null).
	 */
	public void setParent (TextUnit value) {
		parent = value;
	}
	
	public int compareTo (Object object) {
		if ( object == null ) return -1;
		if ( object instanceof TextFragment ) {
			return getCodedText().compareTo(((TextFragment)object).getCodedText());
		}
		// Else, compare string representation
		return toString().compareTo(object.toString());
	}

	@Override
	public boolean equals (Object object) {
		if ( object == null ) return false;
		return (compareTo(object)==0);
	}

	/**
	 * Changes a portion of the coded text into a single code. Any code already
	 * existing that is within the range will be included in the new code.
	 * @param start Position where to start the code.
	 * @param end Position after the last part of the code.
	 * @param tagType Tag type of the new code.
	 * @param type Type of the new code.
	 * @return The different between the coded text length before and after 
	 * the operation.
	 */
	public int changeToCode (int start,
		int end,
		TagType tagType,
		String type)
	{
		// Get the subsequence
		TextFragment sub = subSequence(start, end);
		// Store the length of the coded text before the operation
		int before = text.length();
		// Create the new code, using the text of the subsequence as the data
		Code code = new Code(tagType, type, sub.toString());
		// Remove the section that will be code, this takes care of the codes too
		remove(start, end);
		// Create the new marker
		String marker = null;
		switch ( tagType ) {
		case OPENING:
			marker = ""+((char)MARKER_OPENING)+toChar(codes.size());
			code.id = ++lastCodeID;
			break;
		case CLOSING:
			marker = ""+((char)MARKER_CLOSING)+toChar(codes.size());
			// The id stays -1
			break;
		case PLACEHOLDER:
			marker = ""+((char)MARKER_ISOLATED)+toChar(codes.size());
			code.id = ++lastCodeID;
			break;
		}
		// Insert the new marker into the coded text
		text.insert(start, marker);
		// Add the new code
		codes.add(code);
		isBalanced = false;
		return text.length()-before;
		
	}

	/**
	 * Renumbers the IDs of the codes in the fragment.
	 */
	public void renumberCodes () {
		lastCodeID = 0;
		if ( codes == null ) return;
		for ( Code code : codes ) {
			if ( code.tagType != TagType.CLOSING ) code.id = ++lastCodeID;
		}
		isBalanced = false;
	}
	
	/**
	 * Verifies if a given position in the coded text is on the second special
	 * character of a marker sequence.
	 * @param position The position to text.
	 * @throws InvalidPositionException
	 */
	private void checkPositionForMarker (int position) {
		if ( position > 0 ) {
			switch ( text.charAt(position-1) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				throw new InvalidPositionException (
					String.format("Position %d is inside a marker.", position));
			}
		}
	}
	
	/**
	 * Creates a complete text representation of the code data including any text
	 * coming from sub-flows.
	 * @param code The code to process.
	 * @return The data for the code, including any sub-flows data.
	 */
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
	
	/**
	 * Balances the markers based on the tag type of the codes.
	 */
	private void balanceMarkers () {
		if ( codes == null ) return;
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
					if ( code.id == -1 ) {
						text.setCharAt(i, (char)MARKER_ISOLATED);
						code.id = ++lastCodeID;
					}
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
