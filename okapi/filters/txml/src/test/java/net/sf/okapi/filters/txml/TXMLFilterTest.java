/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.txml;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.LocaleId;

import org.junit.Test;
import static org.junit.Assert.*;

public class TXMLFilterTest {

	private TXMLFilter filter1;
	private GenericContent fmt;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	
	private static final String STARTFILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
		+ "<txml locale=\"EN\" version=\"1.0\" segtype=\"sentence\" createdby=\"WF2.3.0\" datatype=\"regexp\" "
		+ "targetlocale=\"FR\" file_extension=\"html\" editedby=\"WF2.3.0\">\r"
		+ "<skeleton>&lt;html&gt;\r"
		+ "&lt;p&gt;</skeleton>";
	
	public TXMLFilterTest () {
		filter1 = new TXMLFilter();
		fmt = new GenericContent();
		root = TestUtil.getParentDir(this.getClass(), "/Test01.docx.txml");
	}
	
	@Test
	public void testSimpleEntry () {
		String snippet = STARTFILE
			+ "<translatable blockId=\"b1\" datatype=\"html\">"
			+ "<segment segmentId=\"s1\" modified=\"true\">"
			+ "<source>Segment one</source><target>Segment un</target>"
			+ "</segment>"
			+ "<segment segmentId=\"2\">"
			+ "<source>segment two</source>"
			+ "</segment>"
			+ "</translatable>"
			+ "</txml>";

		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(filter1, snippet, locFR), 1);
		assertNotNull(tu);
		assertEquals("b1", tu.getId());
		assertEquals("Segment one", tu.getSource().getFirstSegment().toString());
		TextContainer tc = tu.getTarget(locFR);
		assertNotNull(tc);
		assertEquals("Segment un", tc.getFirstSegment().toString());
	}

	@Test
	public void testEntryWithCodes () {
		String snippet = STARTFILE
			+ "<translatable blockId=\"b1\" datatype=\"html\">"
			+ "<segment segmentId=\"s1\" modified=\"true\">"
			+ "<source>Segment one</source><target>Segment un</target>"
			+ "</segment>"
			+ "<segment segmentId=\"2\">"
			+ "<source>Segment <ut x='1' type='bold'>&lt;b></ut>TWO<ut x='2' type='bold'>&lt;/b></ut></source>"
			+ "</segment>"
			+ "</translatable>"
			+ "</txml>";

		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(filter1, snippet, locFR), 1);
		assertNotNull(tu);
		fmt.setContent(tu.getSource().get(1).getContent());
		assertEquals("[Segment one][Segment <1/>TWO<2/>]", fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("Segment <1/>TWO<2/>", fmt.toString());
	}

	@Test
	public void testWS () {
		String snippet = STARTFILE
			+ "<translatable blockId=\"b1\" datatype=\"html\">"
			+ "<segment segmentId=\"s1\">"
			+ "<ws>  </ws>"
			+ "<source>text S</source>"
			+ "<target>text T</target>"
			+ "<ws>  <ut x='1'>&lt;br/></ut> </ws>"
			+ "</segment>"
			+ "<segment segmentId=\"s2\">"
			+ "<source>text S2</source>"
			+ "<ws> \t</ws>"
			+ "</segment>"
			+ "</translatable>"
			+ "</txml>";

		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(filter1, snippet, locFR), 1);
		
		assertEquals("  [text S]  <1/> [text S2] \t", fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("  [text T]  <1/> [] \t", fmt.printSegmentedContent(tu.getTarget(locFR), true));
	}

	@Test
	public void testSegments () {
		String snippet = STARTFILE
			+ "<translatable blockId=\"b1\" datatype=\"html\">"
			+ "<segment segmentId=\"s1\">"
			+ "<ws>  </ws>"
			+ "<source>textS1</source>"
			+ "<target>textT1</target>"
			+ "<ws>  <ut x='1'>&lt;br/></ut> </ws>"
			+ "</segment>"
			+ "<segment segmentId=\"s2\">"
			+ "<source>textS2</source>"
			+ "<ws> \t</ws>"
			+ "</segment>"
			+ "<segment segmentId=\"s3\">"
			+ "<ws>{{</ws>"
			+ "<source></source>"
			+ "<ws>}}</ws>"
			+ "</segment>"
			+ "</translatable>"
			+ "</txml>";

		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(filter1, snippet, locFR), 1);
		assertEquals("  [textS1]  <1/> [textS2] \t{{[]}}", fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("  [textT1]  <1/> [] \t{{[]}}", fmt.printSegmentedContent(tu.getTarget(locFR), true));
	}
	
	@Test
	public void testEmptySegments () {
		String snippet = STARTFILE
			+ "<translatable blockId=\"b1\" datatype=\"html\">"
			+ "<segment segmentId=\"s1\">"
			+ "<ws>  </ws>"
			+ "<source></source>"
			+ "<ws>  <ut x='1'>&lt;br/></ut> </ws>"
			+ "</segment>"
			+ "<segment segmentId=\"s2\">"
			+ "<source></source>"
			+ "<ws> \t</ws>"
			+ "</segment>"
			+ "</translatable>"
			+ "</txml>";

		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(filter1, snippet, locFR), 1);
		assertEquals("  []  <1/> [] \t", fmt.printSegmentedContent(tu.getSource(), true));
		assertNull(tu.getTarget(locFR));
	}

	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.docx.txml", null));
		list.add(new InputDocument(root+"Test02.html.txml", null));
		list.add(new InputDocument(root+"Test03.mif.txml", null));
		RoundTripComparison rtc = new RoundTripComparison();
		// Use non-forced segmentation output
		assertTrue(rtc.executeCompare(filter1, list, "UTF-8", locEN, locFR));
	}
	
	private ArrayList<Event> getEvents (IFilter filter,
		String snippet,
		LocaleId trgLocId)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN, trgLocId));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
