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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.table.TableFilter;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.filters.table.base.Parameters;
import net.sf.okapi.filters.table.csv.CommaSeparatedValuesFilter;
import net.sf.okapi.filters.table.fwc.FixedWidthColumnsFilter;
import net.sf.okapi.filters.table.tsv.TabSeparatedValuesFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TableFilterTest {

	private TableFilter filter;
	private FilterTestDriver testDriver;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	
	@Before
	public void setUp() {
		filter = new TableFilter();
		assertNotNull(filter);
		testDriver = new FilterTestDriver();
		assertNotNull(testDriver);
		testDriver.setDisplayLevel(0);
		testDriver.setShowSkeleton(true);
        root = TestUtil.getParentDir(this.getClass(), "/csv_test1.txt");
        
        Parameters params = (Parameters) filter.getActiveParameters();
        CommaSeparatedValuesFilterTest.setDefaults(params);
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
	
		// Empty filter parameters, OkapiBadFilterParametersException expected		
			filter.setParameters(null);
			
			InputStream input2 = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		try {
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
		assertEquals(filter.getMimeType(), "text/csv");
		assertEquals(filter.getName(), "okf_table");
		
		// Read lines from a file, check mime types 
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
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
	public void testFileEvents() {
		testDriver.setDisplayLevel(0);
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		assertNotNull(input);
		
		filter.open(new RawDocument(input, "UTF-8", locEN));
		
		testEvent(EventType.START_DOCUMENT, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "FieldName1");
		testEvent(EventType.TEXT_UNIT, "FieldName2");
		testEvent(EventType.TEXT_UNIT, "FieldName3");
		testEvent(EventType.TEXT_UNIT, "FieldName4");
		testEvent(EventType.TEXT_UNIT, "FieldName5");
		testEvent(EventType.TEXT_UNIT, "FieldName6");
		testEvent(EventType.TEXT_UNIT, "FieldName7");
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value11");
		testEvent(EventType.TEXT_UNIT, "Value12");
		testEvent(EventType.TEXT_UNIT, "Value13");
		testEvent(EventType.TEXT_UNIT, "Value14");
		testEvent(EventType.TEXT_UNIT, "Value15");
		testEvent(EventType.TEXT_UNIT, "Value16");
		testEvent(EventType.TEXT_UNIT, "Value17");
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value21");
		testEvent(EventType.TEXT_UNIT, "Value22");
		testEvent(EventType.TEXT_UNIT, "Value23");
		testEvent(EventType.TEXT_UNIT, "Value24");
		testEvent(EventType.TEXT_UNIT, "Value25");
		testEvent(EventType.TEXT_UNIT, "Value26");
		testEvent(EventType.TEXT_UNIT, "Value27");
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.START_GROUP, null);
		testEvent(EventType.TEXT_UNIT, "Value31");
		testEvent(EventType.TEXT_UNIT, "Value32");
		testEvent(EventType.TEXT_UNIT, "Value33");
		testEvent(EventType.TEXT_UNIT, "Value34");
		testEvent(EventType.TEXT_UNIT, "Value35");
		testEvent(EventType.TEXT_UNIT, "Value36");
		testEvent(EventType.TEXT_UNIT, "Value37");
		testEvent(EventType.END_GROUP, null);
		
		testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		// List events		
		String filename = "csv_test1.txt";
		input = TableFilterTest.class.getResourceAsStream("/" + filename);
		assertNotNull(input);
		
		// System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", locEN));
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
			
			filter.open(new RawDocument(input, "UTF-8", locEN));
			
			testEvent(EventType.START_DOCUMENT, null);
			
			testEvent(EventType.START_GROUP, null);
			testEvent(EventType.TEXT_UNIT, "Source");
			testEvent(EventType.TEXT_UNIT, "Target");
			testEvent(EventType.END_GROUP, null);
			
			testEvent(EventType.START_GROUP, null);
			testEvent(EventType.TEXT_UNIT, "Source text 1");
			testEvent(EventType.TEXT_UNIT, "Target text 1");
			testEvent(EventType.END_GROUP, null);
			
			testEvent(EventType.START_GROUP, null);
			testEvent(EventType.TEXT_UNIT, "Source text 2");
			testEvent(EventType.TEXT_UNIT, "Target text 2");
			testEvent(EventType.END_GROUP, null);
			
			testEvent(EventType.END_DOCUMENT, null);
			
			filter.close();
			
			// List events		
			String filename = "csv_test1.txt";
			input = TableFilterTest.class.getResourceAsStream("/" + filename);
			assertNotNull(input);
			
			// System.out.println(filename);
			filter.open(new RawDocument(input, "UTF-8", locEN));
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
			st = getSkeleton(getFullFileName("csv_test1.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test1.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(TableFilterTest.class.getResourceAsStream("/csv_test1.txt"));			
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
			st = getSkeleton(getFullFileName("csv_test2.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
//debug		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test2.txt") + st + "\n----------");
		
		try {
			expected = streamAsString(TableFilterTest.class.getResourceAsStream("/csv_test2.txt"));			
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
			"UTF-8", locEN, locEN));
	}
*/	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test1.txt", ""));
		list.add(new InputDocument(root + "csv_test2.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
	}

	@Test
	public void testIssue124() {
		
		IParameters prms = filter.getParameters();
		assertEquals(net.sf.okapi.filters.table.Parameters.class, prms.getClass());
		
		net.sf.okapi.filters.table.Parameters params = (net.sf.okapi.filters.table.Parameters) filter.getParameters();
		
		URL paramsUrl = TableFilterTest.class.getResource("/okf_table@test124.fprm");
		assertNotNull(paramsUrl);  
		
		try {
			params.load(paramsUrl.toURI(), false);
		} catch (URISyntaxException e) {
		}
		
		filter.open(new RawDocument("", locEN, locFR));
		assertEquals("net.sf.okapi.filters.table.tsv.Parameters", params.getParametersClassName());
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == EventType.START_DOCUMENT);
		
		StartDocument startDoc = (StartDocument) event.getResource();
		IParameters sdps = startDoc.getFilterParameters();
		assertEquals(net.sf.okapi.filters.table.Parameters.class, sdps.getClass());
	}
		
// Helpers
	private String getFullFileName(String fileName) {
//m		URL url = TableFilterTest.class.getResource("/csv_test1.txt");
//m		String root = Util.getDirectoryName(url.getPath());
//m		root = Util.getDirectoryName(root) + "/data/";
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

//	private Parameters _getParameters() {
//		IParameters punk = filter.getParameters();
//		
//		if (punk instanceof Parameters)
//			return (Parameters) punk;
//		else
//			return null;
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
