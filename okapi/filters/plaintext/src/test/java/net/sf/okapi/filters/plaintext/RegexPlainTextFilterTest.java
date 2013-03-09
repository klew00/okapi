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

package net.sf.okapi.filters.plaintext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.plaintext.regex.Parameters;
import net.sf.okapi.filters.plaintext.regex.RegexPlainTextFilter;
import net.sf.okapi.lib.extra.filters.AbstractLineFilter;
// import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexPlainTextFilterTest {
	
	private RegexPlainTextFilter filter;
	private FilterTestDriver testDriver;
    String root;
    private LocaleId locEN = LocaleId.fromString("en"); 
    private LocaleId locFR = LocaleId.fromString("fr"); 

	@Before
	public void setUp() {
		filter = new RegexPlainTextFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
        root = TestUtil.getParentDir(this.getClass(), "/cr.txt");
	}

	@Test
	public void testEmptyInput() {
		// Empty input, check exceptions
				
		// Empty stream, OkapiBadFilterInputException expected, no other		
		InputStream input = null;
		try {
			filter.open(new RawDocument(input, "UTF-8", locEN));
			fail("IllegalArgumentException should've been trown");
		}	
		catch (IllegalArgumentException e) {
		}
		finally {
			filter.close();
		}
				
		// Empty URI, OkapiBadFilterInputException expected, no other
		URI uri = null;
		try {
			filter.open(new RawDocument(uri, "UTF-8", locEN));
			fail("IllegalArgumentException should've been trown");
		}	
		catch (IllegalArgumentException e) {
		}
		finally {
			filter.close();
		}
		
		// Empty char seq, OkapiBadFilterInputException expected, no other		
		String st = null;
		try {
			filter.open(new RawDocument(st, locEN, locEN));
			fail("IllegalArgumentException should've been trown");
		}	
		catch (IllegalArgumentException e) {
		}
		finally {
			filter.close();
		}
		
		// Empty raw doc, open(RawDocument), OkapiBadFilterInputException expected, no other		
		try {
			filter.open(null);
			fail("OkapiBadFilterInputException should've been trown");
		}	
		catch (OkapiBadFilterInputException e) {
		}
		finally {
			filter.close();
		}
	
		// Empty raw doc, open(RawDocument, boolean), OkapiBadFilterInputException expected, no other
		try {
			filter.open(null, true);
			fail("OkapiBadFilterInputException should've been trown");
		}	
		catch (OkapiBadFilterInputException e) {
		}
		finally {
			filter.close();
		}
	
		// Empty filter parameters, no exception expected
		try {
			filter.setParameters(null);
			
			InputStream input2 = ParaPlainTextFilterTest.class.getResourceAsStream("/cr.txt");
			filter.open(new RawDocument(input2, "UTF-8", locEN));
		}	
		finally {
			filter.close();
		}		
	}		
	
	@Test
	public void testParameters() throws URISyntaxException {
		// Test if default regex parameters have been loaded
		IParameters rp = filter.getRegexParameters();
				
		assertNotNull(rp);
		assertTrue(rp instanceof net.sf.okapi.filters.regex.Parameters);
		
		net.sf.okapi.filters.regex.Parameters rpp = (net.sf.okapi.filters.regex.Parameters) rp;
		assertNotNull(rpp.getRules());
		assertFalse(rpp.getRules().isEmpty());
				
		// Check if defaults are set
		Parameters params = new Parameters(); 
		filter.setParameters(params);
		
		assertEquals(params.rule, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_RULE);
		assertEquals(params.sourceGroup, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_GROUP);
		assertEquals(params.regexOptions, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_OPTIONS);
		
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = RegexPlainTextFilter.class.getResource("/test_params1.txt");
		assertNotNull(paramsUrl);  
		
		params.load(Util.toURI(paramsUrl.toURI().getPath()), false);
		assertEquals(params.rule, "(.)"); 
		assertEquals(params.sourceGroup, 1);
		assertEquals(params.regexOptions, 8);
		
		// Save filter parameters to a file, load and check if params have changed
		paramsUrl = RegexPlainTextFilter.class.getResource("/test_params2.txt");
		assertNotNull(paramsUrl);
	
		filter.setRule("(Test (rule))", 2, 0x88);
		
		params.save(paramsUrl.toURI().getPath());
		
		// Test the parameters are loaded into the internal regex and compiled
		filter.open(new RawDocument("Line 1/r/nLine2 Test rule", locEN, locEN), true);
		
		testEvent(EventType.START_DOCUMENT, "");
		testEvent(EventType.DOCUMENT_PART, "Line 1/r/nLine2 ");
		testEvent(EventType.TEXT_UNIT, "rule"); 
		
		params.rule = "(a*+)";
		params.sourceGroup = 1;
		params.regexOptions = 40;
		
		params.load(Util.toURI(paramsUrl.toURI().getPath()), false);
		
		assertEquals(params.rule, "(Test (rule))");
		assertEquals(params.sourceGroup, 2);
		assertEquals(params.regexOptions, 0x88);		
		
		// One more time to make sure that params are saved
		params.rule = "(a*+)";
		params.sourceGroup = 1;
		params.regexOptions = 40;
		
		params.save(paramsUrl.toURI().getPath());
		params.rule = "(Test (rule))";
		params.sourceGroup = 2;
		params.regexOptions = 0x88;
		
		params.load(Util.toURI(paramsUrl.toURI().getPath()), false);
		
		assertEquals(params.rule, "(a*+)");
		assertEquals(params.sourceGroup, 1);
		assertEquals(params.regexOptions, 40);
	}
	
	@Test
	public void testNameAndMimeType() {
		assertEquals(filter.getMimeType(), "text/plain");
		assertEquals(filter.getName(), "okf_plaintext_regex");
		
		// Read lines from a file, check mime types 
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/cr.txt");
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		while (filter.hasNext()) {
			Event event = filter.next();
			assertNotNull(event);
			
			IResource res = event.getResource();
			assertNotNull(res);
			
			switch (event.getEventType()) {
				case TEXT_UNIT:
					assertTrue(res instanceof ITextUnit);
					assertEquals(((ITextUnit)res).getMimeType(), filter.getMimeType());
					break;
					
				case DOCUMENT_PART:
					assertTrue(res instanceof DocumentPart);
					assertEquals(((DocumentPart) res).getMimeType(), null);
					break;
			}
		}
		filter.close();
	}
	
	@Test
	public void testEvents() throws IOException {
		
	}
	
	@Test
	public void testFiles() {
		testFile("cr.txt", false);
		testFile("crlf_end.txt", true);
		testFile("crlf.txt", false);
		testFile("crlfcrlf_end.txt", true);
		testFile("crlfcrlf.txt", false);
		testFile("lf.txt", false);
		testFile("mixture.txt", true);
		testFile("u0085.txt", false);
		testFile("u2028.txt", false);
		testFile("u2029.txt", false);		
	}
			
	@Test
	public void testDoubleExtraction () {
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "cr.txt", ""));
		list.add(new InputDocument(root + "crlf_end.txt", ""));
		list.add(new InputDocument(root + "crlf.txt", ""));
		list.add(new InputDocument(root + "crlfcrlf_end.txt", ""));
		list.add(new InputDocument(root + "crlfcrlf.txt", ""));
		list.add(new InputDocument(root + "lf.txt", ""));
		list.add(new InputDocument(root + "mixture.txt", ""));
		list.add(new InputDocument(root + "u0085.txt", ""));
		list.add(new InputDocument(root + "u2028.txt", ""));
		list.add(new InputDocument(root + "u2029.txt", "")); 
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}
	
	
	
