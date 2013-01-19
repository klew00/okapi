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

package net.sf.okapi.filters.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.LocaleId;

import org.junit.Before;
import org.junit.Test;

public class JSONFilterTest {

	private JSONFilter filter;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");

	@Before
	public void setUp() {
		filter = new JSONFilter();
		root = TestUtil.getParentDir(this.getClass(), "/test01.json");
	}

	@Test
	public void testAllWithKeyNoException () {
		String snippet = "{ \"key1\" : \"Text1\" }";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, null), 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.getSource().toString());
		assertEquals("key1", tu.getName());
	}
	
	@Test
	public void testAllWithKeywithException () {
		String snippet = "{ \"key1\" : \"Text1\" }";
		Parameters params = new Parameters(); // Default: all with keys
		params.setExceptions("key?"); // Except those
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, params), 1);
		assertTrue(tu==null);
	}
	
	@Test
	public void testNoneWithKeywithException () {
		String snippet = "{ \"key1\" : \"Text1\" }";
		Parameters params = new Parameters();
		params.setExtractAllPairs(false); // None with key
		params.setExceptions("key?"); // Except those
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, params), 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.getSource().toString());
		assertEquals("key1", tu.getName());
	}
	
	@Test
	public void testStandaloneYes () {
		String snippet = "{ \"key\" : [ \"Text1\", \"Text2\" ] }";
		Parameters params = new Parameters();
		params.setExtractStandalone(true);
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, params), 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.getSource().toString());
		assertTrue(tu.getName()==null);
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, params), 2);
		assertNotNull(tu);
		assertEquals("Text2", tu.getSource().toString());
	}
	
	@Test
	public void testStandaloneDefaultWhichIsNo () {
		String snippet = "{ \"key\" : [ \"Text1\" ] }";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, null), 1);
		assertTrue(tu==null);
	}
	
	@Test
	public void testEscape () {
		String snippet = "{ \"key1\" : \"agrave=\\u00E0\" }";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, null), 1);
		assertNotNull(tu);
		assertEquals("agrave=\u00e0", tu.getSource().toString());
		assertEquals("key1", tu.getName());
	}
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"test01.json", null));
		list.add(new InputDocument(root+"test02.json", null));
		list.add(new InputDocument(root+"test03.json", null));
		list.add(new InputDocument(root+"test04.json", null));
		list.add(new InputDocument(root+"test05.json", null));
		list.add(new InputDocument(root+"test06.json", null));

		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locEN));
	}

	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		assertNotNull(filter.getDisplayName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testSimpleEntrySkeleton () {
		String snippet = "  {\r  \"key1\" :  \"Text1\"  } \r ";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet, null),
			filter.getEncoderManager(), locEN));
	}

	@Test
	public void testLineBreaks () {
		String snippet = "{ \"key1\" : \"Text1\" }\r";
		StartDocument sd = FilterTestDriver.getStartDocument(getEvents(snippet, null));
		assertNotNull(sd);
		assertEquals("\r", sd.getLineBreak());
	}
	
	private ArrayList<Event> getEvents(String snippet, IParameters params) {
		ArrayList<Event> list = new ArrayList<Event>();
		if ( params == null ) {
			params = filter.getParameters();
			params.reset();
		}
		else {
			filter.setParameters(params);
		}
		filter.open(new RawDocument(snippet, locEN));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
