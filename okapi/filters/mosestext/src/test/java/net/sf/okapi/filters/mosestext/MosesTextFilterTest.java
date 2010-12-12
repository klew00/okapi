/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mosestext;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;

import org.junit.Test;
import static org.junit.Assert.*;

public class MosesTextFilterTest {

	private String root;
	private MosesTextFilter filter;
	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locPT = LocaleId.PORTUGUESE;
	private GenericContent fmt;

	public MosesTextFilterTest () {
		filter = new MosesTextFilter();
		URL url = MosesTextFilterTest.class.getResource("/Test01.txt");
		root = Util.getDirectoryName(url.getPath()) + File.separator;
		fmt = new GenericContent();
	}

	@Test
	public void testDefaultInfo () {
		assertNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testStartDocument () {
		URL url = MosesTextFilterTest.class.getResource("/Test01.txt");
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(url.getPath(), null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testLineBreaks_CR () {
		String snippet = "Line 1\rLine 2\r";
		String result = FilterTestDriver.generateOutput(getEvents(snippet), locEN,
			filter.createSkeletonWriter(), filter.getEncoderManager());
		assertEquals(snippet, result);
	}

	@Test
	public void testineBreaks_CRLF () {
		String snippet = "Line 1\r\nLine 2\r\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet), locEN,
			filter.createSkeletonWriter(), filter.getEncoderManager());
		assertEquals(snippet, result);
	}
	
	@Test
	public void testLineBreaks_LF () {
		String snippet = "Line 1\nLine 2\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet), locEN,
			filter.createSkeletonWriter(), filter.getEncoderManager());
		assertEquals(snippet, result);
	}
	
	@Test
	public void testEntry () {
		String snippet = "Line 1\rLine 2";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertNotNull(tu);
		assertEquals("Line 2", tu.getSource().toString());
	}
	
	@Test
	public void testCode1 () {
		String snippet = "Text <x id='1'/>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(snippet, tu.getSource().toString());
		assertEquals("Text <1/>", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testCode2 () {
		String snippet = "<g id='2'>Text</g> <x id='1'/>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(snippet, tu.getSource().toString());
		assertEquals("<2>Text</2> <1/>", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testCode3 () {
		String snippet = "<g id='1'>Text</g><x id='2'/><g id='3'>t2<x id='4'/><g id='5'>t3</g></g>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals(snippet, tu.getSource().toString());
		assertEquals("<1>Text</1><2/><3>t2<4/><5>t3</5></3>", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testSpecialChars () {
		String snippet = "Line 1\rLine 2 with tab[\t] and more [<{|&/\\}>]";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertNotNull(tu);
		assertEquals("Line 2 with tab[\t] and more [<{|&/\\}>]", tu.getSource().toString());
	}
	
	@Test
	public void testFromFile () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEventsFromFile(filter, root+"/Test01.txt"), 2);
		assertNotNull(tu);
		assertEquals("This is a test on line 1,\nand line two.", tu.getSource().toString());
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.txt", null));
		list.add(new InputDocument(root+"Test02.txt", null));
	
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locPT));
	}

	private ArrayList<Event> getEventsFromFile (IFilter filter,
		String path)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(new File(path).toURI(), "UTF-8", locEN, locPT));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
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
