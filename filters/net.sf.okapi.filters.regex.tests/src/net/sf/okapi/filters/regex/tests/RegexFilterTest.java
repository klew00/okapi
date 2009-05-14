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
import net.sf.okapi.common.Util;
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
		String inputText = "Line 1\n\nLine 2\n\n\n\n\nLine 3\n\n\nLine 4";
		//                  0123456 7 8901234 5 6 7 8 9012345 6 7 890123 
		//                  0           1              2            3            
		
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
		
		FilterTestDriver testDriver = new FilterTestDriver();
		testDriver.setDisplayLevel(2);
		testDriver.setShowSkeleton(true);
		
		// List all events in Console
		filter.open(new RawDocument(inputText, "en"));
		if (!testDriver.process(filter)) Assert.fail();
		filter.close();
					
		// Test individual events
		filter.open(new RawDocument(inputText, "en"));
		
		Event e = filter.next();
		assertEquals(e.getEventType(), EventType.START_DOCUMENT);
		
		_testTuEvent(filter.next(), "Line 1");
		_testTuEvent(filter.next(), "Line 2");
		_testTuEvent(filter.next(), "Line 3");
		_testTuEvent(filter.next(), "Line 4");
	}

	
	private void _testTuEvent(Event e, String expectedText) {
		assertNotNull(e);
		assertEquals(e.getEventType(), EventType.TEXT_UNIT);
		
		IResource res = e.getResource();
		assertTrue(res instanceof TextUnit);
		
		assertEquals(((TextUnit) res).toString(), expectedText);
		
		e = filter.next();
		assertEquals(e.getEventType(), EventType.DOCUMENT_PART);
	}
	
}

