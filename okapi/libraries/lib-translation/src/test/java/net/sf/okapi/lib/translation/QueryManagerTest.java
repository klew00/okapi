/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.translation;

import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryManagerTest {

	private QueryManager qm;
	private GenericContent fmt = new GenericContent();
	private LocaleId locSrc = LocaleId.fromString("src");
	private LocaleId locTrg = LocaleId.fromString("trg");
	
	@Before
	public void setUp() {
		qm = new QueryManager();
	}

	@Test
	public void testLanguages () {
		qm.setLanguages(locSrc, locTrg);
		assertEquals(locSrc, qm.getSourceLanguage());
		assertEquals(locTrg, qm.getTargetLanguage());
	}

//	@Test
//	public void testOptions () {
//		qm.setThreshold(63);
//		assertEquals(63, qm.getThreshold());
//		qm.setMaximumHits(4321);
//		assertEquals(4321, qm.getMaximumHits());
//	}

	@Test
	public void testAdjustNoCodes () {
		TextUnit tu = new TextUnit("1", "src");
		TextFragment newSrc = new TextFragment("src");
		TextFragment newTrg = new TextFragment("trg");
		qm.adjustNewFragment(tu.getSource().getSegments().getFirstContent(), newSrc, newTrg, 99, tu);
		assertEquals(locTrg, newTrg.toString());
	}

	@Test
	public void testAdjustSameMarkers () {
		TextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		qm.adjustNewFragment(tu.getSource().getSegments().getFirstContent(), tf, tf, 99, tu);
		assertEquals("T <b>BOLD</b> T <br/>", tf.toString());
		fmt.setContent(tf);
		assertEquals("T <1>BOLD</1> T <2/>", fmt.toString());
	}

	@Test
	public void testAdjustExtraMarkers () {
		TextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		tf.append(TagType.PLACEHOLDER, "extra", "<EXTRA/>");
		qm.adjustNewFragment(tu.getSource().getSegments().getFirstContent(), tf, tf, 99, tu);
		assertEquals("T <b>BOLD</b> T <br/><EXTRA/>", tf.toString());
		fmt.setContent(tf);
		assertEquals("T <1>BOLD</1> T <2/><3/>", fmt.toString());
	}

	@Test
	public void testAdjustMissingMarker () {
		TextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		tf.append(TagType.PLACEHOLDER, "extra", "<EXTRA/>");
		qm.adjustNewFragment(tu.getSource().getSegments().getFirstContent(), tf, tf, 99, tu);
		assertEquals("T <b>BOLD T <br/><EXTRA/>", tf.toString());
		fmt.setContent(tf);
		assertEquals("T <b1/>BOLD T <2/><3/>", fmt.toString());
	}

	@Test
	public void testAdjustDifferentTextSameMarkers () {
		TextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("U ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" U ");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		// Fuzzy match but codes are the same
		qm.adjustNewFragment(tu.getSource().getFirstPartContent(), tf, tf, 88, tu);
		assertEquals("U <b>BOLD</b> U <br/>", tf.toString());
		assertEquals("U <1>BOLD</1> U <2/>", fmt.setContent(tf).toString());
	}

	private TextUnit createTextUnit1 () {
		TextUnit tu = new TextUnit("1", "t ");
		TextFragment tf = tu.getSource().getSegments().getFirstContent();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" t ");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		return tu;
	}

//	private TextUnit createTextUnit2 () {
//		TextUnit tu = new TextUnit("1", "t ");
//		TextFragment tf = tu.getSourceContent();
//		tf.append(TagType.OPENING, "b", "<b>");
//		tf.append("bold");
//		tf.append(TagType.CLOSING, "b", "</b>");
//		tf.append(" t ");
//		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
//		tf.append(TagType.OPENING, "i", "<i>");
//		tf.append("italics");
//		tf.append(TagType.CLOSING, "i", "</i>");
//		return tu;
//	}

}
