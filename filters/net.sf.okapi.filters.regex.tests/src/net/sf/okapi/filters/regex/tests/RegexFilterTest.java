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

package net.sf.okapi.filters.regex.tests;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.RegexFilter;
import net.sf.okapi.filters.regex.Rule;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author SV
 *
 */
public class RegexFilterTest {
	
	private RegexFilter filter;
	
	@Before
	public void setUp() {
		filter = new RegexFilter();
	}

	@Test
	public void runTest () {
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			// testDriver.setDisplayLevel(0);
			testDriver.setDisplayLevel(2);
			testDriver.setShowSkeleton(true);
			
			IParameters params = new Parameters();
			URL paramsUrl = RegexFilterTest.class.getResource("/okf_regex@StringInfo.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			InputStream input = RegexFilterTest.class.getResourceAsStream("/Test01_stringinfo_en.info");
			filter.open(new RawDocument(input, "UTF-8", "en"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			
			paramsUrl = RegexFilterTest.class.getResource("/okf_regex@SRT.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			input = RegexFilterTest.class.getResourceAsStream("/Test01_srt_en.srt");
			filter.open(new RawDocument(input, "UTF-8", "en"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	@Test
	public void testSimpleRule () {
		String snippet = "test1=\"text1\"\ntest2=\"text2\"\n";
		Parameters params = new Parameters();
		Rule rule = new Rule();
		rule.setRuleType(Rule.RULETYPE_STRING);
		rule.setExpression("=(.+)$");
		rule.setSourceGroup(1);
		params.rules.add(rule);
		filter.setParameters(params);
		// Process
		ArrayList<Event> list = getEvents(snippet);
		TextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text1", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("text2", tu.getSource().toString());
	}

	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, "en"));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

	
// SV =====================================================================================================	
//	
//	private String TEST_PARAMS1 = "/okf_regex@StringInfo.fprm";
//	private String TEST_DOC1 = "/Test01_stringinfo_en.info";
//	
//	private void initFilter(boolean setParams) {
//		FilterTestDriver testDriver = new FilterTestDriver();
//		testDriver.setDisplayLevel(2);
//		testDriver.setShowSkeleton(true);
//	
//		if (!setParams) return;
//		
//		IParameters params = new Parameters();
//		URL paramsUrl = RegexFilterTest.class.getResource(TEST_PARAMS1);
//		params.load(paramsUrl.getPath(), false);
//		filter.setParameters(params);		
//	}
//	
//	@Test
//	public void testDocName() {
//// Pass the 3 input types, docName is expected to be null if the input is stream /char sequence, not null if URI
//		
//		initFilter(true);
//
//		// stream, docName = null 		
//		InputStream input = RegexFilterTest.class.getResourceAsStream(TEST_DOC1);
//		
//		filter.open(new RawDocument(input, "UTF-8", "en"));
//		try {
//			Event event = filter.next();
//			assertTrue(event.getResource() instanceof StartDocument);
//			StartDocument sd = (StartDocument)event.getResource();
//			
//			assertNull(sd.getName());
//		}
//		finally {
//			filter.close();
//		}
//		
//		// char sequence, docName = null 		
//		filter.open(new RawDocument("a char sequence", "UTF-8", "en"));
//		try {
//			Event event2 = filter.next();
//			assertTrue(event2.getResource() instanceof StartDocument);
//			StartDocument sd2 = (StartDocument)event2.getResource();
//			
//			assertNull(sd2.getName());
//		}
//		finally {
//			filter.close();
//		}
//		
//		// URI, docName <> null
//		URL url = RegexFilterTest.class.getResource(TEST_DOC1);
//		URI uri = null;
//		try {
//			uri = url.toURI();
//		}
//		catch (URISyntaxException e){
//		}
//		filter.open(new RawDocument(uri, "UTF-8", "en"));
//		try {
//			Event event3 = filter.next();
//			assertTrue(event3.getResource() instanceof StartDocument);
//			StartDocument sd3 = (StartDocument)event3.getResource();
//			String name = sd3.getName();
//			
//			assertNotNull(name);
//		}
//		finally {
//			filter.close();
//		}
//	}
//	
//	@Test
//	public void testEmptyInput() {
//// Empty input, check exceptions
//				
//		initFilter(true);
//
//		// 1. Empty stream, OkapiBadFilterInputException expected, no other
//		boolean caught = false;
//		boolean caught2 = false;
//		
//		InputStream input = null;
//		try {
//			filter.open(new RawDocument(input, "UTF-8", "en"));
//		}	
//		catch (OkapiBadFilterInputException e) {
//			caught = true;
//		}
//		catch (Exception e) {
//			caught2 = true;
//		}
//		finally {
//			filter.close();
//		}
//		
//		assertTrue(caught);
//		assertFalse(caught2);
//		
//		
//		// 2. Empty raw doc, OkapiBadFilterInputException expected, no other
//		caught = false;
//		caught2 = false;
//		
//		try {
//			filter.open(null);
//		}	
//		catch (OkapiBadFilterInputException e) {
//			caught = true;
//		}
//		catch (Exception e) {
//			caught2 = true;
//		}
//		finally {
//			filter.close();
//		}
//	
//		assertTrue(caught);
//		assertFalse(caught2);
//		
//		
//		// 3. Empty filter parameters, OkapiBadFilterInputException (? a new exception class) expected, no other
//		caught = false;
//		caught2 = false;
//				
//		try {
//			filter.setParameters(null);
//			
//			InputStream input2 = RegexFilterTest.class.getResourceAsStream(TEST_DOC1);
//			filter.open(new RawDocument(input2, "UTF-8", "en"));
//		}	
//		catch (OkapiBadFilterInputException e) {
//			caught = true;
//		}
//		catch (Exception e) {
//			caught2 = true;
//		}
//		finally {
//			filter.close();
//		}
//	
//		assertTrue(caught);
//		assertFalse(caught2);
//		
//	}
//	
//		
//	
//	@Test
//	public void testSetParameters() {
//// Pass fake parameters, check if not eaten
//		
//		initFilter(false);		
//		
//		IParameters fakeParams = new TestParameters();
//		filter.setParameters(fakeParams);
//	}
//	
//	@Test
//	public void testSkeleton_generateSkeletonOption() {
//// Open filter with generateSkeleton true/false, see if the filter reacts
//		
//		boolean hasSkeleton = false;
//		initFilter(true);
//		
//		// generateSkeleton = true 		
//		InputStream input = RegexFilterTest.class.getResourceAsStream(TEST_DOC1);
//		
//		filter.open(new RawDocument(input, "UTF-8", "en"), true); // !!!		
//		try {
//			Event event;
//			while (filter.hasNext()) {
//				event = filter.next();
//				
//				IResource res = event.getResource();
//				ISkeleton skel = res.getSkeleton();
//				String s = null;
//				
//				if (skel != null) {
//					s = skel.toString();	
//					
//					if ((s != null) && (s != "")) {
//						hasSkeleton = true;
//						break;
//					}
//				};
//			}
//			
//			assertTrue(hasSkeleton);			
//		}
//		finally {
//			filter.close();
//		}
//		
//				
//		// generateSkeleton = false
//		hasSkeleton = false;
//		input = RegexFilterTest.class.getResourceAsStream(TEST_DOC1);
//		
//		filter.open(new RawDocument(input, "UTF-8", "en"), false); // !!!		
//		try {
//			Event event;
//			while (filter.hasNext()) {
//				event = filter.next();
//				
//				IResource res = event.getResource();
//				ISkeleton skel = res.getSkeleton();
//				String s = null;
//				
//				if (skel != null) {
//					s = skel.toString();	
//					
//					if ((s != null) && (s != "")) {
//						hasSkeleton = true;
//						break;
//					}
//				};
//			}
//			
//			assertFalse(hasSkeleton);
//		}
//		finally {
//			filter.close();
//		}
//	}
//	
//	
//	@Test
//	public void testSkeleton_outputSkeleton() {
//	// collects parts of the skeleton from events, builds a test string
//		
//		String outputSkeleton = "";
//		
//		InputStream input = RegexFilterTest.class.getResourceAsStream(TEST_DOC1);		
//		filter.open(new RawDocument(input, "UTF-8", "en"), true);
//		
////		filter.open(new RawDocument("Line 1/r/nLine2", "UTF-8", "en"), true);
//		try {
//			Event event;
//			while (filter.hasNext()) {
//				event = filter.next();
//				
//				switch (event.getEventType()) {
////					case START_DOCUMENT:
////					case END_DOCUMENT:
////					case START_SUBDOCUMENT:
////					case END_SUBDOCUMENT:
////					case START_GROUP:
////					case END_GROUP:
////					case TEXT_UNIT:
//					case DOCUMENT_PART:
//					
//					IResource res = event.getResource();
//					ISkeleton skel = res.getSkeleton();
//					String s = null;
//					
//					if (skel != null) {
//						s = skel.toString();	
//						
//						if ((s != null) && (s != "")) {
//							outputSkeleton += s;						
//						}
//					}
//				}
//			}			
//		}
//		finally {
//			filter.close();
//		}
//		System.out.println("----------------------------- outputSkeleton -------------------------------");
//		System.out.println(">>>>>>" + outputSkeleton + "<<<<<<");
//	}
//	
//// Not yet	
//	
//	
////	@Test
//	public void testLinebreakNormalization() {
//// Pass the 3 input types with /r/n linebreaks, check if replaced with /n
//		
//		initFilter(true);
//		
//		// char sequence 		
//		filter.open(new RawDocument("Line 1/r/nLine2", "UTF-8", "en"), true);
//		try {
//			Event event2 = filter.next();
//			assertTrue(event2.getResource() instanceof StartDocument);
//			StartDocument sd2 = (StartDocument)event2.getResource();
//	
//			ISkeleton skel = sd2.getSkeleton();
//			String s = null;
//			if (skel != null) {s = skel.toString();}
//			
//			System.out.println(s);
////			assertNull(sd2.getName());
//		}
//		finally {
//			filter.close();
//		}
//		
//		
//	}
//	
////	@Test
//	public void testSkeleton_common() {
//// Write a skeleton into a file, compare with the input file
//// Test trailing /n
//// Check if open(, generateSkeleton = false) blocks skeleton generation
//// Check if the linebreaks of skeleton are the same as those of the input file 		
//	}
	
}
