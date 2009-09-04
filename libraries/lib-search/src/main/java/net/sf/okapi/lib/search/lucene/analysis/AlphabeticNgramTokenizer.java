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

package net.sf.okapi.lib.search.lucene.analysis;

import java.io.Reader;
import java.util.Locale;

import org.apache.lucene.analysis.Tokenizer;

import com.ibm.icu.lang.UCharacter;

/**
 * Ngram tokenizer for all alphabetic languages - even languages with non-latin
 * alphabets.
 */
public class AlphabeticNgramTokenizer extends Tokenizer implements
		LdsTokenizer, FuzzyTokenizer {
	private static final Locale ARMENIAN = new Locale("hy");
	private static final Locale SINHALA = new Locale("si");

	private int ngramSize = 3; // default ngram size
	private int offset = -1;
	private char[] ngram;
	private Locale locale;

	/** Construct a new LetterTokenizer. */
	public AlphabeticNgramTokenizer(int p_ngramSize, Locale p_locale) {
		ngramSize = p_ngramSize;
		ngram = new char[ngramSize];
		locale = p_locale;
	}

	/** Construct a new LetterTokenizer. */
	public AlphabeticNgramTokenizer(Reader p_input) {
		super(p_input);
		ngram = new char[ngramSize];
	}

	public org.apache.lucene.analysis.Token next() throws java.io.IOException {
		int c;

		offset++;
		input.reset(); // reset to the "first char"-1 of the last ngram
		for (int i = 0; i < ngramSize; i++) {
			c = input.read();
			if (i == 0) {
				input.mark(ngramSize); // mark the second character so we can
										// reset to start the next ngram
			}

			if (c == -1) {
				offset = -1;
				return null;
			}
			ngram[i] = (char) c;
		}
		String s = new String(ngram);
		if (locale != null) {
			if (!(locale.equals(ARMENIAN) || locale.equals(SINHALA))) // FIXME:
																		// we
																		// can't
																		// lowercase
																		// Armenain
																		// yet
			{
				// use ICU4J to lower case - should be more accurate
				s = UCharacter.foldCase(s, false);
			}
		}

		org.apache.lucene.analysis.Token t = new org.apache.lucene.analysis.Token(
				s, offset, offset + s.length(), "ngram");

		return t;
	}

	/**
	 * Getter for property ngramSize.
	 * 
	 * @return Value of property ngramSize.
	 * 
	 */
	public int getNgramSize() {
		return ngramSize;
	}

	/**
	 * Setter for property ngramSize.
	 * 
	 * @param ngramSize
	 *            New value of property ngramSize.
	 * 
	 */
	public void setNgramSize(int p_ngramSize) {
		this.ngramSize = p_ngramSize;
		ngram = new char[ngramSize];
	}

	public void setReader(Reader p_reader) {
		// FIXME: Just to get to compile - uncomment and fix
		// input = p_reader;
	}
}
