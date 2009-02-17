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
package net.sf.okapi.filters.openxml.tests;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OpenXMLConfigurationTest {
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void defaultConfiguration() {		
		URL url = OpenXMLConfigurationTest.class.getResource("/net/sf/okapi/filters/openxml/wordConfiguration.yml");
		TaggedFilterConfiguration rules = new TaggedFilterConfiguration(url);	
		assertEquals(rules.getMainRuleType("w:p"), TaggedFilterConfiguration.RULE_TYPE.TEXT_UNIT_ELEMENT);
		assertEquals(rules.getMainRuleType("wp:docpr"), TaggedFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY);
		
		Map<String, String> attributes = new HashMap<String, String>();
		assertTrue(rules.isTranslatableAttribute("wp:docpr", "name", attributes));
		assertFalse(rules.isTranslatableAttribute("pic:cnvpr", "name", attributes));
		
		attributes.clear();
		attributes.put("w:val", "content-language");
		assertTrue(rules.isWritableLocalizableAttribute("w:lang", "w:val", attributes));

/*	
		attributes.clear();
		attributes.put("http-equiv", "content-type");
		assertFalse(rules.isReadOnlyLocalizableAttribute("meta", "content", attributes));
		
		attributes.clear();
		attributes.put("name", "generator");
		assertTrue(rules.isReadOnlyLocalizableAttribute("meta", "content", attributes));
*/
		url = OpenXMLConfigurationTest.class.getResource("/net/sf/okapi/filters/openxml/excelConfiguration.yml");
		rules = new TaggedFilterConfiguration(url);	
		assertEquals(rules.getMainRuleType("t"), TaggedFilterConfiguration.RULE_TYPE.TEXT_UNIT_ELEMENT);
		assertEquals(rules.getMainRuleType("a:br"), TaggedFilterConfiguration.RULE_TYPE.INLINE_ELEMENT);

		url = OpenXMLConfigurationTest.class.getResource("/net/sf/okapi/filters/openxml/excelConfiguration.yml");
		rules = new TaggedFilterConfiguration(url);	
		assertEquals(rules.getMainRuleType("a:t"), TaggedFilterConfiguration.RULE_TYPE.INLINE_ELEMENT);
	}
}
