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
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class SimpleTermExtractor {

	Parameters params;
	private HashMap<String, Boolean> stopWords;
	private HashMap<String, Boolean> noStartWords;
	private HashMap<String, Boolean> noEndWords;
	private HashMap<String, Integer> terms;
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
		terms = new HashMap<String, Integer>();
		breaker = null;
	}
	
	public void processTextUnit (TextUnit tu) {
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		
		// Get the list of words
		TokensAnnotation annot = tu.getAnnotation(TokensAnnotation.class);
		List<String> words = null;
		
		// Tokens annotation are set at the text container level
		if ( annot == null ) {
			words = getWordsFromDefaultBreaker(tu.getSource());
		}
		else { // Use the existing tokens if they are available
			//TODO
			Tokens tokens = annot.getFilteredList("WORDS");
		}

		// Gather the terms
		String term;
		for ( int i=0; i<words.size(); i++ ) {
			// Skip words too short
			if ( words.get(i).length() < 2 ) continue;
			// Skip stop words
			if ( stopWords.containsKey(words.get(i)) ) continue;

			term = "";
			for ( int j=0; j<params.getMaxWordsPerTerm(); j++ ) {
				// Check we don't go outside the array
				if ( i+j >= words.size() ) continue;
				if ( words.get(i+j).length() == 0 ) continue;

				// Stop at stop words
				if ( stopWords.containsKey(words.get(i+j)) ) {
					j = params.getMaxWordsPerTerm()+1; // Stop here
					continue;
				}

				// Do not include terms starting on a no-start word
				if ( j == 0 ) {
					if ( noStartWords.containsKey(words.get(i+j)) ) {
						j = params.getMaxWordsPerTerm()+1; // Stop here
						continue;
					}
				}

				if ( j > 0 ) term += " ";
				term += words.get(i+j);

				// Do not include term with less than m_nMinWords
				if ( j+1 < params.getMinWordsPerTerm() ) continue;
				// But continue to build the term with more words

				// Do not include terms ending on a no-end word
				if ( noEndWords.containsKey(words.get(i+j)) ) continue;
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
		//TODO
	}
	
	public Map<String, Integer> getTerms () {
		return terms;
	}
	
	public List<String> getWordsFromDefaultBreaker (TextContainer tc) {
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
		
		if ( breaker == null ) {
			breaker = BreakIterator.getWordInstance(srcLocale);
		}
		breaker.setText(content);
		ArrayList<String> words = new ArrayList<String>();
		int start = breaker.first();
		for ( int end=breaker.next(); end!=BreakIterator.DONE; start=end, end=breaker.next()) {
			String word = content.substring(start, end);
			if ( word.length() < 2 ) continue;
			if ( !Character.isLetter(word.codePointAt(0)) ) continue;
			// Add the word (and preserve or not the case)
			if ( params.getKeepCase() ) {
				words.add(word);
			}
			else {
				words.add(word.toLowerCase());
			}
		}
//		// Clean endings
//		for ( int i=0; i<words.length; i++ ) {
//			//TODO
//			//words[i] = words[i]..trim(":;,?.{}()[]@#$%^&*\n\r<>");
//		}
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
