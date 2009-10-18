/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods to manipulate lists.
 */
public class ListUtil {

	protected static final Logger logger = Logger.getLogger(ClassUtil.getClassName(ListUtil.class));
	
	/**
	 * Splits up a string of comma-separated substrings into a string list of those substrings.
	 * @param st string of comma-separated substrings. 
	 * @return a list of substrings.
	 */
	public static List<String> stringAsList(String st) {
		return listTrimValues(stringAsList(st, ","));		
	}
	
//	/**
//	 * Converts an array of string representing languages into a list of languages.
//	 * @param array the array of strings to convert.
//	 * @return a list of languages for the given strings.
//	 */
//	public static List<LocaleId> stringArrayAsLanguageList (String[] array) {
//		List<LocaleId> list = new ArrayList<LocaleId>();
//		for ( int i=0; i<array.length; i++ ) {			
//			list.add(LocaleId.fromString(array[i]));
//		}
//		return list;
//	}

	/**
	 * Splits up a string of comma-separated substrings representing language codes into a string list of languages.
	 * @param st string of comma-separated substrings. 
	 * @return a list of languages.
	 */
	public static List<LocaleId> stringAsLanguageList (String input) {
		if ( input == null ) return null;
		List<LocaleId> res = new ArrayList<LocaleId>();
		List<String> list = new ArrayList<String>();
		stringAsList(list, input, ",");
		for ( String lang : list ) {
			lang = lang.trim();
			if ( !Util.isEmpty(lang) ) {
				res.add(LocaleId.fromString(lang));
			}
		}
		return res;
	}

//	/**
//	 * Converts a list of languages into an array of strings.
//	 * @param list List of languages.
//	 * @return an array of strings for the given languages.
//	 */
//	public static String[] languageListAsStringArray (List<LocaleId> list) {
//		String[] res = new String[list.size()];
//		for ( int i=0; i<list.size(); i++ ) {			
//			res[i] = list.get(i).toString();
//		}
//		return res;
//	}
	
//	/**
//	 * Creates a string output for a list of languages. The language identifiers
//	 * are separated by commas.
//	 * @param list the list of languages to convert.
//	 * @return the output string.
//	 */
//	public static String languageListAsString (List<LocaleId> list) {
//		if ( list == null ) return "";
//		StringBuilder tmp = new StringBuilder();
//		for ( int i=0; i<list.size(); i++ ) {
//			if ( i > 0 ) {
//				tmp.append(",");
//			}
//			tmp.append(list.get(i).toString());
//		}
//		return tmp.toString();
//	}
	
	/**
	 * Splits up a string of comma-separated substrings into a string list of those substrings.
	 * @param list a list to put the substrings.
	 * @param st string of comma-separated substrings. 
	 */
	public static void stringAsList(List<String> list, String st) {
		stringAsList(list, st, ",");
		listTrimValues(list);
	}
	
	/**
	 * Splits up a string of delimited substrings into a string list of those substrings.
	 * @param st string of delimited substrings.
	 * @param delimiter a string delimiting substrings in the string. 
	 * @return a list of substrings.
	 */
	public static List<String> stringAsList(String st, String delimiter) {

		ArrayList<String> res = new ArrayList<String>();
		if (res == null) return null;

		stringAsList(res, st, delimiter);		
		return res;		
	}
	
	/**
	 * Splits up a string of delimited substrings into a string list of those substrings.
	 * @param list a list to put the substrings.
	 * @param st string of delimited substrings.
	 * @param delimiter a string delimiting substrings in the string.	  
	 */
	public static void stringAsList(List<String> list, String st, String delimiter) {
		if (Util.isEmpty(st)) return;
		if (list == null) return;
		
		list.clear();
		
		if (Util.isEmpty(delimiter)) {
						
			list.add(st);
			return;
		}
	
		int start = 0;
		int len = delimiter.length();
		
		while (true) {
			
			int index = st.substring(start).indexOf(delimiter);
			if (index == -1) break;
			
			list.add(st.substring(start, start + index));
			start += index + len;
		}
		
		if (start <= st.length())
			list.add(st.substring(start, st.length()));		
	}
	
