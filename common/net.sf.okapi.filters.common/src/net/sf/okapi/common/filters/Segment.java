/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.filters;

import java.util.ArrayList;

/**
 * Reference implementation of the ISegment interface.
 * <p>The internal representation of the coded text:
 * <p>Each code is coded in two Unicode characters of the Private Use Area.
 * The first one indicates the type of code (isolated, opening or closing.
 * The second one is the index to the code in the codes array, it is
 * zero-based + a base value in the Unicode private Use Area.
 */
public class Segment implements ISegment {

	static private final int INDEXBASE = 0xE200;
	
	private StringBuilder    data;
	private ArrayList<Code>  codes;
	private int              lastCodeID;
	private boolean          isNormalized;
	
	/**
	 * Converts a character into an code index.
	 * @param index The character to convert, taken from the coded string.
	 * @return The index of the code.
	 */
	static private int CtoI (char index) {
		return ((int)index)-INDEXBASE;
	}

	/**
	 * Converts the index of a code into a character to use
	 * in the coded string.
	 * @param index the index to convert.
	 * @return The character to use in the coded string.
	 */
	static private char ItoC (int index) {
		return (char)(index+INDEXBASE);
	}

	public Segment () {
		reset();
	}

	public ISegment clone () {
		Segment newSeg = new Segment();
		newSeg.copyFrom(this);
		return (ISegment)newSeg;
	}
	
	public void reset () {
		data = new StringBuilder();
		codes = new ArrayList<Code>();
		lastCodeID = 0;
		isNormalized = true;
	}
	
	public void append (String text) {
		data.append(text);
	}

	public void append (char value) {
		data.append(value);
	}

	public void append (int type,
		String label,
		String codeData)
	{
		Code code = new Code(type, label, codeData);
		if ( type == CODE_ISOLATED ) {
			code.id = ++lastCodeID;
		}
		else {
			// Do not know the corresponding opening code yet.
			//TODO: Mmmm... maybe we could with a stack... then no need for normalize()
			code.id = -1;
			isNormalized = false;
		}
		codes.add(code);
		data.append((char)type);
		data.append((char)ItoC(codes.size()-1));
	}

	public void append (ISegment segment) {
		// Make sure the receiving segment is normalized 
		if ( !isNormalized ) normalize();
		
		// Change the indices in the coded text to append
		StringBuilder tmp1 = new StringBuilder(segment.getCodedText());
		// First index will be last of receiving codes + 1, size()-1 to start 
		int newIndex = codes.size()-1;
		for ( int i=0; i<tmp1.length(); i++ ) {
			switch ( tmp1.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				if ( i+1 < tmp1.length() ) i++;
				tmp1.setCharAt(i, ItoC(++newIndex));
				continue;
			}
		}
		
		// Go through the codes of the segment to append
		// and change the IDs and add to the codes of this segment
		ArrayList<Code> tmpCodes = new ArrayList<Code>();
		unpackCodes(segment.getCodes(), tmpCodes);
		for ( Code code : tmpCodes ) {
			if ( code.type == CODE_ISOLATED ) {
				code.id = ++lastCodeID; // Update the ID
			}
			else { // Normalize afterward
				code.id = -1;
				isNormalized = false;
			}
			codes.add(code); // Add the code to the list
		}
		
		// Add the updated coded text
		data.append(tmp1);
	}
	
	public void copyFrom (ISegment original) {
		setCodes(original.getCodes());
		setTextFromCoded(original.getCodedText());
	}

	public int getCodeCount () {
		return codes.size();
	}

	public boolean hasCode () {
		return (codes.size() > 0);
	}

	public int getCodeID (int index) {
		// IDs may change when normalizing, so make sure it's done.
		if ( !isNormalized ) normalize();
		return codes.get(index).id;
	}
	
	public String getCodeData (int index) {
		return codes.get(index).data;
	}
		
	public int getCodeIndex (int id,
		int type)
	{
		for ( int i=0; i<codes.size(); i++ ) {
			if (( codes.get(i).id == id )
				&& ( codes.get(i).type == type )) { 
				return i;
			}
		}
		return -1;
	}

