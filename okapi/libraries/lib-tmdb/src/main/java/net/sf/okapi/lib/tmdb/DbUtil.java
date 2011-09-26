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

package net.sf.okapi.lib.tmdb;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

public class DbUtil {
	
	public static final String LANG_SEP = "~";
	public static final String SEGKEY_NAME = "SegKey";
	public static final String FLAG_NAME = "Flag";
	public static final String TUREF_NAME = "TuRef";
	public static final String TEXT_PREFIX = ("Text"+LANG_SEP);
	public static final String CODES_PREFIX = ("Codes"+LANG_SEP);

	/**
	 * Cannot use fully static methods as this is used across threads.
	 */
	private final GenericContent fmt = new GenericContent();
	
	/**
	 * Gets the Olifant locale code for a given LocaleId object.
	 * @param locId the LocaleId to convert. The value must not be null or LocaleId.EMPTY.
	 * @return the Olifant locale code.
	 * @throws IllegalArgumentException if the given LocaleId is invalid.
	 */
	public static String toOlifantLocaleCode (LocaleId locId) {
		if ( locId == LocaleId.EMPTY ) {
			throw new IllegalArgumentException("Cannot use LocaleId.EMPTY");
		}
		String tmp = locId.toString();
		return tmp.toUpperCase().replace('-', '_');
	}

	/**
	 * Indicates if a given field is a segment-level field.
	 * @param name the full name of the field to check.
	 * @return true if the given field corresponds to a segment-level field,
	 * false otherwise, that is: it is a text unit-level field.
	 */
	public static boolean isSegmentField (String name) {
		return (( name.indexOf(DbUtil.LANG_SEP) != -1 )
			|| name.equalsIgnoreCase(SEGKEY_NAME)
			|| name.equalsIgnoreCase(TUREF_NAME)
			|| name.equalsIgnoreCase(FLAG_NAME)
		); 
	}

	/**
	 * Checks a potential field name to be used in Olifant.
	 * @param name the name to check.
	 * @return the valid name, usually un-changed.
	 * @throws IllegalArgumentException if the name is invalid.
	 */
	public static String checkFieldName (String name) {
		if (( name.indexOf(LANG_SEP) != -1 ) || ( name.indexOf('\'') != -1 )) {
			throw new IllegalArgumentException(String.format("The name of a field '%s' cannot have the character ''' or '%s'.",
				name, LANG_SEP));
		}
		return name;
	}

	/**
	 * Splits a text fragment into its generic coded text and a string holding the codes.
	 * @param frag the text fragment to process.
	 * @return An array of two strings:
	 * 0=the coded text, 1=the codes
	 */
	public String[] fragmentToTmFields (TextFragment frag) {
		String[] res = new String[2];
		res[0] = fmt.setContent(frag).toString();
		res[1] = Code.codesToString(frag.getCodes());
		return res;
	}
	
}
