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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.plaintext.base.BasePlainTextFilter;
import net.sf.okapi.filters.plaintext.paragraphs.ParaPlainTextFilter;
import net.sf.okapi.filters.plaintext.paragraphs.Parameters;
import net.sf.okapi.filters.plaintext.regex.RegexPlainTextFilter;
import net.sf.okapi.filters.plaintext.spliced.SplicedLinesFilter;
import net.sf.okapi.lib.extra.filters.AbstractLineFilter;
import net.sf.okapi.lib.extra.filters.WrapMode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlainTextFilterTest {
	
	private PlainTextFilter filter;
	private FilterTestDriver testDriver;
	private String root;
    private LocaleId locEN = LocaleId.fromString("en"); 
    private LocaleId locFR = LocaleId.fromString("fr"); 
	
	@Before
	public void setUp() {
		filter = new PlainTextFilter();
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
	
		// Empty filter parameters, OkapiBadFilterParametersException 
		try {
			filter.setParameters(null);						
			InputStream input2 = PlainTextFilterTest.class.getResourceAsStream("/cr.txt");
			filter.open(new RawDocument(input2, "UTF-8", locEN));
			fail("OkapiBadFilterParametersException should've been trown");
		}	
		catch (OkapiBadFilterParametersException e) {
		}
		finally {
			filter.close();
		}		
	}		
		
	@Test
	public void testNameAndMimeType() {
		assertEquals(filter.getMimeType(), "text/plain");
		assertEquals(filter.getName(), "okf_plaintext");
		
		// Read lines from a file, check mime types 
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/cr.txt");
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
	public void testFiles() {

		filter.setConfiguration(BasePlainTextFilter.FILTER_CONFIG_TRIM_ALL);
		
//		testFile("BOM_MacUTF16withBOM2.txt", false);		
//		testFile("cr.txt", false);
		testFile("crlf_start.txt", true);
//		testFile("crlf_end.txt", true);
//		testFile("crlf.txt", false);
//		testFile("crlfcrlf_end.txt", true);
//		testFile("crlfcrlf.txt", false);
//		testFile("lf.txt", false);
	}
				
	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = getSkeleton(root + "crlf_start.txt"); // Trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "crlf_start.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(PlainTextFilterTest.class.getResourceAsStream("/crlf_start.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton2 () {
		String st = null;
		String expected = null;
		
		try {
			st = getSkeleton(root + "csv_test1.txt"); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test1.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(PlainTextFilterTest.class.getResourceAsStream("/csv_test1.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton3 () {
		String st = null;
		String expected = null;
		
		try {
			st = getSkeleton(root + "csv_test2.txt"); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test2.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(PlainTextFilterTest.class.getResourceAsStream("/csv_test2.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testEvents() {
		String filename = "cr.txt";
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
//debug		System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", locEN));
		if (!testDriver.process(filter)) Assert.fail();
		filter.close();
	}
	
	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"cr.txt", null),
			"UTF-8", locEN, locEN));
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "cr.txt", ""));
		list.add(new InputDocument(root + "csv_test1.txt", ""));
		list.add(new InputDocument(root + "crlf_start.txt", ""));
		list.add(new InputDocument(root + "crlf_end.txt", ""));
		list.add(new InputDocument(root + "crlf.txt", ""));
		list.add(new InputDocument(root + "crlfcrlf_end.txt", ""));
		list.add(new InputDocument(root + "crlfcrlf.txt", ""));
		list.add(new InputDocument(root + "lf.txt", "")); 
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}
	
	@Test
	public void testCancel() {
		testDriver.setDisplayLevel(0);
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/cr.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1");
		testEvent(EventType.TEXT_UNIT, "Line 2");
		filter.cancel();
		testEvent(EventType.CANCELED, null);
		assertFalse(filter.hasNext());
		
		filter.close();		
	}
	
	@Test
	public void testConfigurations() {
		
		List<FilterConfiguration> configs = filter.getConfigurations();
		assertNotNull(configs);
		assertEquals(9, configs.size());
		
		FilterConfiguration fc = configs.get(0);
		assertEquals("okf_plaintext", fc.configId);
		assertEquals("net.sf.okapi.filters.plaintext.PlainTextFilter", fc.filterClass);
		assertTrue(Util.isEmpty(fc.parametersLocation));
		
		fc = configs.get(4);
		assertEquals("okf_plaintext_spliced_backslash", fc.configId);
		assertEquals("net.sf.okapi.filters.plaintext.PlainTextFilter", fc.filterClass);
		assertEquals("okf_plaintext_spliced_backslash.fprm", fc.parametersLocation);
		
		IParameters params = filter.getParameters();
		assertNotNull(params);
		assertEquals("net.sf.okapi.filters.plaintext.Parameters", params.getClass().getName());
	}
		
	@Test
	public void testSynchronization() throws URISyntaxException {
		
		//------------------------
		filter.setConfiguration(ParaPlainTextFilter.FILTER_CONFIG);
		IParameters params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params2 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		filter.setConfiguration(SplicedLinesFilter.FILTER_CONFIG_BACKSLASH);
		IParameters params3 = filter.getActiveParameters();
		assertTrue(params3 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		assertTrue(params3 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params3 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		filter.setConfiguration(RegexPlainTextFilter.FILTER_CONFIG_LINES);
		IParameters params4 = filter.getActiveParameters();
		assertTrue(params4 instanceof net.sf.okapi.filters.plaintext.regex.Parameters);
		assertTrue(params4 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params4 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		filter.setConfiguration(BasePlainTextFilter.FILTER_CONFIG);
		IParameters params5 = filter.getActiveParameters();
		assertTrue(params5 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params5 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		//-------------------------
		net.sf.okapi.filters.plaintext.Parameters params = new net.sf.okapi.filters.plaintext.Parameters();
		filter.setParameters(params);
		
		URL url = PlainTextFilterTest.class.getResource("/test_params1.fprm");
		if (url == null) return;
		
		String root = Util.getDirectoryName(url.toURI().getPath());
		
		params.load(Util.toURI(root + "/test_params1.fprm"), false);

		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/cr.txt");
		filter.open(new RawDocument(input, "UTF-8", locEN));

		IParameters params6 = filter.getActiveParameters();
		assertTrue(params6 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		filter.close();
		
		params.load(Util.toURI(root + "/test_params2.fprm"), false);
		
		input = PlainTextFilterTest.class.getResourceAsStream("/cr.txt");
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		IParameters params7 = filter.getActiveParameters();
		assertTrue(params7 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		filter.close();
		
		// W/o Open()		
		params.load(Util.toURI(root + "/test_params1.fprm"), false);
		
		IParameters params8 = filter.getActiveParameters();
		assertTrue(params8 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		IParameters params81 = params.getActiveParameters();
		assertTrue(params81 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		String c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.spliced.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.base.Parameters"));
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.paragraphs.Parameters"));

		IFilter filter1 = filter.getActiveSubFilter();
		assertTrue(filter1 instanceof net.sf.okapi.filters.plaintext.spliced.SplicedLinesFilter);
		
		params.load(Util.toURI(root + "/test_params2.fprm"), false);
		
		IParameters params9 = filter.getActiveParameters();
		assertTrue(params9 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		IParameters params91 = params.getActiveParameters();
		assertTrue(params91 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.paragraphs.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.base.Parameters"));
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.spliced.Parameters"));

		filter1 = filter.getActiveSubFilter();
		assertTrue(filter1 instanceof net.sf.okapi.filters.plaintext.paragraphs.ParaPlainTextFilter);
		
		//-------------------------
		filter.setConfiguration(ParaPlainTextFilter.FILTER_CONFIG);
		IParameters params10 = params.getActiveParameters();
		assertTrue(params10 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		assertTrue(params10 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params10 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		filter.setConfiguration(SplicedLinesFilter.FILTER_CONFIG_BACKSLASH);
		IParameters params11 = params.getActiveParameters();
		assertTrue(params11 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		assertTrue(params11 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params11 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		filter.setConfiguration(RegexPlainTextFilter.FILTER_CONFIG_LINES);
		IParameters params12 = params.getActiveParameters();
		assertTrue(params12 instanceof net.sf.okapi.filters.plaintext.regex.Parameters);
		assertTrue(params12 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params12 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		filter.setConfiguration(BasePlainTextFilter.FILTER_CONFIG);
		IParameters params13 = params.getActiveParameters();
		assertTrue(params13 instanceof net.sf.okapi.filters.plaintext.base.Parameters);
		assertFalse(params13 instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters);
		
		//-------------------------
		filter.setConfiguration(ParaPlainTextFilter.FILTER_CONFIG);
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.paragraphs.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.base.Parameters"));
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.spliced.Parameters"));
		
		filter.setConfiguration(SplicedLinesFilter.FILTER_CONFIG_BACKSLASH);
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.spliced.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.base.Parameters"));
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.paragraphs.Parameters"));
		
		filter.setConfiguration(RegexPlainTextFilter.FILTER_CONFIG_LINES);
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.regex.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.base.Parameters"));
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.spliced.Parameters"));
		
		filter.setConfiguration(BasePlainTextFilter.FILTER_CONFIG);
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.base.Parameters");
		assertFalse(c.equals("net.sf.okapi.filters.plaintext.paragraphs.Parameters"));
		
		//-------------------------
				
		filter.setConfiguration(SplicedLinesFilter.FILTER_CONFIG_CUSTOM);
		filter1 = filter.getActiveSubFilter();
		assertTrue(filter1 instanceof net.sf.okapi.filters.plaintext.spliced.SplicedLinesFilter);
		
		filter.setConfiguration(RegexPlainTextFilter.FILTER_CONFIG_PARAGRAPHS);
		filter1 = filter.getActiveSubFilter();
		assertTrue(filter1 instanceof net.sf.okapi.filters.plaintext.regex.RegexPlainTextFilter);

		//-------------------------
		params.setParametersClass(net.sf.okapi.filters.plaintext.spliced.Parameters.class);
		
		IParameters params15 = filter.getActiveParameters();
		assertTrue(params15 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		c = params.getParametersClassName();
		assertEquals(c, "net.sf.okapi.filters.plaintext.spliced.Parameters");
		
		IParameters params14 = params.getActiveParameters();
		assertTrue(params14 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
	}
	
	@Test
	public void testLineNumbers() {
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		filter.setConfiguration(ParaPlainTextFilter.FILTER_CONFIG);
		
		Parameters params = (Parameters) filter.getActiveParameters();
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1", 1);
		testEvent(EventType.TEXT_UNIT, "Line 2", 2);
		testEvent(EventType.TEXT_UNIT, "Line 3", 4);
		testEvent(EventType.TEXT_UNIT, "Line 4", 5);
		testEvent(EventType.TEXT_UNIT, "Line 5", 6);
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();		
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1\nLine 2", 1);
		testEvent(EventType.TEXT_UNIT, "Line 3\nLine 4\nLine 5", 4);
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
	
	@Test
	public void testParagraphs() {
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);

		filter.setConfiguration(ParaPlainTextFilter.FILTER_CONFIG);
		
		Parameters params = (Parameters) filter.getActiveParameters();
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1");
		testEvent(EventType.TEXT_UNIT, "Line 2");
		testEvent(EventType.TEXT_UNIT, "Line 3");
		testEvent(EventType.TEXT_UNIT, "Line 4");
		testEvent(EventType.TEXT_UNIT, "Line 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.PLACEHOLDERS;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1");
		testEvent(EventType.TEXT_UNIT, "Line 2");
		testEvent(EventType.TEXT_UNIT, "Line 3");
		testEvent(EventType.TEXT_UNIT, "Line 4");
		testEvent(EventType.TEXT_UNIT, "Line 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.SPACES;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1");
		testEvent(EventType.TEXT_UNIT, "Line 2");
		testEvent(EventType.TEXT_UNIT, "Line 3");
		testEvent(EventType.TEXT_UNIT, "Line 4");
		testEvent(EventType.TEXT_UNIT, "Line 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
				
		//---------------------------------------------------------------------------
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1\nLine 2");
		testEvent(EventType.TEXT_UNIT, "Line 3\nLine 4\nLine 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.SPACES;		
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1 Line 2");
		testEvent(EventType.TEXT_UNIT, "Line 3 Line 4 Line 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.PLACEHOLDERS;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1\rLine 2");
		testEvent(EventType.TEXT_UNIT, "Line 3\rLine 4\rLine 5");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
	
	@Test
	public void testLoadParams() throws URISyntaxException {
		
		IParameters params = filter.getParameters();
		params.load(new File(getPainTextConfig("okf_plaintext_spliced_backslash.fprm")).toURI(), false);
		
		IParameters params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		net.sf.okapi.filters.plaintext.spliced.Parameters params3 = (net.sf.okapi.filters.plaintext.spliced.Parameters) params2;
		assertEquals("\\", params3.splicer);
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		filter.close();
		
		params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		params3 = (net.sf.okapi.filters.plaintext.spliced.Parameters) params2;
		assertEquals("\\", params3.splicer);
		
		//--------------------
		params.load(new File(getPainTextConfig("okf_plaintext_spliced_underscore.fprm")).toURI(), false);
		
		params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		params3 = (net.sf.okapi.filters.plaintext.spliced.Parameters) params2;
		assertEquals("_", params3.splicer);
		
		input = PlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		filter.close();
		
		params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.plaintext.spliced.Parameters);
		
		params3 = (net.sf.okapi.filters.plaintext.spliced.Parameters) params2;
		assertEquals("_", params3.splicer);
	}
	
				
// Helpers
	
	private void testFile(String filename, boolean emptyTail) {
		testDriver.setDisplayLevel(0);
		
		InputStream input = PlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1");
		testEvent(EventType.TEXT_UNIT, "Line 2");
		testEvent(EventType.TEXT_UNIT, "Line 3");
		testEvent(EventType.TEXT_UNIT, "Line 4");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		// List events
		input = PlainTextFilterTest.class.getResourceAsStream("/" + filename);
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
			
			assertEquals(expectedText, res.toString());
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
	
	private void testEvent(EventType expectedType, String expectedText, int expectedLineNum) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof ITextUnit);
			
			assertEquals(expectedText, res.toString());
			
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
	
	private String getPainTextConfig(String fileName) throws URISyntaxException {
		URL url = PlainTextFilter.class.getResource("okf_plaintext_paragraphs.fprm");
		String root = Util.getDirectoryName(url.toURI().getPath());
//		root = Util.getDirectoryName(root) + "/data/";
		return root + "/" + fileName;
	}
	
//	private String _getFullFileName(String fileName) {
//		URL url = PlainTextFilterTest.class.getResource("/cr.txt");
//		String root = Util.getDirectoryName(url.getPath());
//		root = Util.getDirectoryName(root) + "/data/";
//		return root + fileName;
//	}
	
	private String getSkeleton (String fileName) throws UnsupportedEncodingException {
		IFilterWriter writer;
		ByteArrayOutputStream writerBuffer;
										
		writer = filter.createFilterWriter();		
		try {						
			// Open the input
			filter.open(new RawDocument((new File(fileName)).toURI(), "UTF-8", locEN, locFR));
			
			// Prepare the output
			writer.setOptions(locFR, "UTF-16");
			writerBuffer = new ByteArrayOutputStream();
			writer.setOutput(writerBuffer);
			
			// Process the document
			Event event;
			while ( filter.hasNext() ) {
				event = filter.next();
				writer.handleEvent(event);
			}
		}
		finally {
			if ( filter != null ) filter.close();
			if ( writer != null ) writer.close();
		}
		return new String(writerBuffer.toByteArray(), "UTF-16");
	}
	
	private String streamAsString(InputStream input) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		StringBuilder tmp = new StringBuilder();
		char[] buf = new char[2048];
		int count = 0;
		while (( count = reader.read(buf)) != -1 ) {
			tmp.append(buf, 0, count);
		}
		
        return tmp.toString();
    }
			
}
	