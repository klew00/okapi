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

import java.util.ArrayList;
import java.util.Stack;

import org.oasisopen.xliff.v2.ICode;
import org.oasisopen.xliff.v2.IDataStore;
import org.oasisopen.xliff.v2.IFragment;
import org.oasisopen.xliff.v2.InlineType;

/**
 * TEMPORARY implementation.
 * Holds the usable content for XLIFF constructs: text and inline codes.
 */
public class Fragment implements IFragment {
	
	private static final long serialVersionUID = 0100L;
	
	public static final char CODE_OPENING = '\uE101';
	public static final char CODE_CLOSING = '\uE102';
	public static final char CODE_PLACEHOLDER = '\uE103';
	public static final char ANNO_OPENING = '\uE104';
	public static final char ANNO_CLOSING = '\uE105';
	public static final char ANNO_PLACEHOLDER = '\uE106';

	public static final int INDEX_BASE = 0xE110;
	public static final int INDEX_MAX = (0xF8FF-INDEX_BASE);

	private StringBuilder ctext;
	private Codes codes;
	
	/**
	 * Helper method to convert a marker index to its character value in the
	 * coded text string.
	 * @param index the index value to encode.
	 * @return the corresponding character value.
	 */
	public static char toChar (int index) {
		if ( index > INDEX_MAX ) {
			throw new RuntimeException(
				String.format("This implementation cannot have fragments with more than %d inline codes.", INDEX_MAX));
		}
		return (char)(index+INDEX_BASE);
	}

	/**
	 * Helper method to convert the index-coded-as-character part of a marker into 
	 * its index value.
	 * @param index the character to decode.
	 * @return the corresponding index value.
	 */
	public static int toIndex (char index) {
		return ((int)index)-INDEX_BASE;
	}
	
	/**
	 * Helper method that checks if a given character is an inline marker.
	 * @param ch the character to check.
	 * @return true if the character is a marker, false if it is not.
	 */
	public static boolean isMarker (char ch) {
		return (( ch == CODE_PLACEHOLDER )
			|| ( ch == CODE_OPENING )
			|| ( ch == CODE_CLOSING )
			|| ( ch == ANNO_PLACEHOLDER )
			|| ( ch == ANNO_OPENING )
			|| ( ch == ANNO_CLOSING ));
	}

	public Fragment (DataStore store) {
		ctext = new StringBuilder();
		if ( store != null ) {
			this.codes = store.getSourceCodes();
		}
	}
	
	public Fragment (DataStore store,
		boolean target)
	{
		ctext = new StringBuilder();
		if ( store != null ) {
			if ( target ) this.codes = store.getTargetCodes();
			else this.codes = store.getSourceCodes();
		}
	}
	
	public Fragment (DataStore store,
		boolean target,
		String plainText)
	{
		ctext = new StringBuilder(plainText);
		if ( store != null ) {
			if ( target ) this.codes = store.getTargetCodes();
			else this.codes = store.getSourceCodes();
		}
	}
	
	@Override
	public String toString () {
		return ctext.toString();
	}
	
	@Override
	public String getCodedText () {
		return ctext.toString();
	}

	@Override
	public String toXLIFF (int style) {
		switch ( style ) {
		case STYLE_XSDTEMP:
			return toXSDTemp();
		case STYLE_DATAINSIDE:
			return toXLIFFWithOriginalData(true);
		case STYLE_DATAOUTSIDE:
			return toXLIFFWithOriginalData(false);
		case STYLE_NODATA:
		default:
			return toXLIFF();
		}
	}
	
	@Override
	public IDataStore getDataStore () {
		return codes.getDataStore();
	}
	
