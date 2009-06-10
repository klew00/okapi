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
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.WrapMode;
import net.sf.okapi.filters.table.csv.CommaSeparatedValuesFilter;
import net.sf.okapi.filters.table.csv.Parameters;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CommaSeparatedValuesFilterTest {

	private CommaSeparatedValuesFilter filter;
	private FilterTestDriver testDriver;
	
	@Before
	public void setUp() {
		filter = new CommaSeparatedValuesFilter();
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
	
		// Empty filter parameters, OkapiBadFilterParametersException expected		
			filter.setParameters(null);
			
			InputStream input2 = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
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
		assertEquals(filter.getName(), "okf_table_csv");
		
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
	
//	@Test
//	public void testParameters() {
//		
//		// Check if PlainTextFilter params are set for inherited fields
//		Parameters params = (Parameters) filter.getParameters();
//								
//		assertEquals(params.columnWidths, "");
//					
//		// Check if defaults are set
//		params = new Parameters();
//		filter.setParameters(params);
//		
//		params.columnWidths = "";
//		
//		params = _getParameters();
//				
//		assertEquals("", params.columnWidths);
//		
//		// Load filter parameters from a file, check if params have changed
//		URL paramsUrl = TableFilterTest.class.getResource("/test_params3.txt");
//		assertNotNull(paramsUrl);  
//		
//		try {
//			params.load(paramsUrl.toURI(), false);
//		} catch (URISyntaxException e) {
//		}
//		
//		
//		assertEquals("19, 30, 21, 16, 15, 21, 20", params.columnWidths);
//		
//		// Save filter parameters to a file, load and check if params have changed
//		paramsUrl = TableFilterTest.class.getResource("/test_params2.txt");
//		assertNotNull(paramsUrl);
//		
//		params.save(paramsUrl.getPath());
//		
//		// Change params before loading them
//		params = (Parameters) filter.getParameters();
//		
//		params.columnWidths = "1, 23, 30";
//		
//		params.load(Util.toURI(paramsUrl.getPath()), false);		
//		assertEquals("19, 30, 21, 16, 15, 21, 20", params.columnWidths);
//		
//		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
//		filter.open(new RawDocument(input, "UTF-8", "en"));
//		
//		
//		// Check if parameters type is controlled
//		
//		filter.setParameters(new net.sf.okapi.filters.plaintext.base.Parameters());
//		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
//		try {
//			filter.open(new RawDocument(input, "UTF-8", "en"));
//			fail("OkapiBadFilterParametersException should've been trown");
//		}
//		catch (OkapiBadFilterParametersException e) {
//		}
//		
//		filter.close();
//	
//		filter.setParameters(new net.sf.okapi.filters.table.csv.Parameters());
//		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
//		try {
//			filter.open(new RawDocument(input, "UTF-8", "en"));
//		}
//		catch (OkapiBadFilterParametersException e) {
//			fail("OkapiBadFilterParametersException should NOT have been trown");
//		}
//			filter.close();
//	}

	@Test
	public void testParameters() {
		
		// Check if PlainTextFilter params are set for inherited fields
		Parameters params = (Parameters) filter.getParameters();
						
		assertEquals(params.unescapeSource, true);
		assertEquals(params.trimLeft, true);
		assertEquals(params.trimRight, false);
		assertEquals(params.preserveWS, true);
		assertEquals(params.useCodeFinder, false);
		assertEquals(params.regularExpressionForEmbeddedMarkup, "");
					
		// Check if defaults are set
		params = new Parameters();
		filter.setParameters(params);
		
		params.columnNamesLineNum = 1;
		params.valuesStartLineNum = 1;
		params.detectColumnsMode = 1;
		params.numColumns = 1;
		params.sendHeaderMode = 1;
		params.trimMode = 1;
		params.fieldDelimiter = "1";
		params.textQualifier = "1";
		params.sourceIdColumns = "1";
		params.sourceColumns = "1";
		params.targetColumns = "1";
		params.commentColumns = "1";
		params.preserveWS = true;
		params.useCodeFinder = true;
		
		params = _getParameters();
		
		assertEquals(params.fieldDelimiter, "1");
		assertEquals(params.columnNamesLineNum, 1);
		assertEquals(params.numColumns, 1);
		assertEquals(params.sendHeaderMode, 1);
		assertEquals(params.textQualifier, "1");
		assertEquals(params.trimMode, 1);
		assertEquals(params.valuesStartLineNum, 1);
		assertEquals(params.preserveWS, true);
		assertEquals(params.useCodeFinder, true);
		
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = TableFilterTest.class.getResource("/test_params1.txt");
		assertNotNull(paramsUrl);  
		
		try {
			params.load(paramsUrl.toURI(), false);
		} catch (URISyntaxException e) {
		}
		
		assertEquals("2", params.fieldDelimiter);
		assertEquals(params.columnNamesLineNum, 2);
		assertEquals(params.numColumns, 2);
		assertEquals(params.sendHeaderMode, 2);
		assertEquals("2", params.textQualifier);
		assertEquals(params.trimMode, 2);
		assertEquals(params.valuesStartLineNum, 2);
		assertEquals(params.preserveWS, false);
		assertEquals(params.useCodeFinder, false);
		
		// Save filter parameters to a file, load and check if params have changed
		paramsUrl = TableFilterTest.class.getResource("/test_params2.txt");
		assertNotNull(paramsUrl);
		
		params.save(paramsUrl.getPath());
		
		// Change params before loading them
		params = (Parameters) filter.getParameters();
		params.fieldDelimiter = "3";
		params.columnNamesLineNum = 3;
		params.numColumns = 3;
		params.sendHeaderMode = 3;
		params.textQualifier = "3";
		params.trimMode = 3;
		params.valuesStartLineNum = 3;
		params.preserveWS = true;
		params.useCodeFinder = true;
		
		params.load(Util.toURI(paramsUrl.getPath()), false);
		
		assertEquals(params.fieldDelimiter, "2");
		assertEquals(params.columnNamesLineNum, 2);
		assertEquals(params.numColumns, 2);
		assertEquals(params.sendHeaderMode, 2);
		assertEquals(params.textQualifier, "2");
		assertEquals(params.trimMode, 2);
		assertEquals(params.valuesStartLineNum, 2);
		assertEquals(params.preserveWS, false);
		assertEquals(params.useCodeFinder, false);
		
		// Check if parameters type is controlled
		
		filter.setParameters(new net.sf.okapi.filters.plaintext.base.Parameters());
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		try {
			filter.open(new RawDocument(input, "UTF-8", "en"));
			fail("OkapiBadFilterParametersException should've been trown");
		}
		catch (OkapiBadFilterParametersException e) {
		}
		
		filter.close();
	
		filter.setParameters(new net.sf.okapi.filters.table.csv.Parameters());
		input = TableFilterTest.class.getResourceAsStream("/csv_test1.txt");
		try {
			filter.open(new RawDocument(input, "UTF-8", "en"));
		}
		catch (OkapiBadFilterParametersException e) {
			fail("OkapiBadFilterParametersException should NOT have been trown");
		}
			filter.close();
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
		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test1.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(CommaSeparatedValuesFilterTest.class.getResourceAsStream("/csv_test1.txt"));			
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
		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test2.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(CommaSeparatedValuesFilterTest.class.getResourceAsStream("/csv_test2.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testFileEvents() {
		testDriver.setDisplayLevel(2);
		
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
		
		System.out.println(filename);
		filter.open(new RawDocument(input, "UTF-8", "en"));
		if ( !testDriver.process(filter) ) Assert.fail();
		filter.close();
	}
	
	@Test
	public void testQualifiedValues() {
				
		//_getParameters().detectColumnsMode = Parameters.DETECT_COLUMNS_NONE;
		//_getParameters().compoundTuDelimiter = "\n";
		
		Parameters params = (Parameters) filter.getParameters();
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_NONE;
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test3.txt");
		assertNotNull(input);
		
		params.wrapMode = WrapMode.NONE; // !!!
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		// Line 1
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11");
		_testEvent(EventType.TEXT_UNIT, "Value12");
		_testEvent(EventType.TEXT_UNIT, "Value13");
		_testEvent(EventType.TEXT_UNIT, "Value14");		
		_testEvent(EventType.END_GROUP, null);
		
		// Line 2
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21");
		_testEvent(EventType.TEXT_UNIT, "Value22.1,Value22.2, Value22.3");
		_testEvent(EventType.TEXT_UNIT, "Value23");
		_testEvent(EventType.TEXT_UNIT, "Value24");		
		_testEvent(EventType.END_GROUP, null);
				
		// Correct multiline chunk
		// Line 4-7
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31");
		_testEvent(EventType.TEXT_UNIT, "Value32");
		_testEvent(EventType.TEXT_UNIT, "Value33");
		_testEvent(EventType.TEXT_UNIT, "Value34.1\nValue34.2\nValue34.3\nValue34.4,Value34.5");
		_testEvent(EventType.TEXT_UNIT, "Value35");
		_testEvent(EventType.END_GROUP, null);
		
		// Incorrect multiline chunk 1
		// Line 9
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value41");
		_testEvent(EventType.TEXT_UNIT, "Value42");
		_testEvent(EventType.TEXT_UNIT, "Value43");
		_testEvent(EventType.TEXT_UNIT, "\"Value44.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 10
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 11
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 12
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.4");
		_testEvent(EventType.TEXT_UNIT, "Value45.1,Value45.2");
		_testEvent(EventType.END_GROUP, null);		
		
		// Incorrect multiline chunk 2
		// Line 14
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value51");
		_testEvent(EventType.TEXT_UNIT, "Value52");
		_testEvent(EventType.TEXT_UNIT, "Value53");
		_testEvent(EventType.TEXT_UNIT, "\"Value54.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 15
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 16
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 17-18
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.4");
		_testEvent(EventType.TEXT_UNIT, "Value55.1,Value55.2\nValue55.3,Value55.4");
		_testEvent(EventType.END_GROUP, null);
		
		// Incorrect multiline chunk 3
		// Line 20
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value61");
		_testEvent(EventType.TEXT_UNIT, "Value62");
		_testEvent(EventType.TEXT_UNIT, "Value63");
		_testEvent(EventType.TEXT_UNIT, "\"Value64.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 21
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 22
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 23
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.4");
		_testEvent(EventType.TEXT_UNIT, "\"Value65.1");
		_testEvent(EventType.TEXT_UNIT, "Value65.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 24
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value65.3");
		_testEvent(EventType.TEXT_UNIT, "Value65.4");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 25
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value65.5");
		_testEvent(EventType.TEXT_UNIT, "Value66");
		_testEvent(EventType.END_GROUP, null);
		// -------------------------------------------------
		
		// Line 27
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value71");
		_testEvent(EventType.TEXT_UNIT, "Value72 \"quoted part 1\"\"quoted part 2\" value");
		_testEvent(EventType.TEXT_UNIT, "Value73");
		_testEvent(EventType.TEXT_UNIT, "Value74");		
		_testEvent(EventType.END_GROUP, null);
		
		// Line 28
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value81");
		_testEvent(EventType.TEXT_UNIT, "\"Value82 with unclosed quote"); // Preserve the wrong quotation
		_testEvent(EventType.END_GROUP, null);
		
		// Line 29
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value91");
		_testEvent(EventType.TEXT_UNIT, "Value92");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 30
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueA1");
		_testEvent(EventType.TEXT_UNIT, "ValueA2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 31
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueB1");
		_testEvent(EventType.TEXT_UNIT, "Value\"B2,Va\"lueB3");		// If quotation marks are not around field, preserve them 
		_testEvent(EventType.TEXT_UNIT, "Va\"lueB4\"");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 32
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueC1");
		_testEvent(EventType.TEXT_UNIT, "\"ValueC2");		 
		_testEvent(EventType.TEXT_UNIT, "ValueC3");			
		_testEvent(EventType.TEXT_UNIT, "\"ValueC4");
		_testEvent(EventType.TEXT_UNIT, "ValueC5");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		filter.close();
		
		
		// Unwrap lines
		input = TableFilterTest.class.getResourceAsStream("/csv_test3.txt");
		assertNotNull(input);
		
		params.wrapMode = WrapMode.SPACES; // !!!
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		// Line 1
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11");
		_testEvent(EventType.TEXT_UNIT, "Value12");
		_testEvent(EventType.TEXT_UNIT, "Value13");
		_testEvent(EventType.TEXT_UNIT, "Value14");		
		_testEvent(EventType.END_GROUP, null);
		
		// Line 2
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21");
		_testEvent(EventType.TEXT_UNIT, "Value22.1,Value22.2, Value22.3");
		_testEvent(EventType.TEXT_UNIT, "Value23");
		_testEvent(EventType.TEXT_UNIT, "Value24");		
		_testEvent(EventType.END_GROUP, null);
				
		// Correct multiline chunk
		// Line 4-7
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31");
		_testEvent(EventType.TEXT_UNIT, "Value32");
		_testEvent(EventType.TEXT_UNIT, "Value33");
		_testEvent(EventType.TEXT_UNIT, "Value34.1 Value34.2 Value34.3 Value34.4,Value34.5");
		_testEvent(EventType.TEXT_UNIT, "Value35");
		_testEvent(EventType.END_GROUP, null);
		
		// Incorrect multiline chunk 1
		// Line 9
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value41");
		_testEvent(EventType.TEXT_UNIT, "Value42");
		_testEvent(EventType.TEXT_UNIT, "Value43");
		_testEvent(EventType.TEXT_UNIT, "\"Value44.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 10
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 11
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 12
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value44.4");
		_testEvent(EventType.TEXT_UNIT, "Value45.1,Value45.2");
		_testEvent(EventType.END_GROUP, null);		
		
		// Incorrect multiline chunk 2
		// Line 14
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value51");
		_testEvent(EventType.TEXT_UNIT, "Value52");
		_testEvent(EventType.TEXT_UNIT, "Value53");
		_testEvent(EventType.TEXT_UNIT, "\"Value54.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 15
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 16
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 17-18
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value54.4");
		_testEvent(EventType.TEXT_UNIT, "Value55.1,Value55.2 Value55.3,Value55.4");
		_testEvent(EventType.END_GROUP, null);
		
		// Incorrect multiline chunk 3
		// Line 20
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value61");
		_testEvent(EventType.TEXT_UNIT, "Value62");
		_testEvent(EventType.TEXT_UNIT, "Value63");
		_testEvent(EventType.TEXT_UNIT, "\"Value64.1");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 21
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 22
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.3");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 23
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value64.4");
		_testEvent(EventType.TEXT_UNIT, "\"Value65.1");
		_testEvent(EventType.TEXT_UNIT, "Value65.2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 24
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value65.3");
		_testEvent(EventType.TEXT_UNIT, "Value65.4");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 25
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value65.5");
		_testEvent(EventType.TEXT_UNIT, "Value66");
		_testEvent(EventType.END_GROUP, null);
		// -------------------------------------------------
		
		// Line 27
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value71");
		_testEvent(EventType.TEXT_UNIT, "Value72 \"quoted part 1\"\"quoted part 2\" value");
		_testEvent(EventType.TEXT_UNIT, "Value73");
		_testEvent(EventType.TEXT_UNIT, "Value74");		
		_testEvent(EventType.END_GROUP, null);
		
		// Line 28
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value81");
		_testEvent(EventType.TEXT_UNIT, "\"Value82 with unclosed quote"); // Preserve the wrong quotation
		_testEvent(EventType.END_GROUP, null);
		
		// Line 29
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "Value91");
		_testEvent(EventType.TEXT_UNIT, "Value92");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 30
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueA1");
		_testEvent(EventType.TEXT_UNIT, "ValueA2");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 31
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueB1");
		_testEvent(EventType.TEXT_UNIT, "Value\"B2,Va\"lueB3");		// If quotation marks are not around field, preserve them 
		_testEvent(EventType.TEXT_UNIT, "Va\"lueB4\"");
		_testEvent(EventType.END_GROUP, null);
		
		// Line 32
		_testEvent(EventType.START_GROUP, null);		
		_testEvent(EventType.TEXT_UNIT, "ValueC1");
		_testEvent(EventType.TEXT_UNIT, "\"ValueC2");		 
		_testEvent(EventType.TEXT_UNIT, "ValueC3");			
		_testEvent(EventType.TEXT_UNIT, "\"ValueC4");
		_testEvent(EventType.TEXT_UNIT, "ValueC5");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		filter.close();
		
		
//		input = TableFilterTest.class.getResourceAsStream("/csv_test3.txt");
//		assertNotNull(input);
//		
//		params.wrapMode = WrapMode.SPACE;
//		filter.open(new RawDocument(input, "UTF-8", "en"));
//		
//		_testEvent(EventType.START_DOCUMENT, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value11");
//		_testEvent(EventType.TEXT_UNIT, "Value12");
//		_testEvent(EventType.TEXT_UNIT, "Value13");
//		_testEvent(EventType.TEXT_UNIT, "Value14");		
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value21");
//		_testEvent(EventType.TEXT_UNIT, "Value22.1,Value22.2, Value22.3");
//		_testEvent(EventType.TEXT_UNIT, "Value23");
//		_testEvent(EventType.TEXT_UNIT, "Value24");		
//		_testEvent(EventType.END_GROUP, null);
//		
//		// Correct multiline chunk
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value31");
//		_testEvent(EventType.TEXT_UNIT, "Value32");
//		_testEvent(EventType.TEXT_UNIT, "Value33");
////		_testEvent(EventType.TEXT_UNIT, "Value34.1\nValue34.2\nValue34.3\nValue34.4,Value34.5");
//		_testEvent(EventType.TEXT_UNIT, "Value34.1,Value34.2,Value34.3,Value34.4,Value34.5");
//		_testEvent(EventType.TEXT_UNIT, "Value35");
//		_testEvent(EventType.END_GROUP, null);
//		
//		// Incorrect multiline chunk 1
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value31");
//		_testEvent(EventType.TEXT_UNIT, "Value32");
//		_testEvent(EventType.TEXT_UNIT, "Value33");
//		_testEvent(EventType.TEXT_UNIT, "\"Value34.1");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.2");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.3");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.4");
//		_testEvent(EventType.TEXT_UNIT, "Value35.1,Value35.2");
//		_testEvent(EventType.END_GROUP, null);		
//		
//		// Incorrect multiline chunk 2
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value31");
//		_testEvent(EventType.TEXT_UNIT, "Value32");
//		_testEvent(EventType.TEXT_UNIT, "Value33");
//		_testEvent(EventType.TEXT_UNIT, "\"Value34.1");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.2");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.3");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.4");
//		_testEvent(EventType.TEXT_UNIT, "Value35.1,Value35.2,Value35.3,Value35.4");
//		_testEvent(EventType.END_GROUP, null);
//		
//		// Incorrect multiline chunk 3
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value31");
//		_testEvent(EventType.TEXT_UNIT, "Value32");
//		_testEvent(EventType.TEXT_UNIT, "Value33");
//		_testEvent(EventType.TEXT_UNIT, "\"Value34.1");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.2");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.3");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value34.4");
//		_testEvent(EventType.TEXT_UNIT, "\"Value35.1,Value35.2");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value35.3");
//		_testEvent(EventType.TEXT_UNIT, "Value35.4");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value35.5");
//		_testEvent(EventType.TEXT_UNIT, "Value35.6");
//		_testEvent(EventType.END_GROUP, null);
//		// -------------------------------------------------
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value41");
//		_testEvent(EventType.TEXT_UNIT, "Value42 \"quoted part 1\" \"quoted part 2\" value");
//		_testEvent(EventType.TEXT_UNIT, "Value43");
//		_testEvent(EventType.TEXT_UNIT, "Value44");		
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value51");
//		_testEvent(EventType.TEXT_UNIT, "\"Value52 with unclosed quote"); // Preserve the wrong quotation
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value61");
//		_testEvent(EventType.TEXT_UNIT, "Value62");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value71");
//		_testEvent(EventType.TEXT_UNIT, "Value72");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value81");
//		_testEvent(EventType.TEXT_UNIT, "\"Value\"82");		// If quotation marks are not around field, preserve them 
//		_testEvent(EventType.TEXT_UNIT, "Value83");			// If quotation marks are not around field, preserve them
//		_testEvent(EventType.TEXT_UNIT, "Va\"lue84\"");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.START_GROUP, null);
//		_testEvent(EventType.TEXT_UNIT, "Value91");
//		_testEvent(EventType.TEXT_UNIT, "\"Value92");		 
//		_testEvent(EventType.TEXT_UNIT, "Value93");			
//		_testEvent(EventType.TEXT_UNIT, "\"Value94");
//		_testEvent(EventType.TEXT_UNIT, "Value95");
//		_testEvent(EventType.END_GROUP, null);
//		
//		_testEvent(EventType.END_DOCUMENT, null);
//		
//		filter.close();
	}

	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		URL url = TableFilterTest.class.getResource("/csv_test1.txt");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test1.txt", ""));
		list.add(new InputDocument(root + "csv_test2.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}


	
	// Helpers
	private String _getFullFileName(String fileName) {
		URL url = TableFilterTest.class.getResource("/csv_test1.txt");
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

	private Parameters _getParameters() {
		IParameters punk = filter.getParameters();
		
		if (punk instanceof Parameters)
			return (Parameters) punk;
		else
			return null;
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
