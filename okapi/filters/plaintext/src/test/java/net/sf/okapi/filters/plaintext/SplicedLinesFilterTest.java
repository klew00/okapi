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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.plaintext.spliced.Parameters;
import net.sf.okapi.filters.plaintext.spliced.SplicedLinesFilter;
import net.sf.okapi.lib.extra.filters.AbstractLineFilter;

import org.junit.Before;
import org.junit.Test;

public class SplicedLinesFilterTest {

	private SplicedLinesFilter filter;
	private FilterTestDriver testDriver;
    String root;
    private LocaleId locEN = LocaleId.fromString("en"); 
    private LocaleId locFR = LocaleId.fromString("fr"); 
	
	@Before
	public void setUp() {
		filter = new SplicedLinesFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
        root = TestUtil.getParentDir(this.getClass(), "/cr.txt");
	}

	@Test
	public void testCombinedLines() {
		InputStream input = ParaPlainTextFilterTest.class.getResourceAsStream("/combined_lines.txt");
		assertNotNull(input);
		
		Parameters params = (Parameters) filter.getParameters();
		
		// 1.
		params.createPlaceholders = false;
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1 Line 2 Line 3", 1);
		testEvent(EventType.TEXT_UNIT, "Line 4", 4);
		testEvent(EventType.END_DOCUMENT, null);
		
		// 2.
		params.createPlaceholders = true;
		input = ParaPlainTextFilterTest.class.getResourceAsStream("/combined_lines.txt");				
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		testEvent(EventType.TEXT_UNIT, "Line 1 \\\rLine 2 \\\rLine 3");
		testEvent(EventType.TEXT_UNIT, "Line 4\\");
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "combined_lines.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}

	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = getSkeleton(getFullFileName("combined_lines.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "combined_lines.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/combined_lines.txt"));			
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
			st = getSkeleton(getFullFileName("combined_lines_end.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "combined_lines_end.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/combined_lines_end.txt"));			
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
			st = getSkeleton(getFullFileName("combined_lines2.txt")); // No trailing linebreak
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "combined_lines2.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(ParaPlainTextFilterTest.class.getResourceAsStream("/combined_lines2.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
// Helpers
	
	private void testEvent(EventType expectedType, String expectedText) {
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof ITextUnit);
			
			assertEquals(expectedText, ((ITextUnit)res).toString());
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
			
			assertEquals(expectedText, ((ITextUnit)res).toString());
			
			Property prop = ((ITextUnit)res).getSourceProperty(AbstractLineFilter.LINE_NUMBER);
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
	
	private String getFullFileName(String fileName) {
//		URL url = ParaPlainTextFilterTest.class.getResource("/cr.txt");
//		String root = Util.getDirectoryName(url.getPath());
//		root = Util.getDirectoryName(root) + "/data/";
		return root + fileName;
	}
	
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
