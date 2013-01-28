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

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cleaner {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	// TODO: organize these strings better. Dynamically set on instantiation
	// thru private method?
	private static String OPENING_QUOTES_W_SPACE = "([\u00AB\u2039])([\\s\u00A0]+)";
	private static String CLOSING_QUOTES_W_SPACE = "([\\s\u00A0]+)([\u00BB\u203A])";
	private static String DOUBLE_QUOTES = "\u201C|\u201D|\u201E|\u201F|\u00AB|\u00BB";
	private static String DQ_REPLACE = "\"";
	private static String SINGLE_QUOTES = "\u2018|\u2019|\u201A|\u2039|\u203A";
	private static String SQ_REPLACE = "\u0027";
	private static String SPECIALPUNC = "\"\u0027";
	
	private static final String SINGLEQUOTES = "\'‘’‚‛‹›";
	private static final String DOUBLEQUOTES = "\"“”„‟«»";
	private static final String PUNCTUATION = ".,;:!¡?¿";
	private static final String OPENINGQUOTES = "‘‚‹“„«";
	private static final String CLOSINGQUOTES = "’‛›”‟»";
	private static final String MARKS = "\'‘’‚‛‹›\"“”„‟«».,;:!¡?¿";
	private static final String QUOTES = "\'‘’‚‛‹›\"“”„‟«»";

	private Parameters params;
	
	public Cleaner() {
		
	}

	/**
	 * Converts all quotation marks (curly or language specific) to straight
	 * quotes. All apostrophes will also be converted to their straight
	 * equivalents.
	 * 
	 * @param srcFrag
	 *            : original text to be normalized
	 * @param trgFrag
	 *            : target text to be normalized
	 */
	public void normalizeQuotation(TextFragment srcFrag, TextFragment trgFrag) {

		//TODO: update method to take the TU, Segment, Locale as parameters.
		
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

	public void normalizeMarks(ITextUnit tu, Segment seg, LocaleId trgLoc, boolean doQuotes) {
		
		StringBuilder srcText = new StringBuilder(seg.text);
//		StringBuilder trgText = new StringBuilder(tu.getTargetSegment(trgLoc, seg.getId(), false).text);
		
		int cursor = 0;
		int proxyCheck = 0;
		int proximityThreshold = 2;
		int ch = 0;
		int quoteCh = 0;
		int puncCh = 0;
		int nearQuote = -1;
		int nearPunc = -1;
		
		boolean isPunctuation = false;
		boolean isQuote = false;
		boolean isNearQuote = false;
		boolean isNearPuncuation = false;
		
		// normalize source
		while (cursor <= srcText.length() - 1) {
			ch = srcText.charAt(cursor);
			char c = (char)ch;
			if (MARKS.indexOf(ch) != -1) {
				// Punctuation
				if(PUNCTUATION.indexOf(ch) != -1) {
					if(nearQuote >= -1) {
						// check proximity backwards
						proxyCheck = cursor;
						while((proxyCheck > 0) && ((cursor - proxyCheck) < proximityThreshold) && (isNearQuote == false)) {
							proxyCheck -= 1;
							if(QUOTES.indexOf(srcText.charAt(proxyCheck)) != -1) {
								nearQuote = proxyCheck;
								isNearQuote = true;
								quoteCh = srcText.charAt(proxyCheck);
								break;
							}
						}
						// check proximity forwards
						proxyCheck = cursor;
						while ((proxyCheck < srcText.length() - 1) && ((proxyCheck - cursor) < proximityThreshold) && (isNearQuote == false)) {
							proxyCheck += 1;
							if(QUOTES.indexOf(srcText.charAt(proxyCheck)) != -1) {
								nearQuote = proxyCheck;
								isNearQuote = true;
								quoteCh = srcText.charAt(proxyCheck);
								break;
							}
						}
						// TODO: quotes are both back and forwards; "a " for example.
						
						// TODO: process PUNCTUATION based on rules and known attributes
						if(isNearQuote == false) {
							// treat PUNCTUATION according to rules
							switch(ch) {
								case '\u002E': // period "."
									// characters after period
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// end of abbreviation
										if ((Pattern.compile("([a-zA-Z]{1,3}\\.[\\s ]?)+").matcher(srcText.subSequence(cursor - 4, cursor - 1)).find() == true) && (Character.isLowerCase(srcText.charAt(cursor + 1)))) {
											srcText.insert(cursor + 1, ' ');
										}
										// end of a sentence
										if (Character.isUpperCase(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before period
									if (cursor > 0) {
										if ((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											// end of a sentence
											if (cursor == srcText.length() - 1) {
												srcText.deleteCharAt(cursor - 1);
												cursor -= 1;
											} else {
												// period is a decimal marker
												if ((cursor < srcText.length() - 1) && (!Character.isDigit(srcText.charAt(cursor - 1))) && (Character.isDigit(srcText.charAt(cursor + 1)))) {
													srcText.deleteCharAt(cursor - 1);
													cursor -= 1;
												}
											}
										}
									}
									break;
								case '\u002C': // comma ","
									// characters after comma
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// normal instance
										if (Character.isLetterOrDigit(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before comma
									if (cursor > 0) {
										if((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											// comma is a decimal marker
											if ((cursor < srcText.length() - 1) && (!Character.isDigit(srcText.charAt(cursor - 1))) && (Character.isDigit(srcText.charAt(cursor + 1)))) {
												srcText.deleteCharAt(cursor - 1);
												cursor -= 1;
											}
										}
									}
									break;
								case '\u003B': // semicolon ";"
									// characters after colon
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// normal instance
										if (Character.isLetterOrDigit(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before colon
									if (cursor > 0) {
										if ((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor - 1);
											cursor -= 1;
										}
									}
									break;
								case '\u003A': // colon ":"
									// characters after colon
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// normal instance
										if (Character.isLetterOrDigit(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before colon
									if (cursor > 0) {
										if ((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor - 1);
											cursor -= 1;
										}
									}
									break;
								case '\u0021': // exclamation mark "!"
									// characters after colon
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// normal instance
										if (Character.isLetterOrDigit(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before colon
									if (cursor > 0) {
										if ((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor - 1);
											cursor -= 1;
										}
									}
									break;
								case '\u00A1': // inverted exclamation mark "¡"
									break;
								case '\u003F': // question mark "?"
									// characters after colon
									if (cursor < srcText.length() - 1) {
										if ((Character.isWhitespace(srcText.charAt(cursor + 1))) || (srcText.charAt(cursor + 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor + 1);
										}
										// normal instance
										if (Character.isLetterOrDigit(srcText.charAt(cursor + 1))) {
											srcText.insert(cursor + 1, ' ');
										}
									}
									// characters before colon
									if (cursor > 0) {
										if ((Character.isWhitespace(srcText.charAt(cursor - 1))) || (srcText.charAt(cursor - 1) == '\u00A0')) {
											srcText.deleteCharAt(cursor - 1);
											cursor -= 1;
										}
									}
									break;
								case '\u00BF': // inverted question mark "¿"
									break;
								default:
									// other punctuation marks are retained as-is
									break;
							}
						} else {
							// exceptions to the rules
						}
					}
				} else { // end PUNCTUATION
					// Quotation
					if (nearPunc >= -1) {
						// check proximity backwards
						proxyCheck = cursor;
						while((proxyCheck > 0) && ((cursor - proxyCheck) < proximityThreshold) && (isNearPuncuation == false)) {
							proxyCheck -= 1;
							if(PUNCTUATION.indexOf(srcText.charAt(proxyCheck)) != -1) {
								nearPunc = proxyCheck;
								isNearPuncuation = true;
								puncCh = srcText.charAt(srcText.charAt(proxyCheck));
								break;
							}
						}
						// check proximity forwards
						proxyCheck = cursor;
						while((proxyCheck < srcText.length() - 1) && ((proxyCheck - cursor) < proximityThreshold) && (isNearPuncuation == false)) {
							proxyCheck += 1;
							if(PUNCTUATION.indexOf(srcText.charAt(proxyCheck)) != -1) {
								nearPunc = proxyCheck;
								isNearPuncuation = true;
								puncCh = srcText.charAt(srcText.charAt(proxyCheck));
								break;
							}
						}
						// TODO: punctuation is both back and forwards.
						
						// TODO: process QUOTES based on rules and known attributes
						if(isNearPuncuation == false) {
							if (doQuotes == true) {
								// TODO: treat QUOTES according to rules
							}
						} else {
							if (doQuotes == true) {
								// TODO: exceptions to the rules
							}
						}
					}
				} // end QUOTATION
			} // end MARKS
			
			// reset booleans
			// TODO: move these to a better place
			quoteCh = 0;
			puncCh = 0;
			nearQuote = -1;
			nearPunc = -1;			
			isPunctuation = false;
			isQuote = false;
			isNearQuote = false;
			isNearPuncuation = false;
			
			// iterate
			cursor += 1;
		}
		
		// save updated strings
		seg.setContent(new TextFragment(srcText.toString()));
	}
	
	/**
	 * Attempts to make punctuation and spacing around punctuation consistent
	 * according to standard English punctuation rules. Assumptions: 1) all
	 * strings passed have consistent spacing (only single spaces) 2) quotes
	 * have been normalized 3) strings will need post-processing in order to
	 * correct spacing for languages such as French. This ignores locale and
	 * Asian punctuation.
	 * 
	 * @param srcFrag
	 *            : original text to be normalized
	 * @param trgFrag
	 *            : target text to be normalized
	 */
	public void normalizePunctuation(TextFragment srcFrag, TextFragment trgFrag) {

		//TODO: update method to take the TU, Segment, Locale as parameters.
		
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
							if (cur < srcText.length() - 1) {
								if (((Character.isWhitespace(srcText.charAt(cur - 1))) || (srcText.charAt(cur - 1) == '\u00A0'))
										&& (!Character.isDigit(srcText.charAt(cur + 1)))) {
									srcText.deleteCharAt(cur - 1);
									cur -= 1;
								}
							}
						}
						if (cur < srcText.length() - 1) {
							if (Character.isWhitespace(srcText.charAt(cur + 1))) {
								srcText.deleteCharAt(cur + 1);
							}
//							if ((!Character.isWhitespace(srcText.charAt(cur + 1)))
//									&& (srcText.charAt(cur + 1) != '\u00A0')
//									&& (SPECIALPUNC.indexOf(srcText.charAt(cur + 1)) == -1)) {
//								srcText.insert(cur + 1, ' ');
//							}
							char chB = (char)srcText.charAt(cur - 1);
							char chA = (char)srcText.charAt(cur + 1);
							boolean dig = Character.isDigit(srcText.charAt(cur+1));
							int sp = SPECIALPUNC.indexOf(srcText.charAt(cur+1));
							if ((!Character.isDigit(srcText.charAt(cur + 1))) && (SPECIALPUNC.indexOf(srcText.charAt(cur + 1)) == -1)) {
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

	
	
	/**
	 * Effectively marks the segment for removal by setting the target content
	 * to null. Tu will be pruned by a different method (see pruneTextUnit).
	 * @param tu the text unit containing the content
	 * @param seg the segment to be removed
	 * @param trgLoc the locale for which the segment should be removed
	 */
	protected void markSegmentForRemoval(ITextUnit tu, Segment seg, LocaleId trgLoc) {
		
		tu.getTargetSegment(trgLoc, seg.getId(), false).getContent().clear();	
	}
	
	/**
	 * Marks segments for removal that contain text which match given regular expressions.
	 * Allows for marking segments which match user specified regular expressions.
	 * @param tu the text unit containing the segments to be matched
	 * @param seg the segment to analyze
	 * @param trgLoc the locale
	 * @param doUserRegex whether to perform matching of user specified regular expressions
	 * @param userRegex the user specified regular expression
	 */
	public void matchRegexExpressions(ITextUnit tu, Segment seg, LocaleId trgLoc, boolean doUserRegex, String userRegex) {
		
		Pattern pattern;
		StringBuilder srcText = new StringBuilder(seg.text);
		StringBuilder trgText = new StringBuilder(tu.getTargetSegment(trgLoc, seg.getId(), false).text);
		boolean alreadyFound = false;

		// match user specified regex expression
		if (doUserRegex == true) {
			if ((userRegex != null) && (userRegex != "")) {
				// compile user string
				try {
					pattern = Pattern.compile(userRegex);

					// find matching text
					if ((pattern.matcher(srcText).find()) || (pattern.matcher(trgText).find())) {
						alreadyFound = true;
						markSegmentForRemoval(tu, seg, trgLoc);
					}
				} catch (PatternSyntaxException patException) {
					LOGGER.error("The following error occured \"{}\" in the expression: {}.", patException.getDescription(), patException.getPattern());
				}
			}
		}
		// skip "standard" regex expressions if already marked for removal
		if (alreadyFound != true) {
			// TODO: other "standard" regex expressions to match against.
		}
		
	}
	
	/**
	 * Removes segments from the text unit marked as not containing
	 * useful information.
	 * @param tu text unit to be pruned of unwanted segments
	 * @param trgLoc locale of target through which to search
	 * @return true if entire text unit is to be discarded
	 *         false if text unit contains good translated text
	 */
	public boolean pruneTextUnit(ITextUnit tu, LocaleId trgLoc) {
		
		if (!tu.isEmpty()) {
			TextContainer tc = tu.getSource();
			ISegments srcSegs = tc.getSegments();
			int cursor = 0;
			
			while (cursor <= srcSegs.count() - 1)  {
				// get segments
				Segment srcSeg = srcSegs.get(cursor);
				Segment trgSeg = tu.getTargetSegment(trgLoc, srcSeg.getId(), false);
				
				// check for segments to remove 
				if (cursor < srcSegs.count() - 1) {
					if (trgSeg.text.isEmpty()) {
						tc.remove(srcSegs.getIndex(srcSeg.getId()));
						continue;
					}
				} else {
					// last segment in tu
					if (trgSeg.text.isEmpty()) {
						return true;
					}
				}
				// increment
				cursor += 1;
			}
		} else {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * Attempts to detect character corruption from either the source or target.
	 * If any corruption are detected, the segment is marked for removal.
	 * @param tu the text unit to be modified
	 * @param seg the source segment to be modified
	 * @param trgLoc the locale used to fetch the target text
	 */
	private void removeCorruptions(ITextUnit tu, Segment seg, LocaleId trgLoc) {
		
		StringBuilder srcText = new StringBuilder(seg.text);
		StringBuilder trgText = new StringBuilder(tu.getTargetSegment(trgLoc, seg.getId(), false).text);
		
		Matcher matcher;
		String corruptionRegex = "\\u00C3[\\u00A4-\\u00B6]|\\u00C3\\u201E|\\u00C3\\u2026|\\u00C3\\u2013";
		
		// find corruption in source
		matcher = Pattern.compile(corruptionRegex).matcher(srcText);
		if (matcher.find() == true) {
			this.markSegmentForRemoval(tu, seg, trgLoc);
		}
		
		// find corruption in target
		matcher = Pattern.compile(corruptionRegex).matcher(trgText);
		if (matcher.find() == true) {
			this.markSegmentForRemoval(tu, seg, trgLoc);
		}
	}
	
	public void checkCharacters(ITextUnit tu, Segment seg, LocaleId trgLoc) {
		
		CharsetEncoder encoder1 = null;
		CharsetEncoder encoder2 = null;
		Pattern extraCharsAllowed = null;
		Pattern itsAllowedChars = null;
		String itsAllowedCharsPattern = "\u0000";

		StringBuilder trgOri = new StringBuilder(tu.getTargetSegment(trgLoc, seg.getId(), false).text);
		
		StringBuilder badChars = new StringBuilder();
		int pos = -1;
		int badChar = 0;
		int count = 0;

		// this needs to be set some how from trgLoc
		String charsetName = null;

		if (!Util.isEmpty(charsetName)) {
			encoder1 = Charset.forName(charsetName).newEncoder();
		}
		// Extra characters allowed
		// these needs to be set some how
		boolean allowExtraCharacters = false;
		String getExtraCharsAllowed = "";
		if (!allowExtraCharacters) {
			extraCharsAllowed = Pattern.compile(getExtraCharsAllowed);
		}

		for (int i = 0; i < trgOri.length(); i++) {
			char ch = trgOri.charAt(i);

			if (encoder1 != null) {
				if (encoder1.canEncode(ch)) {
					continue; // Allowed, move to the next character
				} else { // Not included in the target charset
							// Check if it is included in the extra characters
							// list
					if (extraCharsAllowed != null) {
						Matcher m = extraCharsAllowed.matcher(trgOri.subSequence(i, i + 1));
						if (m.find()) {
							// Part of the extra character list: it's OK
							continue; // Move to the next character
						}
						// Else: not allowed: fall thru
					}
				}
			} else { // Not charset defined, try just the extra characters list
				if (extraCharsAllowed != null) {
					Matcher m = extraCharsAllowed.matcher(trgOri.subSequence(i, i + 1));
					if (m.find()) {
						// Part of the extra character list: it's OK
						continue; // Move to the next character
					}
					// Else: not allowed: fall thru
				}
				// Else: not in charset, nor in extra characters list: not
				// allowed
			}

			// The character is not allowed: add the error
			if (++count > 1) {
				if (badChars.indexOf(String.valueOf(ch)) == -1) {
					badChars.append(ch);
				}
			} else {
				pos = i;
				badChar = ch;
			}
		}

		// Do we have one or more errors?
//		if (pos > -1) {
//			if (count > 1) {
//				reportIssue(IssueType.ALLOWED_CHARACTERS, tu, null, String.format("The character '%c' (U+%04X) is not allowed in the target text."
//						+ " Other forbidden characters found: ", badChar, (int) badChar)
//						+ badChars.toString(), 0, -1, pos, pos + 1, Issue.SEVERITY_MEDIUM, srcOri, trgOri, null);
//			} else {
//				reportIssue(IssueType.ALLOWED_CHARACTERS, tu, null, String.format("The character '%c' (U+%04X) is not allowed in the target text.", badChar, (int) badChar), 0, -1, pos, pos + 1, Issue.SEVERITY_MEDIUM, srcOri, trgOri, null);
//			}
//		}
	}
	
	
// end class
}
