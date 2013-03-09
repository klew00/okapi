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

package net.sf.okapi.filters.table;

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
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.filters.table.base.Parameters;
import net.sf.okapi.filters.table.tsv.TabSeparatedValuesFilter;
import net.sf.okapi.lib.extra.filters.AbstractLineFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;

import org.junit.Before;
import org.junit.Test;


public class TabSeparatedValuesFilterTest {

	private IFilter filter;
	private FilterTestDriver testDriver;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	private LocaleId locFRCA = LocaleId.fromString("fr-ca");
	
	@Before
	public void setUp() {
		filter = new TabSeparatedValuesFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
        root = TestUtil.getParentDir(this.getClass(), "/csv_test1.txt");
        
        Parameters params = (Parameters) filter.getParameters();
        CommaSeparatedValuesFilterTest.setDefaults(params);
	}
	
	@Test
	public void testFileEvents() {
		
		Parameters params = (Parameters) filter.getParameters();		
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test9.txt");
		assertNotNull(input);
		
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.valuesStartLineNum = 2;
		params.columnNamesLineNum = 1;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
						
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 0, 1);
		testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 0, 2);
		testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 0, 3);
		testEvent(EventType.TEXT_UNIT, "FieldName4", 1, 0, 4);	// Quotes remain part of the value
		testEvent(EventType.TEXT_UNIT, "FieldName5", 1, 0, 5);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value11", 2, 1, 1);
		testEvent(EventType.TEXT_UNIT, "Value12", 2, 1, 2);
		testEvent(EventType.TEXT_UNIT, "Value13", 2, 1, 3);
		testEvent(EventType.TEXT_UNIT, "Value14", 2, 1, 4);
		testEvent(EventType.TEXT_UNIT, "Value15", 2, 1, 5);				
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value21", 3, 2, 1);
		testEvent(EventType.TEXT_UNIT, "Value22", 3, 2, 2);
		testEvent(EventType.TEXT_UNIT, "Value23", 3, 2, 3);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value31", 4, 3, 1);
		testEvent(EventType.TEXT_UNIT, "Value32", 4, 3, 2);
		testEvent(EventType.TEXT_UNIT, "Value33", 4, 3, 3);
		testEvent(EventType.TEXT_UNIT, "Value34", 4, 3, 4);
		testEvent(EventType.TEXT_UNIT, "Value35", 4, 3, 5);				
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		params.valuesStartLineNum = 3;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test9.txt");
		filter.open(new RawDocument(input, "UTF-8", locEN));
				
		testEvent(EventType.START_DOCUMENT, null);
					
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value21", 3, 1, 1);
		testEvent(EventType.TEXT_UNIT, "Value22", 3, 1, 2);
		testEvent(EventType.TEXT_UNIT, "Value23", 3, 1, 3);
		testEvent(EventType.END_GROUP, null);
				
		filter.close();
		
	}
	
	@Test
	public void testFileEvents2() {
		
		Parameters params = (Parameters) filter.getParameters();
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/TSV_test.txt");
		assertNotNull(input);
		
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.valuesStartLineNum = 2;
		params.columnNamesLineNum = 1;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
						
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Source", 1, 0, 1);
		testEvent(EventType.TEXT_UNIT, "Target", 1, 0, 2);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Source text 1", 2, 1, 1);
		testEvent(EventType.TEXT_UNIT, "Target text 1", 2, 1, 2);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Source text 2", 3, 2, 1);
		testEvent(EventType.TEXT_UNIT, "Target text 2", 3, 2, 2);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		params.valuesStartLineNum = 2;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		
		input = TableFilterTest.class.getResourceAsStream("/TSV_test.txt");
		filter.open(new RawDocument(input, "UTF-8", locEN));
				
		testEvent(EventType.START_DOCUMENT, null);
					
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Source text 1", 2, 1, 1);
		testEvent(EventType.TEXT_UNIT, "Target text 1", 2, 1, 2);
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Source text 2", 3, 2, 1);
		testEvent(EventType.TEXT_UNIT, "Target text 2", 3, 2, 2);
		testEvent(EventType.END_GROUP, null);
				
		filter.close();
		
	}
	
	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = getSkeleton(getFullFileName("csv_test9.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test9.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(TabSeparatedValuesFilterTest.class.getResourceAsStream("/csv_test9.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton2 () {
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/TSV_test.txt");
		assertNotNull(input);
		
		String snippet = null;
		
		try {
			snippet = streamAsString(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the parameters
		Parameters params = new Parameters();
		params.sendColumnsMode = Parameters.SEND_COLUMNS_LISTED;
		params.sourceColumns = "1";
		params.targetColumns = "2";
		params.targetLanguages = "fr-ca";
		params.targetSourceRefs = "1";
		filter.setParameters(params);
		
		//System.out.println(snippet);
		
		String result = FilterTestDriver.generateOutput(getEvents(snippet, locEN, locFRCA),
			filter.getEncoderManager(), locFRCA);
		//System.out.println(result);
		assertEquals(snippet, result);
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test9.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}

	// Helpers
	private String getFullFileName(String fileName) {
		return root + fileName;
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

	private void testEvent(EventType expectedType, String expectedText, int expectedLineNum, int expRow, int expCol) {
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
						
			prop = ((ITextUnit)res).getSourceProperty(BaseTableFilter.ROW_NUMBER);
			assertNotNull(prop);
			
			st = prop.getValue();
			assertEquals(expRow, new Integer(st).intValue());
			
			prop = ((ITextUnit)res).getSourceProperty(BaseTableFilter.COLUMN_NUMBER);
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
	
	private ArrayList<Event> getEvents (String snippet,
		LocaleId srcLang,
		LocaleId trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang, trgLang));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
