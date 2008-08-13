/* Copyright (C) 2008 Jim Hargrave
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;

public class ElementExtractionRule {

	public static enum EXTRACTION_RULE_TYPE {
		EXTRACTABLE_ATTRIBUTES, INLINE_ELEMENT, EXCLUDED_ELEMENT, INCLUDED_ELEMENT, GROUP_ELEMENT, TEXT_UNIT_ELEMENT
	};

	private String elementName;
	private List<ConditionalAttribute> extractionConditions;
	private List<AttributeExtractionRule> extractableAttributes;
	private EXTRACTION_RULE_TYPE ruleType;
	private Map<String, String> properties;
	
	public ElementExtractionRule(String elementName, EXTRACTION_RULE_TYPE ruleType) {
		this.setElementName(elementName);
		this.ruleType = ruleType;
		this.extractionConditions = new LinkedList<ConditionalAttribute>();
		this.extractableAttributes = new LinkedList<AttributeExtractionRule>();
		this.properties = new HashMap<String, String>();
	}

	public boolean hasExtractableAttributes() {
		if (extractableAttributes == null || ruleType == EXTRACTION_RULE_TYPE.EXCLUDED_ELEMENT
				|| ruleType == EXTRACTION_RULE_TYPE.EXCLUDED_ELEMENT) {
			return false;
		}
		return true;
	}

	public void addExtractionCondition(ConditionalAttribute conditionalAttribute) {
		this.extractionConditions.add(conditionalAttribute);
	}

	public void addExtractableAttribute(
			AttributeExtractionRule attributeExtractionRule) {
		this.extractableAttributes.add(attributeExtractionRule);
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public void processRules(Element element) {
	}

	/**
	 * @param elementName the elementName to set
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
}
