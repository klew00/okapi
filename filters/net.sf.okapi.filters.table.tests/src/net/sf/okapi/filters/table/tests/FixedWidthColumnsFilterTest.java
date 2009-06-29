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
import java.util.List;

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
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.filters.table.fwc.FixedWidthColumnsFilter;
import net.sf.okapi.filters.table.fwc.Parameters;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthColumnsFilterTest {

	private FixedWidthColumnsFilter filter;
	private FilterTestDriver testDriver;
	
	@Before
	public void setUp() {
		filter = new FixedWidthColumnsFilter();
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
		assertEquals(filter.getName(), "okf_table_fwc");
		
		// Read lines from a file, check mime types 
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
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
	public void testParameters() {
		
		// Check if PlainTextFilter params are set for inherited fields
		Parameters params = (Parameters) filter.getParameters();
								
		//assertEquals(params.columnWidths, "");
		assertEquals(params.columnStartPositions, "");
		assertEquals(params.columnEndPositions, "");
					
		// Check if defaults are set
		params = new Parameters();
		filter.setParameters(params);
		
		//params.columnWidths = "";
		params.columnStartPositions = "";
		params.columnEndPositions = "";
		
		params = _getParameters();
				
		//assertEquals("", params.columnWidths);
		assertEquals("", params.columnStartPositions);
		assertEquals("", params.columnEndPositions);
		
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = TableFilterTest.class.getResource("/test_params3.txt");
		assertNotNull(paramsUrl);  
		
		try {
			params.load(paramsUrl.toURI(), false);
		} catch (URISyntaxException e) {
		}
		
		
		//assertEquals("19, 30, 21, 16, 15, 21, 20", params.columnWidths);
		assertEquals("1, 20, 50, 71, 87, 102, 123, 144", params.columnStartPositions);
		assertEquals("11, 32, 62, 83, 97, 112, 133, 151", params.columnEndPositions);
		
		// Save filter parameters to a file, load and check if params have changed
		paramsUrl = TableFilterTest.class.getResource("/test_params2.txt");
		assertNotNull(paramsUrl);
		
		params.save(paramsUrl.getPath());
		
		// Change params before loading them
		params = (Parameters) filter.getParameters();
		
		//params.columnWidths = "1, 23, 30";
		params.columnStartPositions = "1, 23, 30";
		params.columnEndPositions = "10, 21, 40";
		
		params.load(Util.toURI(paramsUrl.getPath()), false);		
		// assertEquals("19, 30, 21, 16, 15, 21, 20", params.columnWidths);
		assertEquals("1, 20, 50, 71, 87, 102, 123, 144", params.columnStartPositions);
		assertEquals("11, 32, 62, 83, 97, 112, 133, 151", params.columnEndPositions);
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		
		// Check if parameters type is controlled
		
		filter.setParameters(new net.sf.okapi.filters.plaintext.base.Parameters());
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		try {
			filter.open(new RawDocument(input, "UTF-8", "en"));
			fail("OkapiBadFilterParametersException should've been trown");
		}
		catch (OkapiBadFilterParametersException e) {
		}
		
		filter.close();
	
		filter.setParameters(new net.sf.okapi.filters.table.fwc.Parameters());
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		try {
			filter.open(new RawDocument(input, "UTF-8", "en"));
		}
		catch (OkapiBadFilterParametersException e) {
			fail("OkapiBadFilterParametersException should NOT have been trown");
		}
			filter.close();
	}
	
	@Test
	public void testListedColumns() {
		
		Parameters params = (Parameters) filter.getParameters();
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_testb.txt");
		assertNotNull(input);
		
		params.columnNamesLineNum = 0;
		params.valuesStartLineNum = 1;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_NONE;
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.sendColumnsMode = Parameters.SEND_COLUMNS_LISTED;
		//params.columnWidths = "19, 30, 21, 16, 15, 21, 20, 10";
		params.columnStartPositions = "1, 20, 50, 71, 87, 102, 123, 144";
		params.columnEndPositions = "11, 32, 62, 83, 97, 112, 133, 151";
		
		params.sourceColumns = "4, 6";
		params.sourceIdSuffixes = "_name, _descr";
		params.targetColumns = "     2,7   ";
		params.targetLanguages = "ge-sw, it";
		params.targetSourceRefs = "6, 4";
		params.sourceIdColumns = "1, 3";
		params.sourceIdSourceRefs = "4, 6";
		params.commentColumns = "5";
		params.commentSourceRefs = "4";
		params.recordIdColumn = 8;
				
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value24", "Value21", "Value27", "it", "Value25");
		_testEvent(EventType.TEXT_UNIT, "Value26", "Value23", "Value22", "ge-sw", "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value34", "Value31", "Value37", "it", "Value35");
		_testEvent(EventType.TEXT_UNIT, "Value36", "recID2_descr", "Value32", "ge-sw", "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
	
	@Test
	public void testListedColumns2() {
		
		Parameters params = (Parameters) filter.getParameters();
		
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_testa.txt");
		assertNotNull(input);
		
		params.columnNamesLineNum = 1;
		params.valuesStartLineNum = 2;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_NONE;
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.sendColumnsMode = Parameters.SEND_COLUMNS_LISTED;
		//params.columnWidths = "19, 30, 21, 16, 15, 21, 20, 10";
		params.columnStartPositions = "1, 20, 50, 71, 87, 102, 123, 144";
		params.columnEndPositions = "11, 32, 62, 83, 97, 112, 133, 151";
		
		params.sourceColumns = "4, 6";
		params.sourceIdSuffixes = "_name, _descr";
		params.targetColumns = "     2,7   ";
		params.targetLanguages = "ge-sw, it";
		params.targetSourceRefs = "6, 4";
		params.sourceIdColumns = "1, 3";
		params.sourceIdSourceRefs = "4, 6";
		params.commentColumns = "5";
		params.commentSourceRefs = "4";
		params.recordIdColumn = 8;
				
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "ID"); 
		_testEvent(EventType.TEXT_UNIT, "Target1");
		_testEvent(EventType.TEXT_UNIT, "SID1");
		_testEvent(EventType.TEXT_UNIT, "Source1");
		_testEvent(EventType.TEXT_UNIT, "Source2");
		_testEvent(EventType.TEXT_UNIT, "Target2");
		_testEvent(EventType.TEXT_UNIT, "Key");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value14", "Value11", "Value17", "it", "");
		//_testEvent(EventType.TEXT_UNIT, "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value24", "Value28_name", "Value27", "it", "Value25");
		_testEvent(EventType.TEXT_UNIT, "Value26", "Value23", "Value22", "ge-sw", "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value34", "Value31", "", "it", "");
		//_testEvent(EventType.TEXT_UNIT, "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value44", "Value41", "Value47", "it", "Value45");
		_testEvent(EventType.TEXT_UNIT, "Value46", "Value48_descr", "Value42", "ge-sw", "");
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
	}
	@Test
	public void testFileEvents() {
		testDriver.setDisplayLevel(2);
						
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = TableFilterTest.class.getResource("/test_params3.txt");
		assertNotNull(paramsUrl);  
		
		Parameters params = (Parameters) filter.getParameters();
		
		try {
			params.load(paramsUrl.toURI(), false);
		} catch (URISyntaxException e) {
		}
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		assertNotNull(input);
		
		params.valuesStartLineNum = 2;
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
						
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 0, 1);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 0, 2);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 0, 3);
		_testEvent(EventType.TEXT_UNIT, "\"FieldName4\"", 1, 0, 4);	// Quotes remain part of the value
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 1, 0, 5);
		_testEvent(EventType.TEXT_UNIT, "FieldName6", 1, 0, 6);
		_testEvent(EventType.TEXT_UNIT, "FieldName7", 1, 0, 7);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 2, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 2, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 2, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 2, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value17", 2, 1, 7);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 2, 3);
		_testEvent(EventType.TEXT_UNIT, "Value24", 3, 2, 4);
		_testEvent(EventType.TEXT_UNIT, "Value25", 3, 2, 5);
		_testEvent(EventType.TEXT_UNIT, "Value26", 3, 2, 6);
		_testEvent(EventType.TEXT_UNIT, "Value27", 3, 2, 7);			
		_testEvent(EventType.TEXT_UNIT, "Value28", 3, 2, 8);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 4, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 4, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 4, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 4, 3, 4);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value41", 5, 4, 1);
		_testEvent(EventType.TEXT_UNIT, "Value42", 5, 4, 2);
		_testEvent(EventType.TEXT_UNIT, "Value43", 5, 4, 3);
		_testEvent(EventType.TEXT_UNIT, "Value44", 5, 4, 4);
		_testEvent(EventType.TEXT_UNIT, "Value45", 5, 4, 5);
		_testEvent(EventType.TEXT_UNIT, "Value46", 5, 4, 6);
		_testEvent(EventType.TEXT_UNIT, "Value47", 5, 4, 7);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		params.valuesStartLineNum = 3;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value24", 3, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value25", 3, 1, 5);
		_testEvent(EventType.TEXT_UNIT, "Value26", 3, 1, 6);
		_testEvent(EventType.TEXT_UNIT, "Value27", 3, 1, 7);
		_testEvent(EventType.TEXT_UNIT, "Value28", 3, 1, 8);
		_testEvent(EventType.END_GROUP, null);
				
		
		filter.close();
		
		params.valuesStartLineNum = 3;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 1, 1, 10);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 1, 2, 12);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 1, 3, 12);
		_testEvent(EventType.TEXT_UNIT, "Value24", 3, 1, 4, 12);
		_testEvent(EventType.TEXT_UNIT, "Value25", 3, 1, 5, 10);
		_testEvent(EventType.TEXT_UNIT, "Value26", 3, 1, 6, 10);
		_testEvent(EventType.TEXT_UNIT, "Value27", 3, 1, 7, 10);			
		_testEvent(EventType.TEXT_UNIT, "Value28", 3, 1, 8, 7);			
		_testEvent(EventType.END_GROUP, null);
		
		filter.close();
		
		params.valuesStartLineNum = 3;
		params.sendHeaderMode = Parameters.SEND_HEADER_COLUMN_NAMES_ONLY;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 0, 1);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 0, 2);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 0, 3);
		_testEvent(EventType.TEXT_UNIT, "\"FieldName4\"", 1, 0, 4);	// Quotes remain part of the value
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 1, 0, 5);
		_testEvent(EventType.TEXT_UNIT, "FieldName6", 1, 0, 6);
		_testEvent(EventType.TEXT_UNIT, "FieldName7", 1, 0, 7);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 3, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 3, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 3, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value24", 3, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value25", 3, 1, 5);
		_testEvent(EventType.TEXT_UNIT, "Value26", 3, 1, 6);
		_testEvent(EventType.TEXT_UNIT, "Value27", 3, 1, 7);			
		_testEvent(EventType.TEXT_UNIT, "Value28", 3, 1, 8);
		_testEvent(EventType.END_GROUP, null);
		
		filter.close();
		
		params.valuesStartLineNum = 1;
		//params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_FIXED_NUMBER;
		params.numColumns = 3;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 1, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 2, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 2, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 2, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		filter.close();
		
		params.numColumns = 10;
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test6.txt");
		filter.open(new RawDocument(input, "UTF-8", "en"));
				
		_testEvent(EventType.START_DOCUMENT, null);
					
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 1, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 1, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 1, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "\"FieldName4\"", 1, 1, 4);	// Quotes remain part of the value
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 1, 1, 5);
		_testEvent(EventType.TEXT_UNIT, "FieldName6", 1, 1, 6);
		_testEvent(EventType.TEXT_UNIT, "FieldName7", 1, 1, 7);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 2, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 2, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 2, 2, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 2, 2, 4);
		_testEvent(EventType.TEXT_UNIT, "Value17", 2, 2, 7);			// Value28 is ignored
		_testEvent(EventType.END_GROUP, null);
		
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
	public void testHeader() {
		
		// Load filter parameters from a file, check if params have changed
		URL paramsUrl = TableFilterTest.class.getResource("/test_params3.txt");
		assertNotNull(paramsUrl);  
		
		Parameters params = (Parameters) filter.getParameters();
		
		try {
			params.load(paramsUrl.toURI(), false);
		} catch (URISyntaxException e) {
		}
		InputStream input = TableFilterTest.class.getResourceAsStream("/csv_test8.txt");
		assertNotNull(input);
		
		params.valuesStartLineNum = 7;
		params.columnNamesLineNum = 4;
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.TEXT_UNIT, "Test table", 1, 0, true);
		
		_testEvent(EventType.TEXT_UNIT, "Contains column names in the 4-th line, a table caption in the 1-st line, and 4 lines of description. This is the 1-st header row.", 
				2, 0, true);
		
		_testEvent(EventType.TEXT_UNIT, "This is the 2-nd header row. This table also delimits the number of columns by their names (5 columns only are extracted here)", 
				3, 0, true);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 4, 0, 1, true);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 4, 0, 2, true);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 4, 0, 3, true);
		_testEvent(EventType.TEXT_UNIT, "FieldName4", 4, 0, 4, true);	
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 4, 0, 5, true);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.TEXT_UNIT, "This is the 4-th header row.",	5, 0, true);
		_testEvent(EventType.TEXT_UNIT, "This is the 5-th header row. Data start right after here.", 6, 0, true);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 7, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 7, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 7, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 7, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value15", 7, 1, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 8, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 8, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 8, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 9, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 9, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 9, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 9, 3, 4);
		_testEvent(EventType.TEXT_UNIT, "Value35", 9, 3, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
				
		input = TableFilterTest.class.getResourceAsStream("/csv_test8.txt");
		assertNotNull(input);
		
		params.valuesStartLineNum = 7;
		params.columnNamesLineNum = 4;
		params.sendHeaderMode = Parameters.SEND_HEADER_NONE;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
				
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 7, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 7, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 7, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 7, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value15", 7, 1, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 8, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 8, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 8, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 9, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 9, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 9, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 9, 3, 4);
		_testEvent(EventType.TEXT_UNIT, "Value35", 9, 3, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test8.txt");
		assertNotNull(input);
		
		params.valuesStartLineNum = 7;
		params.columnNamesLineNum = 4;
		params.sendHeaderMode = Parameters.SEND_HEADER_COLUMN_NAMES_ONLY;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "FieldName1", 4, 0, 1, true);
		_testEvent(EventType.TEXT_UNIT, "Field Name 2", 4, 0, 2, true);
		_testEvent(EventType.TEXT_UNIT, "Field Name 3", 4, 0, 3, true);
		_testEvent(EventType.TEXT_UNIT, "FieldName4", 4, 0, 4, true);	
		_testEvent(EventType.TEXT_UNIT, "FieldName5", 4, 0, 5, true);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 7, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 7, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 7, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 7, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value15", 7, 1, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 8, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 8, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 8, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 9, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 9, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 9, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 9, 3, 4);
		_testEvent(EventType.TEXT_UNIT, "Value35", 9, 3, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		List<String> list = filter.getColumnNames();
		assertNotNull(list);
		assertEquals(5, list.size());
		
		assertEquals("FieldName1", list.get(0));
		assertEquals("Field Name 3", list.get(2));
		assertEquals("FieldName5", list.get(4));
		
		input = TableFilterTest.class.getResourceAsStream("/csv_test8.txt");
		assertNotNull(input);
		
		params.valuesStartLineNum = 7;
		params.columnNamesLineNum = 0;
		params.sendHeaderMode = Parameters.SEND_HEADER_COLUMN_NAMES_ONLY;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_COL_NAMES;
		
		filter.open(new RawDocument(input, "UTF-8", "en"));
		
		_testEvent(EventType.START_DOCUMENT, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value11", 7, 1, 1);
		_testEvent(EventType.TEXT_UNIT, "Value12", 7, 1, 2);
		_testEvent(EventType.TEXT_UNIT, "Value13", 7, 1, 3);
		_testEvent(EventType.TEXT_UNIT, "Value14", 7, 1, 4);
		_testEvent(EventType.TEXT_UNIT, "Value15", 7, 1, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value21", 8, 2, 1);
		_testEvent(EventType.TEXT_UNIT, "Value22", 8, 2, 2);
		_testEvent(EventType.TEXT_UNIT, "Value23", 8, 2, 3);
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.START_GROUP, null);
		_testEvent(EventType.TEXT_UNIT, "Value31", 9, 3, 1);
		_testEvent(EventType.TEXT_UNIT, "Value32", 9, 3, 2);
		_testEvent(EventType.TEXT_UNIT, "Value33", 9, 3, 3);
		_testEvent(EventType.TEXT_UNIT, "Value34", 9, 3, 4);
		_testEvent(EventType.TEXT_UNIT, "Value35", 9, 3, 5);				
		_testEvent(EventType.END_GROUP, null);
		
		_testEvent(EventType.END_DOCUMENT, null);
		
		filter.close();
		
		list = filter.getColumnNames();
		assertNotNull(list);
		assertEquals(0, list.size());
		
	}
	
	@Test
	public void testSkeleton () {
		String st = null;
		String expected = null;
		
		try {
			st = _getSkeleton(_getFullFileName("csv_test6.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
		System.out.println(String.format("Skeleton of %s\n---\n", "csv_test6.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(FixedWidthColumnsFilterTest.class.getResourceAsStream("/csv_test6.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testSkeleton2 () {
		String st = null;
		String expected = null;
		
		Parameters params = (Parameters) filter.getParameters();
		
		params.columnNamesLineNum = 0;
		params.valuesStartLineNum = 1;
		params.detectColumnsMode = Parameters.DETECT_COLUMNS_NONE;
		params.sendHeaderMode = Parameters.SEND_HEADER_ALL;
		params.sendColumnsMode = Parameters.SEND_COLUMNS_LISTED;
		//params.columnWidths = "19, 30, 21, 16, 15, 21, 20, 10";
		params.columnStartPositions = "1, 20, 50, 71, 87, 102, 123, 144";
		params.columnEndPositions = "11, 32, 62, 83, 97, 112, 133, 151";
		
		params.sourceColumns = "4, 6";
		params.sourceIdSuffixes = "_name, _descr";
		params.targetColumns = "     2,7   ";
		params.targetLanguages = "ge-sw, it";
		params.targetSourceRefs = "6, 4";
		params.sourceIdColumns = "1, 3";
		params.sourceIdSourceRefs = "4, 6";
		params.commentColumns = "5";
		params.commentSourceRefs = "4";
		params.recordIdColumn = 8;
		
		try {
			st = _getSkeleton(_getFullFileName("csv_testb.txt"));
		} 
		catch (UnsupportedEncodingException e) {
		}	
		System.out.println(String.format("Skeleton of %s\n---\n", "csv_testb.txt") + st + "\n----------");
		
		try {
			expected = _streamAsString(FixedWidthColumnsFilterTest.class.getResourceAsStream("/csv_testb.txt"));			
		} 
		catch (IOException e) {
		}
		assertEquals(expected, st);
	}
	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		URL url = TableFilterTest.class.getResource("/csv_test6.txt");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		
		list.add(new InputDocument(root + "csv_test6.txt", ""));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
	}

// Helpers
	private String _getFullFileName(String fileName) {
		URL url = TableFilterTest.class.getResource("/csv_test6.txt");
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

	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum, int expRow, 
			int expCol, int expWidth) {
		
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
						
			prop = ((TextUnit) res).getSourceProperty(FixedWidthColumnsFilter.COLUMN_WIDTH);
			assertNotNull(prop);
			
			st = prop.getValue();
			assertEquals(expWidth, new Integer(st).intValue());
			
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
	
	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum, int expRow, 
			boolean isHeader) {
		
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
	
	private void _testEvent(EventType expectedType, String source, String expName, String target, String language, String comment) {
		
		assertNotNull(filter);
		
		Event event = filter.next();		
		assertNotNull(event);
		
		assertTrue(event.getEventType() == expectedType);
		
		switch (event.getEventType()) {
		
		case TEXT_UNIT:
			IResource res = event.getResource();
			assertTrue(res instanceof TextUnit);
			TextUnit tu = (TextUnit) res;
			
			assertEquals(source, tu.toString());
			
			Property prop = tu.getSourceProperty(AbstractLineFilter.LINE_NUMBER);
			assertNotNull(prop);
			
			if (!Util.isEmpty(expName)) {
				assertEquals(expName, tu.getName());
			}
			
			if (!Util.isEmpty(target) && !Util.isEmpty(language)) {
				
				TextContainer trg = tu.getTarget(language);
				assertNotNull(trg);
				assertEquals(target, trg.toString());
			}
			
			if (!Util.isEmpty(comment)) {
				
				prop = tu.getProperty(Property.NOTE);
				assertNotNull(prop);
				assertEquals(comment, prop.toString());
			}
			
			break;
		}
			
	}
	
	private void _testEvent(EventType expectedType, String expectedText, int expectedLineNum, int expRow, 
			int expCol, boolean isHeader) {
		
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
