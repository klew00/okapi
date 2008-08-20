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

import static net.sf.okapi.filters.html.ConditionalAttributeRule.CONDITIONAL_ATTRIBUTE_TYPE.EQUALS;
import static net.sf.okapi.filters.html.ExtractionRule.EXTRACTION_RULE_TYPE.EXTRACTABLE_ATTRIBUTES;

import java.util.HashMap;
import java.util.Map;

import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class HtmlFilterConfiguration {
	private static final String LOCALIZABLE_PROPERTY = "localizable";
	
	private Map<String, ExtractionRule> ruleMap;
	
	public HtmlFilterConfiguration() {
		ruleMap = new HashMap<String, ExtractionRule>();
	}
	
	public ExtractionRule getRule(String ruleName) {
		return ruleMap.get(ruleName);
	}
	
	public void addRule(String ruleName, ExtractionRule rule) {
		ruleMap.put(ruleName, rule);
	}
	
	public void clearRules() {
		ruleMap.clear();
		//TODO How to unregister custom Jericho tags??
	}
		
	public void initializeDefaultRules() {
		// register custom tags
		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags, otherwise
											// they override processing
											// instructions
		MasonTagTypes.register();		
		ExtractionRule rule;
		
		// default inline elements with option extractable attributes
		ruleMap.put("a", ExtractionRule.createInlineWithAttributeRule("a", "title"));
		
		ruleMap.put("b", ExtractionRule.createInlineRule("b"));		
		ruleMap.put("big", ExtractionRule.createInlineRule("big"));		
		ruleMap.put("em", ExtractionRule.createInlineRule("em"));		
		ruleMap.put("font", ExtractionRule.createInlineRule("font"));		
		ruleMap.put("i", ExtractionRule.createInlineRule("i"));
		
		rule = ExtractionRule.createInlineRule("img");
		rule.addExtractableAttribute(new AttributeExtractionRule("title"));
		rule.addExtractableAttribute(new AttributeExtractionRule("alt"));
		ruleMap.put("img", rule);
		
		ruleMap.put("s", ExtractionRule.createInlineRule("s"));		
		ruleMap.put("samp", ExtractionRule.createInlineRule("samp"));
		ruleMap.put("small", ExtractionRule.createInlineRule("small"));		
		ruleMap.put("span", ExtractionRule.createInlineRule("span"));		
		ruleMap.put("strike", ExtractionRule.createInlineRule("strike"));		
		ruleMap.put("strong", ExtractionRule.createInlineRule("strong"));		
		ruleMap.put("sub", ExtractionRule.createInlineRule("sub"));		
		ruleMap.put("sup", ExtractionRule.createInlineRule("sup"));		
		ruleMap.put("u", ExtractionRule.createInlineRule("u"));
		
		// Ruby inline	
		ruleMap.put("ruby", ExtractionRule.createInlineRule("ruby"));
		ruleMap.put("rt", ExtractionRule.createInlineRule("rt"));
		ruleMap.put("rc", ExtractionRule.createInlineRule("rc"));
		ruleMap.put("rp", ExtractionRule.createInlineRule("rp"));
		ruleMap.put("rbc", ExtractionRule.createInlineRule("rbc"));
		ruleMap.put("rtc", ExtractionRule.createInlineRule("rtc"));
		
		// RoboHelp specific
		ruleMap.put("symbol", ExtractionRule.createInlineRule("symbol"));
		ruleMap.put("face", ExtractionRule.createInlineRule("face"));
		
		// excluded elements (includes children)
		ruleMap.put("style", ExtractionRule.createExcludedRule("style"));		
		ruleMap.put("stylesheet", ExtractionRule.createExcludedRule("stylesheet"));
		
		// extractable attributes (not inline)
		ruleMap.put("div", ExtractionRule.createExtractableAttributeRule("div", "title"));
		
		ruleMap.put("table", ExtractionRule.createExtractableAttributeRule("table", "summary"));
		
		// input conditional extractable attributes
		AttributeExtractionRule attrRule;
		rule = new ExtractionRule("input", EXTRACTABLE_ATTRIBUTES);
		attrRule = new AttributeExtractionRule("title");
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "button"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "default"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "submit"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "reset"));				
		rule.addExtractableAttribute(attrRule);
		
		attrRule = new AttributeExtractionRule("value");
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "button"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "default"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "submit"));
		attrRule.addConditionalAttributeRule(new ConditionalAttributeRule("type", EQUALS, "reset"));				
		rule.addExtractableAttribute(attrRule);
		
		// localizable - but not translatable
		rule = ExtractionRule.createExtractableAttributeAnyElementRule("href");
		rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("href", rule);
		
		rule = ExtractionRule.createExtractableAttributeRule("meta", "content");
		rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("meta", rule);		
	}
}
