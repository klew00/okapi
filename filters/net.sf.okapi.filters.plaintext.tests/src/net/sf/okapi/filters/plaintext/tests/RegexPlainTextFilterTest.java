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

package net.sf.okapi.filters.plaintext.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.common.framework.AbstractLineFilter;
import net.sf.okapi.filters.plaintext.regex.Parameters;
import net.sf.okapi.filters.plaintext.regex.RegexPlainTextFilter;
// import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexPlainTextFilterTest {
	
	private RegexPlainTextFilter filter;
	private FilterTestDriver testDriver;
	
	@Before
	public void setUp() {
		filter = new RegexPlainTextFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(2);
		testDriver.setShowSkeleton(true);
	}

	@Test
	public void testEmptyInput() {
		// Empty input, check exceptions
				
		// Empty stream, OkapiBadFilterInputException expected, no other		
		InputStream input = null;
		try {
			filter.open(new RawDocument(input, "UTF-8", "en"));
			fail("OkapiIOException should've been trown");
		}	
		catch (OkapiIOException e) {
		}
		finally {
			filter.close();
		}
				
		// Empty URI, OkapiBadFilterInputException expected, no other
		URI uri = null;
		try {
			filter.open(new RawDocument(uri, "UTF-8", "en"));
			fail("OkapiIOException should've been trown");
		}	
		catch (OkapiIOException e) {
		}
		finally {
			filter.close();
		}
		
		// Empty char seq, OkapiBadFilterInputException expected, no other		
		String st = null;
		try {
			filter.open(new RawDocument(st, "UTF-8", "en"));
			fail("OkapiIOException should've been trown");
		}	
		catch (OkapiIOException e) {
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
			filter.open(new RawDocument(input2, "UTF-8", "en"));
		}	
		finally {
			filter.close();
		}		
	}		
	
	@Test
	public void testParameters() {
		// Test if default regex parameters have been loaded
		IParameters rp = filter.getRegexParameters();
				
		assertNotNull(rp);
		assertTrue(rp instanceof net.sf.okapi.filters.regex.Parameters);
		
		net.sf.okapi.filters.regex.Parameters rpp = (net.sf.okapi.filters.regex.Parameters) rp;
		assertNotNull(rpp.rules);
		assertFalse(rpp.rules.isEmpty());
				
		// Check if defaults are set
		Parameters params = new Parameters(); 
		filter.setParameters(params);
		
		assertEquals(params.rule, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_RULE);
		assertEquals(params.sourceGroup, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_GROUP);
		assertEquals(params.regexOptions, net.sf.okapi.filters.plaintext.regex.Parameters.DEF_OPTIONS);
		
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = RegexPlainTextFilter.class.getResource("/test_params1.txt");
		assertNotNull(paramsUrl);  
		
		params.load(Util.toURI(paramsUrl.getPath()), false);
		assertEquals(params.rule, "(.)"); 
		assertEquals(params.sourceGroup, 1);
		assertEquals(params.regexOptions, 8);
		
		// Save filter parameters to a file, load and check if params have changed
		paramsUrl = RegexPlainTextFilter.class.getResource("/test_params2.txt");
		assertNotNull(paramsUrl);
	
		filter.setRule("(Test (rule))", 2, 0x88);
		
		params.save(paramsUrl.getPath());
		
		// Test the parameters are loaded into the internal regex and compiled
		filter.open(new RawDocument("Line 1/r/nLine2 Test rule", "UTF-8", "en"), true);
		
		_testEvent(EventType.START_DOCUMENT, "");
		_testEvent(EventType.DOCUMENT_PART, "Line 1/r/nLine2 ");
		_testEvent(EventType.TEXT_UNIT, "rule"); 
		
		params.rule = "(a*+)";
		params.sourceGroup = 1;
		params.regexOptions = 40;
		
		params.load(Util.toURI(paramsUrl.getPath()), false);
		
		assertEquals(params.rule, "(Test (rule))");
		assertEquals(params.sourceGroup, 2);
		assertEquals(params.regexOptions, 0x88);		
		
		// One more time to make sure that params are saved
		params.rule = "(a*+)";
		params.sourceGroup = 1;
		params.regexOptions = 40;
		
		params.save(paramsUrl.getPath());
		params.rule = "(Test (rule))";
		params.sourceGroup = 2;
		params.regexOptions = 0x88;
		
		params.load(Util.toURI(paramsUrl.getPath()), false);
		
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
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		while (filter.hasNext()) {
			Event event = filter.next();
			assertNotNull(event);
			
			IResource res = event.getResource();
			assertNotNull(res);
			
			switch (event.getEventType()) {
				case TEXT_UNIT:
					assertTrue(res instanceof TextUnit);
					assertEquals(((TextUnit) res).getMimeType(), filter.getMimeType());
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
		_testFile("cr.txt", false);
		_testFile("crlf_end.txt", true);
		_testFile("crlf.txt", false);
		_testFile("crlfcrlf_end.txt", true);
		_testFile("crlfcrlf.txt", false);
		_testFile("lf.txt", false);
		_testFile("mixture.txt", true);
		_testFile("u0085.txt", false);
		_testFile("u2028.txt", false);
		_testFile("u2029.txt", false);		
	}
			
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		URL url = ParaPlainTextFilterTest.class.getResource("/cr.txt");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
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
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}
	
	
	
// Helpers
	
	private void _testFile(String filename, boolean emptyTail) {
		testDriver.setDisplayLevel(2);
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1", 1);
		_testEvent(EventType.DOCUMENT_PART, null);
		_testEvent(EventType.TEXT_UNIT, "Line 2", 2);
		_testEvent(EventType.DOCUMENT_PART, null);
		_testEvent(EventType.TEXT_UNIT, "Line 3", 3);
		_testEvent(EventType.DOCUMENT_PART, null);
		_testEvent(EventType.TEXT_UNIT, "Line 4", 4);
		if (emptyTail) _testEvent(EventType.DOCUMENT_PART, null);
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		// List events
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", "en"));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}
	
	private void _testEvent(EventType expectedType, String expectedText) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof TextUnit);
			
			assertEquals(((TextUnit) res).toString(), expectedText);
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
	
	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof TextUnit);
			
			assertEquals(expectedText, ((TextUnit) res).toString());
			
			Property prop = ((TextUnit) res).getSourceProperty(AbstractLineFilter.LINE_NUMBER);
			assertNotNull(prop);
			
			String st = prop.getValue();
			assertEquals(expectedLineNum, new Integer(st).intValue());
			
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

	