	/**
	 * Splits up a string of comma-separated substrings into an array of those substrings.
	 * @param st string of comma-separated substrings.
	 * @return the generated array of strings.
	 */
	public static String[] stringAsArray(String st) {
		List<String> list = stringAsList(st);
		
		if (Util.isEmpty(list))
			return new String[] {};
		
		return (String[]) list.toArray(new String[] {});
	}
	
	/**
	 * Splits up a string of comma-separated substrings into an array of those substrings.
	 * @param st string of comma-separated substrings.
	 * @param delimiter a string delimiting substrings in the string.
	 * @return the generated array of strings.
	 */
	public static String[] stringAsArray(String st, String delimiter) {
		List<String> list = stringAsList(st, delimiter);
		
		if (Util.isEmpty(list))
			return new String[] {};

		return (String[]) list.toArray(new String[] {});
	}
	
	/**
	 * Converts a string of comma-separated numbers into a list of integers.
	 * @param st string of comma-separated numbers. 
	 * @return a list of integers.
	 */
	public static List<Integer> stringAsIntList (String st) {
		return stringAsIntList(st, ",");
	}
	
	/**
	 * Converts a string of comma-separated numbers into a list of integers and sorts the list ascendantly.
	 * @param st string of comma-separated numbers 
	 * @param delimiter a string delimiting numbers in the string
	 * @return a list of integers 
	 */
	public static List<Integer> stringAsIntList(String st, String delimiter) {
		return stringAsIntList(st, delimiter, false);
	}		
	
	/**
	 * Converts a string of comma-separated numbers into a list of integers.
	 * @param st string of comma-separated numbers 
	 * @param delimiter a string delimiting numbers in the string
	 * @param sortList if the numbers in the resulting list should be sorted (ascendantly)
	 * @return a list of integers 
	 */
	public static List<Integer> stringAsIntList(String st, String delimiter, boolean sortList) {
		List<Integer> res = new ArrayList<Integer>(); // Always create the list event if input string is empty
		if (Util.isEmpty(st)) return res;
		
		String[] parts = st.split(delimiter);
		for (String part : parts) {
			
			if (Util.isEmpty(part.trim()))
				res.add(0);
			else
				res.add(Integer.valueOf(part.trim()));
		}
		if (sortList) Collections.sort(res);
		return res;
	}
	
	/**
	 * Remove empty trailing elements of the given list.
	 * Possible empty elements in the head and middle of the list remain if located before a non-empty element.
	 * @param list the list to be trimmed.
	 */
	public static void listTrimTrail(List<String> list) {
	
		if (list == null) return;

		for (int i = list.size() -1; i >= 0; i--) 
			if (Util.isEmpty(list.get(i))) 
				list.remove(i);
			else
				break;
	}
	
	/**
	 * Trim all values of the given list. Empty elements remain on the list, non-empty are trimmed from both sides.
	 * @param list
	 * @return the list with trimmed elements.
	 */
	//TODO: javadoc
	public static List<String> listTrimValues(List<String> list) {
		if ( list == null ) return null;
		List<String> res = new ArrayList<String>();
		
		for (String st : list)
			if (Util.isEmpty(st)) 
				res.add(st);
			else
				res.add(st.trim());
			
		return res;
	}

	/**
	 * Converts a list of strings into an array of those strings.
	 * @param list List of strings.
	 * @return an array of strings.
	 */
	public static String[] listAsArray(List<String> list) {
		if (Util.isEmpty(list))
			return new String[] {};
		
		return (String[]) list.toArray(new String[] {});
	}
	
	//TODO: javadoc
	public static <E> List<E> arrayAsList(E[] array) {
		//return Arrays.asList(array); // Fixed size, no clear() etc. possible
		List<E> list = new ArrayList<E>();
		
		for (int i = 0; i < array.length; i++)			
			list.add(array[i]);
		
		return list;
	}
	
	//TODO: javadoc
	public static String arrayAsString(String[] array) {
		return arrayAsString(array, ",");
	}
	
	//TODO: javadoc
	public static String arrayAsString(String[] array, String delimiter) {
		return listAsString(Arrays.asList(array), delimiter);
	}
	
