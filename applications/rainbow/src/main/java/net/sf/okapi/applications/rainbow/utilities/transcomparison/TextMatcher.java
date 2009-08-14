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

package net.sf.okapi.applications.rainbow.utilities.transcomparison;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.okapi.common.resource.TextFragment;

public class TextMatcher {

	public static final int IGNORE_CASE          = 0x01;
	public static final int IGNORE_WHITESPACES   = 0x02;
	public static final int IGNORE_PUNCTUATION   = 0x04;
	
	private static final int MAXTOKEN = 1024;
	
	private static short[] matP = null; // 'previous' cost array, horizontally
	private static short[] matD = null; // cost array, horizontally

	private BreakIterator breaker1;
	private BreakIterator breaker2;

	/**
	 * Creates a new TextMatcher object.
	 * @param language1 Language code of the first language.
	 * @param language2 Language code of the second language.
	 */
	public TextMatcher (String language1,
		String language2)
	{
		// Get the locale to use.
		Locale loc;
		String[] parts = net.sf.okapi.common.Util.splitLanguageCode(language1);
		if ( parts[1].length() > 0 ) loc = new Locale(parts[0], parts[1]);
		else loc = new Locale(parts[0]);

		// Create the first breaker.
		breaker1 = BreakIterator.getWordInstance(loc);
		if ( language1.equals(language2) ) {
			// Use the same one if the second language is the same.
			breaker2 = breaker1;
		}
		else {
			// If two different languages: create a second breaker.
			parts = net.sf.okapi.common.Util.splitLanguageCode(language1);
			if ( parts[1].length() > 0 ) loc = new Locale(parts[0], parts[1]);
			else loc = new Locale(parts[0]);
			breaker2 = BreakIterator.getWordInstance(loc);
		}
	}
	
	protected static short minimum (int value1,
		int value2,
		int value3)
	{
		return (short)Math.min(value1, Math.min(value2, value3));
	}
	
	protected static int levenshtein (List<String> tokens1,
		List<String> tokens2)
	{
		int n = tokens1.size();
		int m = tokens2.size();
	
		if ( n == 0 ) return m;
		if ( m == 0 ) return n;

		// Create the array if needed
		// We use this to avoid re-creating the array each time
		if ( matP == null ) matP = new short[MAXTOKEN+1];
		if ( matD == null ) matD = new short[MAXTOKEN+1];

		// Artificial limitation due to static arrays
		if ( n > MAXTOKEN ) n = MAXTOKEN;
		if ( m > MAXTOKEN ) m = MAXTOKEN;
		short[] swap; // place-holder to assist in swapping p and d
	
		// Indexes into strings tokens and t
		int i; // Iterates through tokens1
		int j; // Iterates through t
	
		Object obj2j = null; // Object for p_aList2
		int cost; // Cost
	
		for ( i=0; i<=n; i++ ) matP[i] = (short)i;
		for ( j=1; j<=m; j++ ) {
			obj2j = tokens2.get(j-1);
			matD[0] = (short)j;
			Object obj1i = null; // Object for tokens1
			// Not used: object s_i2; // Object for list 2 (at i-1)
			for ( i=1; i<=n; i++ ) {
				obj1i = tokens1.get(i-1);
				cost = (obj1i.equals(obj2j) ? (short)0 : (short)1);
				// Minimum of cell to the left+1, to the top+1, diagonally left and up + cost
				matD[i] = minimum(matD[i-1]+1, matP[i]+1, matP[i-1]+cost);
			}
			// Copy current distance counts to 'previous row' distance counts
			swap = matP; matP = matD; matD = swap;
		}
	
		// The last action in the above loop was to switch d and p
		// so now p has actually the most recent cost counts
		int longest = Math.max(n, m);
		return (100*(longest-matP[n]))/longest;
	}

	/**
	 * Compare two textFragment content. 
	 * @param frag1 The base fragment.
	 * @param frag2 the fragment to compare against the base fragment.
	 * @param options Comparison options.
	 * @return A score between 0 (no match) and 100 (exact match).
	 */
	public int compare (TextFragment frag1,
		TextFragment frag2,
		int options)
	{
		String text1 = frag1.getCodedText();
		String text2 = frag2.getCodedText();
		
		// Check if it actually is exactly the same?
		if ( text1.equals(text2) ) return 100;
		// Check if there is only casing differences
		if ( text1.equalsIgnoreCase(text2) ) {
			return (((options & IGNORE_CASE) == IGNORE_CASE) ? 100 : 99);
		}

		// Break down into tokens
		List<String> tokens1;
		List<String> tokens2;
		
		if ( (options & IGNORE_PUNCTUATION) == IGNORE_PUNCTUATION ) {
			text1 = text1.replaceAll("\\p{Punct}", " ");
			text2 = text2.replaceAll("\\p{Punct}", " ");
		}
		
		if ( (options & IGNORE_CASE) == IGNORE_CASE ) {
			tokens1 = tokenize(text1.toLowerCase(), breaker1);
			tokens2 = tokenize(text2.toLowerCase(), breaker2);
		}
		else { // Keep case differences
			tokens1 = tokenize(text1, breaker1);
			tokens2 = tokenize(text2, breaker2);
		}

		int n = levenshtein(tokens1, tokens2);
		if ( n == 100 ) {
			// Differences are hidden tokenization
			return 99;
		}
		else return n;
	}

