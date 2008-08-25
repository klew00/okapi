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

import static net.sf.okapi.filters.html.ConditionalAttribute.CONDITIONAL_ATTRIBUTE_TYPE.EQUALS;
import static net.sf.okapi.filters.html.ElementExtractionRule.EXTRACTION_RULE_TYPE.EXCLUDED_ELEMENT;
import static net.sf.okapi.filters.html.ElementExtractionRule.EXTRACTION_RULE_TYPE.EXTRACTABLE_ATTRIBUTES;
import static net.sf.okapi.filters.html.ElementExtractionRule.EXTRACTION_RULE_TYPE.INLINE_ELEMENT;

import java.util.HashMap;
import java.util.Map;

public class HtmlFilterConfiguration {
	private static final String LOCALIZABLE_PROPERTY = "localizable";
	
	private Map<String, ElementExtractionRule> ruleMap;
	
	public HtmlFilterConfiguration() {
		ruleMap = new HashMap<String, ElementExtractionRule>();
	}
	
	public ElementExtractionRule getRule(String ruleName) {
		return ruleMap.get(ruleName);
	}
	
	public void addRule(String ruleName, ElementExtractionRule rule) {
		ruleMap.put(ruleName, rule);
	}
	
	public void clearRules() {
		ruleMap.clear();
	}
	
	public void initializeDefaultRules() {
		ElementExtractionRule rule;
		
		// default inline elements with option extractable attributes
		rule = new ElementExtractionRule("a", INLINE_ELEMENT);
		rule.addExtractionCondition(new ConditionalAttribute("title"));
		ruleMap.put("a", rule);
		
		ruleMap.put("b", new ElementExtractionRule("b", INLINE_ELEMENT));		
		ruleMap.put("big", new ElementExtractionRule("big", INLINE_ELEMENT));		
		ruleMap.put("em", new ElementExtractionRule("em", INLINE_ELEMENT));		
		ruleMap.put("font", new ElementExtractionRule("font", INLINE_ELEMENT));		
		ruleMap.put("i", new ElementExtractionRule("i", INLINE_ELEMENT));
		
		rule = new ElementExtractionRule("img", INLINE_ELEMENT);
		rule.addExtractionCondition(new ConditionalAttribute("title"));
		rule.addExtractionCondition(new ConditionalAttribute("alt"));
		ruleMap.put("img", rule);
		
		ruleMap.put("s", new ElementExtractionRule("s", INLINE_ELEMENT));		
		ruleMap.put("samp", new ElementExtractionRule("samp", INLINE_ELEMENT));
		ruleMap.put("small", new ElementExtractionRule("small", INLINE_ELEMENT));		
		ruleMap.put("span", new ElementExtractionRule("span", INLINE_ELEMENT));		
		ruleMap.put("strike", new ElementExtractionRule("strike", INLINE_ELEMENT));		
		ruleMap.put("strong", new ElementExtractionRule("strong", INLINE_ELEMENT));		
		ruleMap.put("sub", new ElementExtractionRule("sub", INLINE_ELEMENT));		
		ruleMap.put("sup", new ElementExtractionRule("sup", INLINE_ELEMENT));		
		ruleMap.put("u", new ElementExtractionRule("u", INLINE_ELEMENT));
		
		// Ruby inline	
		ruleMap.put("ruby", new ElementExtractionRule("ruby", INLINE_ELEMENT));
		ruleMap.put("rt", new ElementExtractionRule("rc", INLINE_ELEMENT));
		ruleMap.put("rc", new ElementExtractionRule("rc", INLINE_ELEMENT));
		ruleMap.put("rp", new ElementExtractionRule("rp", INLINE_ELEMENT));
		ruleMap.put("rbc", new ElementExtractionRule("rbc", INLINE_ELEMENT));
		ruleMap.put("rtc", new ElementExtractionRule("rtc", INLINE_ELEMENT));
		
		// RoboHelp specific
		ruleMap.put("u", new ElementExtractionRule("symbol", INLINE_ELEMENT));
		ruleMap.put("u", new ElementExtractionRule("face", INLINE_ELEMENT));
		
		// excluded elements (includes children)
		ruleMap.put("style", new ElementExtractionRule("style", EXCLUDED_ELEMENT));		
		ruleMap.put("stylesheet", new ElementExtractionRule("stylesheet", EXCLUDED_ELEMENT));
		
		// extractable attributes (not inline)
		rule = new ElementExtractionRule("div", EXTRACTABLE_ATTRIBUTES);
		rule.addExtractionCondition(new ConditionalAttribute("title"));
		ruleMap.put("div", rule);
		
		rule = new ElementExtractionRule("table", EXTRACTABLE_ATTRIBUTES);
		rule.addExtractionCondition(new ConditionalAttribute("summary"));
		ruleMap.put("table", rule);
		
		// input attributes
		AttributeExtractionRule attrRule;
		rule = new ElementExtractionRule("input", EXTRACTABLE_ATTRIBUTES);
		attrRule = new AttributeExtractionRule("title");
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "button"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "default"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "submit"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "reset"));				
		rule.addExtractableAttribute(attrRule);
		
		attrRule = new AttributeExtractionRule("value");
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "button"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "default"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "submit"));
		attrRule.addExtractionCondition(new ConditionalAttribute("type", EQUALS, "reset"));				
		rule.addExtractableAttribute(attrRule);
		
		// localizable - but not translatable
		rule = new ElementExtractionRule("href", EXTRACTABLE_ATTRIBUTES);
		rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("href", rule);
		
		rule = new ElementExtractionRule("meta", EXTRACTABLE_ATTRIBUTES);
		rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("content", rule);
		
		rule = new ElementExtractionRule("table", EXTRACTABLE_ATTRIBUTES);
		rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("summary", rule);
	}
}
