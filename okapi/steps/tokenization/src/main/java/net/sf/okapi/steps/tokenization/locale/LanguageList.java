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
import java.util.TreeMap;
import net.sf.okapi.common.StringUtil;
import com.ibm.icu.util.ULocale;

public class LanguageList {

	// The map keys are ICU locale ID's 
	private static TreeMap<String, ULocale> map = new TreeMap<String, ULocale>();
	private static final ULocale EN = new ULocale("en");
	
	static {
		ULocale[] locales = ULocale.getAvailableLocales();
		
		for (ULocale locale : locales)			
			if (locale != null)
				map.put(locale.getName(), locale);
	}
	
	protected static String formatLanguageInfo(ULocale locale) {		
		if (locale == null) return "";
		
		return locale.getDisplayName(EN);
	}
	
	public static String[] getLanguages() {
		
		ArrayList<String> res = new ArrayList<String> ();

		for (ULocale locale : map.values()) {
			
			res.add(formatLanguageInfo(locale));
		}
		
		return res.toArray(new String[] {});
	}
	
	public static List<String> getAllLanguages() {
		
		ArrayList<String> res = new ArrayList<String> ();

		for (ULocale locale : map.values()) {
			
			res.add(LocaleUtil.normalizeLanguageCode_Okapi(locale.getName()));
		}
		
		return res;
	}

	public static String[] getLanguageCodes_Okapi() {
		
//		ArrayList<String> res = new ArrayList<String> ();
//
//		for (ULocale locale : map.values()) {
//			
//			res.add(LocaleUtil.normalizeLanguageCode_Okapi(locale.getName()));
//		}
		
		List<String> res = getAllLanguages();
		if (res == null) return new String[] {};
		
		return res.toArray(new String[] {});
	}
	
	public static String[] getLanguageCodes_ICU() {
		
		ArrayList<String> res = new ArrayList<String> ();

		for (ULocale locale : map.values()) {
			
			res.add(locale.getName());
		}
		
		return res.toArray(new String[] {});
	}
	
	public static String getDisplayName(String code_Okapi) {
		
		String code_ICU = LocaleUtil.normalizeLanguageCode_ICU(code_Okapi);
		
		return formatLanguageInfo(map.get(code_ICU));
	}
	
}
