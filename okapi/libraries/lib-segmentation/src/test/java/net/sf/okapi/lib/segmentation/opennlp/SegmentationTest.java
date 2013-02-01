/*===========================================================================
  Copyright (C) 2009-2013 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation.opennlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class SegmentationTest {
	private ISegmenter segmenter;
	private ISegmenter segmenterTrim;
	private LocaleId locEN = LocaleId.fromString("en");
	private GenericContent fmt = new GenericContent();
	private LocaleId locFR = LocaleId.FRENCH;
	private LocaleId locAR = LocaleId.ARABIC;

	@Before
	public void setUp() {
		segmenter = new OkapiMaxEntSegmenter(null, locEN);
		segmenterTrim = new OkapiMaxEntSegmenter(null, locEN);
		segmenterTrim.setTrimLeadingWS(true);
		segmenterTrim.setTrimTrailingWS(true);
	}

	@Test
	public void testGetSegmentCount() {
		TextContainer tc = createSegmentedContainer();
		assertEquals(2, tc.getSegments().count());
	}

	@Test
	public void testGetSegments() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		assertEquals("<s>Part 1.</s>", segments.get(0).toString());
		assertEquals(" Part 2.", segments.get(1).toString());
		assertEquals("[<1>Part 1.</1>] Outside[ Part 2.]",
				fmt.printSegmentedContent(tc, true));
	}

	@Test
	public void testMergeOneSegment() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		assertEquals("[<1>Part 1.</1>] Outside[ Part 2.]",
				fmt.printSegmentedContent(tc, true));
		tc.changePart(2);
		assertEquals(1, segments.count());
		assertEquals("<s>Part 1.</s>", segments.get(0).toString());
		assertEquals("[<1>Part 1.</1>] Outside Part 2.",
				fmt.printSegmentedContent(tc, true));
	}

	@Test
	public void testChangeTwoSegmentsToParts() {
		TextContainer tc = createSegmentedContainer();
		tc.changePart(2); // Segment to non-segment
		tc.changePart(0); // try segment to non-segment (but here it's the last
							// segment, so no change)
		assertEquals(1, tc.getSegments().count());
		assertFalse(tc.contentIsOneSegment());
		assertEquals("[<1>Part 1.</1>] Outside Part 2.",
				fmt.printSegmentedContent(tc, true));
		assertEquals("<s>Part 1.</s> Outside Part 2.", tc.toString());
	}

	@Test
	public void testJoinTwoSegmentsIntoOne() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		segments.joinWithNext(0);
		assertEquals(1, segments.count());
		assertTrue(tc.contentIsOneSegment());
		assertEquals("[<1>Part 1.</1> Outside Part 2.]",
				fmt.printSegmentedContent(tc, true));
		assertEquals("<s>Part 1.</s> Outside Part 2.", tc.toString());
	}

	@Test
	public void testMergeAllSegments() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		segments.joinAll();
		assertEquals(1, segments.count());
		assertTrue(tc.contentIsOneSegment());
		assertEquals("[<1>Part 1.</1> Outside Part 2.]",
				fmt.printSegmentedContent(tc, true));
		assertEquals("<s>Part 1.</s> Outside Part 2.", tc.toString());
	}

	@Test
	public void testCreateSegment() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		// "**Part 1.** Outside Part2."
		// 01234567890123456789012345"
		segments.create(11, 19);
		assertEquals(1, segments.count());
		assertEquals(3, tc.count());
		assertEquals(" Outside", segments.get(0).toString());
	}

	@Test
	public void testAppendSegment() {
		TextContainer tc = createSegmentedContainer();
		ISegments segments = tc.getSegments();
		segments.append(new TextFragment(" Added Part."));
		assertEquals(3, segments.count());
		assertEquals(" Added Part.", segments.get(2).toString());
	}

	@Test
	public void testSegmentationSimple1() {
		TextContainer tc = createSegmentedContainer("There is already time. Zoey is there", segmenter);
		ISegments segments = tc.getSegments();
		assertEquals(2, segments.count());
		assertEquals("There is already time.", segments.get(0).toString());
		assertEquals(" Zoey is there", segments.get(1).toString());
	}

	@Test
	public void testSegmentationSimpleWithLeadingTrainlingWS() {
		TextContainer tc = createSegmentedContainer(" a.  ", segmenter);
		ISegments segments = tc.getSegments();
		assertEquals(2, segments.count());
		assertEquals(" a.", segments.get(0).toString());
		assertEquals("  ", segments.get(1).toString());
		// 1 segment only because the last one is only made of whitespaces
		tc = createSegmentedContainer("a. ", segmenterTrim);
		segments = tc.getSegments();
		assertEquals(1, segments.count());
		assertEquals("a.", segments.get(0).toString());
	}

	@Test
	public void testSegmentationWithEmpty() {
		TextContainer tc = createSegmentedContainer("", segmenter);
		ISegments segments = tc.getSegments();
		assertEquals(1, segments.count());
	}

	@Test
	public void testTrimOptionsSetting() {
		ISegmenter segter = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
		segter.setTrimLeadingWS(true);
		segter.setTrimTrailingWS(true);
		
		segter.computeSegments(" a ");
		List<Range> list = segter.getRanges();
		assertEquals(1, list.get(0).start);
		assertEquals(2, list.get(0).end);
	}

	@Test
	public void testTUCreateSourceSegmentation() {
		ITextUnit tu = new TextUnit("tuid");
		tu.setSource(createSimpleContent());
		tu.createSourceSegmentation(segmenter);
		assertEquals("[<1>Part 1.</1>][ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
	}

	@Test
	public void testTUCreateSourceSegmentationOverwrite() {
		ITextUnit tu = new TextUnit("tuid");
		tu.setSource(createSegmentedContainer()); // hard-coded
		assertEquals("[<1>Part 1.</1>] Outside[ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
		tu.createSourceSegmentation(segmenter); // From the segmenter
		assertEquals("[<1>Part 1.</1>][ Outside Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
	}

	@Test
	public void testTUSourceSegmentationInTarget() {
		ITextUnit tu = new TextUnit("tuid");
		tu.setSource(createSimpleContent());
		tu.createSourceSegmentation(segmenter);
		assertEquals("[<1>Part 1.</1>][ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
		// Creates the target and translate it
		TextContainer tc = tu.createTarget(locFR, true, IResource.COPY_ALL);
		for (Segment seg : tc.getSegments()) {
			seg.text.setCodedText(seg.text.getCodedText().toUpperCase() + " FR");
		}
		assertEquals("[<1>PART 1.</1> FR][ PART 2. FR]",
				fmt.printSegmentedContent(tc, true));
	}

	@Test
	public void testTUSynchronizeSourceSegmentationForTarget() {
		ITextUnit tu = new TextUnit("tuid");
		tu.setSource(createSimpleContent());
		tu.createSourceSegmentation(segmenter);
		assertEquals("[<1>Part 1.</1>][ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
		// No changes for FR
		TextContainer tc1 = tu.createTarget(locFR, true, IResource.COPY_ALL);
		// One segment for AR
		TextContainer tc2 = tu.createTarget(locAR, true, IResource.COPY_ALL);
		tc2.getSegments().joinWithNext(0); // Make it one segment for AR
		List<Range> ranges = new ArrayList<Range>();
		// "**Part 1.** Part 2."
		// 0123456789012345678
		ranges.add(new Range(0, 19));
		tu.getVariantSources().create(locAR, true, IResource.COPY_ALL);
		tu.getVariantSources().get(locAR).getSegments().create(ranges);
		// Check the FR against the source
		Segment srcSeg;
		ISegments segs = tu.getVariantSources().getSegments(locFR);
		for (Segment seg : tc1.getSegments()) {
			srcSeg = segs.get(seg.id);
			assertNotNull(srcSeg);
			assertEquals(seg.text, srcSeg.text);
		}
		// Test AR against the source
		segs = tu.getVariantSources().getSegments(locAR);
		for (Segment seg : tc2.getSegments()) {
			srcSeg = segs.get(seg.id);
			assertNotNull(srcSeg);
			assertEquals(seg.text, srcSeg.text);
		}
	}

	@Test
	public void testCreateTargetSegmentation() {
		ITextUnit tu = new TextUnit("tuid");
		tu.setSource(createSimpleContent());
		tu.createTarget(locFR, true, IResource.COPY_ALL);
		// Segment both with the same segmenter
		tu.createSourceSegmentation(segmenter);
		tu.createTargetSegmentation(segmenter, locFR);
		// We should get the same result
		assertEquals("[<1>Part 1.</1>][ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
		assertEquals("[<1>Part 1.</1>][ Part 2.]",
				fmt.printSegmentedContent(tu.getTarget(locFR), true));
	}

	@Test
	public void testCreateSegmentationWithCodes() {
		ITextUnit tu = new TextUnit("tuid");
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "s", "<s>");
		tf.append("Part 1");
		tf.append(TagType.CLOSING, "s", "</s>");
		tf.append(". Part 2.");
		tu.setSource(new TextContainer(tf));
		// Segment
		tu.createSourceSegmentation(segmenter);
		// We should get the same result
		assertEquals("[<1>Part 1</1>.][ Part 2.]",
				fmt.printSegmentedContent(tu.getSource(), true));
	}
	
	@Test
	public void testRealSentences() {
		OkapiMaxEntSegmenter seg = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
		assertNotNull(seg);
		assertEquals(seg.getLanguage(), "en");
		assertNull(seg.getRanges()); // Null set yet
		seg.computeSegments("Mr. Holmes. The detective.");
		assertNotNull(seg.getRanges());
		assertEquals(seg.getRanges().size(), 2);
		seg.computeSegments("MR. Holmes. The detective.");
		assertEquals(seg.getRanges().size(), 3);
		
		TextFragment tf = new TextFragment("One.");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(" Two.");
		tf.append(TagType.CLOSING, "b", "</b>");
		TextContainer tc = new TextContainer(tf);
		seg.setOptions(true, true, true, false, false, false, false);
		seg.computeSegments(tc);
		// "One.XX Two.YY" --> "[One.XX][ Two.YY]"
		List<Range> ranges = seg.getRanges();
		assertNotNull(ranges);
		assertEquals(ranges.size(), 2);
		assertEquals(ranges.get(0).end, 6);
		assertEquals(ranges.get(1).start, 6);
		seg.setOptions(true, false, true, false, false, false, false);
		seg.computeSegments(tc);
		// "One.XX Two.YY" --> "[One.][XX Two.YY]"
		ranges = seg.getRanges();
		assertNotNull(ranges);
		assertEquals(ranges.size(), 2);
		assertEquals(ranges.get(0).end, 4);
		assertEquals(ranges.get(1).start, 4);
	}

	private TextContainer createSimpleContent() {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "s", "<s>");
		tf.append("Part 1.");
		tf.append(TagType.CLOSING, "s", "</s>");
		tf.append(" Part 2.");
		return new TextContainer(tf);
	}

	private TextContainer createSegmentedContainer() {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "s", "<s>");
		tf.append("Part 1.");
		tf.append(TagType.CLOSING, "s", "</s>");
		tf.append(" Part 2.");
		TextContainer tc = new TextContainer(tf);
		segmenter.computeSegments(tc);
		tc.getSegments().create(segmenter.getRanges());
		// Insert in holder between the two segments
		tc.insert(1, new TextPart(new TextFragment(" Outside")));
		return tc;
	}

	private TextContainer createSegmentedContainer(String text,
			ISegmenter segmenter) {
		TextContainer tc = new TextContainer(text);
		segmenter.computeSegments(tc);
		tc.getSegments().create(segmenter.getRanges());
		return tc;
	}
}
