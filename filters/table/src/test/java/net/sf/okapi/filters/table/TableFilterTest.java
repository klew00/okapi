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

package net.sf.okapi.filters.table;

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
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.table.TableFilter;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.filters.table.base.Parameters;
import net.sf.okapi.filters.table.csv.CommaSeparatedValuesFilter;
import net.sf.okapi.filters.table.fwc.FixedWidthColumnsFilter;
import net.sf.okapi.filters.table.tsv.TabSeparatedValuesFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TableFilterTest {

	private TableFilter filter;
	private FilterTestDriver testDriver;
	private String root;
	
	@Before
	public void setUp() {
		filter = new TableFilter();
		assertNotNull(filter);
		
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);

//m		URL url = TableFilterTest.class.getResource("/csv_test1.txt");
//m		root = Util.getDirectoryName(url.getPath());
//m		root = Util.getDirectoryName(root) + "/data/";
        root = TestUtil.getParentDir(this.getClass(), "/csv_test1.txt"); // maven
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
	
		// Empty filter parameters, OkapiBadFilterParametersException expected		
			filter.setParameters(null);
			
			InputStream input2 = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		try {
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
		assertEquals(filter.getMimeType(), "text/csv");
		assertEquals(filter.getName(), "okf_table");
		
		// Read lines from a file, check mime types 
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
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
	public void testFileEvents() {
		testDriver.setDisplayLevel(0);
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1");
		_testEvent(EventType.TEXT_UNIT, "FieldName2");
		_testEvent(EventType.TEXT_UNIT, "FieldName3");
		_testEvent(EventType.TEXT_UNIT, "FieldName4");
		_testEvent(EventType.TEXT_UNIT, "FieldName5");
		_testEvent(EventType.TEXT_UNIT, "FieldName6");
		_testEvent(EventType.TEXT_UNIT, "FieldName7");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11");
		_testEvent(EventType.TEXT_UNIT, "Value12");
		_testEvent(EventType.TEXT_UNIT, "Value13");
		_testEvent(EventType.TEXT_UNIT, "Value14");
		_testEvent(EventType.TEXT_UNIT, "Value15");
		_testEvent(EventType.TEXT_UNIT, "Value16");
		_testEvent(EventType.TEXT_UNIT, "Value17");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21");
		_testEvent(EventType.TEXT_UNIT, "Value22");
		_testEvent(EventType.TEXT_UNIT, "Value23");
		_testEvent(EventType.TEXT_UNIT, "Value24");
		_testEvent(EventType.TEXT_UNIT, "Value25");
		_testEvent(EventType.TEXT_UNIT, "Value26");
		_testEvent(EventType.TEXT_UNIT, "Value27");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31");
		_testEvent(EventType.TEXT_UNIT, "Value32");
		_testEvent(EventType.TEXT_UNIT, "Value33");
		_testEvent(EventType.TEXT_UNIT, "Value34");
		_testEvent(EventType.TEXT_UNIT, "Value35");
		_testEvent(EventType.TEXT_UNIT, "Value36");
		_testEvent(EventType.TEXT_UNIT, "Value37");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		// List events		
		String filename = "csv_test1.txt";
		input = TableFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		// System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", "en"));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}
			

	@Test
		public void testFileEvents2() {
			testDriver.setDisplayLevel(0);

			filter.setConfiguration(TabSeparatedValuesFilter.FILTER_CONFIG);		
			InputStream input = TableFilterTest.class.getResourceAsStream("/TSV_test.txt");
			assertNotNull(input);
			
			net.sf.okapi.filters.table.tsv.Parameters params = (net.sf.okapi.filters.table.tsv.Parameters) filter.getActiveParameters();
			
			params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
			params.valuesStartLineNum = 2;
			params.columnNamesLineNum = 1;
			params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
			
			filter.open(new RawDocument(input, "UTF-8", "en"));
			
			_testEvent(EventType.START_DOCUMENT, null);
			
			_testEvent(EventType.START_GROUP, null);
			_testEvent(EventType.TEXT_UNIT, "Source");
			_testEvent(EventType.TEXT_UNIT, "Target");
			_testEvent(EventType.END_GROUP, null);
			
			_testEvent(EventType.START_GROUP, null);
			_testEvent(EventType.TEXT_UNIT, "Source text 1");
			_testEvent(EventType.TEXT_UNIT, "Target text 1");
			_testEvent(EventType.END_GROUP, null);
			
			_testEvent(EventType.START_GROUP, null);
			_testEvent(EventType.TEXT_UNIT, "Source text 2");
			_testEvent(EventType.TEXT_UNIT, "Target text 2");
			_testEvent(EventType.END_GROUP, null);
			
			_testEvent(EventType.END_DOCUMENT, null);
			
			filter.close();
			
			// List events		
			String filename = "csv_test1.txt";
			input = TableFilterTest.class.getResourceAsStream("/" + filename);
			assertNotNull(input);
			
			// System.out.println(filename);
			filter.open(new RawDocument(input, "UTF-8", "en"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
		}
	
	
	@Test
	public void testSynchronization() {
		
		//------------------------
		filter.setConfiguration(CommaSeparatedValuesFilter.FILTER_CONFIG);
		IParameters params2 = filter.getActiveParameters();
		assertTrue(params2 instanceof net.sf.okapi.filters.table.csv.Parameters);
		assertTrue(params2 instanceof net.sf.okapi.filters.table.base.Parameters);
		assertFalse(params2 instanceof net.sf.okapi.filters.table.fwc.Parameters);
		
		filter.setConfiguration(FixedWidthColumnsFilter.FILTER_CONFIG);
		IParameters params3 = filter.getActiveParameters();
		assertTrue(params3 instanceof net.sf.okapi.filters.table.fwc.Parameters);
		assertTrue(params3 instanceof net.sf.okapi.filters.table.base.Parameters);
		assertFalse(params3 instanceof net.sf.okapi.filters.table.csv.Parameters);
		
		filter.setConfiguration(TabSeparatedValuesFilter.FILTER_CONFIG);
		IParameters params4 = filter.getActiveParameters();
		assertTrue(params4 instanceof net.sf.okapi.filters.table.tsv.Parameters);
		assertTrue(params4 instanceof net.sf.okapi.filters.table.base.Parameters);
		assertFalse(params4 instanceof net.sf.okapi.filters.table.fwc.Parameters);
		
		filter.setConfiguration(BaseTableFilter.FILTER_CONFIG);
		IParameters params5 = filter.getActiveParameters();
		assertTrue(params5 instanceof net.sf.okapi.filters.table.base.Parameters);
		assertFalse(params5 instanceof net.sf.okapi.filters.table.csv.Parameters);		
	}
	
	@Test
	public void testTrimMode() {
		
	}
	
	@Test
	public void testMultilineColNames() {
		
	}
	
	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = _getSkeleton(_getFullFileName("csv_test1.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test1.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(TableFilterTest.class.getResourceAsStream("/csv_test1.txt"));			
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
			st = _getSkeleton(_getFullFileName("csv_test2.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test2.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(TableFilterTest.class.getResourceAsStream("/csv_test2.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}		
/*	
	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"csv_test1.txt", ""),
			"UTF-8", "en", "en"));
	}
*/	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test1.txt", ""));
		list.add(new InputDocument(root + "csv_test2.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}

// Helpers
	private String _getFullFileName(String fileName) {
//m		URL url = TableFilterTest.class.getResource("/csv_test1.txt");
//m		String root = Util.getDirectoryName(url.getPath());
//m		root = Util.getDirectoryName(root) + "/data/";
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

//	private Parameters _getParameters() {
//		IParameters punk = filter.getParameters();
//		
//		if (punk instanceof Parameters)
//			return (Parameters) punk;
//		else
//			return null;
//	}

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