	private String toXSDTemp () {
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<ctext.length(); i++ ) {
			int cp = ctext.codePointAt(i);
			if ( cp == CODE_OPENING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else if ( cp == CODE_CLOSING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else if ( cp == CODE_PLACEHOLDER ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getInternalId()));
			}
			else {
				switch ( cp ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				case '\n':
				case '\t':
					tmp.append((char)cp);
					break;
				default:
					if (( cp < 0x0020 )
						|| (( cp >0xD7FF ) && ( cp < 0xE000 ))
						|| ( cp == 0xFFFF ))
					{
						// Invalid
						tmp.append(String.format("<cp hex=\"%04X\"/>", cp));
					}
					else if ( cp < 0xFFFF ) {
						// Valid char 
						tmp.append((char)cp);
					}
					else if ( cp > 0xFFFF ) {
						// Valid pair
						tmp.append(Character.toChars(cp));
						i++; // Skip second char of the pair
					}
					break;
				}
			}
		}
		return tmp.toString();
	}
	
	private String toXLIFFWithOriginalData (boolean dataInside) {
		StringBuilder tmp = new StringBuilder();
		ICode code;
		int index;
		for ( int i=0; i<ctext.length(); i++ ) {
			int cp = ctext.codePointAt(i);
			if ( cp == CODE_OPENING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<sc id=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toSafeXML(code.getOriginalData())+"</sc>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getDataStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else if ( cp == CODE_CLOSING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ec rid=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toSafeXML(code.getOriginalData())+"</ec>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getDataStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else if ( cp == CODE_PLACEHOLDER ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ph id=\"%s\"", code.getId()));
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else {
						tmp.append(">"+Util.toSafeXML(code.getOriginalData())+"</ph>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						tmp.append(String.format(" nid=\"%s\"",
							codes.getDataStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else {
				switch ( cp ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				case '\n':
				case '\t':
					tmp.append((char)cp);
					break;
				default:
					if (( cp < 0x0020 )
						|| (( cp >0xD7FF ) && ( cp < 0xE000 ))
						|| ( cp == 0xFFFF ))
					{
						// Invalid
						tmp.append(String.format("<cp hex=\"%04X\"/>", cp));
					}
					else if ( cp < 0xFFFF ) {
						// Valid char 
						tmp.append((char)cp);
					}
					else if ( cp > 0xFFFF ) {
						// Valid pair
						tmp.append(Character.toChars(cp));
						i++; // Skip second char of the pair
					}
					break;
				}
			}
		}
		return tmp.toString();
	}

	@Override
	public String toXLIFF () {
		StringBuilder tmp = new StringBuilder();
		ICode code;
		ArrayList<String> verified = new ArrayList<String>();
		for ( int i=0; i<ctext.length(); i++ ) {
			int cp = ctext.codePointAt(i);
			if ( cp == CODE_OPENING ) {
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
			else if ( cp == CODE_CLOSING ) {
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
			else if ( cp == CODE_PLACEHOLDER ) {
				tmp.append(String.format("<ph id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getId()));
			}
			else {
				// In XML 1.0 the valid characters are:
				// #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
				switch ( cp ) {
				case '\r':
					tmp.append("&#13;"); // Literal
					break;
				case '<':
					tmp.append("&lt;");
					break;
				case '&':
					tmp.append("&amp;");
					break;
				case '\n':
				case '\t':
					tmp.append((char)cp);
					break;
				default:
					if (( cp < 0x0020 )
						|| (( cp >0xD7FF ) && ( cp < 0xE000 ))
						|| ( cp == 0xFFFF ))
					{
						// Invalid
						tmp.append(String.format("<cp hex=\"%04X\"/>", cp));
					}
					else if ( cp < 0xFFFF ) {
						// Valid char 
						tmp.append((char)cp);
					}
					else if ( cp > 0xFFFF ) {
						// Valid pair
						tmp.append(Character.toChars(cp));
						i++; // Skip second char of the pair
					}
				}
			}
		}
		return tmp.toString();
	}

	@Override
	public boolean isEmpty () {
		return (ctext.length()==0);
	}

	@Override
	public boolean isWellFormed (ICode openingCode,
		int from)
	{
		Stack<String> stack = new Stack<String>();
		for ( int i=from; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			ICode code;
			if ( ch == CODE_OPENING ) {
				code = codes.get(toIndex(ctext.charAt(++i)));
				stack.push(code.getId());
			}
			else if ( ch == CODE_CLOSING ) {
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
			else if ( ch == CODE_PLACEHOLDER ) {
				i++;
			}
		}
		// Closing part not found: not well-formed.
		return false;
	}
	
	@Override
	public void append (String plainText) {
		ctext.append(plainText);
	}
	
	@Override
	public void append (char ch) {
		ctext.append(ch);
	}

	@Override
	public ICode append (InlineType type,
		String id,
		String originalData)
	{
		if ( codes == null ) {
			throw new RuntimeException("Cannot add codes in this fragment because it has no associated store of codes.");
		}
		ICode code = new Code(type, id, originalData);
		codes.add(code);
		switch ( type ) {
		case OPENING:
			ctext.append(""+CODE_OPENING+toChar(codes.size()-1));
			break;
		case CLOSING:
			ctext.append(""+CODE_CLOSING+toChar(codes.size()-1));
			break;
		case PLACEHOLDER:
			ctext.append(""+CODE_PLACEHOLDER+toChar(codes.size()-1));
			break;
		}
		return code;
	}

}
