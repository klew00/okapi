/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.transtable;

import java.util.ArrayList;

import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TransTableFilterTest {
	
	private TransTableFilter filter;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	
	@Before
	public void setUp() {
		filter = new TransTableFilter();
		root = TestUtil.getParentDir(this.getClass(),"/test01.xml.txt");
	}

	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"test01.xml.txt", null),
			"UTF-8", locEN, locEN));
	}
	
	@Test
	public void testMinimalInput () {
		String snippet = "TransTableV1\ten\tfr\n"
			+ "\"okpCtx:tu=1\"\t\"source\"";	
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertNotNull(tu);
		assertEquals("source", tu.getSource().toString());
		assertEquals("1", tu.getId());
	}
		
	@Test
	public void testMinimalSourceTarget () {
		String snippet = "TransTableV1\ten\tfr\n"
			+ "\"okpCtx:tu=1\"\t\"source\"\t\"target\"";	
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertNotNull(tu);
		assertEquals("source", tu.getSource().toString());
		assertEquals("target", tu.getTarget(locFR).toString());
		assertEquals("1", tu.getId());
	}

	@Test
	public void testQuotesInput () {
		String snippet = "\"TransTableV1\"\t\"en\"\t\"fr\"\n"
			+ "okpCtx:tu=1\tsource";	
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertNotNull(tu);
		assertEquals("source", tu.getSource().toString());
		assertEquals("1", tu.getId());
	}
		
	
//	@Test
//	public void testDoubleExtraction () {
//		// Read all files in the data directory
//		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
//		list.add(new InputDocument(root+"Test01_srt_en.srt", "okf_regex@SRT.fprm"));
//		RoundTripComparison rtc = new RoundTripComparison();
//		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locFR));
//	}

	private ArrayList<Event> getEvents(String snippet,
		LocaleId srcLang,
		LocaleId trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang, trgLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}

