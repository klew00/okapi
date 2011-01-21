/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif.tests;

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.mif.MIFFilter;

import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MIFFilterTest {
	
	private LocaleId locEN = LocaleId.fromString("en");

	private String root;
	private MIFFilter filter;
	
	@Before
	public void setUp() {
		filter = new MIFFilter();
		URL url = MIFFilterTest.class.getResource("/Test01.mif");
		root = Util.getDirectoryName(url.getPath()) + File.separator;
	}

	@Test
	public void testDefaultInfo () {
		//Not using parameters yet: assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"Test01.mif", null),
			"UTF-8", locEN, locEN));
	}

	@Test
	public void testSimpleText () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents("Test01.mif"), 1);
//		assertNotNull(tu);
//		assertEquals("Text1", tu.getSource().toString());
	}
	
	@Test
	public void testOutput () {
		rewriteFile("Test01.mif");
		rewriteFile("Test02-v9.mif");
		rewriteFile("Test01-v7.mif");
	}
	
	private void rewriteFile (String fileName) {
		filter.open(new RawDocument(Util.toURI(root+fileName), "Window-1252", locEN));
		IFilterWriter writer = filter.createFilterWriter();
		writer.setOptions(locEN, "Windows-1252");
		writer.setOutput(root+fileName+".out.mif");
		while ( filter.hasNext() ) {
			writer.handleEvent(filter.next());
		}
		writer.close();
		filter.close();
	}
	
	@Test
	public void testDoubleExtraction () throws MalformedURLException {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.mif", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "Windows-1252", locEN, locEN));
	}

	private ArrayList<Event> getEvents(String filename) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(Util.toURI(root+filename), "UTF-8", locEN));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
}
