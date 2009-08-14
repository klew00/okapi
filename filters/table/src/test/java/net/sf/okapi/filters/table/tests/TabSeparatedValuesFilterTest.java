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

package net.sf.okapi.filters.table.tests;

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
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.filters.table.base.Parameters;
import net.sf.okapi.filters.table.tsv.TabSeparatedValuesFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;

import org.junit.Before;
import org.junit.Test;


public class TabSeparatedValuesFilterTest {

	private TabSeparatedValuesFilter filter;
	private FilterTestDriver testDriver;
	
	@Before
	public void setUp() {
		filter = new TabSeparatedValuesFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(2);
		testDriver.setShowSkeleton(true);
	}

	
//	@Test
//	public void testSplit() {
//		
////		String st1 = "hello <b>world</b>";
////		String[] chunks1 = st1.split("(<[^>]*>)");  // Returns ["hello ","<b>","world","</b>",""] 
//		
//		//String line = "a1\t\ta2\t\ta3\ta4\t\t\t";
//		String line = "abc\td";
//		
//		ArrayList<String> chunks = new ArrayList<String>();
//				
//		int start = -1;
//		int prevStart = -1;
//		
//		for (int i = 0; i < line.length(); i++) {
//						
//			if (start > -1 && line.charAt(i) < ' ') {
//				if (prevStart > -1)
//					chunks.add(line.substring(prevStart, start));
//				
//				prevStart = start;
//				start = -1;
//				continue;
//			}
//			
//			if (start == -1 && line.charAt(i) > ' ') {
//				start = i;
//			}						
//		}
//		
//		if (start == -1) start = line.length();
//		
//		if (prevStart > -1 && start > -1)
//			chunks.add(line.substring(prevStart, start));
//		
//		if (start < line.length()) {
//			chunks.add(line.substring(start, line.length()));
//		}
//			
//		
////		//String st2 = "a1,a2,a3,a4";
////		
////		String[] chunks1 = st1.split("\t+");
//		//String[] chunks2 = st2.split(",");
//		
//		System.out.println(chunks.size());
//		//System.out.println(chunks2.length);
//		for (String st : chunks) {
//			System.out.println("\"" + st + "\"");
//		}
//	}
	
	
	@Test
	public void testFileEvents() {
		
		Parameters params = (Parameters) filter.getParameters();
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test9.txt");
		assertNotNull(input);
		
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.valuesStartLineNum = 2;
		params.columnNamesLineNum = 1;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
						
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 0, 1);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 0, 2);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 0, 3);
		_testEvent(EventType.TEXT_UNIT, "FieldName4", 1, 0, 4);	// Quotes remain part of the value
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 1, 0, 5);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 2, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 2, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 2, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 2, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value15", 2, 1, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 4, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 4, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 4, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 4, 3, 4);
		_testEvent(EventType.TEXT_UNIT, "Value35", 4, 3, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		params.valuesStartLineNum = 3;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test9.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 1, 3);
		_testEvent(EventType.END_GROUP, null);
				
		filter.close();
		
	}

	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = _getSkeleton(_getFullFileName("csv_test9.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test9.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(TabSeparatedValuesFilterTest.class.getResourceAsStream("/csv_test9.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		URL url = TableFilterTest.class.getResource("/csv_test9.txt");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test9.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}


// Helpers
	private String _getFullFileName(String fileName) {
		URL url = TableFilterTest.class.getResource("/csv_test9.txt");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		return root + fileName;
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
			
			assertEquals(expectedText, ((TextUnit) res).toString());
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

	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum, int expRow, int expCol) {
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
						
			prop = ((TextUnit) res).getSourceProperty(BaseTableFilter.ROW_NUMBER);
			assertNotNull(prop);
			
			st = prop.getValue();
			assertEquals(expRow, new Integer(st).intValue());
			
			prop = ((TextUnit) res).getSourceProperty(BaseTableFilter.COLUMN_NUMBER);
			assertNotNull(prop);
			
			st = prop.getValue();
			assertEquals(expCol, new Integer(st).intValue());
						
			
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
		BufferedReader reader = null;
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
