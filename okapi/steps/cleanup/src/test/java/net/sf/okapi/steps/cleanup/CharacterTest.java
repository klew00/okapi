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
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;


public class CharacterTest {

	private final LocaleId locFR = LocaleId.FRENCH;
	private final LocaleId locDE = LocaleId.GERMAN;
	
	private GenericContent fmt;
	private Cleaner cleaner;
	private Parameters params;
	
	@Before
	public void setup() {
		
		params = new Parameters();
		params.setCheckCharacters(true);
		fmt = new GenericContent();
		cleaner = new Cleaner();
	}
	
	@Test
	public void testSimpleCharacter() {

		TextFragment srcTf = new TextFragment("This is some normal text");
		// Ëþáûå äâèæåíèÿ
		TextFragment trgTf = new TextFragment("\u00CB\u00FE\u00E1\u00FB\u00E5 \u00E4\u00E2\u00E8\u00E6\u00E5\u00ED\u00E8\u00FF");
		
		ITextUnit tu = new TextUnit("tu1");
		TextContainer srcTc = tu.getSource();
		srcTc.append(new Segment("seg1", srcTf));
		
		TextContainer trgTc = tu.createTarget(locFR, true, IResource.CREATE_EMPTY);
		trgTc.append(new Segment("seg1", trgTf));
		
		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				Segment trgSeg = tu.getTargetSegment(locFR, srcSeg.getId(), false);
				if (trgSeg != null) {
					cleaner.checkCharacters(tu, srcSeg, locFR);			
				}
			}
		}		
		
		assertEquals("[This is some normal text]", fmt.printSegmentedContent(tu.getSource(), true, false));
		assertEquals("[This is some normal text]", fmt.printSegmentedContent(tu.getSource(), true, true));
		assertEquals("[]", fmt.printSegmentedContent(tu.getTarget(locFR), true, false));
		assertEquals("[]", fmt.printSegmentedContent(tu.getTarget(locFR), true, true));
	}
}