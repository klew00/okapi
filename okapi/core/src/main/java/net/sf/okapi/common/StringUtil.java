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

package net.sf.okapi.common;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.Util;

/**
 * Helper methods to manipulate strings.
 */
public class StringUtil {

	// String formatting
	
	/**
	 * Returns a title-case representation of a given string. The first character is capitalized, following
	 * characters are in lower case.
	 * @param st the give string.
	 * @return a copy of the given string normalized to the title case. 
	 */
	public static String titleCase(String st) {
		if (Util.isEmpty(st)) 
			return st;
		
		return st.substring(0,1).toUpperCase() + st.substring(1).toLowerCase();
	}
	
	/**
	 * Removes qualifiers (quotation marks etc.) around text in a given string. 
	 * @param st the given string.
	 * @param startQualifier the qualifier to be removed before the given string.
	 * @param endQualifier the qualifier to be removed after the given string.
	 * @return a copy of the given string without qualifiers.
	 */
	public static String removeQualifiers(String st, String startQualifier, String endQualifier) {
	
		if (Util.isEmpty(st)) return st; 
		if (Util.isEmpty(startQualifier)) return st;
		if (Util.isEmpty(endQualifier)) return st;
		
		int startQualifierLen = startQualifier.length();
		int endQualifierLen = endQualifier.length();
		
		if (st.startsWith(startQualifier) && st.endsWith(endQualifier))
			return st.substring(startQualifierLen, Util.getLength(st) - endQualifierLen);
			
		return st;
	}
	
	/**
	 * Removes qualifiers (quotation marks etc.) around text in a given string. 
	 * @param st the given string.
	 * @param qualifier the qualifier to be removed before and after text in the string.
	 * @return a copy of the given string without qualifiers.
	 */
	public static String removeQualifiers(String st, String qualifier) {
	
		if (Util.isEmpty(st) || Util.isEmpty(qualifier))
			return st;
		
		int qualifierLen = qualifier.length();
		
		if (st.startsWith(qualifier) && st.endsWith(qualifier))
			return st.substring(qualifierLen, Util.getLength(st) - qualifierLen);
			
		return st;
	}
	
	/**
	 * Removes quotation marks around text in a given string. 
	 * @param st the given string.
	 * @return a copy of the given string without quotation marks.
	 */
	public static String removeQualifiers(String st) {
	
		return removeQualifiers(st, "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Converts line breaks in a given string to the Unix standard (\n).
	 * @param string the given string.
	 * @return a copy of the given string, all line breaks are \n.
	 */
	public static String normalizeLineBreaks(String string) {
		
		String res = string;
		
		if (!Util.isEmpty(res)) {
		
			res = res.replaceAll("\r\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			res = res.replace("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			res = res.replace("\r", "\n");  //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return res;
	}

	private static boolean checkRegex(String regex) {
		
		try {			
			Pattern.compile(regex);		
		} 
		catch (PatternSyntaxException e) {			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Converts shell wildcards (e.g. * and ?) in a given string to its Java regex representation.
	 * @param string the given string.
	 * @return a copy of the given string, all wildcards are converted into a correct Java regular expression. 
	 * The result is checked for being a correct regex pattern. If it is not, then the given original string is returned as
	 * being most likely already a regex pattern.
	 */
	public static String normalizeWildcards(String string) {

		if (Util.isEmpty(string)) return string;
		if (!containsWildcards(string)) return string;
						
		String normalized = string.replaceAll("\\?", ".").replaceAll("\\*", ".*?");
		
		// Make sure we're not normalizing a correct regex thus damaging it
		return checkRegex(normalized) ? normalized : string;
	}
	
	/**
	 * Detects if a given string contains shell wildcard characters (e.g. * and ?).
	 * @param string the given string.
	 * @return true if the string contains the asterisk (*) or question mark (?).
	 */
	public static boolean containsWildcards(String string) {
	
		if (Util.isEmpty(string)) return false;
	
		return string.indexOf('*') != -1 || string.indexOf('?') != -1;
	}

	public static String[] split(String string, String delimRegex, int group) {
		
		String delimPlaceholder = "<delimiter>";
		
		string = RegexUtil.replaceAll(string, delimRegex, group, delimPlaceholder);
		return ListUtil.stringAsArray(string, delimPlaceholder);
	}
	
	public static String[] split(String string, String delimRegex) {
		
		return split(string, delimRegex, 0);
	}
	
	/**
	 * Returns a number of occurrences of a given substring in a given string.
	 * @param str the given string.
	 * @param substr the given substring being sought.
	 * @return the number of occurrences of the substring in the string.
	 */
	public static int getNumOccurrences(String str, String substr){
		
	    String temp = str;
		int count = 0;
		int i = temp.indexOf(substr);
		
		while(i >= 0){
			
		    count++;
		    temp = temp.substring(i + 1);
		    i = temp.indexOf(substr);
		}
		
		return count;
	}
	
}
