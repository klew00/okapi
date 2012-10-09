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

package net.sf.okapi.filters.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.wiki.WikiFilter;

import org.junit.Before;
import org.junit.Test;

public class WikiFilterTest {
	
	private WikiFilter filter;
	private String root;
		
	@Before
	public void setUp() {
		filter = new WikiFilter();
		filter.setOptions(LocaleId.ENGLISH, LocaleId.SPANISH, "UTF-8", true);
		root = TestUtil.getParentDir(this.getClass(), "/dokuwiki.txt");
	}
	
	@Test
	public void testDefaultInfo () {		
		assertNotNull(filter.getName());
		assertNotNull(filter.getDisplayName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}
	
	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root + "dokuwiki.txt", null),
			"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));
	}

	@Test
	public void testSimpleLine() {
		String snippet = "This is a test.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("This is a test.", tu.getSource().toString());
	}
	
	@Test
	public void testMultipleLines() {
		String snippet = "This is \na \ntest.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("This is a test.", tu.getSource().toString());
	}
	
	@Test
	public void testHeader() {
		String snippet = "=== This is a test. ===";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("This is a test.", tu.getSource().toString());
	}
	
	@Test
	public void testTable() {
		String snippet = "^ Table header 1 ^ Table header 2 |\n"
					+ "| Table cell 1 | Table cell 2 |";
		ArrayList<Event> events = getEvents(snippet);
		ITextUnit tu1 = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu1);
		assertEquals("Table header 1", tu1.getSource().toString());
		ITextUnit tu2 = FilterTestDriver.getTextUnit(events, 4);
		assertNotNull(tu2);
		assertEquals("Table cell 2", tu2.getSource().toString());
	}
	
	@Test
	public void testImageCaption() {
		String snippet = "This is a test. {{image.jpg|This is a caption.}}";
		ArrayList<Event> events = getEvents(snippet);
		ITextUnit tu1 = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu1);
		assertEquals("This is a caption.", tu1.getSource().toString());
		ITextUnit tu2 = FilterTestDriver.getTextUnit(events, 2);
		assertNotNull(tu2);
		assertEquals("This is a test. {{image.jpg|[#$tu2]}}", tu2.getSource().toString());
		assertEquals("This is a test. ", tu2.getSource().getCodedText());
	}
	
	@Test
	public void testDoubleExtraction() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"dokuwiki.txt", null));		
		RoundTripComparison rtc = new RoundTripComparison(false);
		assertTrue(rtc.executeCompare(filter, list, "utf-8", LocaleId.ENGLISH, LocaleId.ENGLISH));
	}
	
	@Test
	public void testOpenTwiceWithString() {
		RawDocument rawDoc = new RawDocument("This is a test.", LocaleId.ENGLISH);
		filter.open(rawDoc);
		filter.open(rawDoc);
		filter.close();
	}
	
	private ArrayList<Event> getEvents(String snippet)
	{
		ArrayList<Event> list = new ArrayList<Event>();		
		filter.open(new RawDocument(snippet, LocaleId.ENGLISH, LocaleId.SPANISH));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
}
