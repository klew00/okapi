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
import java.util.HashMap;
import java.util.Map;

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
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertEquals(rules.getMainRuleType("title"),
				TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTE_TRANS);
		assertEquals(rules.getMainRuleType("abbr"),
				TaggedFilterConfiguration.RULE_TYPE.INLINE_ELEMENT);
		assertEquals(rules.getMainRuleType("area"),
				TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY);
		assertEquals(rules.getMainRuleType("script"),
				TaggedFilterConfiguration.RULE_TYPE.EXCLUDED_ELEMENT);
		assertEquals(rules.getMainRuleType("pre"),
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE);
		assertEquals(rules.getMainRuleType("meta"),
				TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY);
	}

	@Test
	public void metaTag() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", "keywords");
		assertTrue(rules.isTranslatableAttribute("meta", "content", attributes));
		assertFalse(rules.isTranslatableAttribute("dummy", "content",
				attributes));
		attributes.put("name", "description");
		assertTrue(rules.isTranslatableAttribute("meta", "content", attributes));
		assertFalse(rules.isTranslatableAttribute("dummy", "content",
				attributes));

		attributes.clear();
		attributes.put("http-equiv", "content-language");
		assertTrue(rules.isWritableLocalizableAttribute("meta", "content",
				attributes));

		attributes.clear();
		attributes.put("http-equiv", "content-type");
		assertFalse(rules.isReadOnlyLocalizableAttribute("meta", "content",
				attributes));

		attributes.clear();
		attributes.put("name", "generator");
		assertTrue(rules.isReadOnlyLocalizableAttribute("meta", "content",
				attributes));
	}

	@Test
	public void preserveWhileSpace() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		assertEquals(rules.getMainRuleType("pre"),
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE);
		assertEquals(rules.getMainRuleType("style"),
				TaggedFilterConfiguration.RULE_TYPE.EXCLUDED_ELEMENT);
		assertTrue(rules.isRuleType("pre",
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertFalse(rules.isRuleType("stylesheet",
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
		assertFalse(rules.isRuleType("p",
				TaggedFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE));
	}

	@Test
	public void langAndXmlLang() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.clear();
		attributes.put("lang", "en");
		assertTrue(rules
				.isWritableLocalizableAttribute("x", "lang", attributes));
		assertFalse(rules.isReadOnlyLocalizableAttribute("x", "lang",
				attributes));
		assertFalse(rules.isTranslatableAttribute("x", "lang", attributes));

		assertTrue(rules
				.isWritableLocalizableAttribute("p", "lang", attributes));
		assertFalse(rules.isReadOnlyLocalizableAttribute("p", "lang",
				attributes));
		assertFalse(rules.isTranslatableAttribute("p", "lang", attributes));

		attributes.clear();
		attributes.put("xml:lang", "en");
		assertTrue(rules.isWritableLocalizableAttribute("x", "xml:lang",
				attributes));
	}

	@Test
	public void genericCodeTypes() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		assertEquals(rules.getElementType("b"), "bold");
		assertEquals(rules.getElementType("i"), "italic");
		assertEquals(rules.getElementType("u"), "underlined");
		assertEquals(rules.getElementType("img"), "image");
		assertEquals(rules.getElementType("a"), "link");
		//assertEquals(rules.getElementType("p"), "paragraph");
		assertEquals(rules.getElementType("x"), "x");
	}

	@Test
	public void collapseWhitespace() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);
		assertTrue(rules.collapseWhitespace());

		url = HtmlConfigurationTest.class
				.getResource("/collapseWhitespaceOff.yml");
		rules = new TaggedFilterConfiguration(url);
		assertFalse(rules.collapseWhitespace());
	}

	@Test
	public void inputAttributes() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();

		attributes.clear();
		attributes.put("type", "hidden");
		assertFalse(rules.isTranslatableAttribute("input", "alt", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "value", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "accesskey",
				attributes));
		assertFalse(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "image");
		assertFalse(rules.isTranslatableAttribute("input", "alt", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "value", attributes));
		assertFalse(rules.isTranslatableAttribute("input", "accesskey",
				attributes));
		assertFalse(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "submit");
		assertTrue(rules.isTranslatableAttribute("input", "alt", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "value", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "accesskey",
				attributes));
		assertTrue(rules.isTranslatableAttribute("input", "title", attributes));

		attributes.clear();
		attributes.put("type", "button");
		assertTrue(rules.isTranslatableAttribute("input", "alt", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "value", attributes));
		assertTrue(rules.isTranslatableAttribute("input", "accesskey",
				attributes));
		assertTrue(rules.isTranslatableAttribute("input", "title", attributes));
	}

	//@Test
	public void attributeID() {
		URL url = HtmlFilter.class
				.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.clear();
		attributes.put("id", "value");
		assertTrue(rules.isIdAttribute("p", "id", attributes));
		assertFalse(rules.isIdAttribute("p", "foo", attributes));
	}
}
