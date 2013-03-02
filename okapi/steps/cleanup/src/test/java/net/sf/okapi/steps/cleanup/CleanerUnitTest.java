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

public class CleanerUnitTest {

	private final LocaleId locFR = LocaleId.FRENCH;
	private final LocaleId locDE = LocaleId.GERMAN;

	private GenericContent fmt;
	private Cleaner cleaner;

	@Before
	public void setup() {

		fmt = new GenericContent();
		cleaner = new Cleaner();
	}
	
//	@Test
//	public void testCleanUnit() {
//
//		// “t1, ” t2 ,“‘t3 , ’ t4 :”
//		TextFragment srcTf1 = new TextFragment("\u201Ct1\u002C \u201D t2 \u002C\u201C\u2018t3 \u002C\u2019 t4 \u003A\u201D");
//		// t1 “t2” ; . 352
//		TextFragment srcTf2 = new TextFragment("t1 \u201Ct2\u201D \u003B \u002E 352");
//		// t1 ‘t2 ’ ““t3 ! ””
//		TextFragment srcTf3 = new TextFragment("t1 \u2018t2 \u2019 \u201C\u201Ct3 \u0021 \u201D\u201D");
//
//		// « t1 » ,l’t2,« t3 , t4 » :
//		TextFragment frTf1 = new TextFragment("\u00AB\u00A0t1\u00A0\u00BB \u002Cl\u2019t2\u002C\u00AB\u00A0t3 \u002C t4\u00A0\u00BB \u003A");
//		// t1 «  t2» ;0, 352
//		TextFragment frTf2 = new TextFragment("t1 \u00AB\u00A0 t2\u00BB\u003B0\u002C 352");
//		// t1 ‘t2’ « t3 ! »
//		TextFragment frTf3 = new TextFragment("t1 \u2018t2\u2019 \u00AB\u00A0t3\u00A0\u0021\u00A0\u00BB");
//
//		ITextUnit tu = new TextUnit("tu1");
//		TextContainer srcTc = tu.getSource();
//		srcTc.append(new Segment("seg1", srcTf1));
//		srcTc.append(new TextPart(" "));
//		srcTc.append(new Segment("seg2", srcTf2));
//		srcTc.append(new TextPart(" "));
//		srcTc.append(new Segment("seg3", srcTf3));
//
//		TextContainer frTc = tu.createTarget(locFR, true, IResource.CREATE_EMPTY);
//		frTc.append(new Segment("seg1", frTf1));
//		frTc.append(new TextPart(" "));
//		frTc.append(new Segment("seg2", frTf2));
//		frTc.append(new TextPart(" "));
//		frTc.append(new Segment("seg3", frTf3));
//
//		if (!tu.isEmpty()) {
//			ISegments srcSegs = tu.getSourceSegments();
//			for (Segment srcSeg : srcSegs) {
//				Segment trgSeg = tu.getTargetSegment(locFR, srcSeg.getId(), false);
//				if (trgSeg != null) {
//					cleaner.normalizeQuotation(srcSeg.text, trgSeg.text);
//					cleaner.normalizePunctuation(srcSeg.text, trgSeg.text);
//					cleaner.normalizeMarks(tu, srcSeg, locFR, false);
//				}
//			}
//		}
//
//		assertEquals("[\"t1,\" t2, \"\'t3,\' t4:\"]", fmt.printSegmentedContent(tu.getSource(), true, false));
//		assertEquals("[\"t1,\" t2, \"\'t3,\' t4:\"] [t1 \"t2\"; .352] [t1 \'t2 \' \"\"t3!\"\"]", fmt.printSegmentedContent(tu.getSource(), true, false));
//		assertEquals("[\"t1,\" t2, \"\'t3,\' t4:\"] [t1 \"t2\"; .352] [t1 \'t2 \' \"\"t3!\"\"]", fmt.printSegmentedContent(tu.getSource(), true, true));
//		assertEquals("[\"t1\", l\'t2, \"t3, t4\":] [t1 \"t2\"; 0,352] [t1 \'t2\' \"t3!\"]", fmt.printSegmentedContent(tu.getTarget(locFR), true, false));
//		assertEquals("[\"t1\", l\'t2, \"t3, t4\":] [t1 \"t2\"; 0,352] [t1 \'t2\' \"t3!\"]", fmt.printSegmentedContent(tu.getTarget(locFR), true, true));
//	}
}