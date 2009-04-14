/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xml.tests;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.xml.XMLFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class XMLFilterTest {

	private XMLFilter xmlFilter;

	@Before
	public void setUp() {
		xmlFilter = new XMLFilter();
	}

	@Test
	public void testExternalFile () {
		FilterTestDriver testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
		XMLFilter filter = null;		
		try {
			filter = new XMLFilter();
			URL url = XMLFilterTest.class.getResource("/Translate1.xml");
			filter.open(new InputResource(new URI(url.toString()), "UTF-16", "en", "es"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}

	@Test
	public void testStartDocument () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r"
			+ "<doc>text</doc>";
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(snippet));
		assertNotNull(sd);
		assertNotNull(sd.getEncoding());
		assertNotNull(sd.getType());
		assertNotNull(sd.getMimeType());
		assertNotNull(sd.getLanguage());
		assertEquals("\r", sd.getLineBreak());
	}
	
	@Test
	public void testOutputBasic_Comment () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><!--c--></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputBasic_PI () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><?pi ?></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputBasic_OneChar () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc>T</doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputBasic_EmptyRoot () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc/>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputSimpleContent () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>test</p></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}

	@Test
	public void testOutputSimpleContent_WithEscapes () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>&amp;=amp, &lt;=lt, &quot;=quot..</p></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputSimpleContent_WithLang () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc xml:lang='en'>test</doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc xml:lang='FR'>test</doc>";
		//TODO: Make this work
		//assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "FR"));
	}
	
	@Test
	public void testOutputSupplementalChars () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<p>[&#x20000;]=U+D840,U+DC00</p>";
		String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<p>[\uD840\uDC00]=U+D840,U+DC00</p>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputWhitespaces_Preserve () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>part 1\npart 2</p>"
			+ "<p xml:space=\"preserve\">part 1\npart 2</p></doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>part 1 part 2</p>"
			+ "<p xml:space=\"preserve\">part 1\npart 2</p></doc>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testOutputWhitespaces_Default () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<p>part 1\npart 2\n  part3\n\t part4</p>";
		String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<p>part 1 part 2 part3 part4</p>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void testSeveralUnits () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>text 1</p><p>text 2</p><p>text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		TextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text 1", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("text 2", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertNotNull(tu);
		assertEquals("text 3", tu.getSource().toString());
	}
	
	@Test
	public void testTranslatableAttributes () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><its:rules version=\"1.0\" xmlns:its=\"http://www.w3.org/2005/11/its\">"
			+ "<its:translateRule selector=\"//*/@text\" translate=\"yes\"/></its:rules>"
			+ "<p text=\"value 1\">text 1</p><p>text 2</p><p>text 3</p></doc>";
		ArrayList<Event> list = getEvents(snippet);
		TextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("value 1", tu.getSource().toString());
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		xmlFilter.open(new InputResource(snippet, "en"));
		while (xmlFilter.hasNext()) {
			Event event = xmlFilter.next();
			list.add(event);
		}
		xmlFilter.close();
		return list;
	}

}
