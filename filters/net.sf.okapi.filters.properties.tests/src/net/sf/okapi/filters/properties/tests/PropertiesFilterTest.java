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

package net.sf.okapi.filters.properties.tests;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.filters.properties.PropertiesFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PropertiesFilterTest {
	
	private PropertiesFilter filter;

	@Before
	public void setUp() {
		filter = new PropertiesFilter();
	}

	@Test
	public void externalFileTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(-1);
		testDriver.setShowSkeleton(true);
		try {
			URL url = PropertiesFilterTest.class.getResource("/Test01.properties");
			filter.open(new InputResource(new URI(url.toString()), "windows-1252", "en", "es"));
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
	public void lineBreaksTest () {
		String snippet = "Key1=Text1\rKey2=Text2\r";
		String result = FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en");
		assertEquals(snippet, result);
		snippet = "Key1=Text1\r\nKey2=Text2\r\n";
		result = FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en");
		assertEquals(snippet, result);
		snippet = "Key1=Text1\n\n\nKey2=Text2\n";
		result = FilterTestDriver.generateOutput(getEvents(snippet), snippet, "en");
		assertEquals(snippet, result);
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new InputResource(snippet, "en"));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
