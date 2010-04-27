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
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextContainer.Segments;
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
		Segments segments = tc.getSegments();
		int n = seg.computeSegments(tc);
		assertEquals(2, n);
		segments.create(seg.getRanges());
		assertEquals(2, segments.count());
		assertEquals("Part 1.", segments.get(0).toString());
		assertEquals(" Part 2.", segments.get(1).toString());
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
	
}
