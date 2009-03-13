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

import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.xml.XMLFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XMLFilterTest {

	private XMLFilter xmlFilter;

	@Before
	public void setUp() {
		xmlFilter = new XMLFilter();
	}

	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		XMLFilter filter = null;		
		try {
			filter = new XMLFilter();
			filter.setOptions("en", "es", "UTF-16", true);
			
			URL url = XMLFilterTest.class.getResource("/input.xml");
			filter.open(url.toURI());
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();

//			filter.open("<doc>\n <h>\n  <t>text1</t>\n  <t>text2</t></h></doc>");
//			if ( !testDriver.process(filter) ) Assert.fail();
//			filter.close();
			
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
	public void basicElement () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><!--c--></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
		snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><?pi ?></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
		snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc>T</doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
		snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc/>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
	}
	
	@Test
	public void simpleContent () {
		String snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>test</p></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
		snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc><p>&amp;=amp, &lt;=lt, &quot;=quot..</p></doc>";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en"));
		snippet = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc xml:lang='en'>test</doc>";
		String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<doc xml:lang='FR'>test</doc>";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet), snippet, "FR"));
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		xmlFilter.open(snippet);
		while (xmlFilter.hasNext()) {
			Event event = xmlFilter.next();
			list.add(event);
		}
		xmlFilter.close();
		return list;
	}

	
}
