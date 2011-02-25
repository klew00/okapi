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
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.mif.MIFFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MIFFilterTest {

	private final static String STARTMIF = "<MIFFile 9.00 <TextFlow <Para ";
	private final static String ENDMIF = ">>>";
	
	private LocaleId locEN = LocaleId.fromString("en");

	private String root;
	private MIFFilter filter;
	private GenericContent fmt = new GenericContent();
	
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
			null, locEN, locEN));
	}

	@Test
	public void testSimpleText () {
		List<Event> list = getEventsFromFile("Test01.mif");
		TextUnit tu = FilterTestDriver.getTextUnit(list, 185);
		assertNotNull(tu);
		assertEquals("Line 1\nLine 2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		
		tu = FilterTestDriver.getTextUnit(list, 186);
		assertNotNull(tu);
		assertEquals("\u00e0=agrave", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testSimpleEntry () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String `text'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("text", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testNoTextEntry () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <TextRectID 9> >"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu==null);
	}
	
	@Test
	public void testTwoPartsEntry () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String `Part 1'><ParaLine <String ` and part 2'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("Part 1 and part 2", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testEmptyString () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String `Text 1'><Dummy 1><Char ThinSpace><String `'><Dummy 2><String ` end'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("Text 1<1/>\u2009<2/> end", fmt.setContent(tu.getSource().getFirstContent()).toString());
		Code code = tu.getSource().getFirstContent().getCode(0);
		assertEquals("'><Dummy 1><String `", code.getData());
		code = tu.getSource().getFirstContent().getCode(1);
		assertEquals("'><Dummy 2><String `", code.getData());
	}
	
	@Test
	public void testTabs () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String ` '><Var 1><Char Tab><Char Tab>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu==null);
	}

	@Test
	public void testTabsAndCodes () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <Char Tab><Font 1><Var 1><Font 2><Char Tab><ParaLine <Font 3>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu==null);
		DocumentPart dp = FilterTestDriver.getDocumentPart(getEvents(snippet), 2);
		assertEquals("<TextFlow <Para <Unique 12345><ParaLine <String `'><Char Tab><String `'><Font 1><Var 1><Font 2><String `'><Char Tab><String `'><ParaLine <Font 3>>>>", dp.getSkeleton().toString());
	}

	@Test
	public void testDummyBeforeChar () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String `Text 1'><Dummy <InDummy 2>><Char ThinSpace><String `Text 2'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("Text 1<1/>\u2009Text 2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		Code code = tu.getSource().getFirstContent().getCode(0);
		assertEquals("'><Dummy <InDummy 2>><String `", code.getData());
	}

	@Test
	public void testCharOnly () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <Dummy 1><Char Tab><Dummy 2>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu==null);
	}

	@Test
	public void testEndsInCharAndCode () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <String `aaa'><Dummy 1><Char Tab><Dummy 2>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("aaa<1/>\t", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testDummyCharString () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <Var 1><Char Tab><String `aaa'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("\taaa", fmt.setContent(tu.getSource().getFirstContent()).toString());
		assertEquals("<TextFlow <Para <Unique 12345><ParaLine <Var 1><String `[#$$self$]'>>>", tu.getSkeleton().toString());
	}

	@Test
	public void testEmptyFTag () {
		String snippet = STARTMIF
			+ "<Unique 12345><ParaLine <Dummy 1><String `Text 1'><Char ThinSpace><Dummy 2><String `5'><Dummy 3>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("Text 1\u2009<1/>5", fmt.setContent(tu.getSource().getFirstContent()).toString());
		Code code = tu.getSource().getFirstContent().getCode(0);
		assertEquals("'><Dummy 2><String `", code.getData());
	}
	
	@Test
	public void testSoftHyphen () {
		String snippet = STARTMIF
			+ "<Unique 123><ParaLine <TextRectID 20><String `How'><Char SoftHyphen>>"
			+ "<ParaLine <String `ever.'>>"
			+ ENDMIF;
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("However.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testOutput () {
		rewriteFile("Test01.mif");
		rewriteFile("Test02-v9.mif");
		rewriteFile("Test01-v7.mif");
	}
	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.mif", null));
		list.add(new InputDocument(root+"Test01-v7.mif", null));
		list.add(new InputDocument(root+"Test02-v9.mif", null));
		list.add(new InputDocument(root+"Test03.mif", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-16", locEN, locEN));
	}

	private void rewriteFile (String fileName) {
		filter.open(new RawDocument(Util.toURI(root+fileName), null, locEN));
		IFilterWriter writer = filter.createFilterWriter();
		writer.setOptions(locEN, null);
		writer.setOutput(root+fileName+".out.mif");
		while ( filter.hasNext() ) {
			writer.handleEvent(filter.next());
		}
		writer.close();
		filter.close();
	}
	
	private ArrayList<Event> getEventsFromFile (String filename) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(Util.toURI(root+filename), null, locEN));
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
