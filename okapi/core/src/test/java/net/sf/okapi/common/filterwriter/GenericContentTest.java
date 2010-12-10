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

import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GenericContentTest {

	private GenericContent fmt;
	
	@Before
	public void setUp() throws Exception {
		fmt = new GenericContent();
	}
	
	@Test
	public void testSimple_Default () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		String gtext = fmt.setContent(tf).toString();
		assertEquals("t1<1><2><3/>t2</2></1>t3", gtext);
		// Reconstruct it
		TextFragment tf2 = tf.clone();
		fmt.updateFragment(gtext, tf2, false);
		assertEquals("t1<1><2><3/>t2</2></1>t3", fmt.setContent(tf2).toString());
	}
	
	@Test
	public void testSimple_WithOption () {
		TextFragment tf = createTextUnit();
		assertEquals(tf.getCodes().size(), 5);
		fmt.setContent(tf);
		assertEquals("t1<b1><b2><x1/>t2</b2></b1>t3", fmt.toString(true));
		assertEquals("t1<1><2><3/>t2</2></1>t3", fmt.toString(false));
	}
	
	@Test
	public void testMisOrderedCodes () {
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
		fmt.setContent(tf);
		// Not real XML so mis-ordering is OK
		assertEquals("t1<b1>t2<b2>t3</b1>t4</b2>t5", fmt.toString(true));
		String gtext = fmt.toString(false);
		assertEquals("t1<b1/>t2<b2/>t3<e1/>t4<e2/>t5", gtext);
		// Reconstruct it
		TextFragment tf2 = tf.clone();
		fmt.updateFragment(gtext, tf2, false);
		assertEquals("t1<b1>t2<b2>t3</b1>t4</b2>t5", fmt.setContent(tf2).toString(true));
		assertEquals("t1<b1/>t2<b2/>t3<e1/>t4<e2/>t5", fmt.setContent(tf2).toString());
	}
	
	@Test
	public void testReOrderingCodes () {
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
		fmt.setContent(tf);
		// Not real XML so mis-ordering is OK
		assertEquals("t1<b1>t2<b2>t3</b1>t4</b2>t5", fmt.toString(true));
		assertEquals("t1<b1/>t2<b2/>t3<e1/>t4<e2/>t5", fmt.toString(false));
		// Reconstruct it in a different order
//TODO
//		TextFragment tf2 = tf.clone();
//		fmt.updateFragment("t1<b1/>t2<b2/>t4<e2/>t5 t3<e1/>", tf2, false);
//		assertEquals("t1<1>t2<2>t4</2>t5 t3</1>", fmt.setContent(tf2).toString());
	}

	private TextFragment createTextUnit () {
		TextFragment tf = new TextFragment();
		tf.append("t1");
		tf.append(TagType.OPENING, "b1", "<b1>");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("t2");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append(TagType.CLOSING, "b1", "</b1>");
		tf.append("t3");
		return tf;
	}
	
}
