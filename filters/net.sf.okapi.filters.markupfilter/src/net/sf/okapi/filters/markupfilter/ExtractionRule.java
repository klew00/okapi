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

package net.sf.okapi.filters.markupfilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * General HTML extraction rule can handle the following cases:<br/><br/>
 * 
 * NON EXTRACTABLE - Default rule - don't extract it.<br/> INLINE - Elements
 * that are included with text.<br/> EXCLUDED -Element and children that should
 * be excluded from extraction.<br/> INCLUDED - Elements and children within
 * EXLCUDED ranges that should be extracted.<br/> GROUP - Elements that are
 * grouped together structurally such as lists, tables etc..<br/> ATTRIBUTES -
 * Attributes on specific elements which should be extracted. May be
 * translatable or localizable. <br/> ATTRIBUTES ANY ELEMENT - Convenience rule
 * for attributes which can occur on any element. May be translatable or
 * localizable. <br/> TEXT UNIT - Elements whose start and end tags become part
 * of a {@link TextUnit} rather than {@link SkeletonUnit}.<br/> <br/> Any of the
 * above rules may have conditional rules based on attribute names and/or
 * values. Conditional rules ({@link ConditionalAttributeRule}) may be attached
 * to both elements and attributes. More than one conditional rules are
 * evaluated as OR expressions. For example, "type=button" OR "type=default".
 */
public class ExtractionRule {

	public static enum EXTRACTION_RULE_TYPE {
		NON_EXTRACTABLE, EXTRACTABLE_ATTRIBUTES, EXTRACTABLE_ATTRIBUTE_ANY_ELEMENT, INLINE_ELEMENT, EXCLUDED_ELEMENT, INCLUDED_ELEMENT, GROUP_ELEMENT, TEXT_UNIT_ELEMENT, PRESERVE_WHITESPACE
	};

	private String elementName;
	private String attributeName;
	private List<ConditionalAttributeRule> extractionConditions;
	private List<AttributeExtractionRule> extractableAttributes;
	private EXTRACTION_RULE_TYPE ruleType;
	private Map<String, String> properties;

	/**
	 * Convenience rule factory for elements that must preserve whitespace.
	 * 
	 * @param elementName
	 *            lowercase element name.
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createPreserveWhiteSpaceRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.PRESERVE_WHITESPACE);
	}

	/**
	 * Convenience rule factory for inline elements.
	 * 
	 * @param elementName
	 *            lowercase element name.
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createInlineRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.INLINE_ELEMENT);
	}

	/**
	 * Convenience rule factory for inline elements with a single extractable
	 * attribute. No conditions apply to the attribute.
	 * 
	 * @param elementName
	 *            lowercase element name.
	 * @param attributeName
	 *            lowercase attribute name.
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createInlineWithAttributeRule(String elementName, String attributeName) {
		ExtractionRule rule = new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.INLINE_ELEMENT);
		rule.addExtractableAttribute(new AttributeExtractionRule(attributeName));
		return rule;
	}

	/**
	 * Convenience rule factory for excluded elements. Conditions can be applied
	 * after rule creation.
	 * 
	 * @param elementName
	 *            lowercase element name
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createExcludedRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.EXCLUDED_ELEMENT);
	}

	/**
	 * Convenience rule factory for included elements. Conditions can be applied
	 * after rule creation.
	 * 
	 * @param elementName
	 *            lowercase element name
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createIncludedRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.INCLUDED_ELEMENT);
	}

	/**
	 * Convenience rule factory for non-extractable elements with extractable
	 * attributes.. No conditions apply to the attribute.
	 * 
	 * @param elementName
	 *            lowercase element name.
	 * @param attributeName
	 *            lowercase attribute name.
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createExtractableAttributeRule(String elementName, String attributeName) {
		ExtractionRule rule = new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.EXTRACTABLE_ATTRIBUTES);
		rule.addExtractableAttribute(new AttributeExtractionRule(attributeName));
		return rule;
	}

	/**
	 * Convenience rule factory for extractable attributes that may appear on
	 * any element.
	 * 
	 * @param attributeName
	 *            lowercase attribute name
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createExtractableAttributeAnyElementRule(String attributeName) {
		return new ExtractionRule(attributeName);
	}

	/**
	 * Convenience rule factory for extractable elements that start groups.
	 * 
	 * @param elementName
	 *            lowercase element name
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createGroupRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.GROUP_ELEMENT);
	}

	/**
	 * Convenience rule factory for extractable elements where the start and end
	 * tags belong to a {@link TextUnit} rather than a {@link SkeletonUnit}.
	 * 
	 * @param elementName
	 *            lowercase element name
	 * @return {@link ExtractionRule}
	 */
	public static ExtractionRule createTextUnitRule(String elementName) {
		return new ExtractionRule(elementName, EXTRACTION_RULE_TYPE.TEXT_UNIT_ELEMENT);
	}

	/**
	 * Default constructor for all element rules.
	 * 
	 * @param elementName
	 *            lowercase element name.
	 * @param ruleType
	 *            One of EXTRACTION_RULE_TYPE's.
	 */
	public ExtractionRule(String elementName, EXTRACTION_RULE_TYPE ruleType) {
		this.setElementName(elementName);
		this.setAttributeName(null);
		this.ruleType = ruleType;
		this.extractionConditions = new LinkedList<ConditionalAttributeRule>();
		this.extractableAttributes = new LinkedList<AttributeExtractionRule>();
		this.properties = new HashMap<String, String>();
	}

	/**
	 * Default constructor for attribute rule where attribute can occur on many
	 * elements.
	 * 
	 * @param attributeName
	 *            lowercase attribute name.
	 */
	public ExtractionRule(String attributeName) {
		this.setElementName(null);
		this.setAttributeName(attributeName);
		this.ruleType = EXTRACTION_RULE_TYPE.EXTRACTABLE_ATTRIBUTE_ANY_ELEMENT;
		this.extractionConditions = new LinkedList<ConditionalAttributeRule>();
		this.extractableAttributes = null;
		this.properties = new HashMap<String, String>();
	}

	/**
	 * Does this rule contain extractable attributes of any kind?
	 * 
	 * @return true if any extractable attributes, false otherwise.
	 */
	public boolean hasExtractableAttributes() {
		if (extractableAttributes == null && getAttributeName() == null) {
			return false;
		}
		return true;
	}

	/**
	 * Add a condition to this rule.
	 * 
	 * @param conditionalAttribute
	 *            The conditional rule. {@link ConditionalAttributeRule}
	 */
	public void addConditionalAttributeRule(ConditionalAttributeRule conditionalAttribute) {
		this.extractionConditions.add(conditionalAttribute);
	}

	/**
	 * Add an extractable attribute to the rule. Conditions may be applied to
	 * attributeExtractionRule.
	 * 
	 * @param attributeExtractionRule
	 *            extractable attribute {@link AttributeExtractionRule}
	 */
	public void addExtractableAttribute(AttributeExtractionRule attributeExtractionRule) {
		this.extractableAttributes.add(attributeExtractionRule);
	}

	/**
	 * Add a property to the rule which is passed to the {@link Group} or
	 * {@link TextUnit} matching the rule.
	 * 
	 * @param key
	 *            property
	 * @param value
	 *            additional information on the property.
	 */
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * @param elementName
	 *            the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public EXTRACTION_RULE_TYPE getRuleType() {
		return ruleType;
	}
}
