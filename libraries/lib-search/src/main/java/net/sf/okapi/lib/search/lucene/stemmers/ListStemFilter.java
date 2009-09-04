package net.sf.okapi.lib.search.lucene.stemmers;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or lied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * Transforms the token stream as per the Porter stemming algorithm. Note: the
 * input to the stemming filter must already be in lower case, so you will need
 * to use LowerCaseFilter or LowerCaseTokenizer farther down the Tokenizer chain
 * in order for this to work properly!
 * <P>
 * To use this filter with other analyzers, you'll want to write an Analyzer
 * class that sets up the TokenStream chain as you want it. To use this with
 * LowerCaseTokenizer, for example, you'd write an analyzer like this:
 * <P>
 * 
 * <PRE>
 * class MyAnalyzer extends Analyzer {
 * 	public final TokenStream tokenStream(String fieldName, Reader reader) {
 * 		return new PorterStemFilter(new LowerCaseTokenizer(reader));
 * 	}
 * }
 * </PRE>
 */
public final class ListStemFilter extends TokenFilter {
	private PorterRockwellStemmer stemmer;
	private EnglishListStemmer steamer;

	public ListStemFilter(TokenStream in, EnglishListStemmer st,
			boolean isEnglish) {
		super(in);
		if (isEnglish)
			stemmer = new PorterRockwellStemmer();
		else
			stemmer = null;
		steamer = st;
	}

	/** Returns the next input Token, after being stemmed */
	public final org.apache.lucene.analysis.Token next() throws IOException {
		org.apache.lucene.analysis.Token token = input.next();
		if (token == null)
			return null;
		else {
			String s = steamer.getBaseForm(token.termText()); // DWH 3-3-06
			if (s.equals("")) // DWH 3-14-06 didn't find it, so use Porter
			// Stemmer
			{
				if (stemmer != null) // DWH 12-7-06
					s = stemmer.stem(token.termText()); // DWH 3-3-06 default to
				// Porter Stemmer
				if (s != token.termText()) // Yes, I mean object reference
					// comparison here
					token.setTermText(s);
			} else if (s != token.termText()) // Yes, I mean object reference
				// comparison here
				token.setTermText(s);
			return token;
		}
	}
}
