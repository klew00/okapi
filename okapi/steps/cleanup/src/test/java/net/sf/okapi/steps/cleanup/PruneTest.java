/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.cleanup;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class PruneTest {

	private final LocaleId locFR = LocaleId.FRENCH;
	private final LocaleId locDE = LocaleId.GERMAN;

	private GenericContent fmt;
	private Cleaner cleaner;

	@Before
	public void setup() {
		
		Parameters params = new Parameters();
		fmt = new GenericContent();
		cleaner = new Cleaner(params);
	}

	@Test
	public void simpleMarkTest() {

		int numDelete = 1;
		int num = 0;

		TextFragment srcTf1 = new TextFragment("t1 t2");
		TextFragment srcTf2 = new TextFragment("t3 t4");
		TextFragment srcTf3 = new TextFragment("t5 t6");

		TextFragment frTf1 = new TextFragment("t1 t2");
		TextFragment frTf2 = new TextFragment("t3 t4");
		TextFragment frTf3 = new TextFragment("t5 t6");

		ITextUnit tu = new TextUnit("tu1");
		TextContainer srcTc = tu.getSource();
		srcTc.append(new Segment("seg1", srcTf1));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg2", srcTf2));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg3", srcTf3));

		TextContainer frTc = tu.createTarget(locFR, true, IResource.CREATE_EMPTY);
		frTc.append(new Segment("seg1", frTf1));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg2", frTf2));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg3", frTf3));

		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				if (num >= numDelete) {
					cleaner.markSegmentForRemoval(tu, srcSeg, locFR);
				}
				num += 1;
			}
		}

		assertEquals("[t1 t2] [] []", fmt.printSegmentedContent(tu.getTarget(locFR), true, false));
		assertEquals("[t1 t2] [] []", fmt.printSegmentedContent(tu.getTarget(locFR), true, true));
	}

	@Test
	public void simplePruneSegmentsTest() {

		int numDelete = 1;
		int num = 0;

		TextFragment srcTf1 = new TextFragment("s1 s2");
		TextFragment srcTf2 = new TextFragment("s3 s4");
		TextFragment srcTf3 = new TextFragment("s5 s6");

		TextFragment frTf1 = new TextFragment("t1 t2");
		TextFragment frTf2 = new TextFragment("t3 t4");
		TextFragment frTf3 = new TextFragment("t5 t6");

		ITextUnit tu = new TextUnit("tu1");
		TextContainer srcTc = tu.getSource();
		srcTc.append(new Segment("seg1", srcTf1));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg2", srcTf2));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg3", srcTf3));

		TextContainer frTc = tu.createTarget(locFR, true, IResource.CREATE_EMPTY);
		frTc.append(new Segment("seg1", frTf1));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg2", frTf2));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg3", frTf3));

		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				if (num >= numDelete) {
					cleaner.markSegmentForRemoval(tu, srcSeg, locFR);
				}
				num += 1;
			}
		}

		assertEquals("[t1 t2] [] []", fmt.printSegmentedContent(tu.getTarget(locFR), true, false));
		assertEquals("[t1 t2] [] []", fmt.printSegmentedContent(tu.getTarget(locFR), true, true));
	}
	
	@Test
	public void simplePruneUnitTest() {

		int numDelete = 0;
		int num = 0;

		TextFragment srcTf1 = new TextFragment("s1 s2");
		TextFragment srcTf2 = new TextFragment("s3 s4");
		TextFragment srcTf3 = new TextFragment("s5 s6");

		TextFragment frTf1 = new TextFragment("t1 t2");
		TextFragment frTf2 = new TextFragment("t3 t4");
		TextFragment frTf3 = new TextFragment("t5 t6");

		ITextUnit tu = new TextUnit("tu1");
		TextContainer srcTc = tu.getSource();
		srcTc.append(new Segment("seg1", srcTf1));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg2", srcTf2));
		srcTc.append(new TextPart(" "));
		srcTc.append(new Segment("seg3", srcTf3));

		TextContainer frTc = tu.createTarget(locFR, true, IResource.CREATE_EMPTY);
		frTc.append(new Segment("seg1", frTf1));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg2", frTf2));
		frTc.append(new TextPart(" "));
		frTc.append(new Segment("seg3", frTf3));

		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				if (num >= numDelete) {
					cleaner.markSegmentForRemoval(tu, srcSeg, locFR);
				}
				num += 1;
			}
		}
		
		assertEquals(true, cleaner.run(tu, locFR));
	}

}
