package net.sf.okapi.filters.html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.filters.ParserConfigurationReader;

public class HtmlFilterConfiguration {
	public static enum RULE_TYPE {
		INLINE_ELEMENT, EXCLUDED_ELEMENT, INCLUDED_ELEMENT, GROUP_ELEMENT, TEXT_UNIT_ELEMENT, PRESERVE_WHITESPACE, SCRIPT_ELEMENT, SERVER_ELEMENT, ATTRIBUTE, ATTRIBUTES_ONLY, UNKOWN
	};

	private ParserConfigurationReader configReader;

	public HtmlFilterConfiguration(String configurationPathAsResource) {
		configReader = new ParserConfigurationReader(configurationPathAsResource);
	}

	public HtmlFilterConfiguration(File configurationFile) {
		configReader = new ParserConfigurationReader(configurationFile);
	}

	@SuppressWarnings("unchecked")
	public RULE_TYPE getMainRuleType(String ruleName) {
		Map rule = configReader.getRule(ruleName);
		if (rule == null) {
			return RULE_TYPE.UNKOWN;
		}
		
		List ruleTypes = (List) rule.get("ruleTypes");
		Integer ruleType = (Integer) ruleTypes.get(0);
		switch (ruleType.intValue()) {
		case 1:
			return RULE_TYPE.INLINE_ELEMENT;
		case 2:
			return RULE_TYPE.GROUP_ELEMENT;
		case 3:
			return RULE_TYPE.EXCLUDED_ELEMENT;
		case 4:
			return RULE_TYPE.INCLUDED_ELEMENT;
		case 5:
			return RULE_TYPE.TEXT_UNIT_ELEMENT;
		case 6:
			return RULE_TYPE.PRESERVE_WHITESPACE;
		case 7:
			return RULE_TYPE.SCRIPT_ELEMENT;
		case 8:
			return RULE_TYPE.SERVER_ELEMENT;
		case 9:
			return RULE_TYPE.ATTRIBUTE;
		case 10:
			return RULE_TYPE.ATTRIBUTES_ONLY;
		default:
			return RULE_TYPE.UNKOWN;
		}
	}

}
