/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.xliff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

/**
 * TEMPORARY implementation.
 * Holds the usable content for XLIFF constructs: text and inline codes.
 */
public class Fragment implements Serializable {
	
	private static final long serialVersionUID = 0100L;
	
	public static final int STYLE_XSDTEMP = -1;
	public static final int STYLE_NODATA = 0;
	public static final int STYLE_DATAINSIDE = 1;
	public static final int STYLE_DATAOUTSIDE = 2;

	public static final char MARKER_OPENING = '\uE101';
	public static final char MARKER_CLOSING = '\uE102';
	public static final char MARKER_PLACEHOLDER = '\uE103';
	public static final int CHARBASE = 0xE110;
	public static final int LASTINDEX = 0xF8FF;

	private StringBuilder ctext;
	private Codes codes;
	
	/**
	 * Helper method to convert a marker index to its character value in the
	 * coded text string.
	 * @param index the index value to encode.
	 * @return the corresponding character value.
	 */
	public static char toChar (int index) {
		return (char)(index+CHARBASE);
	}

	/**
	 * Helper method to convert the index-coded-as-character part of a marker into 
	 * its index value.
	 * @param index the character to decode.
	 * @return the corresponding index value.
	 */
	public static int toIndex (char index) {
		return ((int)index)-CHARBASE;
	}
	
	/**
	 * Helper method that checks if a given character is an inline code marker.
	 * @param ch the character to check.
	 * @return true if the character is a code marker, false if it is not.
	 */
	public static boolean isMarker (char ch) {
		return (( ch == MARKER_OPENING )
			|| ( ch == MARKER_CLOSING )
			|| ( ch == MARKER_PLACEHOLDER ));
	}

	public Fragment (CodeStore store) {
		ctext = new StringBuilder();
		if ( store != null ) {
			this.codes = store.getSourceCodes();
		}
	}
	
	public Fragment (CodeStore store,
		boolean target)
	{
		ctext = new StringBuilder();
		if ( store != null ) {
			if ( target ) this.codes = store.getTargetCodes();
			else this.codes = store.getSourceCodes();
		}
	}
	
	public Fragment (CodeStore store,
		boolean target,
		String plainText)
	{
		ctext = new StringBuilder(plainText);
		if ( store != null ) {
			if ( target ) this.codes = store.getTargetCodes();
			else this.codes = store.getSourceCodes();
		}
	}
	
	public String getString (int style) {
		switch ( style ) {
		case STYLE_XSDTEMP:
			return getStringXSDTemp();
		case STYLE_DATAINSIDE:
			return getStringWithOriginalData(true);
		case STYLE_DATAOUTSIDE:
			return getStringWithOriginalData(false);
		case STYLE_NODATA:
		default:
			return toString();
		}
	}
	
	public CodeStore getCodeStore () {
		return codes.getCodeStore();
	}
	