	public String getCodeLabel (int index) {
		return codes.get(index).label;
	}

	public String toString () {
		return toString(TEXTTYPE_ORIGINAL);
	}
	
	public String toString (int textType) {
		switch ( textType ) {
		case TEXTTYPE_ORIGINAL:
			return getOriginalText();
		case TEXTTYPE_CODED:
			return getCodedText();
		case TEXTTYPE_GENERIC:
			return getGenericText();
		case TEXTTYPE_PLAINTEXT:
			return getPlainText();
		case TEXTTYPE_TMX14:
			return getTMXText();
		case TEXTTYPE_XLIFF12:
			return getXLIFFText(true);
		case TEXTTYPE_XLIFF12XG:
			return getXLIFFText(false);
		}
		return null;
	}
	
	public int getLength (int textType) {
		switch ( textType ) {
		case TEXTTYPE_ORIGINAL:
			return getOriginalText().length();
		case TEXTTYPE_CODED:
			return getCodedText().length();
		case TEXTTYPE_GENERIC:
			return getGenericText().length();
		case TEXTTYPE_PLAINTEXT:
			return getPlainText().length();
		case TEXTTYPE_TMX14:
			return getTMXText().length();
		case TEXTTYPE_XLIFF12:
			return getXLIFFText(true).length();
		case TEXTTYPE_XLIFF12XG:
			return getXLIFFText(false).length();
		}
		return 0;
	}

	public String getCodedText () {
		return data.toString();
	}

	public String getCodes () {
		if ( !hasCode() ) return "";
		if ( !isNormalized ) normalize();
		StringBuffer sbTmp = new StringBuffer(100);
		for ( Code code : codes )
		{
			sbTmp.append(String.format("%1$d\u0086%2$d\u0086%3$s\u0086%4$s\u0087",
				code.id, code.type, code.data, code.label));
		}
		return sbTmp.toString();
	}

	public void setText (String text) {
		if ( text == null ) text = "";
		data = new StringBuilder(text.length());
		codes = new ArrayList<Code>();
		lastCodeID = 0;
		isNormalized = true;
		data.append(text);
	}
	
	public void setTextFromCoded (String codedText) {
		if ( codedText == null ) codedText = "";
		data = new StringBuilder(codedText.length());
		data.append(codedText);
	}

	public void setTextFromGeneric (String genericText) {
		if ( genericText == null ) genericText = "";
		data = new StringBuilder(genericText.length());
		
		int type = ISegment.CODE_OPENING;
		int state = 0;
		int start = -1;

		for ( int i=0; i<genericText.length(); i++ ) {
			switch ( state ) {
			case 0:
				if ( genericText.codePointAt(i) == '<' ) {
					start = i;
					state = 1;
				}
				else {
					data.append(genericText.charAt(i));
				}
				continue;

			case 1: // After <
				if ( genericText.codePointAt(i) == '/' ) {
					type = ISegment.CODE_CLOSING; // Closing code
					state = 2;
					continue;
				}
				if ( !Character.isDigit(genericText.codePointAt(i)) ) {
					data.append('<'); // Output '<'
					i--; // Then redo the char after '<'
					state = 0; // In normal state
					continue;
				}
				// Else: opening/closing code
				type = ISegment.CODE_OPENING; // Assumes opening code
				state = 2;
				continue;

			case 2: // Waiting for '/' or '>'
				if ( genericText.codePointAt(i) == '/' ) {
					i++;
					if (( i > genericText.length() ) || ( genericText.codePointAt(i) != '>' )) {
						// Not the end of a place-holder
						data.append(genericText.subSequence(start, i));
						i -= 2; // Re-do the current char
						state = 0;
						continue;
					}
					// Else: isolated code
					data.append((char)ISegment.CODE_ISOLATED);
					int n = Integer.valueOf(genericText.substring(start+1, i-1));
					n = getCodeIndex(n, ISegment.CODE_ISOLATED);
					data.append(ItoC(n));
					state = 0;
					continue;
				}
				if ( genericText.codePointAt(i) == '>' ) { // Opening or closing code
					data.append((char)type);
					int n = Integer.valueOf(genericText.substring(
						start+1+((type==ISegment.CODE_CLOSING) ? 1 : 0), i));
					n = getCodeIndex(n, type);
					data.append(ItoC(n));
					state = 0;
					continue;
				}
				if ( !Character.isDigit(genericText.codePointAt(i))
						&& ( genericText.codePointAt(i) != '|' )) {
					// Not an id
					data.append('<');
					if ( state == 1 ) data.append('/');
					data.append(genericText.substring(start, i));
					i--; // Re-do the current char
					state = 0;
					continue;
				}
				continue;
			}
		}

		if ( state > 0 ) {
			// Case of "<123" or "</123"
			data.append('<');
			data.append(genericText.substring(start));
		}
	}

