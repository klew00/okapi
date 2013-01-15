/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.cleanup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;

public class Cleaner {

	// TODO: organize these strings better. Dynamically set on instantiation
	// thru private method?
	private static String OPENING_QUOTES_W_SPACE = "([\u00AB\u2039])([\\s\u00A0]+)";
	private static String CLOSING_QUOTES_W_SPACE = "([\\s\u00A0]+)([\u00BB\u203A])";
	private static String DOUBLE_QUOTES = "\u201C|\u201D|\u201E|\u201F|\u00AB|\u00BB";
	private static String DQ_REPLACE = "\"";
	private static String SINGLE_QUOTES = "\u2018|\u2019|\u201A|\u2039|\u203A";
	private static String SQ_REPLACE = "\u0027";

	// . , ; : ! ¡ ? ¿
	private static String PUNCTUATION = "\u002E\u002C\u003B\u003A\u0021\u00A1\u003F\u00BF";

	public Cleaner() {

	}

	/**
	 * Converts all quotation marks (curly or language specific) to straight
	 * quotes. All apostrophes will also be converted to their straight
	 * equivalents.
	 * 
	 * @param srcFrag: original text to be normalized
	 * @param trgFrag: target text to be normalized
	 */
	public void normalizeQuotation(TextFragment srcFrag, TextFragment trgFrag) {

		String srcText = srcFrag.getCodedText();
		String trgText = trgFrag.getCodedText();

		Pattern pattern;
		Matcher matcher;

		// update quote spacing
		pattern = Pattern.compile(OPENING_QUOTES_W_SPACE);
		matcher = pattern.matcher(trgText);
		trgText = matcher.replaceAll("$1");
		pattern = Pattern.compile(CLOSING_QUOTES_W_SPACE);
		matcher = pattern.matcher(trgText);
		trgText = matcher.replaceAll("$2");

		// update source quotes
		pattern = Pattern.compile(DOUBLE_QUOTES);
		matcher = pattern.matcher(srcText);
		srcText = matcher.replaceAll(DQ_REPLACE).toString();
		pattern = Pattern.compile(SINGLE_QUOTES);
		matcher = pattern.matcher(srcText);
		srcText = matcher.replaceAll(SQ_REPLACE).toString();

		// update target quotes
		pattern = Pattern.compile(DOUBLE_QUOTES);
		matcher = pattern.matcher(trgText);
		trgText = matcher.replaceAll(DQ_REPLACE).toString();
		pattern = Pattern.compile(SINGLE_QUOTES);
		matcher = pattern.matcher(trgText);
		trgText = matcher.replaceAll(SQ_REPLACE).toString();

		// save updated strings
		srcFrag.setCodedText(srcText);
		trgFrag.setCodedText(trgText);
	}

