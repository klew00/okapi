/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.steps.segmentation;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.desegmentation.DesegmentationStep;
import net.sf.okapi.steps.segmentation.Parameters;
import net.sf.okapi.steps.segmentation.Parameters.SegmStrategy;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Unittest related to code ID renumbering during
 * segmentation and desegmentation. 
 */
public class TestRenumberingSegmentationStep {
	private SegmentationStep segStep;
	private DesegmentationStep dsegStep;
	private Parameters params;
	net.sf.okapi.steps.desegmentation.Parameters dparams;
	
	@Before
	public void setup() throws URISyntaxException {
		segStep = new SegmentationStep();
		params = (Parameters)segStep.getParameters();
		segStep.setSourceLocale(LocaleId.ENGLISH);
		segStep.setTargetLocales(Arrays.asList(LocaleId.FRENCH, LocaleId.GERMAN));
		params = (Parameters) segStep.getParameters();
		String srxFile = this.getClass().getResource("/Test01.srx").toURI().getPath();
		params.setSourceSrxPath(srxFile);
		params.setTargetSrxPath(srxFile);
		params.segmentTarget = true;
		params.setRenumberCodes(true);
		params.setSegmentationStrategy(SegmStrategy.OVERWRITE_EXISTING);
		segStep.handleEvent(new Event(EventType.START_BATCH_ITEM));
		
		dsegStep = new DesegmentationStep();
		dsegStep.setTargetLocales(Arrays.asList(LocaleId.FRENCH, LocaleId.GERMAN));
		dparams = (net.sf.okapi.steps.desegmentation.Parameters)dsegStep.getParameters();
		dparams.setDesegmentSource(true);
		dparams.setDesegmentTarget(true);
		dparams.setRenumberCodes(true);
	}
	
	private ITextUnit getSimpleTu() {
		ITextUnit tu1 = new TextUnit("tu1");
		TextContainer source = tu1.getSource();
		TextFragment tf = new TextFragment();
		tf.append("This is the ");
		tf.append(TagType.OPENING, "strong", "<strong>");
		tf.append("first");
		tf.append(TagType.CLOSING, "strong", "</strong>");
		tf.append(" sentence. This is the ");
		tf.append(TagType.OPENING, "strong", "<strong>");
		tf.append("second");
		tf.append(TagType.CLOSING, "strong", "</strong>");
		tf.append(" sentence.");
		source.append(new TextPart(tf));
		return tu1;
	}
	
	@Test
	public void testPlaceholderRenumbering() {		
		ITextUnit tu1 = getSimpleTu();
		
		// Run it and verify that the codes have ID 1, 
		// regardless of segment
		segStep.handleTextUnit(new Event(EventType.TEXT_UNIT, tu1));
		ISegments segs = tu1.getSource().getSegments();
		assertEquals(2, segs.count());
		for (Code code : segs.get(0).getContent().getCodes()) {
			assertEquals(1, code.getId());
		}
		for (Code code : segs.get(1).getContent().getCodes()) {
			assertEquals(1, code.getId());
		}
		
		// Verify that the target also has this, due to the copySource flag
		assertEquals(2, tu1.getTargetLocales().size());
		for (LocaleId locale : tu1.getTargetLocales()) {
			ISegments tgtSegs = tu1.getTarget(locale).getSegments();
			assertEquals(2, tgtSegs.count());
			for (Code code : tgtSegs.get(0).getContent().getCodes()) {
				assertEquals(1, code.getId());
			}
			for (Code code : tgtSegs.get(1).getContent().getCodes()) {
				assertEquals(1, code.getId());
			}
		}
		
		dsegStep.handleEvent(new Event(EventType.TEXT_UNIT, tu1));
		segs = tu1.getSource().getSegments();
		assertEquals(1, segs.count());
		Segment s = segs.get(0);
		List<Code> codes = s.getContent().getCodes();
		assertEquals(1, codes.get(0).getId());
		assertEquals(1, codes.get(1).getId());
		assertEquals(2, codes.get(2).getId());
		assertEquals(2, codes.get(3).getId());
	}

	// Nasty corner case - tags split over a segment boundary.
	// Renumbering can't break up the split, so we 
	// renumber all the connected segments as a unit.
	@Test
	public void testDanglingPlaceholderRenumbering() {
		ITextUnit tu1 = new TextUnit("tu1");
		TextContainer source = tu1.getSource();
		
		// Text:
		// <b>Sentence 1</b>.  This is <br/>sentence 2 with <b>formatting.  The formatting is </b> split across <b>multiple</b> sentences <br />.
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("Sentence 1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(". ");
		tf.append("This is ");
		tf.append(TagType.PLACEHOLDER, "br", "<br />");
		tf.append("sentence 2 with ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("formatting.  The formatting is ");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" split across ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("multiple");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" sentences ");
		tf.append(TagType.PLACEHOLDER, "br", "<br />");
		tf.append(".");
		
		source.append(new TextPart(tf));
		
		segStep.handleTextUnit(new Event(EventType.TEXT_UNIT, tu1));
		ISegments segs = source.getSegments();
		assertEquals(3, segs.count());
		// <b>Sentence 1</b>
		for (Code code : segs.get(0).getContent().getCodes()) {
			assertEquals(1, code.getId());
		}
		// This is <br/>sentence 2 with <b>formatting. 
		assertEquals(1, segs.get(1).getContent().getCode(0).getId());
		assertEquals(2, segs.get(1).getContent().getCode(1).getId());
		// The formatting is </b> split across <b>multiple</b> sentences <br />.
		assertEquals(2, segs.get(2).getContent().getCode(0).getId());
		assertEquals(3, segs.get(2).getContent().getCode(1).getId());
		assertEquals(3, segs.get(2).getContent().getCode(2).getId());
		assertEquals(4, segs.get(2).getContent().getCode(3).getId());
		
		tu1.setSource(source);
		dsegStep.handleEvent(new Event(EventType.TEXT_UNIT, tu1));
		segs = source.getSegments();
		assertEquals(1, segs.count());
		Segment s = segs.get(0);
		List<Code> codes = s.getContent().getCodes();
		// <b>Sentence 1</b>.  This is <br/>sentence 2 with <b>formatting.  The formatting is </b> split across <b>multiple</b> sentences <br />.
		assertEquals(1, codes.get(0).getId());
		assertEquals(1, codes.get(1).getId());
		assertEquals(2, codes.get(2).getId());
		assertEquals(3, codes.get(3).getId());
		assertEquals(3, codes.get(4).getId());
		assertEquals(4, codes.get(5).getId());
		assertEquals(4, codes.get(6).getId());
		assertEquals(5, codes.get(7).getId());
	}	
}
