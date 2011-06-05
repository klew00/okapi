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

/**
 * TEMPORARY implementation.
 * Holds the usable content for XLIFF constructs: text and inline codes.
 */
public class Fragment {
	
	public static final int STYLE_XSDTEMP = -1;
	public static final int STYLE_NODATA = 0;
	public static final int STYLE_DATAINSIDE = 1;
	public static final int STYLE_DATAOUTSIDE = 2;

	public static final char MARKER_OPENING = '\uE101';
	public static final char MARKER_CLOSING = '\uE102';
	public static final char MARKER_PLACEHOLDER = '\uE103';
	public static final int CHARBASE = 0xE110;

	private StringBuilder ctext;
	private ArrayList<Code> codes;
	private int lastAutoId;
	
	public static String toXML (String text,
		boolean attribute)
	{
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		if ( attribute ) {
			text = text.replace("\"", "&quot;");
		}
		return text;
	}

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
	
	public String getString (int style) {
		switch ( style ) {
		case STYLE_XSDTEMP:
			return getStringXSDTemp();
		case STYLE_DATAINSIDE:
			return getStringDataInside();
		case STYLE_DATAOUTSIDE:
			//todo
			// for now just do no-data
		case STYLE_NODATA:
		default:
			return toString();
		}
	}
	
	private String getStringXSDTemp () {
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			if ( ctext.charAt(i) == MARKER_OPENING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getId()));
			}
			else if ( ctext.charAt(i) == MARKER_CLOSING ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
					codes.get(toIndex(ctext.charAt(++i))).getId()));
			}
			else if ( ctext.charAt(i) == MARKER_PLACEHOLDER ) {
				tmp.append(String.format("<inline id=\"%s\"/>",
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
	
	public String getStringDataInside () {
		StringBuilder tmp = new StringBuilder();
		Code code;
		int index;
		//TODO: Handle overlapping/partial pairs
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			if ( ctext.charAt(i) == MARKER_OPENING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<sc id=\"%s\">", code.getId()));
				tmp.append(toXML(code.getNativeData(), false));
				tmp.append("</sc>");
			}
			else if ( ctext.charAt(i) == MARKER_CLOSING ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ec id=\"%s\">", code.getId()));
				tmp.append(toXML(code.getNativeData(), false));
				tmp.append("</ec>");
			}
			else if ( ctext.charAt(i) == MARKER_PLACEHOLDER ) {
				index = toIndex(ctext.charAt(++i));
				code = codes.get(index);
				tmp.append(String.format("<ph id=\"%s\">", code.getId()));
				tmp.append(toXML(code.getNativeData(), false));
				tmp.append("</ic>");
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
		for ( int i=0; i<ctext.length(); i++ ) {
			char ch = ctext.charAt(i);
			//TODO: Handle overlapping/partial spans
			if ( ctext.charAt(i) == MARKER_OPENING ) {
				tmp.append(String.format("<pc id=\"%s\">",
					codes.get(toIndex(ctext.charAt(++i))).getId()));
			}
			else if ( ctext.charAt(i) == MARKER_CLOSING ) {
				tmp.append("</pc>");
				i++; // Skip index
			}
			else if ( ctext.charAt(i) == MARKER_PLACEHOLDER ) {
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

	public Fragment () {
		ctext = new StringBuilder();
		lastAutoId = 0;
	}
	
	public Fragment (String plainText) {
		ctext = new StringBuilder(plainText);
		lastAutoId = 0;
	}

	public boolean isEmpty () {
		return (ctext.length()==0);
	}
	
	public void clear () {
		ctext.setLength(0);
		if ( codes != null ) codes.clear();
		codes = null;
		lastAutoId = 0;
	}
	
	public void append (String plainText) {
		ctext.append(plainText);
	}
	
	public void append (char ch) {
		ctext.append(ch);
	}

	public Code append (CodeType type,
		String nativeData)
	{
		return append(type, null, nativeData);
	}
	
	public Code append (CodeType type,
		String id,
		String nativeData)
	{
		if ( codes == null ) {
			codes = new ArrayList<Code>();
		}
		Code code = new Code(type, nativeData);
		code.setId(checkId(id));
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

	private String checkId (String id) {
		// Create a new ID if the one provided is null or empty
		if (( id == null ) || id.isEmpty() ) {
			id = String.valueOf(++lastAutoId);
		}
		// Checks if the ID is already used
		boolean exists = true;
		while ( exists ) {
			exists = false;
			for ( int i=0; i<codes.size(); i++ ) {
				if ( codes.get(i).getId().equals(id) ) {
					// If it is, we just try the next auto value
					id = String.valueOf(++lastAutoId);
					exists = true;
					break;
				}
			}
		}
		// Returns the validated (and possibly modified id)
		return id;
	}

}
