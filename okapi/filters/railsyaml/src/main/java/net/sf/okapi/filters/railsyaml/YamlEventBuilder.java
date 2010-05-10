/**
 * 
 */
package net.sf.okapi.filters.railsyaml;

//import java.util.regex.Pattern;

import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * @author PerkinsGW
 *
 */
public class YamlEventBuilder extends EventBuilder {

//	private static final String YAML_WHITESPACE_REGEX = "[ ]+";
//	private static final Pattern YAML_WHITESPACE_PATTERN = Pattern.compile(YAML_WHITESPACE_REGEX);

	/**
	 * 
	 */
	public YamlEventBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Normalize text after TextUnit is complete. Called after endTextUnit
	 */
	@Override
	protected TextUnit postProcessTextUnit(TextUnit textUnit) {
		TextFragment text = textUnit.getSource().getFirstContent();
		text.setCodedText(normalizeEntities(text.getCodedText()));
		return textUnit;
	}

	private String normalizeEntities(String text) {
		text = text.replace("&#34;", "\"");
		text = text.replace("&#42;", "*");
		text = text.replace("&#45;", "-");
		text = text.replace("&#47;", "/");
		text = text.replace("&#58;", ":");
		text = text.replace("&#63;", "?");
		text = text.replace("&#x2F;", "/");

		return text;
	}

}
