package net.sf.okapi.filters.html;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.htmlparser.jericho.Attributes;
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
		Map<String, Object> rule = configReader.getRule(ruleName);
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

	@SuppressWarnings("unchecked")
	public boolean hasTranslatableAttributes(String ruleName) {
		Map rule = configReader.getRule(ruleName);
		if (rule != null && rule.containsKey("translatableAttributes")) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean hasLocalizableAttributes(String ruleName) {
		Map<String, Object> rule = configReader.getRule(ruleName);
		if (rule != null && rule.containsKey("localizableAttributes")) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isTranslatableAttribute(Map<String, Object> elementRule, String attribute, Attributes attributes) {
		if (elementRule == null) {
			return false;
		}

		Object ta = elementRule.get("translatableAttributes");
		if (ta instanceof List) {
			List translatableAttributes = (List) elementRule.get("translatableAttributes");
			for (Iterator<String> i = translatableAttributes.iterator(); i.hasNext();) {
				String a = i.next();
				if (a.equals(attribute)) {
					return true;
				}
			}

		} else if (ta instanceof Map) {
			Map translatableAttributes = (Map) elementRule.get("translatableAttributes");
			if (translatableAttributes.containsKey(attribute)) {
				List condition = (List) translatableAttributes.get(attribute);
				// case where there is no condition applied to attribute
				if (condition == null) {
					return true;
				} else {
					// apply conditions
					return applyConditions(condition, attribute, attributes);
				}

			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean applyConditions(List<?> condition, String attribute, Attributes attributes) {
		// attribute that must have conditional value
		String conditionalAttribute = (String) condition.get(0);

		// '=', '!=' or regex
		int compareType = (Integer) condition.get(1);

		// multiple condition values OR'ed together
		if (condition.get(2) instanceof List) {
			List conditionValues = (List) condition.get(2);
			for (Iterator<String> i = conditionValues.iterator(); i.hasNext();) {
				String value = i.next();
				if (applyCondition(attributes.getValue(conditionalAttribute), compareType, value)) {
					return true;
				}
			}
		}
		// single condition
		else if (condition.get(2) instanceof String) {
			String conditionValue = (String) condition.get(2);
			return applyCondition(attributes.getValue(conditionalAttribute), compareType, conditionValue);
		} else {
			throw new RuntimeException("Error reading attributes from config file");
		}

		return false;
	}

	private static final int EQUALS = 1;
	private static final int NOT_EQUALS = 2;
	private static final int MATCH = 3;

	private boolean applyCondition(String attributeValue, int compareType, String conditionValue) {
		switch (compareType) {
		case EQUALS:
			return attributeValue.equalsIgnoreCase(conditionValue);

		case NOT_EQUALS:
			return !(attributeValue.equalsIgnoreCase(conditionValue));

		case MATCH:
			boolean result = false;
			Pattern matchPattern = Pattern.compile(conditionValue);
			try {
				Matcher m = matchPattern.matcher(attributeValue);
				result = m.matches();
			} catch (PatternSyntaxException e) {
				throw new IllegalConditionalAttributeMatchTypeException(e);
			}
			return result;
		default:
			throw new IllegalConditionalAttributeMatchTypeException("Unkown match type");

		}
	}
}
