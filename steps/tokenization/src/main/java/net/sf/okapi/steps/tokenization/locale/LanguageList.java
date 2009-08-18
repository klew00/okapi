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
import java.util.TreeMap;

import net.sf.okapi.filters.plaintext.common.StringUtils;

import com.ibm.icu.util.ULocale;

public class LanguageList {

	private static TreeMap<String, ULocale> map = new TreeMap<String, ULocale>();
	
	static {
		ULocale[] locales = ULocale.getAvailableLocales();
		
		for (ULocale locale : locales)			
			map.put(formatLanguageInfo(locale), locale);
	}
	
	protected static String formatLanguageInfo(ULocale locale) {
		
		if (locale == null) return "";
		return StringUtils.titleCase(locale.getDisplayName());
	}
	
	public static String[] getLanguages() {
		
		return map.keySet().toArray(new String[] {});
	}
	
	public static String[] getLanguageCodes_Okapi() {
		
		ArrayList<String> codes = new ArrayList<String> ();

		for (ULocale locale : map.values()) {
			
			codes.add(LocaleUtils.normalizeLanguageCode_Okapi(locale.getName()));
		}
		
		return codes.toArray(new String[] {});
	}
	
	public static String[] getLanguageCodes_ICU() {
		
		ArrayList<String> codes = new ArrayList<String> ();

		for (ULocale locale : map.values()) {
			
			codes.add(locale.getName());
		}
		
		return codes.toArray(new String[] {});
	}
	
	public static ULocale getLocale(String languageInfo) {
		
		return map.get(languageInfo);
	}
}
