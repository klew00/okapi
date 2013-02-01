/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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
import static org.junit.Assert.assertTrue;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class SegmenterTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testDefaultOptions () {
		OkapiMaxEntSegmenter seg = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
		// Check default options
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
		OkapiMaxEntSegmenter seg = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
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
	public void testSimpleSegmentationDefault () {
		ISegmenter seg = createSegmenterWithRules(LocaleId.fromString("en"));
		TextContainer tc = new TextContainer("Part 1.  Part 2. ");
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(3, n);
		segments.create(seg.getRanges());
		assertEquals(3, segments.count());
		assertEquals("Part 1.", segments.get(0).toString());
		assertEquals("  Part 2.", segments.get(1).toString());
		assertEquals(" ", segments.get(2).toString());
	}
	
	@Test
	public void testSimpleSegmentationTrimLeading () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), seg.includeStartCodes(), seg.includeEndCodes(), seg.includeIsolatedCodes(),
			seg.oneSegmentIncludesAll(), true, false);
		TextContainer tc = new TextContainer(" Part 1.  Part 2. ");
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(2, n);
		segments.create(seg.getRanges());
		assertEquals(2, segments.count());
		assertEquals("Part 1.", segments.get(0).toString());
		assertEquals("Part 2.", segments.get(1).toString());
	}
	
	@Test
	public void testSimpleSegmentationTrimTrailing () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), seg.includeStartCodes(), seg.includeEndCodes(), seg.includeIsolatedCodes(),
			seg.oneSegmentIncludesAll(), false, true);
		TextContainer tc = new TextContainer(" Part 1.  Part 2. ");
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(2, n);
		segments.create(seg.getRanges());
		assertEquals(2, segments.count());
		assertEquals(" Part 1.", segments.get(0).toString());
		assertEquals("  Part 2.", segments.get(1).toString());
	}
	
	@Test
	public void testSimpleSegmentationOneIsAll () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), seg.includeStartCodes(), seg.includeEndCodes(), seg.includeIsolatedCodes(),
			true, true, true);
		TextContainer tc = new TextContainer(" Part 1  ");
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(1, n);
		segments.create(seg.getRanges());
		assertEquals(1, segments.count());
		assertEquals(" Part 1  ", segments.get(0).toString());
	}
	
	@Test
	public void testTUSegmentation () {
		ITextUnit tu = createSegmentedTextUnit();
		// Check default source segmentation
		ISegments segs = tu.getSource().getSegments();
		assertEquals(3, segs.count());
		assertEquals("This is the First part.", segs.get(0).toString());
		assertEquals(" A second part now is seen.", segs.get(1).toString());
		assertEquals(" A surprising third part is now visibile.", segs.get(2).toString());		
	}

	@Test
	public void testCodedSegmentationDefault1 () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), false, true, false,
			false, false, false);
		TextContainer tc = createCodedText();
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(5, n);
		segments.create(seg.getRanges());
		assertEquals(5, segments.count());
		assertEquals("PH after.", segments.get(0).toString());
		assertEquals("<br/><b> End after.</b>", segments.get(1).toString());
		assertEquals(" Start after.", segments.get(2).toString());
		assertEquals("<i> Text.</i>", segments.get(3).toString());
		assertEquals("  ", segments.get(4).toString());
	}
	
	@Test
	public void testCodedSegmentationNotDefault1 () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), true, false, true, false, false, false);
		TextContainer tc = createCodedText();
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(5, n);
		segments.create(seg.getRanges());
		assertEquals(5, segments.count());
		assertEquals("PH after.<br/><b>", segments.get(0).toString());
		assertEquals(" End after.", segments.get(1).toString());
		assertEquals("</b> Start after.<i>", segments.get(2).toString());
		assertEquals(" Text.", segments.get(3).toString());
		assertEquals("</i>  ", segments.get(4).toString());
	}
	
	@Test
	public void testCodedSegmentationDefault2 () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), false, true, false,
			false, false, false);
		TextContainer tc = createCodedText2();
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(5, n);
		segments.create(seg.getRanges());
		assertEquals(5, segments.count());
		assertEquals("PH after.", segments.get(0).toString());
		assertEquals("<br/><br/><b><i> End after.</i></b>", segments.get(1).toString());
		assertEquals(" Start after.", segments.get(2).toString());
		assertEquals("<u><i> Text.</i></u>", segments.get(3).toString());
		assertEquals("  ", segments.get(4).toString());
	}

	@Test
	public void testCodedSegmentationNotDefault2 () {
		OkapiMaxEntSegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);
		seg.setOptions(seg.segmentSubFlows(), true, false, true, false, false, false);
		TextContainer tc = createCodedText2();
		ISegments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(5, n);
		segments.create(seg.getRanges());
		assertEquals(5, segments.count());
		assertEquals("PH after.<br/><br/><b><i>", segments.get(0).toString());
		assertEquals(" End after.", segments.get(1).toString());
		assertEquals("</i></b> Start after.<u><i>", segments.get(2).toString());
		assertEquals(" Text.", segments.get(3).toString());
		assertEquals("</i></u>  ", segments.get(4).toString());
	}	
	
	@Test
	public void testTUSegmentationRemovalAll () {
		ITextUnit tu = createSegmentedTextUnit();	
		tu.removeAllSegmentations();
		assertFalse(tu.hasVariantSources());
		// Nothing is segmented now
		ISegments segs = tu.getSource().getSegments();
		assertEquals(1, segs.count());
		assertEquals("This is the First part. A second part now is seen. A surprising third part is now visibile.", tu.getVariantSources().get(LocaleId.GERMAN).getLastContent().toText());
		assertEquals("This is the First part. A second part now is seen. A surprising third part is now visibile.", tu.getVariantSources().get(LocaleId.FRENCH).getLastContent().toText());
	}
	
	@Test
	public void testICUSpecificPatterns () {
		// Create the segmenter
		ISegmenter segmenter = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
		
		assertEquals(2, segmenter.computeSegments("e ")); // e + space
		assertEquals(2, segmenter.computeSegments("e\u00a0")); // e + nbsp
		assertEquals(2, segmenter.computeSegments("e\u1680")); // e + Ogham space
		assertEquals(2, segmenter.computeSegments("\u0104 ")); // A-ogonek + space
		assertEquals(2, segmenter.computeSegments("\u0104\u00a0")); // A-ogonek + nbsp
		assertEquals(2, segmenter.computeSegments("\u0104\u1680")); // A-ogonek + Ogham space

		assertEquals(2, segmenter.computeSegments("1 ")); // 1 + space
		assertEquals(2, segmenter.computeSegments("\u0b66 ")); // Oryia zero + space
		assertEquals(2, segmenter.computeSegments("\uff19 ")); // Full-width 9 + space
		assertEquals(2, segmenter.computeSegments("1\u1680")); // 1 + Ogham space
		assertEquals(2, segmenter.computeSegments("\u0b66\u1680")); // Oryia zero + Ogham space
		assertEquals(2, segmenter.computeSegments("\uff19\u1680")); // Full-width 9 + Ogham space
		
		assertEquals(2, segmenter.computeSegments("\u301c\u1680")); // wave-dash + Ogham space

		// FIXME: fails
		//assertEquals(2, segmenter.computeSegments("z\u0608")); // z + Arabic ray
		//assertEquals(1, segmenter.computeSegments("\u20ac\u1680")); // Euro + Ogham space -> no break
	}
	
	@Test
	public void testWithWithoutTrailingWhitespace() {
		ISegmenter seg = createSegmenterWithRules(LocaleId.ENGLISH);		
		GenericContent fmt = new GenericContent();
		
		ITextUnit tu = new TextUnit("1", "This sentence should not be split.");
		seg.computeSegments(tu.getSource());
		assertEquals("[This sentence should not be split.]", fmt.printSegmentedContent(tu.getSource(), true));
		
		tu = new TextUnit("1", "This sentence should not be split. ");
		seg.computeSegments(tu.getSource());
		// why aren't two segments produced? [This sentence should not be split.][ ]
		assertEquals("[This sentence should not be split. ]", fmt.printSegmentedContent(tu.getSource(), true));
	}
	
	private OkapiMaxEntSegmenter createSegmenterWithRules (LocaleId locId) {
		return new OkapiMaxEntSegmenter(null, locId);
	}

	private ITextUnit createSegmentedTextUnit () {
		ISegmenter segmenter = new OkapiMaxEntSegmenter(null, LocaleId.ENGLISH);
		// Create the source and segment it
		ITextUnit tu = new TextUnit("id1", "This is the First part. A second part now is seen. A surprising third part is now visibile.");
		tu.createSourceSegmentation(segmenter);		
		return tu;
	}

	private TextContainer createCodedText () {
		TextFragment tf = new TextFragment();
		tf.append("PH after.");
		tf.append(TagType.PLACEHOLDER, "break", "<br/>");
		tf.append(TagType.OPENING, "bold", "<b>");
		tf.append(" End after.");
		tf.append(TagType.CLOSING, "bold", "</b>");
		tf.append(" Start after.");
		tf.append(TagType.OPENING, "italics", "<i>");
		tf.append(" Text.");
		tf.append(TagType.CLOSING, "italics", "</i>");
		tf.append("  ");
		return new TextContainer(tf);
	}

	private TextContainer createCodedText2 () {
		TextFragment tf = new TextFragment();
		tf.append("PH after.");
		tf.append(TagType.PLACEHOLDER, "break", "<br/>");
		tf.append(TagType.PLACEHOLDER, "break", "<br/>");
		tf.append(TagType.OPENING, "bold", "<b>");
		tf.append(TagType.OPENING, "italics", "<i>");
		tf.append(" End after.");
		tf.append(TagType.CLOSING, "italics", "</i>");
		tf.append(TagType.CLOSING, "bold", "</b>");
		tf.append(" Start after.");
		tf.append(TagType.OPENING, "under", "<u>");
		tf.append(TagType.OPENING, "italics", "<i>");
		tf.append(" Text.");
		tf.append(TagType.CLOSING, "italics", "</i>");
		tf.append(TagType.CLOSING, "under", "</u>");
		tf.append("  ");
		return new TextContainer(tf);
	}
}
