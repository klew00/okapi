/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.abstractmarkup;

import java.util.regex.Pattern;

import net.htmlparser.jericho.CharacterReference;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class AbstractMarkupEventBuilder extends EventBuilder {
	/*
	 * Typical whitespace space (U+0020) tab (U+0009) form feed (U+000C) line feed
	 * (U+000A) carriage return (U+000D) zero-width space (U+200B) (IE6 does not
	 * recognize these, they are treated as unprintable characters)
	 */
	private static final String WHITESPACE_REGEX = "[ \t\r\n\f\u200B]+";
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile(WHITESPACE_REGEX);
		
	private boolean useCodeFinder = false;
	private InlineCodeFinder codeFinder;
	
	public AbstractMarkupEventBuilder(String rootId) {
		super(rootId);		
		codeFinder = new InlineCodeFinder();
	}
	
	/**
	 * Initializes the code finder. this must be called before the first time using it, for example 
	 * when starting to process the inputs.
	 * @param useCodeFinder true to use the code finder.
	 * @param rules the string representation of the rules.
	 */
	public void initializeCodeFinder (boolean useCodeFinder,
		String rules)
	{
		this.useCodeFinder = useCodeFinder;
		if ( useCodeFinder ) {
			codeFinder.fromString(rules);
			codeFinder.compile();
		}
	}

	@Override
	protected TextUnit postProcessTextUnit(TextUnit textUnit) {
		// We can use getFirstPartContent() because nothing is segmented
		TextFragment text = textUnit.getSource().getFirstContent();
		// Treat the white spaces
		text.setCodedText(normalizeHtmlText(text.getCodedText(), false, isPreserveWhitespace()));
		// Apply the in-line codes rules if needed
		if ( useCodeFinder ) {
			codeFinder.process(text);
		}
		return textUnit;
	}
	
	public String normalizeHtmlText(String text, boolean insideAttribute, boolean preserveWhitespace) {
		// convert all entities to Unicode
		String decodedValue = CharacterReference.decode(text, insideAttribute);
		
		if (!preserveWhitespace) {
			decodedValue = collapseWhitespace(decodedValue);
			decodedValue = decodedValue.trim();
		}

		decodedValue = Util.normalizeNewlines(decodedValue);
		return decodedValue;
	}
	
	private String collapseWhitespace(String text) {
		return WHITESPACE_PATTERN.matcher(text).replaceAll(" ");
	}
}
