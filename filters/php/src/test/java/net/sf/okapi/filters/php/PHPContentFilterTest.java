/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.php;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PHPContentFilterTest {
	
	private PHPContentFilter filter;
	private String root;

	@Before
	public void setUp() {
		filter = new PHPContentFilter();
		root = TestUtil.getParentDir(this.getClass(), "/test01.phpcnt");
	}

	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}
	
	@Test
	public void testCommentsSingleLine () {
		String snippet = "// $a='abc';\n$b=\"def\";";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("def", tu.getSource().toString());
	}
	
	@Test
	public void testCommentsMultiline () {
		String snippet = "/* $a='abc';\nstuff // etc. * / \n */$b=\"def\";";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("def", tu.getSource().toString());
	}
	
	@Test
	public void testSingleQuotedString () {
		String snippet = "$a='\\\\text\\'';\n$b='\\'\"text\"';";
		// Check first TU
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("\\\\text\\'", tu.getSource().toString());
	}
	
	@Test
	public void testDoubleQuotedString () {
		String snippet = "$a=\"text\\\"\";\n$b=\"'text\\\"\";";
		// Check second TU
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertTrue(tu!=null);
		assertEquals("'text\\\"", tu.getSource().toString());
	}
	
	@Test
	public void testHeredocString () {
		String snippet = "$a=<<<EOT\ntext\nEOT \n\nEOT;";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("text\nEOT \n", tu.getSource().toString());
	}
	
	@Test
	public void testSemiColumnHeredocString () {
		String snippet = "$a=<<<EOT\ntext\nEOT \n;\nEOT;";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("text\nEOT \n;", tu.getSource().toString());
	}
	
	@Test
	public void testMultipleLinesHeredocString () {
		String snippet = "$a=<<<EOT\ntext\nEOT \n EOT \n\nEOT;\n";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("text\nEOT \n EOT \n", tu.getSource().toString());
	}
	
	@Test
	public void testEmptyHeredocString () {
		String snippet = "$a=<<<EOT\n\nEOT;";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertTrue(tu!=null);
		assertEquals("", tu.getSource().toString());
	}
	
	@Test
	public void testOutputSimple () {
		String snippet = "$a='abc';\n$b=\"def\";";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), "en"));
	}
	
	@Test
	public void testLineBreakType () {
		String snippet = "$a='abc';\r\n$b=\"def\";\r\n";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), "en"));
	}
	
	@Test
	public void testOutputWithNoStrings () {
		String snippet = "echo $a=$b; and other dummy code";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), "en"));
	}
	
	@Test
	public void testOutputHeredoc () {
		String snippet = "$a=<<<EOT\ntext\nEOT \n EOT \n\nEOT;\n";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), "en"));
	}
	
	@Test
	public void testOutputMix () {
		String snippet = "$a=<<<EOT\ntext\nEOT \n EOT \n\nEOT;\n"
			+ "$b=\"abc\"\n// 'comments'\n$c = 'def';\n"
			+ "/* $c=\"abc\" */";
		assertEquals(snippet, FilterTestDriver.generateOutput(getEvents(snippet), "en"));
	}
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"test01.phpcnt", null));

		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "en"));
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

}
