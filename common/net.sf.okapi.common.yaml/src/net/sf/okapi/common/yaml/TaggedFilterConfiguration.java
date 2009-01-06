package net.sf.okapi.common.yaml;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TaggedFilterConfiguration {
	private static final String INLINE = "INLINE";
	private static final String GROUP = "GROUP";
	private static final String EXCLUDE = "EXCLUDE";
	private static final String INCLUDE = "INCLUDE";
	private static final String TEXTUNIT = "TEXTUNIT";
	private static final String PRESERVE_WHITESPACE = "PRESERVE_WHITESPACE";
	private static final String SCRIPT = "SCRIPT";
	private static final String SERVER = "SERVER";
	private static final String ATTRIBUTE = "ATTRIBUTE";
	private static final String ATTRIBUTES_ONLY = "ATTRIBUTES_ONLY";

	private static final String EQUALS = "EQUALS";
	private static final String NOT_EQUALS = "NOT_EQUALS";
	private static final String MATCH = "MATCH";

	public static enum RULE_TYPE {
		INLINE_ELEMENT, EXCLUDED_ELEMENT, INCLUDED_ELEMENT, GROUP_ELEMENT, TEXT_UNIT_ELEMENT, PRESERVE_WHITESPACE, SCRIPT_ELEMENT, SERVER_ELEMENT, ATTRIBUTE, ATTRIBUTES_ONLY, UNKOWN
	};

	private YamlConfigurationReader configReader;

	public TaggedFilterConfiguration(URL configurationPathAsResource) {
		configReader = new YamlConfigurationReader(configurationPathAsResource);
	}

	public TaggedFilterConfiguration(File configurationFile) {
		configReader = new YamlConfigurationReader(configurationFile);
	}

	public TaggedFilterConfiguration(String configurationScript) {
		configReader = new YamlConfigurationReader(configurationScript);
	}

	@Override
	public String toString() {
		return configReader.toString();
	}

	@SuppressWarnings("unchecked")
	public RULE_TYPE getMainRuleType(String ruleName) {
		Map<String, Object> rule = configReader.getRule(ruleName);
		if (rule == null) {
			return RULE_TYPE.UNKOWN;
		}

		List ruleTypes = (List) rule.get("ruleTypes");
		String ruleType = (String) ruleTypes.get(0);

		if (ruleType.equals(INLINE)) {
			return RULE_TYPE.INLINE_ELEMENT;
		} else if (ruleType.equals(GROUP)) {
			return RULE_TYPE.GROUP_ELEMENT;
		} else if (ruleType.equals(EXCLUDE)) {
			return RULE_TYPE.EXCLUDED_ELEMENT;
		} else if (ruleType.equals(INCLUDE)) {
			return RULE_TYPE.INCLUDED_ELEMENT;
		} else if (ruleType.equals(TEXTUNIT)) {
			return RULE_TYPE.TEXT_UNIT_ELEMENT;
		} else if (ruleType.equals(PRESERVE_WHITESPACE)) {
			return RULE_TYPE.PRESERVE_WHITESPACE;
		} else if (ruleType.equals(SCRIPT)) {
			return RULE_TYPE.SCRIPT_ELEMENT;
		} else if (ruleType.equals(SERVER)) {
			return RULE_TYPE.SERVER_ELEMENT;
		} else if (ruleType.equals(ATTRIBUTE)) {
			return RULE_TYPE.ATTRIBUTE;
		} else if (ruleType.equals(ATTRIBUTES_ONLY)) {
			return RULE_TYPE.ATTRIBUTES_ONLY;
		} else {
			return RULE_TYPE.UNKOWN;
		}
	}

	public boolean hasActionableAttributes(String ruleName) {
		return hasTranslatableAttributes(ruleName) || hasLocalizableAttributes(ruleName);
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
		if (rule != null
				&& (rule.containsKey("writableLocalizableAttributes") || rule
						.containsKey("readOnlyLocalizableAttributes"))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean hasReadOnlyLocalizableAttributes(String ruleName) {
		Map<String, Object> rule = configReader.getRule(ruleName);
		if (rule != null && rule.containsKey("readOnlyLocalizableAttributes")) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean hasWritableLocalizableAttributes(String ruleName) {
		Map<String, Object> rule = configReader.getRule(ruleName);
		if (rule != null && rule.containsKey("writableLocalizableAttributes")) {
			return true;
		}
		return false;
	}

	public boolean isTranslatableAttribute(String elementName, String attribute, Map<String, String> attributes) {
		return isActionableAttribute("translatableAttributes", elementName, attribute, attributes);
	}

	public boolean isReadOnlyLocalizableAttribute(String elementName, String attribute, Map<String, String> attributes) {
		return isActionableAttribute("readOnlyLocalizableAttributes", elementName, attribute, attributes);
	}

	public boolean isWritableLocalizableAttribute(String elementName, String attribute, Map<String, String> attributes) {
		return isActionableAttribute("writableLocalizableAttributes", elementName, attribute, attributes);
	}

	@SuppressWarnings("unchecked")
	private boolean isActionableAttribute(String type, String elementName, String attribute,
			Map<String, String> attributes) {
		Map elementRule = configReader.getRule(elementName);
		if (elementRule == null) {
			return false;
		}

		Object ta = elementRule.get(type);

		if (ta instanceof List) {
			List actionableAttributes = (List) elementRule.get(type);
			for (Iterator<String> i = actionableAttributes.iterator(); i.hasNext();) {
				String a = i.next();
				if (a.equals(attribute)) {
					return true;
				}
			}

		} else if (ta instanceof Map) {
			Map actionableAttributes = (Map) elementRule.get(type);
			if (actionableAttributes.containsKey(attribute)) {
				List condition = (List) actionableAttributes.get(attribute);
				// case where there is no condition applied to attribute
				if (condition == null) {
					return true;
				} else {
					// apply conditions
					if (condition.get(0) instanceof List) {
						// We have multiple conditions - individual results are OR'ed together
						// so only one condition need be true for the rule to apply
						for (int i = 0; i <= condition.size() - 1; i++) {
							List c = (List)condition.get(i);
							if (applyConditions(c, attribute, attributes)) {
								return true;
							}
						}
						return false;
					}
					return applyConditions(condition, attribute, attributes);
				}

			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean applyConditions(List<?> condition, String attribute, Map<String, String> attributes) {
		String conditionalAttribute = null;
		conditionalAttribute = (String) condition.get(0);

		// we didn't find the conditional test attribute - we assume no
		// extraction
		if (attributes.get(conditionalAttribute) == null) {
			return false;
		}

		// '=', '!=' or regex
		String compareType = (String) condition.get(1);

		// multiple condition values OR'ed together
		if (condition.get(2) instanceof List) {
			List conditionValues = (List) condition.get(2);
			for (Iterator<String> i = conditionValues.iterator(); i.hasNext();) {
				String value = i.next();
				if (applyCondition(attributes.get(conditionalAttribute), compareType, value)) {
					return true;
				}
			}
		}
		// single condition
		else if (condition.get(2) instanceof String) {
			String conditionValue = (String) condition.get(2);
			return applyCondition(attributes.get(conditionalAttribute), compareType, conditionValue);
		} else {
			throw new RuntimeException("Error reading attributes from config file");
		}

		return false;
	}

	private boolean applyCondition(String attributeValue, String compareType, String conditionValue) {
		if (compareType.equals(EQUALS)) {
			return attributeValue.equalsIgnoreCase(conditionValue);
		} else if (compareType.equals(NOT_EQUALS)) {
			return !(attributeValue.equalsIgnoreCase(conditionValue));
		} else if (compareType.equals(MATCH)) {
			boolean result = false;
			Pattern matchPattern = Pattern.compile(conditionValue);
			try {
				Matcher m = matchPattern.matcher(attributeValue);
				result = m.matches();
			} catch (PatternSyntaxException e) {
				throw new IllegalConditionalAttributeException(e);
			}
			return result;
		} else {
			throw new IllegalConditionalAttributeException("Unkown match type");
		}
	}
}
