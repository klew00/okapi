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
import org.oasisopen.xliff.v2.IMarker;
import org.oasisopen.xliff.v2.IMarkers;
import org.oasisopen.xliff.v2.InlineType;

/**
 * Holds the usable content for XLIFF constructs: text and inline markers.
 */
public class Fragment implements IFragment {
	
	private static final long serialVersionUID = 0100L;
	
	public static final char CODE_OPENING = '\uE101';
	public static final char CODE_CLOSING = '\uE102';
	public static final char CODE_PLACEHOLDER = '\uE103';
	public static final char ANNO_OPENING = '\uE104';
	public static final char ANNO_CLOSING = '\uE105';

	public static final int INDEX_BASE = 0xE110;
	public static final int INDEX_MAX = (0xF8FF-INDEX_BASE);

	private StringBuilder ctext;
	private IMarkers markers;
	
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
			|| ( ch == ANNO_OPENING )
			|| ( ch == ANNO_CLOSING ));
	}

	public Fragment (DataStore store) {
		ctext = new StringBuilder();
		if ( store != null ) {
			this.markers = store.getSourceMarkers();
		}
	}
	
	public Fragment (DataStore store,
		boolean target)
	{
		ctext = new StringBuilder();
		if ( store != null ) {
			if ( target ) this.markers = store.getTargetMarkers();
			else this.markers = store.getSourceMarkers();
		}
	}
	
	public Fragment (DataStore store,
		boolean target,
		String plainText)
	{
		ctext = new StringBuilder(plainText);
		if ( store != null ) {
			if ( target ) this.markers = store.getTargetMarkers();
			else this.markers = store.getSourceMarkers();
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
		if ( markers == null ) {
			throw new RuntimeException("This fragment has no associated store.");
		}
		return markers.getDataStore();
	}

	private String toXLIFFWithOriginalData (boolean dataInside) {
		StringBuilder tmp = new StringBuilder();
		ICode code;
		int index;
		ArrayList<String> verified = new ArrayList<String>();
		
		for ( int i=0; i<ctext.length(); i++ ) {
			int cp = ctext.codePointAt(i);
			if ( cp == CODE_OPENING ) {
				code = (ICode)markers.get(toIndex(ctext.charAt(++i)));
				// Check if the corresponding closing part is in the same fragment
				ICode closing = null;
				if ( !dataInside ) {
					// For data outside we can use <pc>
					closing = (ICode)getWellFormedClosing(code, i);
				}
				if ( closing != null ) {
					tmp.append(String.format("<pc id=\"%s\"", code.getId()));
					verified.add(code.getId());
				}
				else {
					// No corresponding closing part or data inside
					tmp.append(String.format("<sc id=\"%s\"", code.getId()));
				}
				printCommonAttributes(code, tmp, closing, true); // closing can be null
				
				if ( dataInside ) {
					if ( Util.isNullOrEmpty(code.getOriginalData()) ) {
						tmp.append("/>");
					}
					else { // Data inside is always using <sc>
						tmp.append(">"+Util.toSafeXML(code.getOriginalData())+"</sc>");
					}
				}
				else {
					if ( code.hasOriginalData() ) {
						String ending = (closing==null ? "" : "Start");
						tmp.append(String.format(" nid%s=\"%s\"", ending,
							markers.getDataStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append(closing==null ? "/>" : ">");
				}
			}
			else if ( cp == CODE_CLOSING ) {
				code = (ICode)markers.get(toIndex(ctext.charAt(++i)));
				if ( verified.contains(code.getId()) ) {
					// This pair was verified
					tmp.append("</pc>");
					// No need to remove the code from the verified list
					// as it's not used again (no need to waste time cleaning it)
				}
				else { // Not in the verified list: use <ec>
					tmp.append(String.format("<ec rid=\"%s\"", code.getId()));
					printCommonAttributes(code, tmp, null, false);
				
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
								markers.getDataStore().getIdForOriginalData(code.getOriginalData())));
						}
						tmp.append("/>");
					}
				}
			}
			else if ( cp == CODE_PLACEHOLDER ) {
				index = toIndex(ctext.charAt(++i));
				code = (ICode)markers.get(index);
				tmp.append(String.format("<ph id=\"%s\"", code.getId()));
				printCommonAttributes(code, tmp, null, false);
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
							markers.getDataStore().getIdForOriginalData(code.getOriginalData())));
					}
					tmp.append("/>");
				}
			}
			else if ( cp == ANNO_OPENING ) {
				//TODO
				i++;
			}
			else if ( cp == ANNO_CLOSING ) {
				//TODO
				i++;
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
	
	/**
	 * Output the common attributes
	 * @param code the code to output.
	 * @param tmp the buffer where to output.
	 * @param closing the closing code if this is a paired-code.
	 */
	private void printCommonAttributes (ICode code,
		StringBuilder tmp,
		ICode closing,
		boolean outputNid)
	{
		if ( code.getType() != null ) {
			tmp.append(String.format(" type=\"%s\"", code.getType()));
		}

		String ending = (closing == null ? "" : "Start");
		if ( code.getEquiv() != null ) {
			tmp.append(String.format(" equiv%s=\"%s\"", ending, code.getEquiv()));
		}
		if ( code.getDisp() != null ) {
			tmp.append(String.format(" disp%s=\"%s\"", ending, code.getDisp()));
		}
		if ( code.getSubFlows() != null ) {
			tmp.append(String.format(" subFlows%s=\"%s\"", ending, code.getSubFlows()));
		}
		
		if ( closing != null ) {
			if ( closing.getEquiv() != null ) {
				tmp.append(String.format(" equivEnd=\"%s\"", closing.getEquiv()));
			}
			if ( closing.getDisp() != null ) {
				tmp.append(String.format(" dispEnd=\"%s\"", closing.getDisp()));
			}
			if ( closing.getSubFlows() != null ) {
				tmp.append(String.format(" subFlowsEnd=\"%s\"", closing.getSubFlows()));
			}

			if ( outputNid && closing.hasOriginalData() ) {
				tmp.append(String.format(" nidEnd=\"%s\"",
					markers.getDataStore().getIdForOriginalData(closing.getOriginalData())));
			}
			
		}
	}

	@Override
	public String toXLIFF () {
		StringBuilder tmp = new StringBuilder();
		ICode code;
		ArrayList<String> verified = new ArrayList<String>();
		for ( int i=0; i<ctext.length(); i++ ) {
			int cp = ctext.codePointAt(i);
			if ( cp == CODE_OPENING ) {
				code = (ICode)markers.get(toIndex(ctext.charAt(++i)));
				// Check if the corresponding closing part is in the same fragment
				ICode closing = (ICode)getWellFormedClosing(code, i);
				if ( closing != null ) {
					tmp.append(String.format("<pc id=\"%s\"", code.getId()));
					printCommonAttributes(code, tmp, closing, false);
					verified.add(code.getId());
					tmp.append(">");
				}
				else {
					// No corresponding closing part
					tmp.append(String.format("<sc id=\"%s\"", code.getId()));
					printCommonAttributes(code, tmp, null, false);
					tmp.append("/>");
				}
			}
			else if ( cp == CODE_CLOSING ) {
				code = (ICode)markers.get(toIndex(ctext.charAt(++i)));
				if ( verified.contains(code.getId()) ) {
					// This pair was verified
					tmp.append("</pc>");
					// No need to remove the code from the verified list
					// as it's not used again (no need to waste time cleaning it)
				}
				else { // Not in the verified list
					tmp.append(String.format("<ec rid=\"%s\"", code.getId()));
					printCommonAttributes(code, tmp, null, false);
					tmp.append("/>");
				}
			}
			else if ( cp == CODE_PLACEHOLDER ) {
				code = (ICode)markers.get(toIndex(ctext.charAt(++i)));
				tmp.append(String.format("<ph id=\"%s\"", code.getId()));
				printCommonAttributes(code, tmp, null, false);
				tmp.append("/>");
			}
			else if ( cp == ANNO_OPENING ) {
				//TODO
				i++;
			}
			else if ( cp == ANNO_CLOSING ) {
				//TODO
				i++;
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
	public IMarker getWellFormedClosing (IMarker openingMarker,
		int from)
	{
		Stack<String> stack = new Stack<String>();
		for ( int i=from; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			IMarker marker;
			if (( ch == CODE_OPENING ) || ( ch == ANNO_OPENING )) {
				marker = markers.get(toIndex(ctext.charAt(++i)));
				stack.push(marker.getId());
			}
//TODO: annotation take precedence over codees for well-formness!!			
			else if (( ch == CODE_CLOSING ) || ( ch == ANNO_CLOSING )) {
				marker = markers.get(toIndex(ctext.charAt(++i)));
				if ( marker.getId().equals(openingMarker.getId()) ) {
					// Well-formed if the stack is empty
					if ( stack.isEmpty() ) return marker;
					else return null;
				}
				// If it's not our closing code and the stack is already empty
				// That's not a well-formed pattern
				if ( stack.isEmpty() ) {
					return null;
				}
				// Remove the marker
				// If it's at the top it's like a pop()
				// Otherwise it says the opening was closed
				stack.remove(marker.getId());
			}
			else if ( ch == CODE_PLACEHOLDER ) {
				i++;
			}
		}
		// Closing part not found: not well-formed.
		return null;
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
	public ICode append (ICode code) {
		markers.add(code);
		switch ( code.getInlineType() ) {
		case OPENING:
			ctext.append(""+CODE_OPENING+toChar(markers.size()-1));
			break;
		case CLOSING:
			ctext.append(""+CODE_CLOSING+toChar(markers.size()-1));
			break;
		case PLACEHOLDER:
			ctext.append(""+CODE_PLACEHOLDER+toChar(markers.size()-1));
			break;
		}
		return code;
	}

	@Override
	public ICode append (InlineType type,
		String id,
		String originalData)
	{
		if ( markers == null ) {
			throw new RuntimeException("Cannot add codes in this fragment because it has no associated store.");
		}
		ICode code = new Code(type, id, originalData);
		return append(code);
	}

}
