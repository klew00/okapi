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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.RegexFilter;
import net.sf.okapi.filters.regex.Rule;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexFilterTest {
	
	private RegexFilter filter;
	
	@Before
	public void setUp() {
		filter = new RegexFilter();
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		URL url = RegexFilterTest.class.getResource("/Test01_stringinfo_en.info");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";
		
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01_srt_en.srt", "okf_regex@SRT.fprm"));
		list.add(new InputDocument(root+"Test01_stringinfo_en.info", "okf_regex@StringInfo.fprm"));
		list.add(new InputDocument(root+"TestRules01.txt", "okf_regex@TestRules01.fprm"));
		list.add(new InputDocument(root+"TestRules02.txt", "okf_regex@TestRules02.fprm"));
		list.add(new InputDocument(root+"TestRules03.txt", "okf_regex@TestRules03.fprm"));
		list.add(new InputDocument(root+"TestRules04.txt", "okf_regex@TestRules04.fprm"));
		list.add(new InputDocument(root+"TestRules05.txt", "okf_regex@TestRules05.fprm"));
		list.add(new InputDocument(root+"TestRules06.txt", "okf_regex@TestRules06.fprm")); 
	
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "fr"));
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

	@Test
	public void testIDAndText () {
		String snippet = "[Id1]\tText1\r\n[Id2]\tText2";
		Parameters params = new Parameters();
		Rule rule = new Rule();
		rule.setRuleType(Rule.RULETYPE_CONTENT);
		rule.setExpression("^\\[(.*?)]\\s*(.*?)(\\n|\\Z)");
		rule.setSourceGroup(2);
		rule.setNameGroup(1);
		rule.setPreserveWS(true);
		params.rules.add(rule);
		filter.setParameters(params);
		// Process
		ArrayList<Event> list = getEvents(snippet);
		TextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("Text1", tu.getSource().toString());
		assertEquals("Id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text2", tu.getSource().toString());
		assertEquals("Id2", tu.getName());
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

	@Test
	public void testEmptyLines() {
		Parameters params = new Parameters();
		Rule rule = new Rule();
		rule.setRuleType(Rule.RULETYPE_CONTENT);
		params.regexOptions = Pattern.MULTILINE;
		
		rule.setExpression("^(.*?)$");
		rule.setSourceGroup(1);
		
		//rule.setExpression("(^(?=.+))(.*?)$");
		//rule.setSourceGroup(2);
		
		params.rules.add(rule);
		filter.setParameters(params);
						
		String inputText = "Line 1\n\nLine 2\n\n\n\n\nLine 3\n\n\nLine 4";
		//                  0123456 7 8901234 5 6 7 8 9012345 6 7 890123 
		//                  0           1              2            3
				
		_listEvents(inputText);
		
		// Test individual events
		filter.open(new RawDocument(inputText, "en"));
		
		_testEvent(EventType.START_DOCUMENT, "");
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.DOCUMENT_PART, "\n\n");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.DOCUMENT_PART, "\n\n\n\n\n");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.DOCUMENT_PART, "\n\n\n");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
		_testEvent(EventType.END_DOCUMENT, "");
		
		
		String inputText2 = "Line 1\nLine 2\n\nLine 3\n\n\nLine 4\n\n\n\n\n\n";
		//                   0123456 7890123 4 5678901 2 3 4567890 1 2 3 4 5  
		//                   0          1           2            3
		
		filter.open(new RawDocument(inputText2, "en"));
		
		_testEvent(EventType.START_DOCUMENT, "");
		_testEvent(EventType.TEXT_UNIT, "Line 1");
		_testEvent(EventType.DOCUMENT_PART, "\n");
		_testEvent(EventType.TEXT_UNIT, "Line 2");
		_testEvent(EventType.DOCUMENT_PART, "\n\n");
		_testEvent(EventType.TEXT_UNIT, "Line 3");
		_testEvent(EventType.DOCUMENT_PART, "\n\n\n");
		_testEvent(EventType.TEXT_UNIT, "Line 4");
		_testEvent(EventType.DOCUMENT_PART, "\n\n\n\n\n\n");
		_testEvent(EventType.END_DOCUMENT, "");
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
			
			assertEquals(((TextUnit) res).toString(), expectedText);
			break;
			
		case DOCUMENT_PART:
			res = event.getResource();
			assertTrue(res instanceof DocumentPart);
			
			ISkeleton skel = res.getSkeleton();
			if (skel != null) {
				assertEquals(skel.toString(), expectedText);
			}
			break;
		}
	}
		
	private void _listEvents(String inputText) { 
		// List all events in Console
		FilterTestDriver testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(2);
		testDriver.setShowSkeleton(true);
		
		filter.open(new RawDocument(inputText, "en"));
		if (!testDriver.process(filter)) Assert.fail();
		filter.close();
	}
	
}

