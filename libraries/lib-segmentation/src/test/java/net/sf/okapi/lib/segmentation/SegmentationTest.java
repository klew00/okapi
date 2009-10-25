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

package net.sf.okapi.lib.segmentation;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.segmentation.ISegmenter;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SegmentationTest {

	private ISegmenter segmenter;
	private ISegmenter segmenterTrim;
	private LocaleId locEN = LocaleId.fromString("en");
	
	@Before
	public void setUp() {
		SRXDocument doc1 = new SRXDocument();
		LanguageMap langMap = new LanguageMap(".*", "default");
		doc1.addLanguageMap(langMap);
		// Add the rules
		ArrayList<Rule> langRules = new ArrayList<Rule>();
		langRules.add(new Rule("\\.", "\\s", true));
		langRules.add(new Rule("\\|", "", true));
		// Add the ruls to the document
		doc1.addLanguageRule("default", langRules);
		// Create the segmenter
		segmenter = doc1.compileLanguageRules(locEN, null);

		SRXDocument doc2 = new SRXDocument();
		doc2.addLanguageMap(langMap);
		doc2.addLanguageRule("default", langRules);
		doc2.setTrimLeadingWhitespaces(true);
		doc2.setTrimTrailingWhitespaces(true);
		// Create the segmenter
		segmenterTrim = doc2.compileLanguageRules(locEN, null);
	}

	@Test
	public void testGetSegmentCount () {
		TextContainer tc = createSegmentedContainer();
		assertEquals(2, tc.getSegmentCount());
	}
	
	@Test
	public void testGetSegments () {
		TextContainer tc = createSegmentedContainer();
		List<Segment> list = tc.getSegments();
		assertNotNull(list);
		assertEquals("<s>Part 1.</s>", list.get(0).toString());
		assertEquals(" Part 2.", list.get(1).toString());
		assertEquals("0Outside1", tc.toString());
	}
	
	@Test
	public void testMergeOneSegment () {
		TextContainer tc = createSegmentedContainer();
		tc.mergeSegment(1);
		assertEquals(1, tc.getSegmentCount());
		assertEquals("<s>Part 1.</s>", tc.getSegments().get(0).toString());
		assertEquals("0Outside Part 2.", tc.toString());
	}
	
	@Test
	public void testMergeTwoSegments () {
		TextContainer tc = createSegmentedContainer();
		tc.mergeSegment(1);
		tc.mergeSegment(0);
		assertEquals(0, tc.getSegmentCount());
		assertEquals("<s>Part 1.</s>Outside Part 2.", tc.toString());
	}
	
	@Test
	public void testMergeAllSegments () {
		TextContainer tc = createSegmentedContainer();
		tc.mergeAllSegments();
		assertEquals(0, tc.getSegmentCount());
		assertEquals("<s>Part 1.</s>Outside Part 2.", tc.toString());
	}
	
	@Test
	public void testJoinWithNext () {
		TextContainer tc = createSegmentedContainer();
		tc.joinSegmentWithNext(0);
		assertEquals(1, tc.getSegmentCount());
		assertEquals("0", tc.toString());
		assertEquals("<s>Part 1.</s>Outside Part 2.", tc.getSegments().get(0).toString());
	}
	
	@Test
	public void testCreateSegment () {
		TextContainer tc = createSegmentedContainer();
		// "..Outside.."
		tc.createSegment(2, 9);
		assertEquals(3, tc.getSegmentCount());
		assertEquals("<s>Part 1.</s>", tc.getSegments().get(0).toString());
		assertEquals("Outside", tc.getSegments().get(1).toString());
		assertEquals(" Part 2.", tc.getSegments().get(2).toString());
	}
	
	@Test
	public void testAppendSegment () {
		TextContainer tc = createSegmentedContainer();
		tc.appendSegment(new TextFragment(" Added Part."));
		assertEquals(3, tc.getSegmentCount());
		assertEquals(" Added Part.", tc.getSegments().get(2).toString());
	}
	
	@Test
	public void testSegmentationSimple1 () {
		TextContainer tc = createSegmentedContainer("a. z", segmenter);
		assertEquals(2, tc.getSegmentCount());
		assertEquals("a.", tc.getSegments().get(0).toString());
		assertEquals(" z", tc.getSegments().get(1).toString());
		tc = createSegmentedContainer("a. z", segmenterTrim);
		assertEquals(2, tc.getSegmentCount());
		assertEquals("a.", tc.getSegments().get(0).toString());
		assertEquals("z", tc.getSegments().get(1).toString());
	}
	
	@Test
	public void testSegmentationSimpleWithLeadingTrainlingWS () {
		TextContainer tc = createSegmentedContainer(" a.  ", segmenter);
		assertEquals(2, tc.getSegmentCount());
		assertEquals(" a.", tc.getSegments().get(0).toString());
		assertEquals("  ", tc.getSegments().get(1).toString());
		// 1 segment only because the last one is only made of whitespaces
		tc = createSegmentedContainer("a. ", segmenterTrim);
		assertEquals(1, tc.getSegmentCount());
		assertEquals("a.", tc.getSegments().get(0).toString());
	}
	
	@Test
	public void testSegmentationWithEmpty () {
		TextContainer tc = createSegmentedContainer(" a. | b.", segmenter);
		assertEquals(3, tc.getSegmentCount());
		assertEquals(" a.", tc.getSegments().get(0).toString());
		assertEquals(" |", tc.getSegments().get(1).toString());
		assertEquals(" b.", tc.getSegments().get(2).toString());
		// 1 segment only because the last one is only made of whitespaces
		tc = createSegmentedContainer(" a. |  b.", segmenterTrim);
		assertEquals(3, tc.getSegmentCount());
		assertEquals("a.", tc.getSegments().get(0).toString());
		assertEquals("|", tc.getSegments().get(1).toString());
		assertEquals("b.", tc.getSegments().get(2).toString());
	}

	private TextContainer createSegmentedContainer () {
		TextContainer tc = new TextContainer();
		tc.append(TagType.OPENING, "s", "<s>");
		tc.append("Part 1.");
		tc.append(TagType.CLOSING, "s", "</s>");
		tc.append(" Part 2.");
		segmenter.computeSegments(tc);
		tc.createSegments(segmenter.getRanges());
		tc.insert(2, new TextFragment("Outside"));
		return tc;
	}

	private TextContainer createSegmentedContainer (String text,
		ISegmenter segmenter)
	{
		TextContainer tc = new TextContainer(text);
		segmenter.computeSegments(tc);
		tc.createSegments(segmenter.getRanges());
		return tc;
	}

}
