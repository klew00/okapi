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
package net.sf.okapi.filters.html.tests;

import java.util.HashMap;
import java.util.Map;

import net.sf.okapi.common.groovy.GroovyFilterConfiguration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlConfigurationTest {
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void defaultConfiguration() {		
		GroovyFilterConfiguration rules = new GroovyFilterConfiguration("/net/sf/okapi/filters/html/defaultConfiguration.groovy");	
		assertEquals(rules.getMainRuleType("title"), GroovyFilterConfiguration.RULE_TYPE.ATTRIBUTE);
		assertEquals(rules.getMainRuleType("abbr"), GroovyFilterConfiguration.RULE_TYPE.INLINE_ELEMENT);
		assertEquals(rules.getMainRuleType("area"), GroovyFilterConfiguration.RULE_TYPE.ATTRIBUTES_ONLY);
		assertEquals(rules.getMainRuleType("script"), GroovyFilterConfiguration.RULE_TYPE.SCRIPT_ELEMENT);
		assertEquals(rules.getMainRuleType("pre"), GroovyFilterConfiguration.RULE_TYPE.PRESERVE_WHITESPACE);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("http-equiv", "keywords");		
		assertTrue(rules.isTranslatableAttribute("meta", "content", attributes));
		assertFalse(rules.isTranslatableAttribute("dummy", "content", attributes));
		
		attributes.clear();
		attributes.put("http-equiv", "content-language");
		assertTrue(rules.isLocalizableAttribute("meta", "content", attributes));

		attributes.clear();
		attributes.put("http-equiv", "content-type");
		assertTrue(rules.isLocalizableAttribute("meta", "content", attributes));
	}
}
