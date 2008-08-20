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


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import net.sf.okapi.filters.html.HtmlFilterConfiguration;
import net.sf.okapi.filters.html.ExtractionRule;

/**
 * @author HargraveJE
 *
 */
public class HtmlConfigurationTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void defaultConfiguration() {
		ExtractionRule rule;
		HtmlFilterConfiguration rules = new HtmlFilterConfiguration();
		rules.initializeDefaultRules();
		
		rule = rules.getRule("a");
		assertEquals(rule.getElementName(), "a");
		
		rule = rules.getRule("img");
		assertEquals(rule.getElementName(), "img");
		assertTrue(rule.hasExtractableAttributes());
	}
}