	private String getStringXSDTemp () {
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			if ( ctext.charAt(i) == MARKER_OPENING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else if ( ctext.charAt(i) == MARKER_CLOSING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else if ( ctext.charAt(i) == MARKER_PLACEHOLDER ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else {
				switch ( ch ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				default:
					tmp.append(ch);
					break;
				}
			}
		}
		return tmp.toString();
	}
	
	public String getStringWithOriginalData (boolean dataInside) {
		StringBuilder tmp = new StringBuilder();
		Code code;
		int index;
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			if ( ctext.charAt(i) == MARKER_OPENING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<sc id=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toXML(code.getOriginalData(), false)+"</sc>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getCodeStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else if ( ctext.charAt(i) == MARKER_CLOSING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ec rid=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toXML(code.getOriginalData(), false)+"</ec>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getCodeStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else if ( ctext.charAt(i) == MARKER_PLACEHOLDER ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ph id=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toXML(code.getOriginalData(), false)+"</ph>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getCodeStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else {
				switch ( ch ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				default:
					tmp.append(ch);
					break;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * Returns an XLIFF representation of this fragment in the style {@link STYLE_NODATA}.
	 */
	@Override
	public String toString () {
		StringBuilder tmp = new StringBuilder();
		Code code;
		ArrayList<String> verified = new ArrayList<String>();
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			if ( ch == MARKER_OPENING ) {
				code = codes.get(toIndex(ctext.charAt(++i)));
				// Check if the corresponding closing part is in the same fragment
				if ( isWellFormed(code, i) ) {
					tmp.append(String.format("<pc id=\"%s\">", code.getId()));
					verified.add(code.getId());
				}
				else {
					// No corresponding closing part
					tmp.append(String.format("<sc id=\"%s\"/>", code.getId()));
				}
			}
			else if ( ch == MARKER_CLOSING ) {
				code = codes.get(toIndex(ctext.charAt(++i)));
				if ( verified.contains(code.getId()) ) {
					// This pair was verified
					tmp.append("</pc>");
					// No need to remove the code from the verified list
					// as it's not used again (no need to waste time cleaning it)
				}
				else { // Not in the verified list
					tmp.append(String.format("<ec rid=\"%s\"/>", code.getId()));
				}
			}
			else if ( ch == MARKER_PLACEHOLDER ) {
				tmp.append(String.format("<ph id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getId()));
			}
			else {
				switch ( ch ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				default:
					tmp.append(ch);
					break;
				}
			}
		}
		return tmp.toString();
	}

	public boolean isEmpty () {
		return (ctext.length()==0);
	}

	public boolean isWellFormed (Code openingCode,
		int from)
	{
		Stack<String> stack = new Stack<String>();
		for ( int i=from; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			Code code;
			if ( ch == MARKER_OPENING ) {
				code = codes.get(toIndex(ctext.charAt(++i)));
				stack.push(code.getId());
			}
			else if ( ch == MARKER_CLOSING ) {
				code = codes.get(toIndex(ctext.charAt(++i)));
				if ( code.getId().equals(openingCode.getId()) ) {
					// Well-formed if the stack is empty
					return stack.isEmpty();
				}
				// If it's not our closing code and the stack is already empty
				// That's not a well-formed pattern
				if ( stack.isEmpty() ) {
					return false;
				}
				// If the top of the stack is not the closing of the current
				// element, it's not well-formed.
				if ( !stack.pop().equals(code.getId()) ) {
					return false;
				}
				// Else: keep going
			}
			else if ( ch == MARKER_PLACEHOLDER ) {
				i++;
			}
		}
		// Closing part not found: not well-formed.
		return false;
	}
	
	
//	public void clear () {
//		ctext.setLength(0);
//		if ( codes != null ) codes.clear();
//		codes = null;
//	}
	
	public void append (String plainText) {
		ctext.append(plainText);
	}
	
	public void append (char ch) {
		ctext.append(ch);
	}

	public Code append (InlineType type,
		String id,
		String originalData)
	{
		if ( codes == null ) {
			throw new RuntimeException("Cannot add codes in this fragment because it has no associated store of codes.");
		}
		Code code = new Code(type, id, originalData);
		codes.add(code);
		switch ( type ) {
		case OPENING:
			ctext.append(""+MARKER_OPENING+toChar(codes.size()-1));
			break;
		case CLOSING:
			ctext.append(""+MARKER_CLOSING+toChar(codes.size()-1));
			break;
		case PLACEHOLDER:
			ctext.append(""+MARKER_PLACEHOLDER+toChar(codes.size()-1));
			break;
		}
		return code;
	}

//	private String checkId (String id) {
//		// Create a new ID if the one provided is null or empty
//		if (( id == null ) || id.isEmpty() ) {
//			id = String.valueOf(++lastAutoId);
//		}
//		// Checks if the ID is already used
//		boolean exists = true;
//		while ( exists ) {
//			exists = false;
//			for ( int i=0; i<codes.size(); i++ ) {
//				if ( codes.get(i).getId().equals(id) ) {
//					// If it is, we just try the next auto value
//					id = String.valueOf(++lastAutoId);
//					exists = true;
//					break;
//				}
//			}
//		}
//		// Returns the validated (and possibly modified id)
//		return id;
//	}

}
