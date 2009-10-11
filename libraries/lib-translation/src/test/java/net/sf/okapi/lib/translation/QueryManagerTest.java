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

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryManagerTest {

	private QueryManager qm;
	
	@Before
	public void setUp() {
		qm = new QueryManager();
	}

	@Test
	public void testLanguages () {
		qm.setLanguages("src", "trg");
		assertEquals("src", qm.getSourceLanguage());
		assertEquals("trg", qm.getTargetLanguage());
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
		TextFragment newFrag = new TextFragment("trg");
		qm.adjustNewFragment(tu.getSourceContent(), newFrag, false, tu);
		assertEquals("trg", newFrag.toString());
	}

	@Test
	public void testAdjustSameMarkers () {
		TextUnit tu = createTextUnit();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		qm.adjustNewFragment(tu.getSourceContent(), tf, false, tu);
		assertEquals("T <b>BOLD</b> T <br/>", tf.toString());
	}

	@Test
	public void testAdjustExtraMarkers () {
		TextUnit tu = createTextUnit();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		tf.append(TagType.PLACEHOLDER, "extra", "<EXTRA/>");
		qm.adjustNewFragment(tu.getSourceContent(), tf, false, tu);
		assertEquals("T <b>BOLD</b> T <br/><EXTRA/>", tf.toString());
	}

	private TextUnit createTextUnit () {
		TextUnit tu = new TextUnit("1", "t ");
		TextFragment tf = tu.getSourceContent();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" t ");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		return tu;
	}
}
