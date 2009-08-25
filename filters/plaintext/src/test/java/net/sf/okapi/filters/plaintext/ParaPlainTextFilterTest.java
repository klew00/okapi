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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;
import net.sf.okapi.filters.plaintext.common.WrapMode;
import net.sf.okapi.filters.plaintext.paragraphs.ParaPlainTextFilter;
import net.sf.okapi.filters.plaintext.paragraphs.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParaPlainTextFilterTest {
	
	private ParaPlainTextFilter filter;
	private FilterTestDriver testDriver;
	private Parameters params;
    @SuppressWarnings("unused")
	private String root;
	
	@Before
	public void setUp() {
		filter = new ParaPlainTextFilter();
		assertNotNull(filter);

		params = (Parameters) filter.getParameters();
		assertNotNull(params);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
        root = TestUtil.getParentDir(this.getClass(), "/crt.txt");
	}

	@Test
	public void testUtils() {
		String st = "12345678";
		assertEquals("45678", Util.trimStart(st, "123"));
		assertEquals("12345", Util.trimEnd(st, "678"));
		assertEquals("12345678", Util.trimEnd(st, "9"));
		
		st = "     ";
		assertEquals("", Util.trimStart(st, " "));
		assertEquals("", Util.trimEnd(st, " "));
		
		st = "  1234   ";
		TextFragment tf = new TextFragment(st);
		TextUnitUtils.trimLeading(tf, null);
		assertEquals("1234   ", tf.toString());
		TextUnitUtils.trimTrailing(tf, null);
		assertEquals("1234", tf.toString());
		
		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtils.trimLeading(tf, null);
		assertEquals("", tf.toString());
		TextUnitUtils.trimTrailing(tf, null);
		assertEquals("", tf.toString());
		
		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtils.trimTrailing(tf, null);
		assertEquals("", tf.toString());
		
		TextFragment tc = new TextFragment("test");
		
		Code c = new Code(TagType.PLACEHOLDER, "code");
		tc.append(c);
		
		tc.append(" string");
//debug		System.out.println(tc.toString());
//debug		System.out.println(tc.getCodedText());
		
		
		//--------------------
		//TextContainer tcc = new TextContainer("    123456  ");
		TextContainer tcc = new TextContainer();
		Code c2 = new Code(TagType.PLACEHOLDER, "code");
		tcc.append(c2);
		tcc.append("    123456  ");
		
		GenericSkeleton skel = new GenericSkeleton();
		TextUnitUtils.trimLeading(tcc, skel);
		
		assertEquals("123456  ", tcc.getCodedText());
		assertEquals("    ", skel.toString());
		
		//--------------------
		TextContainer tcc2 = new TextContainer("    123456  ");
		Code c3 = new Code(TagType.PLACEHOLDER, "code");
		tcc2.append(c3);
		
		GenericSkeleton skel2 = new GenericSkeleton();
		TextUnitUtils.trimTrailing(tcc2, skel2);
		
		assertEquals("    123456", tcc2.getCodedText());
		assertEquals("  ", skel2.toString());
		
		//--------------------
		TextContainer tcc4 = new TextContainer("    123456  ");
		Code c4 = new Code(TagType.PLACEHOLDER, "code");
		tcc4.append(c4);
		
		char ch = TextUnitUtils.getLastChar(tcc4);
		assertEquals('6', ch);
		
		//--------------------
		TextContainer tcc5 = new TextContainer("    123456  ");
		
		TextUnitUtils.deleteLastChar(tcc5);
		assertEquals("    12345  ", tcc5.getCodedText());
		
		//--------------------
		TextContainer tcc6 = new TextContainer("123456_    ");
		
		assertTrue(TextUnitUtils.endsWith(tcc6, "_"));
		assertTrue(TextUnitUtils.endsWith(tcc6, "6_"));
		assertFalse(TextUnitUtils.endsWith(tcc6, "  "));
		
		TextContainer tcc7 = new TextContainer("123456<splicer>    ");
		assertTrue(TextUnitUtils.endsWith(tcc7, "<splicer>"));
		assertTrue(TextUnitUtils.endsWith(tcc7, "6<splicer>"));
		assertFalse(TextUnitUtils.endsWith(tcc7, "  "));
		
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
	
		// Empty filter parameters, OkapiBadFilterParametersException 
		try {
			filter.setParameters(null);						
			InputStream input2 = ParaPlainTextFilterTest.class.getResourceAsStream("/cr.txt");
			filter.open(new RawDocument(input2, "UTF-8", "en"));
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
		assertEquals(filter.getName(), "okf_plaintext_paragraphs");
		
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
	public void testFiles() {
		
		params.extractParagraphs = false;
		params.preserveWS = false;
		
		_testFile("BOM_MacUTF16withBOM2.txt", false);		
		_testFile("cr.txt", false);
		_testFile("crlf_start.txt", true);
		_testFile("crlf_end.txt", true);
		_testFile("crlf.txt", false);
		_testFile("crlfcrlf_end.txt", true);
		_testFile("crlfcrlf.txt", false);
		_testFile("lf.txt", false);
	}
				
	@Test
	public void testFiles2() {
		
		params.extractParagraphs = true;
//		params.reset();
		
		_testFile("crlfcrlf_end.txt", true);
	}
	
	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		params.extractParagraphs = false;
		
		try {
			st = _getSkeleton(_getFullFileName("crlf_start.txt")); // Trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "crlf_start.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/crlf_start.txt"));			
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
			st = _getSkeleton(_getFullFileName("csv_test1.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test1.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/csv_test1.txt"));			
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
			st = _getSkeleton(_getFullFileName("csv_test2.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test2.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/csv_test2.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton4 () {
		String st = null;
		String expected = null;
		
		try {
			st = _getSkeleton(_getFullFileName("crlfcrlf.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "crlfcrlf.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/crlfcrlf.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton5 () {
		String st = null;
		String expected = null;
		
		try {
			st = _getSkeleton(_getFullFileName("lgpl.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "lgpl.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/lgpl.txt"));			
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
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
//debug		System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", "en"));
		if (!testDriver.process(filter)) Assert.fail();
		filter.close();
	}
	
/*  TODO: fix these
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		params.extractParagraphs = true;
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "cr.txt", ""));
		list.add(new InputDocument(root + "csv_test1.txt", ""));
		list.add(new InputDocument(root + "crlf_start.txt", ""));
		list.add(new InputDocument(root + "crlf_end.txt", ""));
		list.add(new InputDocument(root + "crlf.txt", ""));		
		list.add(new InputDocument(root + "lf.txt", "")); 
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}

	@Test
	public void testDoubleExtraction2() {
		// Read all files in the data directory
		params.extractParagraphs = true;
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "crlfcrlf.txt", ""));
		list.add(new InputDocument(root + "crlfcrlf_end.txt", ""));
		list.add(new InputDocument(root + "crlfcrlfcrlf.txt", ""));
		list.add(new InputDocument(root + "crlfcrlfcrlf_end.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}
	
	@Test
	public void testDoubleExtraction3() {
		// Read all files in the data directory
		params.extractParagraphs = true;
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "lgpl.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}
*/

	@Test
	public void testCancel() {
		testDriver.setDisplayLevel(0);
		params.extractParagraphs = false;
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/cr.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		filter.cancel();
		_testEvent(EventType.CANCELED, null);
		assertFalse(filter.hasNext());
		
		filter.close();		
	}
	
	@Test
	public void testLineNumbers() {
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		Parameters params = (Parameters) filter.getParameters();
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1", 1);
		_testEvent(EventType.TEXT_UNIT, "Line 2", 2);
		_testEvent(EventType.TEXT_UNIT, "Line 3", 4);
		_testEvent(EventType.TEXT_UNIT, "Line 4", 5);
		_testEvent(EventType.TEXT_UNIT, "Line 5", 6);
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();		
		
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1\nLine 2", 1);
		_testEvent(EventType.TEXT_UNIT, "Line 3\nLine 4\nLine 5", 4);
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
	
	@Test
	public void testParagraphs() {
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		Parameters params = (Parameters) filter.getParameters();
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
		_testEvent(EventType.TEXT_UNIT, "Line 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.PLACEHOLDERS;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
		_testEvent(EventType.TEXT_UNIT, "Line 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = false;
		params.wrapMode = WrapMode.SPACES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
		_testEvent(EventType.TEXT_UNIT, "Line 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
				
		//---------------------------------------------------------------------------
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.NONE;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1\nLine 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3\nLine 4\nLine 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.SPACES;		
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1 Line 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3 Line 4 Line 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/test_paragraphs1.txt");
		assertNotNull(input);
		
		params.extractParagraphs = true;
		params.wrapMode = WrapMode.PLACEHOLDERS;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1\rLine 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3\rLine 4\rLine 5");
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
				
// Helpers
	
	private void _testFile(String filename, boolean emptyTail) {
		testDriver.setDisplayLevel(0);
		
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
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
	
	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof TextUnit);
			
			assertEquals(expectedText, res.toString());
			
			Property prop = ((TextUnit) res).getSourceProperty(AbstractLineFilter.LINE_NUMBER);
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
	
	private String _getFullFileName(String fileName) {
		return TestUtil.getParentDir(this.getClass(), "/cr.txt") + fileName;
	}
	
	private String _getSkeleton (String fileName) throws UnsupportedEncodingException {
		IFilterWriter writer;
		ByteArrayOutputStream writerBuffer;
										
		writer = filter.createFilterWriter();		
		try {						
			// Open the input
			filter.open(new RawDocument((new File(fileName)).toURI(), "UTF-8", "en", "fr"));
			
			// Prepare the output
			writer.setOptions("fr", "UTF-16");
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
	
	private String _streamAsString(InputStream input) throws IOException {
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
	