	private void unpackCodes (String rawData,
		ArrayList<Code> codesToUpdate)
	{
		codesToUpdate.clear();
		String[] aTmp1 = rawData.split("\u0087");
		for ( String tmp : aTmp1 )
		{
			String[] aTmp2 = tmp.split("\u0086");
			if ( aTmp2.length < 4 ) break; // End
			Code code = new Code();
			code.id = Integer.parseInt(aTmp2[0]);
			code.type = Integer.parseInt(aTmp2[1]);
			code.data = aTmp2[2];
			code.label = aTmp2[3];
			codesToUpdate.add(code);
		}
	}
	
	public void setCodes(String data) {
		unpackCodes(data, codes);
	}

	public boolean hasText(boolean whiteSpaceIsText)
	{
		for ( int i=0; i<data.length(); i++ ) {
			switch ( data.codePointAt(i) ) {
				case CODE_OPENING:
				case CODE_CLOSING:
				case CODE_ISOLATED:
					if ( i+1 < data.length() ) i++;
					continue;
				default:
					if ( whiteSpaceIsText )
						return true; // At least one character is in the text
					// Else: Text only if not white-psace
					if ( !Character.isWhitespace(data.charAt(i)) ) return true;
					break;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return (data.length() == 0);
	}

	public ISegment subSegment (int start,
		int length,
		boolean addMissingCodes)
	{
		if (( start < 0 ) || ( start > data.length()-1 )) return null;
		if (( length < 0 ) || ( length > (data.length()-start) )) return null;

		// Create a new segment and copy all the original data
		// Use Segment not ISegment to have access to all the members.
		Segment seg = new Segment();
		seg.copyFrom(this);

		// Reset the text/codes
		seg.data.setLength(0);
		seg.codes = new ArrayList<Code>();

		// Grab the part to extract
		seg.data.append(data.toString().substring(start, start+length));
		// Shortcut if there are no code to deal with
		if ( !hasCode() ) return seg;

		// Check that we don't split codes markers
		// Check for presence of inline codes and adjust as needed
		ArrayList<Integer> indices = new ArrayList<Integer>();
		Code code;

		for ( int i=0; i<seg.data.length(); i++ ) {
			switch ( seg.data.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				// Check we do not split a marker
				if ( ++i >= seg.data.length() ) return null;
				// Copy the code info
				indices.add(CtoI(seg.data.charAt(i)));
				code = new Code(codes.get(CtoI(seg.data.charAt(i))));
				code.id = -1*code.id;
				// Add the code to the extracted item
				seg.codes.add(code); // Use negative value for flags
				seg.data.setCharAt(i, ItoC(seg.codes.size()-1));
				break;
			}
		}

		// Now check of opening/closing orphans and add the match
		// based on the codes of the original item
		// Note: code.id contains the ID of the code in the original item
		int textInsertPoint = seg.data.length();
		int i = 0;
		while ( i < seg.codes.size() ) {
			code = seg.codes.get(i);
			if ( code.id < 0 ) {
				// If it's a closing code: it's isolated
				if ( code.type == CODE_CLOSING ) {
					if ( addMissingCodes ) {
						Code newCode = new Code(seg.codes.get(i));
						newCode.type = CODE_OPENING;
						seg.codes.get(i).id = ++seg.lastCodeID;
						// Get the data from the original item
						for ( Code tmpCode : codes ) {
							if (( tmpCode.type == CODE_OPENING )
								&& ( tmpCode.id == -1*newCode.id )) {
								newCode.data = tmpCode.data;
								break;
							}
						}
						
						newCode.id = seg.lastCodeID; // Same ID as the closing
						seg.codes.add(newCode);
						seg.data.insert(0, (char)CODE_OPENING);
						seg.data.insert(1, ItoC(seg.codes.size()-1));
					}
					else {
						seg.codes.get(i).id = ++seg.lastCodeID;
						seg.changeCodeType(i, CODE_ISOLATED);
					}
				}
				else if ( code.type == CODE_OPENING ) {
					// Else, it's an opening code: search matching closing code
					int j = i+1;
					boolean bFound = false;
					int stack = 1;
					while ( j < seg.codes.size() ) {
						if ( seg.codes.get(j).id < 0 ) {
							// Try only the codes not matched yet
							if ( seg.codes.get(j).type == CODE_CLOSING ) {
								if ((( code.label == null ) && ( seg.codes.get(j).label == null ))
									|| seg.codes.get(j).label.equals(code.label) )
								{
									if ( (--stack) == 0 ) {
										bFound = true;
										break;
									}
								}
							}
							else if ( seg.codes.get(j).type == CODE_OPENING ) {
								if ((( code.label == null ) && ( seg.codes.get(j).label == null ))
									|| seg.codes.get(j).label.equals(code.label) )
								{
									stack++;
								}
							}
						}
						j++;
					}

					// Set the matching code or add it
					if ( bFound ) {
						seg.codes.get(i).id = ++seg.lastCodeID;
						seg.codes.get(j).id = seg.lastCodeID; // Same ID
					}
					else {
						if ( addMissingCodes ) {
							Code newCode = new Code(seg.codes.get(i));
							newCode.type = CODE_CLOSING;
							seg.codes.get(i).id = ++seg.lastCodeID;
							// Get the data from the original item
							for ( Code tmpCode : codes ) {
								if (( tmpCode.type == CODE_CLOSING )
									&& ( tmpCode.id == -1*newCode.id ))
								{
									newCode.data = tmpCode.data;
									break;
								}
							}
							newCode.id = seg.lastCodeID; // Same ID as the opening
							seg.codes.add(newCode);
							seg.data.insert(textInsertPoint, (char)CODE_CLOSING);
							seg.data.insert(textInsertPoint+1, ItoC(seg.codes.size()-1));
						}
						else {
							seg.codes.get(i).id = ++seg.lastCodeID;
							seg.changeCodeType(i, CODE_ISOLATED);
						}
					}
				}
				else { // Isolated code
					// Just update the ID
					seg.codes.get(i).id = ++seg.lastCodeID;
				}
			}
			i++;
		}
		return seg;
	}
	
	private void normalize () {
		if ( isNormalized ) return;
		
		int i = 0;
		int j;
		boolean bFound;
		Code code;
		int nStack;

		while ( i < codes.size() ) {
			code = codes.get(i);
			if ( code.id == -1 ) {
				// If it's a closing code: it's isolated
				if ( code.type == CODE_CLOSING ) {
					codes.get(i).id = ++lastCodeID;
					changeCodeType(i, CODE_ISOLATED);
					continue;
				}

				// Else, it's a BPT: search corresponding closing code
				j = i+1; bFound = false;
				nStack = 1;
				while ( j < codes.size() ) {
					if ( codes.get(j).type == CODE_CLOSING ) {
						if ((( codes.get(j).label == null ) && ( code.label == null ))
							|| (( codes.get(j).label.equals(code.label) ))) {
							if ( (--nStack) == 0 ) {
								bFound = true;
								break;
							}
						}
					}
					else if ( codes.get(j).type == CODE_OPENING ) {
						if ((( codes.get(j).label == null ) && ( code.label == null )) 
							|| ( codes.get(j).label.equals(code.label) )) {
							nStack++;
						}
					}
					j++;
				}

				if ( bFound ) {
					codes.get(i).id = ++lastCodeID;
					codes.get(j).id = lastCodeID; // Same ID
				}
				else {
					// Change to isolated code (update the type)
					codes.get(i).id = ++lastCodeID;
					changeCodeType(i, CODE_ISOLATED);
				}
			}
			i++;
		}
		isNormalized = true;
	}

	private void changeCodeType (int index,
		int newType)
	{
		// Search for the index in the string (i+1) after a code
		for ( int i=0; i<data.length(); i++ ) {
			switch ( data.codePointAt(i) ) {
				case CODE_OPENING:
				case CODE_CLOSING:
					if ( i+1 > data.length() ) continue;
					if ( index == CtoI(data.charAt(i+1)) ) {
						codes.get(index).type = CODE_ISOLATED; 
						data.setCharAt(i, (char)CODE_ISOLATED);
						return;
					}
					i++; // Skip over the code index
					continue;

				case CODE_ISOLATED: // Treat those too to avoid any mistake
					if ( i+1 > data.length() ) continue;
					i++; // Skip over the code index
					continue;
			}	
		}
	}

	private String getGenericText () {
		StringBuffer sbTmp = new StringBuffer(data.length());

		// Return the plain string if no codes
		if ( !hasCode() ) {
			return data.toString();
		}

		// Normalize if there are any codes
		if ( !isNormalized ) normalize();

		// Then convert them to generic tags
		for ( int i=0; i<data.length(); i++ ) {
			switch ( data.codePointAt(i) ) {
			case CODE_OPENING:
				if ( i+1 >= data.length() )	continue;
				sbTmp.append("<"+getCodeID(CtoI(data.charAt(++i)))+">");
				continue;
			case CODE_CLOSING:
				if ( i+1 >= data.length() )	continue;
				sbTmp.append("</"+getCodeID(CtoI(data.charAt(++i)))+">");
				continue;
			case CODE_ISOLATED:
				if ( i+1 >= data.length() )	continue;
				sbTmp.append("<"+getCodeID(CtoI(data.charAt(++i)))+"/>");
				continue;
			default:
				sbTmp.append(data.charAt(i));
				continue;
			}
		}
		return sbTmp.toString();
	}

	private String getOriginalText () {
		// Return the plain string if no codes
		if ( !hasCode() ) {
			return data.toString();
		}

		// Note: No need to normalize the in-line codes for this output
		StringBuilder sbTmp = new StringBuilder(data.length());
		for ( int i=0; i<data.length(); i++ ) {
			switch ( data.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				if ( i+1 >= data.length() )	continue;
				sbTmp.append(getCodeData(CtoI(data.charAt(++i))));
				continue;
			default:
				sbTmp.append(data.charAt(i));
				continue;
			}
		}
		return sbTmp.toString();
	}
	
	private String getPlainText () {
		// Return the plain string if no codes
		if ( !hasCode() ) {
			return data.toString();
		}
		
		// Note: No need to normalize the in-line codes for this output
		StringBuffer sbTmp = new StringBuffer(data.length());
		for ( int i=0; i<data.length(); i++ ) {
			switch ( data.codePointAt(i) )
			{
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				if ( i+1 < data.length() ) i++;
				continue;
			default:
				sbTmp.append(data.charAt(i));
				continue;
			}
		}
		return sbTmp.toString();
	}

	private String getTMXText () {
		// Return the plain string if no codes
		if ( !hasCode() ) {
			// Order is important: do '&' first
			String tmp = data.toString().replaceAll("&", "&amp;");
			tmp = tmp.replaceAll("\\<", "&lt;");
			return tmp.replace("\\]\\>", "]&gt;");
		}
		return null;
	}

	private String getXLIFFText (boolean useBPT) {
		// Return the plain string if no codes
		if ( !hasCode() ) {
			// Order is important: do '&' first
			String tmp = data.toString().replaceAll("&", "&amp;");
			tmp = tmp.replaceAll("\\<", "&lt;");
			return tmp.replace("\\]\\>", "]&gt;");
		}
		return null;
	}

}
