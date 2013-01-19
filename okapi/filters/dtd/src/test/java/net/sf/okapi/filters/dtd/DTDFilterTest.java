/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.dtd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.dtd.DTDFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;

import org.junit.Before;
import org.junit.Test;

public class DTDFilterTest {

	private DTDFilter filter;
	private LocaleId locEN = LocaleId.fromString("en");

	@Before
	public void setUp() {
		filter = new DTDFilter();
	}

	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testStartDocument () throws URISyntaxException {
		URL url = DTDFilterTest.class.getResource("/Test01.dtd");
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(url.toURI().getPath(), null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testSimpleEntry () {
		String snippet = "<!--Comment-->\n<!ENTITY entry1 \"Text1\"><!ENTITY test2 \"text2\">";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.getSource().toString());
		assertEquals("entry1", tu.getName());
		Property prop = tu.getProperty(Property.NOTE);
		assertNotNull(prop);
		assertEquals("Comment", prop.getValue());
	}
	
	@Test
	public void testLineBreaks () {
		String snippet = "<!--Comment-->\r<!ENTITY entry1 \"Text1\">\r";
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(snippet));
		assertNotNull(sd);
		assertEquals("\r", sd.getLineBreak());
	}

	@Test
	public void testEntryWithEnitties () {
		String snippet = "<!ENTITY entry1 \"&ent1;=ent1, %pent1;=pent1\">";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		List<Code> codes = tu.getSource().getFirstContent().getCodes();
		assertEquals(2, codes.size());
		assertEquals("&ent1;", codes.get(0).getData());
		assertEquals("%pent1;", codes.get(1).getData());
	}
	
	@Test
	public void testEntryWithNCRs () {
		String snippet = "<!ENTITY entry1 \"&#xe3;, &#xE3;, &#227;\">";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("\u00e3, \u00e3, \u00e3", tu.getSource().toString());
	}
	
//	@Test
//	public void testLineBreaks () {
//		String snippet = "<!--Comment-->\r<!ENTITY e1 \"t1\">\r<!ENTITY e2 \"t2\">\r";
//TODO		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), locEN));
//	}
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		URL url = DTDFilterTest.class.getResource("/Test01.dtd");
		list.add(new InputDocument(url.toURI().getPath(), null));
		url = DTDFilterTest.class.getResource("/Test02.dtd");
		list.add(new InputDocument(url.toURI().getPath(), null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locEN));
	}

	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
