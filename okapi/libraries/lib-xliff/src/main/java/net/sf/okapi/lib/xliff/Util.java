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

import org.oasisopen.xliff.v2.InlineType;

class Util {

	final static String NS_XLIFF20 = "urn:oasis:names:tc:xliff:document:2.0";
	final static String NS_XML = "http://www.w3.org/XML/1998/namespace";
		
	final static String ELEM_DOC = "xliff";
	final static String ELEM_SECTION = "file";
	final static String ELEM_GROUP = "group";
	final static String ELEM_UNIT = "unit";
	final static String ELEM_SEGMENT = "segment";
	final static String ELEM_IGNORABLE = "ignorable";
	final static String ELEM_SOURCE = "source";
	final static String ELEM_TARGET = "target";
	final static String ELEM_CANDIDATE = "match";
	final static String ELEM_NOTE = "simpleNote";
	final static String ELEM_OPENINGCODE = "sc";
	final static String ELEM_CLOSINGCODE = "ec";
	final static String ELEM_PLACEHOLDER = "ph";
	final static String ELEM_PAIREDCODES = "pc";
	final static String ELEM_CP = "cp";
	final static String ELEM_ORIGINALDATA = "originalData";
	final static String ELEM_DATA = "data";

	final static String ATTR_ID = "id";
	final static String ATTR_RID = "rid";
	final static String ATTR_NID = "nid";
	final static String ATTR_TYPE = "type";
	final static String ATTR_HEX = "hex";
	final static String ATTR_EQUIV = "equiv";
	final static String ATTR_EQUIVSTART = "equivStart";
	final static String ATTR_EQUIVEND = "equivEnd";
	final static String ATTR_DISP = "disp";
	final static String ATTR_DISPSTART = "dispStart";
	final static String ATTR_DISPEND = "dispEnd";
	final static String ATTR_SUBFLOWS = "subFlows";
	final static String ATTR_SUBFLOWSSTART = "subFlowsStart";
	final static String ATTR_SUBFLOWSEND = "subFlowsEnd";
	final static String ATTR_SOURCELANG = "srclang";
	final static String ATTR_TARGETLANG = "tgtlang";
	final static String ATTR_TRANSLATABLE = "translatable";
	final static String ATTR_APPLIESTO = "appliesTo";
	
	static String toInternalId (String id,
		InlineType inlineType)
	{
		switch ( inlineType ) {
		case CLOSING:
			return "c"+id;
		case OPENING:
			return "o"+id;
		case PLACEHOLDER:
		default:
			return "p"+id;
		}
	}
	
	/**
	 * Checks if a string is null or empty.
	 * @param string the string to check.
	 * @return true if the given string is null or empty.
	 */
	static boolean isNullOrEmpty (String string) {
		return (( string == null ) || string.isEmpty() );
	}

	/**
	 * Convert a text to an XML-escaped text.
	 * This method assumes the output is in UTF-16 or UTF-8 and that all characters
	 * are supported. It also assumes attribute values are between ouble-quotes.
	 * @param text the text to convert.
	 * @param attribute true if the text is to be an XML attribute value.
	 * @return the escaped text.
	 */
	static String toXML (String text,
		boolean attribute)
	{
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		if ( attribute ) {
			text = text.replace("\"", "&quot;");
		}
		return text;
	}

	static String toSafeXML (String text) {
		// In XML 1.0 the valid characters are:
		// #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			int cp = text.codePointAt(i);
			switch ( cp ) {
			case '&':
				tmp.append("&amp;");
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case 0x0009:
			case 0x000A:
			case 0x000D:
				tmp.append((char)cp);
				continue;
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
				continue;
			}
		}
		return tmp.toString();
	}
}