	//TODO: javadoc
	public static String listAsString(List<String> list) {
		return listAsString(list, ",");
	}
	
	//TODO: javadoc
	public static String listAsString(List<String> list, String delimiter) {
		if (list == null) return "";
		String res = "";
		
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) 
				res = res + delimiter + list.get(i);
			else
				res = list.get(i);			
		}
		
		return res;
	}
	
	//TODO: javadoc
	public static String intListAsString(List<Integer> list) {
		return intListAsString(list, ",");
	}
	
	//TODO: javadoc
	public static String intListAsString(List<Integer> list, String delimiter) {
		List<String> stList = new ArrayList<String>();
		for (Integer value : list) {
			stList.add(Util.intToStr(value));
		}
		return listAsString(stList, delimiter);
	}

	//TODO: javadoc
	public static <E> void remove(List<E> list, int start, int end) {
		
		if (list == null) return;
		if (Util.isEmpty(list)) return;
		
		//for (int i = start; (i >= 0 && i < end && i < list.size()); i++)
		for (int i = start; (i < end); i++)
			list.remove(start);
			
	}

	@SuppressWarnings("unchecked") 
	//TODO: javadoc
	public static <E> List<E> copyItems(List<E> list, int start, int end) {
		// No way to determine the actual type of E at compile time to cast newInstance(), so @SuppressWarnings("unchecked") 
	
		if (list == null) return null;
		if (Util.isEmpty(list)) return null;
		if (list.getClass() == null) return null;
		
		List<E> res = null;
			try {
				res = list.getClass().newInstance();
				
			} catch (InstantiationException e) {
				
				logMessage(Level.FINE, "List instantiation failed in ListUtil.copyItems(): " + e.getMessage());
				return null;
				
			} catch (IllegalAccessException e) {
				
				logMessage(Level.FINE, "List instantiation failed in ListUtil.copyItems(): " + e.getMessage());
				return null;
			}
			
			res.addAll(list.subList(start, end + 1));		
					
		return res;
	}
		
	//TODO: javadoc
	public static <E> List<E> moveItems(List<E> buffer, int start, int end) {
	
		List<E> res = copyItems(buffer, start, end);
		if (res == null) return null;
		
		buffer.subList(start, end + 1).clear();
		
		return res;
	}
	
	//TODO: javadoc
	public static <E> List<E> moveItems(List<E> buffer) {
		
		List<E> res = copyItems(buffer, 0, buffer.size() - 1);
		if (res == null) return null;
		
		buffer.clear();
		
		return res;
	}
	
	//TODO: javadoc
	public static <E> E getFirstNonNullItem(List<E> list) {
		
		if (Util.isEmpty(list)) return null;

		for (E item : list)			
			if (item != null) return item;

		return null;		
	}
	
	//TODO: javadoc
	protected static void logMessage (Level level, String text) {
		
		if (logger != null)
			logger.log(level, text);
	}

	//TODO: javadoc
	public static List<String> loadList(Class<?> classRef, String resourceLocation) {
		
		List<String> res = new ArrayList<String>();
		
		loadList(res, classRef, resourceLocation);		
		return res;
	}
	
	//TODO: javadoc
	public static void loadList(List<String> list, Class<?> classRef, String resourceLocation) {

		if (list == null) return;
		if (classRef == null) return;
		if (Util.isEmpty(resourceLocation)) return;
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(classRef.getResourceAsStream(resourceLocation), "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			
			logMessage(Level.FINE, String.format("ListUtil.loadList() encoding problem of \"%s\": %s", resourceLocation, e.getMessage()));
			return;
		}
		
		try {
			while (reader.ready()) {
				
				String line = reader.readLine();
				if (line == null) break;
				if (Util.isEmpty(line)) continue;
				
				if (!line.startsWith("#"))
					list.add(line);
			}
		} catch (IOException e) {
			
			logMessage(Level.FINE, String.format("ListUtil.loadList() IO problem of \"%s\": %s", resourceLocation, e.getMessage()));
			return;
		}	
	}
	
}
