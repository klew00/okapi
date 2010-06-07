/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.qualitycheck;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QualityCheckerTest {
	
	private QualityChecker checker = new QualityChecker();
	private LocaleId locFR = LocaleId.FRENCH;
	
	@Before
	public void setUp() {
		checker.initialize(locFR, null);
	}

	@Test
	public void testMISSING_TARGETTU () {
		// Create source with non-empty content
		// but no target
		TextUnit tu = new TextUnit("id", "source");

		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.MISSING_TARGETTU, issues.get(0).issueType);
	}

	@Test
	public void testEMPTY_TARGETSEG () {
		// Create TU with source of non-empty segment
		// and target of empty segment
		TextUnit tu = new TextUnit("id", "source");
		tu.setTarget(locFR, new TextContainer());
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.EMPTY_TARGETSEG, issues.get(0).issueType);
	}

	@Test
	public void testMISSING_TARGETSEG () {
		// Create TU with source of two segments
		// and target of one segment
		TextContainer tc = new TextContainer("srctext1");
		tc.getSegments().append(new Segment("s2", new TextFragment("srctext2")));
		TextUnit tu = new TextUnit("id");
		tu.setSource(tc);
		tu.setTarget(locFR, new TextContainer("trgext1"));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.MISSING_TARGETSEG, issues.get(0).issueType);
	}

	@Test
	public void testEMPTY_TARGETSEG2 () {
		// Create TU with source of two segments
		// and target of two segments but one empty
		TextContainer tc = new TextContainer("srctext1");
		tc.getSegments().append(new Segment("s2", new TextFragment("srctext2")));
		TextUnit tu = new TextUnit("id");
		tu.setSource(tc);
		tc = new TextContainer("trgtext1");
		tc.getSegments().append(new Segment("s2", new TextFragment()));
		tu.setTarget(locFR, tc);
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.EMPTY_TARGETSEG, issues.get(0).issueType);
	}

	@Test
	public void testMISSINGORDIFF_LEADINGWS () {
		TextUnit tu = new TextUnit("id", "  srctext");
		tu.setTarget(locFR, new TextContainer("trgext"));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.MISSINGORDIFF_LEADINGWS, issues.get(0).issueType);
	}

	@Test
	public void testMISSINGORDIFF_TRAILINGWS () {
		TextUnit tu = new TextUnit("id", " srctext ");
		tu.setTarget(locFR, new TextContainer(" trgext"));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.MISSINGORDIFF_TRAILINGWS, issues.get(0).issueType);
	}

	@Test
	public void testEXTRAORDIFF_LEADINGWS () {
		TextUnit tu = new TextUnit("id", "  srctext");
		tu.setTarget(locFR, new TextContainer("   trgext"));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.EXTRAORDIFF_LEADINGWS, issues.get(0).issueType);
	}

	@Test
	public void testEXTRAORDIFF_TRAILINGWS () {
		TextUnit tu = new TextUnit("id", "srctext  ");
		tu.setTarget(locFR, new TextContainer("trgtext   "));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.EXTRAORDIFF_TRAILINGWS, issues.get(0).issueType);
	}

	@Test
	public void testTARGET_SAME_AS_SOURCE () {
		TextUnit tu = new TextUnit("id", "src text");
		tu.setTarget(locFR, new TextContainer("src text"));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.TARGET_SAME_AS_SOURCE, issues.get(0).issueType);
	}
	
	@Test
	public void testTARGET_SAME_AS_SOURCE_WithSameCodes () {
		TextUnit tu = new TextUnit("id", "src text");
		tu.getSource().getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<code/>");
		tu.setTarget(locFR, new TextContainer("src text"));
		tu.getTarget(locFR).getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<code/>");
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueType.TARGET_SAME_AS_SOURCE, issues.get(0).issueType);
	}
	
	@Test
	public void testTARGET_SAME_AS_SOURCE_WithDifferentCodes () {
		TextUnit tu = new TextUnit("id", "src text");
		tu.getSource().getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<code/>");
		tu.setTarget(locFR, new TextContainer("src text"));
		tu.getTarget(locFR).getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<CODE/>");
		
		checker.getParameters().setCodeDifference(false);
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		// Codes are different, since the option is code-sensitive: no issue (target not the same as source)
		assertEquals(0, issues.size());
	}

	@Test
	public void testTARGET_SAME_AS_SOURCE_WithDifferentCodes_CodeInsensitive () {
		TextUnit tu = new TextUnit("id", "src text");
		tu.getSource().getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<code/>");
		tu.setTarget(locFR, new TextContainer("src text"));
		tu.getTarget(locFR).getSegments().get(0).text.append(TagType.PLACEHOLDER, "codeType", "<CODE/>");
		
		checker.getParameters().setCodeDifference(false);
		checker.getParameters().setTargetSameAsSourceWithCodes(false);
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		// Codes are different, since the option is NOT code-sensitive: issue raised (target = source)
		assertEquals(1, issues.size());
		assertEquals(IssueType.TARGET_SAME_AS_SOURCE, issues.get(0).issueType);
	}
	
	@Test
	public void testTARGET_SAME_AS_SOURCE_NoIssue () {
		TextUnit tu = new TextUnit("id", "  \t\n ");
		tu.setTarget(locFR, new TextContainer("  \t\n "));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(0, issues.size());
	}

	@Test
	public void testNoIssues () {
		TextUnit tu = new TextUnit("id", "  Text with 123. ");
		tu.setTarget(locFR, new TextContainer("  Texte avec 123. "));
		
		checker.processTextUnit(tu);
		List<Issue> issues = checker.getIssues();
		assertEquals(0, issues.size());
	}

}
