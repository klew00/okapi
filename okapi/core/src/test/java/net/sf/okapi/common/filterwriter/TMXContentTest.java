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

package net.sf.okapi.common.filterwriter;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TMXContentTest {

	private TMXContent fmt;

	@Before
	public void setUp() throws Exception {
		fmt = new TMXContent();
	}
	
	@Test
	public void testSimple_Default () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		assertEquals("t1<bpt i=\"1\">&lt;b1&gt;</bpt><bpt i=\"2\">&lt;b2&gt;</bpt><ph x=\"3\">{\\x1\\}</ph>t2<ept i=\"2\">&lt;/b2&gt;</ept><ept i=\"1\">&lt;/b1&gt;</ept>t3",
			fmt.setContent(tf).toString());
	}
	
	@Test
	public void testSimple_OmegaT () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setLetterCodedMode(true, true);
		fmt.setContent(tf);
		assertEquals("t1<bpt i=\"1\">&lt;g0&gt;</bpt><bpt i=\"2\">&lt;g1&gt;</bpt><ph x=\"3\">&lt;x2/&gt;</ph>t2<ept i=\"2\">&lt;/g1&gt;</ept><ept i=\"1\">&lt;/g0&gt;</ept>t3",
			fmt.toString());
	}

	@Test
	public void testSimple_OmegaTUsingCodeMode () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setCodeMode(TMXContent.CODEMODE_LETTERCODED);
		fmt.setContent(tf);
		assertEquals("t1<bpt i=\"1\">&lt;g1&gt;</bpt><bpt i=\"2\">&lt;g2&gt;</bpt><ph x=\"3\">&lt;x3/&gt;</ph>t2<ept i=\"2\">&lt;/g2&gt;</ept><ept i=\"1\">&lt;/g1&gt;</ept>t3",
			fmt.toString());
	}

	@Test
	public void testSimple_EmptyCodes () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setCodeMode(TMXContent.CODEMODE_EMPTY);
		fmt.setContent(tf);
		assertEquals("t1<bpt i=\"1\"></bpt><bpt i=\"2\"></bpt><ph x=\"3\"></ph>t2<ept i=\"2\"></ept><ept i=\"1\"></ept>t3",
			fmt.toString());
	}

	@Test
	public void testSimple_GenericCodes () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setCodeMode(TMXContent.CODEMODE_GENERIC);
		fmt.setContent(tf);
		assertEquals("t1<bpt i=\"1\">&lt;1&gt;</bpt><bpt i=\"2\">&lt;2&gt;</bpt><ph x=\"3\">&lt;3/&gt;</ph>t2<ept i=\"2\">&lt;/2&gt;</ept><ept i=\"1\">&lt;/1&gt;</ept>t3",
				fmt.toString());
	}

	@Test
	public void testSimple_OriginalCodes () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setCodeMode(TMXContent.CODEMODE_ORIGINAL);
		fmt.setContent(tf);
		// Same as default
		assertEquals("t1<bpt i=\"1\">&lt;b1&gt;</bpt><bpt i=\"2\">&lt;b2&gt;</bpt><ph x=\"3\">{\\x1\\}</ph>t2<ept i=\"2\">&lt;/b2&gt;</ept><ept i=\"1\">&lt;/b1&gt;</ept>t3",
			fmt.setContent(tf).toString());
	}

	@Test
	public void testSimple_Trados () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setTradosWorkarounds(true);
		fmt.setContent(tf);
		assertEquals("t1<bpt i=\"1\">&lt;b1&gt;</bpt><bpt i=\"2\">&lt;b2&gt;</bpt><ut>{\\cs6\\f1\\cf6\\lang1024 </ut>{\\x1\\}<ut>}</ut>t2<ept i=\"2\">&lt;/b2&gt;</ept><ept i=\"1\">&lt;/b1&gt;</ept>t3",
			fmt.toString());
	}
	
	private TextFragment createTextUnit () {
		TextFragment tf = new TextFragment();
		tf.append("t1");
		tf.append(TagType.OPENING, "b1", "<b1>");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append(TagType.PLACEHOLDER, "x1", "{\\x1\\}");
		tf.append("t2");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append(TagType.CLOSING, "b1", "</b1>");
		tf.append("t3");
		return tf;
	}
	
}
