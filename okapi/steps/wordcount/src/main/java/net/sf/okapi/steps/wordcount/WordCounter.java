/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.steps.tokenization.Tokenizer;
import net.sf.okapi.steps.tokenization.tokens.Tokens;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;
import net.sf.okapi.steps.wordcount.common.StructureParameters;

/**
 * Word Count engine. Contains static methods to calculate number of words in a given text fragment. 
 * 
 * @version 0.1 07.07.2009
 */

public class WordCounter extends BaseCounter {

	private static StructureParameters params;
	
	protected static void loadParameters() {
		
		if (params != null) return; // Already loaded
		
		params = new StructureParameters();
		if (params == null) return;
		
		params.loadFromResource("word_counter.tprm");
	}
	
	@Override
	protected long doCount(String text, LocaleId language) {
		
		Tokens tokens = Tokenizer.tokenize(text, language, getTokenName());		
		if (tokens == null) return 0;
		
		// DEBUG
//		System.out.println(String.format("Tokens: %d (%s)", tokens.size(), text));
//		System.out.println();
//		System.out.println(tokens.toString());
//		System.out.println();
		
		return tokens.size();
	}

	/**
	 * Counts words in the source part of a given text unit.
	 * @param textUnit the given text unit
	 * @param language the language of the source
	 * @return number of words
	 */	
	public static long count(ITextUnit textUnit, LocaleId language) {
		return count(WordCounter.class, textUnit, language);		
	}
	
	/**
	 * Counts words in a given text container.
	 * @param textContainer the given text container
	 * @param language the language of the text
	 * @return number of words
	 */
	public static long count(TextContainer textContainer, LocaleId language) {
		return count(WordCounter.class, textContainer, language);		
	}
	
	/**
	 * Counts words in a given segment.
	 * @param segment the given segment
	 * @param language the language of the text
	 * @return number of words
	 */
	public static long count(Segment segment, LocaleId language) {
		return count(WordCounter.class, segment, language);		
	}

	/**
	 * Counts words in a given text fragment.
	 * @param textFragment the given text fragment
	 * @param language the language of the text
	 * @return number of words
	 */
	public static long count(TextFragment textFragment, LocaleId language) {
		return count(WordCounter.class, textFragment, language);		
	}
	
	/**
	 * Counts words in a given string.
	 * @param string the given string
	 * @param language the language of the text
	 * @return number of words
	 */
	public static long count(String string, LocaleId language) {
		return count(WordCounter.class, string, language);		
	}
	
	/**
	 * Returns the word count information stored by WordCountStep in annotations of a given resource. 
	 * @param tu the given resource
	 * @return number of words (0 if no word count information found)
	 */
	public static long getCount(IResource res) {
		return BaseCounter.getCount(res, GMX.TotalWordCount);
	}
	
	/**
	 * Returns the word count information stored by WordCountStep in the source part of a given text unit. 
	 * @param tu the given text unit
	 * @return number of words (0 if no word count information found)
	 */
	public static long getCount(ITextUnit tu) {
		return BaseCounter.getCount(tu, GMX.TotalWordCount);
	}
	
	/**
	 * Returns the word count information stored by WordCountStep in the given text container. 
	 * @param tc the given text container
	 * @return number of words (0 if no word count information found)
	 */
	public static long getCount(TextContainer tc) {
		return BaseCounter.getCount(tc, GMX.TotalWordCount);
	}
	
	/**
	 * Returns the word count information stored by WordCountStep in a given segment of the source part of a given text unit.
	 * @param tu the given tu
	 * @param segIndex index of the segment in the source
	 * @return number of words (0 if no word count information found)
	 */
	public static long getCount(ITextUnit tu, int segIndex) {
		ISegments segments = tu.getSource().getSegments();
		return getCount(segments.get(segIndex));		
	}
	
	/**
	 * Returns the word count information stored by WordCountStep in a given segment of the source part of a given text unit.
	 * @param segment the given segment
	 * @return number of words (0 if no word count information found)
	 */
	public static long getCount(Segment segment) {
		return BaseCounter.getCount(segment, GMX.TotalWordCount);
	}
	
	public static String getTokenName() {		
		loadParameters();
		
		if (params == null) return "";
		return params.getTokenName();
	}
	
	public static void setCount(IResource res, long count) {
		MetricsAnnotation ma = res.getAnnotation(MetricsAnnotation.class);
		
		if (ma == null) {			
			ma = new MetricsAnnotation();
			res.setAnnotation(ma);
		}
		
		Metrics m = ma.getMetrics();		
		m.setMetric(GMX.TotalWordCount, count);
	}
}
