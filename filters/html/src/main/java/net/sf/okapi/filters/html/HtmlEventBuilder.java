package net.sf.okapi.filters.html;

import java.util.regex.Pattern;

import net.htmlparser.jericho.CharacterReference;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class HtmlEventBuilder extends EventBuilder {
	/*
	 * HTML whitespace space (U+0020) tab (U+0009) form feed (U+000C) line feed
	 * (U+000A) carriage return (U+000D) zero-width space (U+200B) (IE6 does not
	 * recognize these, they are treated as unprintable characters)
	 */
	private static final String HTML_WHITESPACE_REGEX = "[ \t\r\n\f\u200B]+";
	private static final Pattern HTML_WHITESPACE_PATTERN = Pattern.compile(HTML_WHITESPACE_REGEX);
	
	private boolean collapseWhitespace = true;
	
	/**
	 * @return the collapseWhitespace
	 */
	public boolean isCollapseWhitespace() {
		return collapseWhitespace;
	}

	/**
	 * @param collapseWhitespace the collapseWhitespace to set
	 */
	public void setCollapseWhitespace(boolean collapseWhitespace) {
		this.collapseWhitespace = collapseWhitespace;
	}

	/**
	 * Normalize HTML text after TextUnit is complete. Called after endTextUnit
	 */
	@Override
	protected TextUnit postProcessTextUnit(TextUnit textUnit) {
		TextFragment text = textUnit.getSourceContent();
		text.setCodedText(normalizeHtmlText(text.getCodedText(), false, isCollapseWhitespace()));
		return textUnit;
	}
	
	public String normalizeHtmlText(String text, boolean insideAttribute, boolean collapseWhitespace) {
		// convert all entities to Unicode
		String decodedValue = CharacterReference.decode(text, insideAttribute);
		
		if (collapseWhitespace) {
			decodedValue = collapseWhitespace(decodedValue);
			decodedValue = decodedValue.trim();
		}

		decodedValue = Util.normalizeNewlines(decodedValue);
		return decodedValue;
	}
	
	private String collapseWhitespace(String text) {
		return HTML_WHITESPACE_PATTERN.matcher(text).replaceAll(" ");
	}
}
