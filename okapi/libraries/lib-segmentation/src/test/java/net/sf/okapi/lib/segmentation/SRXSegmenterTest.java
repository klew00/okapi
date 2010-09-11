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

package net.sf.okapi.lib.segmentation;

import java.util.ArrayList;

import org.junit.Before;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.SRXSegmenter;

import org.junit.Test;
import static org.junit.Assert.*;

public class SRXSegmenterTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testDefaultOptions () {
		SRXSegmenter seg = new SRXSegmenter();
		// Check default options
		assertFalse(seg.cascade());
		assertTrue(seg.segmentSubFlows());
		assertFalse(seg.includeStartCodes());
		assertTrue(seg.includeEndCodes());
		assertFalse(seg.includeIsolatedCodes());
		assertFalse(seg.oneSegmentIncludesAll());
		assertFalse(seg.trimLeadingWhitespaces());
		assertFalse(seg.trimTrailingWhitespaces());
	}		
	
	@Test
	public void testChangedOptions () {
		SRXSegmenter seg = new SRXSegmenter();
		// Check changing options
		seg.setOptions(false, true, false, true, true, true, true);
		assertFalse(seg.segmentSubFlows());
		assertTrue(seg.includeStartCodes());
		assertFalse(seg.includeEndCodes());
		assertTrue(seg.includeIsolatedCodes());
		assertTrue(seg.oneSegmentIncludesAll());
		assertTrue(seg.trimLeadingWhitespaces());
		assertTrue(seg.trimTrailingWhitespaces());
	}
	
	@Test
	public void testSimpleSegmentation () {
		ISegmenter seg = createSegmenterWithRules(LocaleId.fromString("en"));
		TextContainer tc = new TextContainer("Part 1. Part 2.");
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(2, n);
		segments.create(seg.getRanges());
		assertEquals(2, segments.count());
		assertEquals("Part 1.", segments.get(0).toString());
		assertEquals(" Part 2.", segments.get(1).toString());
	}
	
	@Test
	public void testTUSegmentation () {
		TextUnit tu = createMultiTargetSegmentedTextUnit();
		// Check default source segmentation
		ISegments segs = tu.getSource().getSegments();
		assertEquals(3, segs.count());
		assertEquals("Part 1.", segs.get(0).toString());
		assertEquals(" Part 2.", segs.get(1).toString());
		assertEquals(" Part 3.", segs.get(2).toString());
		// Switch to French
		tu.synchronizeSourceSegmentation(LocaleId.FRENCH);
		assertEquals(2, segs.count());
		assertEquals("Part 1. Part 2.", segs.get(0).toString());
		assertEquals("Part 3.", segs.get(1).toString());
		// back to German
		tu.synchronizeSourceSegmentation(LocaleId.GERMAN);
		assertEquals(" Part 2.", segs.get(1).toString());
	}

	@Test
	public void testTUSegmentationRemoval () {
		TextUnit tu = createMultiTargetSegmentedTextUnit();
		tu.removeTarget(LocaleId.FRENCH);
		tu.synchronizeSourceSegmentation(LocaleId.FRENCH);
		// Stays with default segmentation
		ISegments segs = tu.getSource().getSegments();
		assertEquals(3, segs.count());
		assertEquals(" Part 2.", segs.get(1).toString());
	}
	
	@Test
	public void testTUSegmentationRemovalAll () {
		TextUnit tu = createMultiTargetSegmentedTextUnit();
		tu.removeAllSegmentations();
		// Nothing is segmented now
		ISegments segs = tu.getSource().getSegments();
		assertEquals(1, segs.count());
		assertEquals("Part 1. Part 2. Part 3.", tu.getSource().getLastContent().toText());
		tu.synchronizeSourceSegmentation(LocaleId.FRENCH);
		assertEquals("Part 1. Part 2. Part 3.", tu.getSource().getLastContent().toText());
	}
	
	private ISegmenter createSegmenterWithRules (LocaleId locId) {
		SRXDocument doc = new SRXDocument();
		LanguageMap langMap = new LanguageMap(".*", "default");
		doc.addLanguageMap(langMap);
		// Add the rules
		ArrayList<Rule> langRules = new ArrayList<Rule>();
		langRules.add(new Rule("\\.", "\\s", true));
		// Add the ruls to the document
		doc.addLanguageRule("default", langRules);
		// Create the segmenter
		return doc.compileLanguageRules(locId, null);
	}

	private TextUnit createMultiTargetSegmentedTextUnit () {
		ISegmenter segmenter = createSegmenterWithRules(LocaleId.fromString("en"));
		// Create the source and segment it
		TextUnit tu = new TextUnit("id1", "Part 1. Part 2. Part 3.");
		tu.createSourceSegmentation(segmenter);
		// Create the German target
		TextContainer tc1 = tu.setTarget(LocaleId.GERMAN, new TextContainer("DE_Part 1. DE_Part 2. DE_Part 3."));
		// Create same segmentation
		segmenter.computeSegments(tc1);
		tc1.getSegments().create(segmenter.getRanges());
		// Create the French target
		TextContainer tc2 = tu.setTarget(LocaleId.FRENCH, new TextContainer("FR_Part 1 and part 2. FR_Part 3."));
		// Create same segmentation
		segmenter.computeSegments(tc2);
		tc2.getSegments().create(segmenter.getRanges());
		// Create the source segmentation corresponding to the French segmentation
		ArrayList<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0, 15, "0"));
		ranges.add(new Range(16, 23, "1"));
		tu.setSourceSegmentationForTarget(LocaleId.FRENCH, ranges);
		return tu;
	}

}
