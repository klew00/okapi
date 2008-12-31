/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.segmentation.tests;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;
import junit.framework.*;

public class SRXDocumentTest extends TestCase {

	public void testGetSet () {
		SRXDocument doc = new SRXDocument();
		
		// Check defaults
		assertFalse(doc.cascade());
		assertFalse(doc.includeStartCodes());
		assertTrue(doc.includeEndCodes());
		assertFalse(doc.includeIsolatedCodes());
		assertTrue(doc.segmentSubFlows());
		assertFalse(doc.oneSegmentIncludesAll());
		assertFalse(doc.trimLeadingWhitespaces());
		assertFalse(doc.trimTrailingWhitespaces());
		
		// Check changing options
		doc.setCascade(true);
		assertTrue(doc.cascade());
		doc.setIncludeStartCodes(true);
		assertTrue(doc.includeStartCodes());
		doc.setIncludeEndCodes(false);
		assertFalse(doc.includeEndCodes());
		doc.setIncludeIsolatedCodes(true);
		assertTrue(doc.includeIsolatedCodes());
		doc.setSegmentSubFlows(false);
		assertFalse(doc.segmentSubFlows());
		doc.setOneSegmentIncludesAll(true);
		assertTrue(doc.oneSegmentIncludesAll());
		doc.setTrimLeadingWhitespaces(true);
		assertTrue(doc.trimLeadingWhitespaces());
		doc.setTrimTrailingWhitespaces(true);
		assertTrue(doc.trimTrailingWhitespaces());
		
	}

	public void testObjects () {
		// Check rule
		Rule rule = new Rule();
		assertEquals(rule.getBefore(), "");
		assertEquals(rule.getAfter(), "");
		assertTrue(rule.isBreak());
		assertTrue(rule.isActive());
		String tmp = "regex";
		rule.setAfter(tmp);
		assertEquals(rule.getAfter(), tmp);
		rule.setBefore(tmp);
		assertEquals(rule.getBefore(), tmp);
		rule.setIsBreak(false);
		assertFalse(rule.isBreak());
		rule.setIsActive(false);
		assertFalse(rule.isActive());
		
		// Check LanguageMap
		String pattern = "pattern";
		String ruleName = "ruleName";
		LanguageMap lm = new LanguageMap(pattern, ruleName);
		assertEquals(lm.getPattern(), pattern);
		assertEquals(lm.getRuleName(), ruleName);
	}
	
	public void testRules () {
		SRXDocument doc = new SRXDocument();
		doc.setCascade(true);

		ArrayList<Rule> list = new ArrayList<Rule>();
		list.add(new Rule("Mr\\.", "\\s", false));
		doc.addLanguageRule("english", list);
		ArrayList<Rule> rules = doc.getLanguageRules("english");
		assertEquals(rules.size(), 1);
		
		list = new ArrayList<Rule>();
		list.add(new Rule("\\.+", "\\s", true));
		doc.addLanguageRule("default", list);

		doc.addLanguageMap(new LanguageMap("en.*", "english"));
		doc.addLanguageMap(new LanguageMap(".*", "default"));

		Segmenter seg = doc.applyLanguageRules("en", null);
		assertNotNull(seg);
		assertEquals(seg.getLanguage(), "en");
		assertNull(seg.getSegmentRanges()); // Null set yet
		seg.computeSegments("Mr. Holmes. The detective.");
		assertNotNull(seg.getSegmentRanges());
		assertEquals(seg.getSegmentRanges().size(), 2);
		seg.computeSegments("MR. Holmes. The detective.");
		assertEquals(seg.getSegmentRanges().size(), 3);
		
		TextContainer tc = new TextContainer();
		tc.append("One.");
		tc.append(TagType.OPENING, "b", "<b>");
		tc.append(" Two.");
		tc.append(TagType.CLOSING, "b", "</b>");
		seg.setOptions(true, true, true, false, false, false, false);
		seg.computeSegments(tc);
		// "One.XX Two.YY" --> "[One.XX][ Two.YY]"
		List<Range> ranges = seg.getSegmentRanges();
		assertNotNull(ranges);
		assertEquals(ranges.size(), 2);
		assertEquals(ranges.get(0).end, 6);
		assertEquals(ranges.get(1).start, 6);
		seg.setOptions(true, false, true, false, false, false, false);
		seg.computeSegments(tc);
		// "One.XX Two.YY" --> "[One.][XX Two.YY]"
		ranges = seg.getSegmentRanges();
		assertNotNull(ranges);
		assertEquals(ranges.size(), 2);
		assertEquals(ranges.get(0).end, 4);
		assertEquals(ranges.get(1).start, 4);
	}

}
