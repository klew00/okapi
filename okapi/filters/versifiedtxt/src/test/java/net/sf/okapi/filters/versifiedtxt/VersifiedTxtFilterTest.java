/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.versifiedtxt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.resource.AlignmentStatus;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

import org.junit.Before;
import org.junit.Test;

public class VersifiedTxtFilterTest {
	private VersifiedTextFilter filter;
	private String root;
		
	@Before
	public void setUp() {
		filter = new VersifiedTextFilter();
		filter.setOptions(LocaleId.ENGLISH, LocaleId.SPANISH, "UTF-8", true);
		root = TestUtil.getParentDir(this.getClass(), "/part1.txt");
	}
	
	@Test
	public void testDefaultInfo () {		
		assertNotNull(filter.getName());
		assertNotNull(filter.getDisplayName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}
	
	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root + "part1.txt", null),
			"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));
	}

	@Test
	public void testSimpleVerse() {
		String snippet = "|btest\n|v1\nThis is a test.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("This is a test.", tu.getSource().toString());
		assertEquals("test::1", tu.getName());
	}
	
	@Test
	public void testSimpleBookChapterVerse() {
		String snippet = "|bbook\n|cchapter\n|v1\nThis is a test.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("This is a test.", tu.getSource().toString());
		assertEquals("book:chapter:1", tu.getName());
	}
	
	@Test
	public void testOutputSimpleBookChapterVerseWithMacLB () {
		String snippet = "|bbook\r|cchapter\r|v0\rTest\r\r|v1\rThis is a test.";
		String expected = snippet;
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.FRENCH);
		assertEquals(expected, result);
	}

	@Test
	public void testOutputSimpleBookChapterVerse() {
		String snippet = "|bbook\n|cchapter\n|v1\nThis is a test.";
		String expected = "|bbook\n|cchapter\n|v1\nThis is a test."; 
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.ENGLISH);
		assertEquals(expected, result);
	}

	@Test
	public void testOutputSimpleBookChapterVerseMultilingual () {
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\n\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String expected = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\n\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.FRENCH);
		assertEquals(expected, result);
	}

	@Test
	public void testOutputSimpleBookChapterVerseMultilingualFillTarget () {
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\n\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String expected = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\nsource\n\n|v2\nsource2\n<TARGET>\ntarget2";
		IParameters p = filter.getParameters();
		p.setBoolean(GenericSkeletonWriter.ALLOWEMPTYOUTPUTTARGET, false);
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.FRENCH);
		assertEquals(expected, result);
	}
	
	@Test
	public void testSimplePlaceholders() {
		String snippet = "|bbook\n|cchapter\n|v1\n{1}This is {2}a test{3}";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("{1}This is {2}a test{3}", tu.getSource().toString());
		assertEquals("book:chapter:1", tu.getName());
	}
	
	@Test
	public void testEmptyVerses() {
		String snippet = "|bbook\n|cchapter\n|v1\n|v2\n";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("", tu.getSource().toString());
		
		tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertNotNull(tu);
		assertEquals("", tu.getSource().toString());
	}

	@Test
	public void testBilingual() {		
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\ntarget\n\n|v2\nsource2\n<TARGET>\ntarget2\n\n";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("source", tu.getSource().toString());		
		assertEquals("target", tu.getTarget(filter.getTrgLoc()).toString());
		
		tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertNotNull(tu);
		assertEquals("source2", tu.getSource().toString());
		// filter will remove all training newlines on last entry
		assertEquals("target2", tu.getTarget(filter.getTrgLoc()).toString());
	}
	
	@Test
	public void testBilingualWithGenericWriter() {
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\ntarget\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String expected = snippet;
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.SPANISH);
		assertEquals(expected, result);
	}
	
	@Test
	public void testBilingualWithInternalNewlinesWithGenericWriter() {
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\ntarget1\n\ntarget2\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String expected = snippet;
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.SPANISH);
		assertEquals(expected, result);
	}
	
	@Test
	public void testBilingualWithGenericWriterWithMissingNewlines() {
		String snippet = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\ntarget\n|v2\nsource2\n<TARGET>\ntarget2";
		String expected = "|bbook\n|cchapter\n|v1\nsource\n<TARGET>\ntarget\n\n|v2\nsource2\n<TARGET>\ntarget2";
		String result = FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), LocaleId.SPANISH);
		assertEquals(expected, result);
	}
	
	@Test
	public void testBilingualWithEmptyVerses() {
		String snippet = "|bbook\n|cchapter\n|v1\n<TARGET>\n|v2\n<TARGET>\n";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertNotNull(tu);
		assertEquals("", tu.getSource().toString());
		assertEquals("", tu.getTarget(filter.getTrgLoc()).toString());
		
		tu = FilterTestDriver.getTextUnit(getEvents(snippet), 2);
		assertNotNull(tu);
		assertEquals("", tu.getSource().toString());
		assertEquals("", tu.getTarget(filter.getTrgLoc()).toString());
	}
	
	@Test
	public void testDoubleExtraction() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"part1.txt", null));		
		RoundTripComparison rtc = new RoundTripComparison(false);
		assertTrue(rtc.executeCompare(filter, list, "windows-1252", LocaleId.ENGLISH, LocaleId.ENGLISH));
	}
	
	@Test
	public void testDoubleExtractionBilingual() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"bilingual.txt", null));		
		RoundTripComparison rtc = new RoundTripComparison(false);
		assertTrue(rtc.executeCompare(filter, list, "windows-1252", LocaleId.ENGLISH, LocaleId.SPANISH));
	}
	
	@Test
	public void testDoubleExtractionEmptyVerses() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"empty_verses.vrsz", null));		
		RoundTripComparison rtc = new RoundTripComparison(false);
		assertTrue(rtc.executeCompare(filter, list, "windows-1252", LocaleId.ENGLISH, LocaleId.SPANISH));
	}
	
	@Test
	public void testTrados() throws URISyntaxException {
		RawDocument rawDoc = new RawDocument(Util.toURI(root+"trados.vrsz"), "windows-1252", LocaleId.ENGLISH, LocaleId.SPANISH);
		filter.open(rawDoc);
		List<Event> events = new LinkedList<Event>();
		while(filter.hasNext()) {
			events.add(filter.next());
		}
		filter.close();
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertEquals("gh", tu.getName());
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals(2, tu.getSource().getSegments().count());
		assertEquals("a record. ", tu.getSource().getFirstSegment().toString());
		assertEquals("A RECORD. ", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());
		
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertEquals("source", tu.getName());
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals(1, tu.getSource().getSegments().count());
		assertEquals("Add a Source", tu.getSource().getFirstSegment().toString());
		assertEquals("ADD A SOURCE", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());

		tu = FilterTestDriver.getTextUnit(events, 3);
		assertEquals("newsource", tu.getName());
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals(1, tu.getSource().getSegments().count());
		assertEquals("Add a New Source", tu.getSource().getFirstSegment().toString());
		assertEquals("ADD A NEW SOURCE", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());

		tu = FilterTestDriver.getTextUnit(events, 4);
		assertEquals("sourcelink", tu.getName());
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals(1, tu.getSource().getSegments().count());
		assertEquals("Create a New Source", tu.getSource().getFirstSegment().toString());
		assertEquals("CREATE A NEW SOURCE", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());

		tu = FilterTestDriver.getTextUnit(events, 5);
		assertEquals("suredetach", tu.getName());
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals(1, tu.getSource().getSegments().count());
		assertEquals("detach this source?", tu.getSource().getFirstSegment().toString());
		assertEquals("DETACH THIS SOURCE FROM THIS INDIVIDUAL?", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());
	}
	
	//@Test
	public void testTradosRoundtrip() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"trados.vrsz", null));		
		RoundTripComparison rtc = new RoundTripComparison(false);
		assertTrue(rtc.executeCompare(filter, list, "windows-1252", LocaleId.ENGLISH, LocaleId.SPANISH));
	}
	
	@Test
	public void testOpenTwiceWithString() {
		RawDocument rawDoc = new RawDocument("|vtest", LocaleId.ENGLISH);
		filter.open(rawDoc);
		filter.open(rawDoc);
		filter.close();
	}

	@Test(expected=OkapiBadFilterInputException.class)
	public void testMissingVerse() {
		String snippet = "|btest\nThis is a test.";
		FilterTestDriver.getTextUnit(getEvents(snippet), 1);
	}
	
	@Test
	public void testMissingBook() {
		String snippet = "|v1\nThis is a test.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertEquals("This is a test.", tu.getSource().toString());
	}
	
	@Test
	public void testSidWithSpecialTerminator() {
		String snippet = "|v1 (sid)+| \nThis is a test.";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertEquals("This is a test.", tu.getSource().toString());
	}
	
	@Test
	public void testSidAndTradosSegmentMarkers() {
		String snippet = "|v1 (SOURCE)+| \n{0>SOURCE<}100{>TARGET<0}";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet), 1);
		assertEquals(AlignmentStatus.ALIGNED, tu.getAlignedSegments().getAlignmentStatus(LocaleId.SPANISH));
		assertEquals("SOURCE", tu.getSource().getFirstSegment().toString());
		assertEquals("TARGET", tu.getTarget(LocaleId.SPANISH).getFirstSegment().toString());
	}
	
	@Test(expected=OkapiBadFilterInputException.class)
	public void testSidAndBrokenTradosSegmentMarkers() {
		String snippet = "|v1\n{0>SOURCE<}100{>TARGET<0";
		FilterTestDriver.getTextUnit(getEvents(snippet), 1);
	}
	
	private ArrayList<Event> getEvents (String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();		
		filter.open(new RawDocument(snippet, LocaleId.ENGLISH, LocaleId.SPANISH));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
}
