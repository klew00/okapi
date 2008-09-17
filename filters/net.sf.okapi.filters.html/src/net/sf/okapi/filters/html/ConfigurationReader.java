/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class ConfigurationReader implements IHtmlFilterConfiguration {
	
	private static String DEFAULT_CONFIG;
	
	private boolean preserveWhitespace;
	private ConfigSlurper configSlurper;
	private ConfigObject config;
	private HashMap<String, ExtractionRule> ruleMap; 
	
	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	public ConfigurationReader() {
		DEFAULT_CONFIG = 
			"enum Day {INLINE, GROUP, EXCLUDED, INCLUDED, TEXT_UNIT, PRESERVE_WHITESPACE}\n" +
			"preserveWhitespace=false\n" +			
			"pre { rules=[PRESERVE_WHITESPACE] }\n" +
			"a { rules=[INLINE] translatableAttributes=['title'] }\n";		
		
		configSlurper = new ConfigSlurper();		
		config = configSlurper.parse(DEFAULT_CONFIG);		
	}
	
	public ConfigurationReader(String configurationPath) {
		configSlurper = new ConfigSlurper();
		URL url = HtmlParser.class.getResource(configurationPath);
		config = configSlurper.parse(url);		
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
		DEFAULT_CONFIG =			
			"inline = ['a', 'b', 'big', 'em', 'font', 'i', 'img', 's', 'samp', " +
			"'small', 'span', 'strike', 'strong', 'sub', 'sup', 'u', 'ruby', 'rt'" +
			"'rc', 'rp', 'rbc', 'rtc', 'symbol', 'face']\n" +
			"exclude = ['style', 'stylesheet']\n" +
			"include = []\n" +
			"group = []\n" +
			"textUnit = []\n" +
			"preserveWhitespace = ['pre']\n" +
									
			"a { rules=[INLINE:true] translatableAttributes=}\n";		
		
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
		
		rule = ExtractionRule.createInlineRule("img");
		rule.addExtractableAttribute(new AttributeExtractionRule("title"));
		rule.addExtractableAttribute(new AttributeExtractionRule("alt"));
		ruleMap.put("img", rule);
		
		// excluded elements (includes children)
		// TODO: we want to handle script sections with sub-filters
		ruleMap.put("script", ExtractionRule.createExcludedRule("script"));		
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
		//rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("href", rule);
		
		rule = ExtractionRule.createExtractableAttributeRule("meta", "content");
		//rule.addProperty(LOCALIZABLE_PROPERTY, LOCALIZABLE_PROPERTY);
		ruleMap.put("meta", rule);		
	}
}
