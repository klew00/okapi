/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.termextraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.icu.text.BreakIterator;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class SimpleTermExtractor {

	Parameters params;
	private Map<String, Boolean> stopWords;
	private Map<String, Boolean> noStartWords;
	private Map<String, Boolean> noEndWords;
	private Map<String, Integer> terms;
	private Locale srcLocale;
	private BreakIterator breaker;
	
	public void initialize (Parameters params,
		LocaleId sourceLocaleId)
	{
		this.srcLocale = sourceLocaleId.toJavaLocale();
		this.params = params;
		stopWords = loadList(params.getStopWordsPath(), "stopWords_en.txt");
		noStartWords = loadList(params.getNoStartWordsPath(), "noStartWords_en.txt");
		noEndWords = loadList(params.getNoEndWordsPath(), "noEndWords_en.txt");
		terms = new LinkedHashMap<String, Integer>();
		breaker = null;
	}
	
	public void processTextUnit (TextUnit tu) {
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		
		// Get the list of words
		TokensAnnotation annot = tu.getAnnotation(TokensAnnotation.class);
		List<String> words = null;
		
		// Get the "words" to use for the extraction.
		// First try to use the TokensAnnotation if one is present
		if ( annot != null ) {
			Tokens tokens = annot.getFilteredList("WORD", "KANA", "IDEOGRAM");
			words = new ArrayList<String>();
			for ( Token token : tokens ) {
				addWord(words, token.getValue());
			}
		}
		else { // If no annotation is available: use the default word-breaker
			words = getWordsFromDefaultBreaker(tu.getSource());
		}

		// Gather the term candidates
		String term;
		for ( int i=0; i<words.size(); i++ ) {
			// Skip stop words
			if ( stopWords.containsKey(words.get(i)) ) continue;
			// Start term candidate
			term = "";
			for ( int j=0; j<params.getMaxWordsPerTerm(); j++ ) {
				// Check we don't go outside the array
				if ( i+j >= words.size() ) continue;
				String word = words.get(i+j);
				// Not needed, no word should be empty at this point: if ( word.length() == 0 ) continue;

				// Stop at stop words
				if ( stopWords.containsKey(word) ) {
					j = params.getMaxWordsPerTerm()+1; // Stop here
					continue;
				}

				// Do not include terms starting on a no-start word
				if ( j == 0 ) {
					if ( noStartWords.containsKey(word) ) {
						j = params.getMaxWordsPerTerm()+1; // Stop here
						continue;
					}
				}
				// Add space if needed
				if ( j > 0 ) {
					String sep = " ";
					// Check the last character of the term
					if ( term.charAt(term.length()-1) > 0x0700 ) {
						// If it is OTHER_LETTER above U+700 it's likely to be CJK, Thai, Devanagari, etc.
						switch ( Character.getType(term.charAt(term.length()-1)) ) {
						case Character.OTHER_LETTER:
							sep = ""; // No space separation for those
							break;
						}
					}
					term += sep;
				}
				term += word;

				// Do not include term with less than m_nMinWords
				if ( j+1 < params.getMinWordsPerTerm() ) continue;
				// But continue to build the term with more words

				// Do not include terms ending on a no-end word
				if ( noEndWords.containsKey(word) ) continue;
				// But continue to build the term with more words

				// Add or increment the term
				if ( terms.containsKey(term) ) {
					terms.put(term, terms.get(term)+1);
				}
				else {
					terms.put(term, 1);
					//count++;
				}
			}
		}
	}
	
	public void completeExtraction () {
		// Remove entries with less occurrences than allowed
		Iterator<Entry<String, Integer>> iter = terms.entrySet().iterator();
		while ( iter.hasNext() ) {
			Entry<String, Integer> entry = iter.next();
			if ( entry.getValue() < params.getMinOccurrences() ) {
				iter.remove();
			}
		}

		// Sort by frequency if requested
		//TODO
		
		// Output the report
		PrintWriter writer = null;
		try {
			Util.createDirectories(params.getOutputPath());
			writer = new PrintWriter(params.getOutputPath(), "UTF-8");
			for ( Entry<String, Integer> entry : terms.entrySet() ) {
				writer.println(String.format("%d\t%s", entry.getValue(), entry.getKey()));
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error when writing output file.", e);
		}
		finally {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
	}
	
	public Map<String, Integer> getTerms () {
		return terms;
	}

	private void addWord (List<String> list,
		String token)
	{
		// No empty words and keep only extended single-char
		if (( token.length() == 0 )
			|| (( token.length() == 1 ) && ( token.codePointAt(0) < 126 ))) return;
		// Keep only "letters" (includes CJK characters)
		if ( !Character.isLetter(token.codePointAt(0)) ) return;
		
		// Add the word (and preserve or not the case)
		if ( params.getKeepCase() ) {
			list.add(token);
		}
		else {
			list.add(token.toLowerCase(srcLocale));
		}
	}

	public List<String> getWordsFromDefaultBreaker (TextContainer tc) {
		// Get the plain text to process
		String content;
		if ( tc.contentIsOneSegment() ) {
			content = TextUnitUtil.getText(tc.getFirstContent()); 
		}
		else {
			content = TextUnitUtil.getText(tc.getUnSegmentedContentCopy());
		}
		if ( content.length() == 0 ) {
			return Collections.emptyList();
		}

		// Break down the text into "words"
		if ( breaker == null ) {
			breaker = BreakIterator.getWordInstance(srcLocale);
		}
		breaker.setText(content);
		ArrayList<String> words = new ArrayList<String>();
		int start = breaker.first();
		for ( int end=breaker.next(); end!=BreakIterator.DONE; start=end, end=breaker.next()) {
			addWord(words, content.substring(start, end));
		}

		return words;
	}

	private HashMap<String, Boolean> loadList (String path,
		String defaultFile)
	{
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		BufferedReader reader = null;
		try {
			InputStream is;
			// Use the default resource if no path is provided
			if ( Util.isEmpty(path) ) {
				is = SimpleTermExtractor.class.getResourceAsStream(defaultFile);
			}
			else {
				is = new FileInputStream(path);
			}
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null ) {
				line = line.trim();
				if ( line.length() == 0 ) continue;
				if ( line.charAt(0) == '#' ) continue;
				// Add the word to the list, make sure we skip duplicate to avoid error
				if ( !map.containsKey(line) ) map.put(line, false);
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error reading word list.", e);
		}
		finally {
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch ( IOException e ) {
					throw new RuntimeException("Error reading word list.", e);
				}
			}
		}
		return map;
	}

}