	/**
	 * Attempts to make punctuation and spacing around punctuation consistent
	 * according to standard English punctuation rules. 
	 * Assumptions:
	 * 1) all strings passed have consistent spacing (only single spaces)
	 * 2) quotes have been normalized
	 * 3) strings will need post-processing in order to correct spacing for
	 *    languages such as French. This ignores locale and Asian
	 *    punctuation.
	 * 
	 * @param srcFrag: original text to be normalized
	 * @param trgFrag: target text to be normalized
	 */
	public void normalizePunctuation(TextFragment srcFrag, TextFragment trgFrag) {

		StringBuilder srcText = new StringBuilder(srcFrag.getCodedText());
		StringBuilder trgText = new StringBuilder(trgFrag.getCodedText());

		int cur = 0;
		int ch = 0;

		// normalize source
		while (cur <= srcText.length() - 1) {
			ch = srcText.charAt(cur);
			if (PUNCTUATION.indexOf(ch) != -1) {
				switch (ch) {
					case '\u002E': // .
						if (cur < srcText.length() - 1) {
							if ((Character.isWhitespace(srcText.charAt(cur + 1)))
									|| (srcText.charAt(cur + 1) == '\u00A0')) {
								srcText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								if (cur == srcText.length() - 1) {
									srcText.deleteCharAt(cur - 1);
									cur -= 1;
								} else if ((cur < srcText.length() - 1) 
										&& (Character.isDigit(srcText.charAt(cur + 1)))) {
									srcText.deleteCharAt(cur - 1);
									cur -= 1;
								} else {
									break;
								}
							}
						}
						break;
					case '\u002C': // ,
						if (cur > 0) {
							if ((cur < srcText.length() - 1) 
									&& (Character.isDigit(srcText.charAt(cur + 1)))) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							} else {
								break;
							}
						}
						if (cur < srcText.length() - 1) {
							if ((!Character.isWhitespace(srcText.charAt(cur + 1)))
									&& (srcText.charAt(cur + 1) != '\u00A0')) {
								srcText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u003B': // ;
						if (cur > 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						if (cur < srcText.length() - 1) {
							if ((!Character.isWhitespace(srcText.charAt(cur + 1)))
									&& (srcText.charAt(cur + 1) != '\u00A0')) {
								srcText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u003A': // :
						if (cur > 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						if (cur < srcText.length() - 1) {
							if ((!Character.isWhitespace(srcText.charAt(cur + 1)))
									&& (srcText.charAt(cur + 1) != '\u00A0')) {
								srcText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u0021': // !
						if (cur < srcText.length() - 1) {
							if ((Character.isWhitespace(srcText.charAt(cur + 1)))
									|| (srcText.charAt(cur + 1) == '\u00A0')) {
								srcText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u00A1': // ¡
						if (cur >= 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u003F': // ?
						if (cur < srcText.length() - 1) {
							if ((Character.isWhitespace(srcText.charAt(cur + 1)))
									|| (srcText.charAt(cur + 1) == '\u00A0')) {
								srcText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u00BF': // ¿
						if (cur >= 0) {
							if ((Character.isWhitespace(srcText.charAt(cur - 1)))
									|| (srcText.charAt(cur - 1) == '\u00A0')) {
								srcText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					default:
						break;
				}
			}
			cur += 1;
		}

		// normalize target
		cur = 0;
		while (cur <= trgText.length() - 1) {
			ch = trgText.charAt(cur);
			if (PUNCTUATION.indexOf(ch) != -1) {
				switch (ch) {
					case '\u002E': // .
						if (cur < trgText.length() - 1) {
							if ((Character.isWhitespace(trgText.charAt(cur + 1)))
									|| (trgText.charAt(cur + 1) == '\u00A0')) {
								trgText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								if (cur == trgText.length() - 1) {
									trgText.deleteCharAt(cur - 1);
									cur -= 1;
								} else if ((cur < trgText.length() - 1) 
										&& (Character.isDigit(trgText.charAt(cur + 1)))) {
									trgText.deleteCharAt(cur - 1);
									cur -= 1;
								} else {
									break;
								}
							}
						}
						break;
					case '\u002C': // ,
						if (cur > 0) {
							if ((cur < trgText.length() - 1) 
									&& (!Character.isDigit(trgText.charAt(cur + 1)))) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							} else {
								break;
							}
						}
						if (cur < trgText.length() - 1) {
							if ((!Character.isWhitespace(trgText.charAt(cur + 1)))
									&& (trgText.charAt(cur + 1) != '\u00A0')) {
								trgText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u003B': // ;
						if (cur > 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						if (cur < trgText.length() - 1) {
							if ((!Character.isWhitespace(trgText.charAt(cur + 1)))
									&& (trgText.charAt(cur + 1) != '\u00A0')) {
								trgText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u003A': // :
						if (cur > 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						if (cur < trgText.length() - 1) {
							if ((!Character.isWhitespace(trgText.charAt(cur + 1)))
									&& (trgText.charAt(cur + 1) != '\u00A0')) {
								trgText.insert(cur + 1, ' ');
							}
						}
						break;
					case '\u0021': // !
						if (cur < trgText.length() - 1) {
							if ((Character.isWhitespace(trgText.charAt(cur + 1)))
									|| (trgText.charAt(cur + 1) == '\u00A0')) {
								trgText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u00A1': // ¡
						if (cur >= 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u003F': // ?
						if (cur < trgText.length() - 1) {
							if ((Character.isWhitespace(trgText.charAt(cur + 1)))
									|| (trgText.charAt(cur + 1) == '\u00A0')) {
								trgText.deleteCharAt(cur + 1);
							}
						}
						if (cur > 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					case '\u00BF': // ¿
						if (cur >= 0) {
							if ((Character.isWhitespace(trgText.charAt(cur - 1)))
									|| (trgText.charAt(cur - 1) == '\u00A0')) {
								trgText.deleteCharAt(cur - 1);
								cur -= 1;
							}
						}
						break;
					default:
						break;
				}
			}
			cur += 1;
		}

		// save updated strings
		srcFrag.setCodedText(srcText.toString());
		trgFrag.setCodedText(trgText.toString());
	}

	public void pruneTextUnit(ITextUnit tu) {

	}

}