// Helpers
	
	private void testFile(String filename, boolean emptyTail) {
		testDriver.setDisplayLevel(0);
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1", 1);
		testEvent(EventType.DOCUMENT_PART, null);
		testEvent(EventType.TEXT_UNIT, "Line 2", 2);
		testEvent(EventType.DOCUMENT_PART, null);
		testEvent(EventType.TEXT_UNIT, "Line 3", 3);
		testEvent(EventType.DOCUMENT_PART, null);
		testEvent(EventType.TEXT_UNIT, "Line 4", 4);
		if (emptyTail) testEvent(EventType.DOCUMENT_PART, null);
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		// List events
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		//debug				System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", locEN));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}
	
	private void testEvent(EventType expectedType, String expectedText) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof ITextUnit);
			
			assertEquals(((ITextUnit)res).toString(), expectedText);
			break;
			
		case DOCUMENT_PART:
			if (expectedText == null) break;
			res = event.getResource();
			assertTrue(res instanceof DocumentPart);
			
			ISkeleton skel = res.getSkeleton();
			if (skel != null) {
				assertEquals(skel.toString(), expectedText);
			}
			break;
		}
	}
	
	private void testEvent(EventType expectedType, String expectedText, int expectedLineNum) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof ITextUnit);
			
			assertEquals(expectedText, ((ITextUnit)res).toString());
			
			Property prop = ((ITextUnit)res).getSourceProperty(AbstractLineFilter.LINE_NUMBER);
			assertNotNull(prop);
			
			String st = prop.getValue();
			assertEquals(expectedLineNum, Integer.parseInt(st));
			
			break;
			
		case DOCUMENT_PART:
			if (expectedText == null) break;
			res = event.getResource();
			assertTrue(res instanceof DocumentPart);
			
			ISkeleton skel = res.getSkeleton();
			if (skel != null) {
				assertEquals(expectedText, skel.toString());
			}
			break;
		}
	}
			
}

	