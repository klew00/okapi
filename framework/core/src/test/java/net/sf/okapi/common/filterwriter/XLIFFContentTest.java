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

public class XLIFFContentTest {

	private XLIFFContent fmt;

	@Before
	public void setUp() throws Exception {
		fmt = new XLIFFContent();
	}
	
	@Test
	public void testSimpleDefault () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		assertEquals("t1<bpt id=\"1\">&lt;b1&gt;</bpt><bpt id=\"2\">&lt;b2&gt;</bpt><ph id=\"3\">{\\x1\\}</ph>t2<ept id=\"2\">&lt;/b2&gt;</ept><ept id=\"1\">&lt;/b1&gt;</ept>t3",
			fmt.setContent(tf).toString());
	}
	
	@Test
	public void testSimpleGX () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		assertEquals("t1<g id=\"1\"><g id=\"2\"><x id=\"3\"/>t2</g></g>t3",
			fmt.setContent(tf).toString(true));
	}

	@Test
	public void testMisOrderedGX () {
		TextFragment tf = createMisOrderedTextUnit();
		assertEquals(tf.getCodes().size(), 4);
		assertEquals("t1<bx id=\"1\"/>t2<bx id=\"2\"/>t3<ex id=\"1\"/>t4<ex id=\"2\"/>t5",
			fmt.setContent(tf).toString(true));
	}
	
	@Test
	public void testMisOrderedComplexGX () {
		TextFragment tf = createMisOrderedComplexTextUnit();
		assertEquals(tf.getCodes().size(), 8);
		assertEquals("<bx id=\"1\"/><bx id=\"2\"/><g id=\"3\"></g><ex id=\"1\"/><bx id=\"4\"/><ex id=\"2\"/><ex id=\"4\"/>",
			fmt.setContent(tf).toString(true));
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
	
	private TextFragment createMisOrderedTextUnit () {
		TextFragment tf = new TextFragment();
		tf.append("t1");
		tf.append(TagType.OPENING, "b1", "<b1>");
		tf.append("t2");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append("t3");
		tf.append(TagType.CLOSING, "b1", "</b1>");
		tf.append("t4");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append("t5");
		return tf;
	}

	private TextFragment createMisOrderedComplexTextUnit () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b1", "<b1>");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append(TagType.CLOSING, "b1", "</b1>");
		tf.append(TagType.OPENING, "b3", "<b3>");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append(TagType.CLOSING, "b3", "</b3>");
		return tf;
	}
	
}
