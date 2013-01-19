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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlConfigurationTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void defaultConfiguration() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertTrue(rules.isRuleType("title", TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTE_TRANS));
		assertEquals(TaggedFilterConfiguration.RULE_TYPE.TEXT_UNIT_ELEMENT, rules.getElementRuleTypeCandidate("title"));
		assertEquals(TaggedFilterConfiguration.RULE_TYPE.INLINE_EXCLUDED_ELEMENT, rules.getElementRuleTypeCandidate("abbr"));
		assertEquals(TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY, rules.getElementRuleTypeCandidate("area"));
		assertEquals(TaggedFilterConfiguration.RULE_TYPE.EXCLUDED_ELEMENT, rules.getElementRuleTypeCandidate("script"));
		assertTrue(rules.isRuleType("pre", TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertEquals(TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY, rules.getElementRuleTypeCandidate("meta"));
	}

	@Test
	public void metaTag() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", "keywords");
		assertTrue(rules.isTranslatableAttribute("meta", "content", attributes));
		assertFalse(rules.isTranslatableAttribute("dummy", "content", attributes));
		attributes.put("name", "description");
		assertTrue(rules.isTranslatableAttribute("meta", "content", attributes));
		assertFalse(rules.isTranslatableAttribute("dummy", "content", attributes));

		attributes.clear();
		attributes.put("http-equiv", "content-language");
		assertTrue(rules.isWritableLocalizableAttribute("meta", "content", attributes));

		attributes.clear();
		attributes.put("http-equiv", "content-type");
		assertFalse(rules.isReadOnlyLocalizableAttribute("meta", "content", attributes));

		attributes.clear();
		attributes.put("name", "generator");
		assertTrue(rules.isReadOnlyLocalizableAttribute("meta", "content", attributes));
	}

	@Test
	public void preserveWhiteSpace() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		assertTrue(rules.isRuleType("pre", TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertEquals(rules.getElementRuleTypeCandidate("style"),
				TaggedFilterConfiguration.RULE_TYPE.EXCLUDED_ELEMENT);
		assertTrue(rules.isRuleType("pre", TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertFalse(rules.isRuleType("stylesheet",
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertFalse(rules.isRuleType("p", TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
	}

	@Test
	public void langAndXmlLang() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.clear();
		attributes.put("lang", "en");
		assertTrue(rules.isWritableLocalizableAttribute("x", "lang", attributes));
		assertFalse(rules.isReadOnlyLocalizableAttribute("x", "lang", attributes));
		assertFalse(rules.isTranslatableAttribute("x", "lang", attributes));

		assertTrue(rules.isWritableLocalizableAttribute("p", "lang", attributes));
		assertFalse(rules.isReadOnlyLocalizableAttribute("p", "lang", attributes));
		assertFalse(rules.isTranslatableAttribute("p", "lang", attributes));

		attributes.clear();
		attributes.put("xml:lang", "en");
		assertTrue(rules.isWritableLocalizableAttribute("x", "xml:lang", attributes));
	}

	@Test
	public void genericCodeTypes() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		assertEquals(getElementType(rules, "b"), "bold");
		assertEquals(getElementType(rules, "i"), "italic");
		assertEquals(getElementType(rules, "u"), "underlined");
		assertEquals(getElementType(rules, "img"), "image");
		assertEquals(getElementType(rules, "a"), "link");
		assertEquals(getElementType(rules, "x"), "x");
	}

	@Test
	public void textUnitCodeTypes() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/wellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertEquals(getElementType(rules, "p"), "paragraph");
	}

	@Test
	public void collapseWhitespace() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertFalse(rules.isGlobalPreserveWhitespace());

		url = HtmlConfigurationTest.class.getResource("/collapseWhitespaceOff.yml");
		rules = new TaggedFilterConfiguration(url);
		assertTrue(rules.isGlobalPreserveWhitespace());
	}

	@Test
	public void testCodeFinderRules() {
		URL url = HtmlConfigurationTest.class.getResource("/withCodeFinderRules.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertTrue(rules.isUseCodeFinder());
		InlineCodeFinder cf = new InlineCodeFinder();
		cf.fromString(rules.getCodeFinderRules());
		cf.compile();
		ArrayList<String> list = cf.getRules();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("\\bVAR\\d\\b", list.get(0));
	}

	@Test
	public void inputAttributes() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/nonwellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();

		attributes.clear();
		attributes.put("type", "hidden");
		assertFalse(rules.isTranslatableAttribute("input", "alt", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "value", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "accesskey", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "image");
		assertFalse(rules.isTranslatableAttribute("input", "alt", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "value", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "accesskey", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "submit");
		assertTrue(rules.isTranslatableAttribute("input", "alt", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "value", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "accesskey", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "button");
		assertTrue(rules.isTranslatableAttribute("input", "alt", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "value", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "accesskey", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "title", attributes));
	}

	@Test
	public void attributeID() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/wellformedConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.clear();
		attributes.put("id", "value");
		assertTrue(rules.isIdAttribute("p", "id", attributes));
		assertFalse(rules.isIdAttribute("p", "foo", attributes));
	}

	@SuppressWarnings("unchecked")
	private String getElementType(TaggedFilterConfiguration rules, String elementName) {
		Map<String, Object> rule = rules.getConfigReader().getElementRule(elementName.toLowerCase());
		if (rule != null && rule.containsKey(TaggedFilterConfiguration.ELEMENT_TYPE)) {
			return (String) rule.get(TaggedFilterConfiguration.ELEMENT_TYPE);
		}
		return elementName;
	}
}