	/**
	 * Breaks the text into words (or equivalents).
	 * @param text The text to break down.
	 * @return The list of the "words" generated.
	 */
	private List<String> tokenize (String text,
		BreakIterator breakerToUse)
	{
		breakerToUse.setText(text);
		ArrayList<String> list = new ArrayList<String>();
		int start = breakerToUse.first();
		for (int end = breakerToUse.next(); end != BreakIterator.DONE; start=end, end=breakerToUse.next()) {
			list.add(text.substring(start,end));
		}
		return list;
	}

	/* ====== Old code
	private List<String> tokenize_OLD (String text) {
		int len = text.length();
		boolean isWord = false;
		StringBuilder token = new StringBuilder();
		int count = 0;
		ArrayList<String> list = new ArrayList<String>();

		for ( int i=0; i<len; i++ ) {
			char ch = text.charAt(i);
			if ( isWord ) {
				if ( Character.isLetter(ch) ) {
					token.append(ch);
				}
				else {
					count++;
					isWord = false;
					list.add(token.toString());
				}
			}
			else {
				if ( Character.isLetter(ch) ) {
					if ( isCJK(ch) ) {
						//TODO: Same for Thai, etc.
						count++;
						list.add(String.valueOf(ch));
					}
					else {
						isWord = true;
						token.setLength(0); // Reset for next token
						token.append(ch);
					}
				}
			}
		}
		if ( isWord ) {
			count++;
			list.add(token.toString());
		}
		return list;
	}
	=== */
	
	/**
	 * Checks if the given character is some-kind of CJK character
	 * @param value The character to lookup.
	 * @return True if it is recognized as a "CJK" character, false if not.
	 === Old code
	private boolean isCJK (char value) {
		// To go faster (most of the cases)
		if ( value < 0x1100 ) return false;
		// Otherwise: check CJK-related ranges (most likely to be used are listed first)
		// Hiragana U+3040 U+309F 
		if (( value >= 0x3040 ) || ( value <= 0x309F )) return true;
		// Katakana U+30A0 U+30FF 
		if (( value >= 0x30A0 ) || ( value <= 0x30FF )) return true;
		// Unified ideographs U+4E00 U+9FFF
		if (( value >= 0x4E00 ) || ( value <= 0x9FFF )) return true;
		// CJK Compatibility U+3300 U+33FF
		if (( value >= 0x3300 ) || ( value <= 0x33FF )) return true;
		// CJK Compatibility forms U+FE30 U+FE4F
		if (( value >= 0xFE30 ) || ( value <= 0xFE4F )) return true;
		// CJK Compatibility Ideographs U+F900 U+FAFF
		if (( value >= 0xF900 ) || ( value <= 0xFAFF )) return true;
		// CJK Radical Supplement U+2E80 U+2EFF
		if (( value >= 0x2E80 ) || ( value <= 0x2EFF )) return true;
		// CJK Symbols and Punctuations U+3000 U+303F
		if (( value >= 0x3000 ) || ( value <= 0x303F )) return true;
		// CJK Unified Ideographs Extension A U+3400 U+4DBF
		if (( value >= 0x3400 ) || ( value <= 0x4DBF )) return true;
		// Enclosed CJK Letters and Months U+3200 U+32FF
		if (( value >= 0x3200 ) || ( value <= 0x32FF )) return true;
		// Kanbun U+3190 U+319F
		if (( value >= 0x3190 ) || ( value <= 0x319F )) return true;
		// Katakana Phonetic Extension U+31F0 U+31FF
		if (( value >= 0x31F0 ) || ( value <= 0x31FF )) return true;
		// Bopomofo U+3100 U+312F
		if (( value >= 0x3100 ) || ( value <= 0x312F )) return true;
		// Bopomofo Extended U+31A0 U+31BF 
		if (( value >= 0x31A0 ) || ( value <= 0x31BF )) return true;
		// Kangxi radicals U+2F00 U+2FDF
		if (( value >= 0x2F00 ) || ( value <= 0x2FDF )) return true;
		// Hangul compatibility jamo U+3130 U+318F
		if (( value >= 0x3130 ) || ( value <= 0x318F )) return true;
		// Hangul jamo U+1100 U+11FF
		if (( value >= 0x1100 ) || ( value <= 0x11FF )) return true;
		// Hangul syllables U+AC00 U+D7AF
		if (( value >= 0xAC00 ) || ( value <= 0xD7AF )) return true;
		// Default
		return false;
	}
	=== */

}
