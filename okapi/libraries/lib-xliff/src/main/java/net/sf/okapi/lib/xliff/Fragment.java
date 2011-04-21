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

	public static final char MARKER_OPENING = '\uE101';
	public static final char MARKER_CLOSING = '\uE102';
	public static final char MARKER_PLACEHOLDER = '\uE103';
	public static final int CHARBASE = 0xE110;

	private StringBuilder data;
	private ArrayList<Code> codes;
	
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
	
	/**
	 * Returns an XLIFF representation of this fragment.
	 */
	@Override
	public String toString () {
		/*
		 * Use a temporary representation with <inline> for all codes.
		 */
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<data.length(); i++ ) {
			char ch = data.charAt(i);
			if ( data.charAt(i) == MARKER_OPENING ) {
				tmp.append(String.format("<inline id=\"%d\"/>",
					toIndex(data.charAt(++i))));
			}
			else if ( data.charAt(i) == MARKER_CLOSING ) {
				tmp.append(String.format("<inline id=\"%d\"/>",
					toIndex(data.charAt(++i))));
			}
			else if ( data.charAt(i) == MARKER_PLACEHOLDER ) {
				tmp.append(String.format("<inline id=\"%d\"/>",
					toIndex(data.charAt(++i))));
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
		data = new StringBuilder();
	}
	
	public Fragment (String text) {
		data = new StringBuilder(text);
	}

	public boolean isEmpty () {
		return (data.length()==0);
	}
	
	public void clear () {
		data.setLength(0);
		codes = null;
	}
	
	public void append (String text) {
		data.append(text);
	}
	
	public void append (char ch) {
		data.append(ch);
	}
	
	public Code append (int type,
		String nativeData)
	{
		if ( codes == null ) codes = new ArrayList<Code>();
		Code code = new Code(nativeData);
		codes.add(code);
		switch ( type ) {
		case 0:
			data.append(""+MARKER_OPENING+toChar(codes.size()-1));
			break;
		case 1:
			data.append(""+MARKER_CLOSING+toChar(codes.size()-1));
			break;
		case 2:
			data.append(""+MARKER_PLACEHOLDER+toChar(codes.size()-1));
			break;
		}
		return code;
	}

}
