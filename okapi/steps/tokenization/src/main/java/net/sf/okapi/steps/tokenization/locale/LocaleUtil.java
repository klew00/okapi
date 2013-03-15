/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.locale;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.util.ULocale;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;

public class LocaleUtil {
	
	/**
	 * Converts the language tag (language ID and an optional region/country part) to the Okapi format: lower-case, delimited with a dash.
	 * @param languageCode
	 * @return languageCode in Okapi format
	 */
	public static String normalizeLanguageCode_Okapi(String languageCode) {
		if (Util.isEmpty(languageCode))
			return null;
		
		String[] parts = LocaleId.splitLanguageCode(languageCode);		
		StringBuilder res = new StringBuilder();
		
		res.append(parts[0].toLowerCase());  
		if (!Util.isEmpty(parts[1])) {
			res.append("-");
			res.append(parts[1].toLowerCase());
		}
		return res.toString();
	}
	
	/**
	 * Converts a list of language tags (language ID and an optional region/country part) to the Okapi format: upper case, delimited with a dash.
	 * @param languageCodes List of language codes to normalize.
	 * @return A new list, containing language codes in Okapi format.
	 */
	public static List<String> normalizeLanguageCodes_Okapi(List<String> languageCodes) {
		
		if (languageCodes == null) return null;
		
		List<String> res = new ArrayList<String>();
		
		for (String languageCode : languageCodes)
			res.add(normalizeLanguageCode_Okapi(languageCode));
		
		return res;
	}
	
	/**
	 * Converts the language tag (language ID and an optional region/country part) to the ICU format: lower case for the language,
	 * upper case for the region, parts are delimited with an underscore.
	 * @param languageCode
	 * @return languageCode in ICU format
	 */
	public static String normalizeLanguageCode_ICU(String languageCode) {		
		if (Util.isEmpty(languageCode))
			return null;
		
//		String[] parts = Util.splitLanguageCode(languageCode);		
//		StringBuilder res = new StringBuilder();
//		
//		res.append(parts[0].toLowerCase());  
//		if (!Util.isEmpty(parts[1])) {
//			res.append("_");
//			res.append(parts[1].toUpperCase());
//		}
//		return res.toString();
		
		return ULocale.canonicalize(languageCode);
	}

}
