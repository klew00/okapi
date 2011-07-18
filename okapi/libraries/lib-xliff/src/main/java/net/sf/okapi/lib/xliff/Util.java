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

class Util {

	final static String NS_XLIFF20 = "urn:oasis:names:tc:xliff:document:2.0";

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
	final static String ELEM_ORIGINALDATA = "originalData";
	final static String ELEM_DATA = "data";

	final static String ATTR_ID = "id";
	final static String ATTR_RID = "rid";
	final static String ATTR_NID = "nid";
	final static String ATTR_TYPE = "type";
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

}
