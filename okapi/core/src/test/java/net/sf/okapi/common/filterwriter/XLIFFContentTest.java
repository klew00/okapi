/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
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
		TextFragment tf = createTextFragment();
		assertEquals(tf.getCodes().size(), 5);
		assertEquals("t1<bpt id=\"1\">&lt;b1&gt;</bpt><bpt id=\"2\">&lt;b2&gt;</bpt><ph id=\"3\">{\\x1\\}</ph>t2<ept id=\"2\">&lt;/b2&gt;</ept><ept id=\"1\">&lt;/b1&gt;</ept>t3",
			fmt.setContent(tf).toString());
	}
	
	@Test
	public void testSimpleGX () {
		TextFragment tf = createTextFragment();
		assertEquals(tf.getCodes().size(), 5);
		assertEquals("t1<g id=\"1\"><g id=\"2\"><x id=\"3\"/>t2</g></g>t3",
			fmt.setContent(tf).toString(true));
	}

	@Test
	public void testMisOrderedGX1 () {
		TextFragment tf = createMisOrderedTextFragment1();
		assertEquals(tf.getCodes().size(), 4);
		assertEquals("t1<bx id=\"1\"/>t2<bx id=\"2\"/>t3<ex id=\"1\"/>t4<ex id=\"2\"/>t5",
			fmt.setContent(tf).toString(true));
	}
	
	@Test
	public void testMisOrderedGX2 () {
		TextFragment tf = createMisOrderedTextFragment2();
		assertEquals(tf.getCodes().size(), 4);
		assertEquals("<ex id=\"3\"/><g id=\"1\"></g><bx id=\"2\"/>",
			fmt.setContent(tf).toString(true));
	}
	
	@Test
	public void testMisOrderedComplexGX () {
		TextFragment tf = createMisOrderedComplexFragmentUnit();
		assertEquals(tf.getCodes().size(), 8);
		assertEquals("<bx id=\"1\"/><bx id=\"2\"/><g id=\"3\"></g><ex id=\"1\"/><bx id=\"4\"/><ex id=\"2\"/><ex id=\"4\"/>",
			fmt.setContent(tf).toString(true));
	}
	
	@Test
	public void testMisOrderedComplexBPT () {
		TextFragment tf = createMisOrderedComplexFragmentUnit();
		assertEquals(tf.getCodes().size(), 8);
		assertEquals("<it id=\"1\" pos=\"open\">&lt;b1&gt;</it><it id=\"2\" pos=\"open\">&lt;b2&gt;</it><bpt id=\"3\">&lt;b2&gt;</bpt><ept id=\"3\">&lt;/b2&gt;</ept><it id=\"1\" pos=\"close\">&lt;/b1&gt;</it><it id=\"4\" pos=\"open\">&lt;b3&gt;</it><it id=\"2\" pos=\"close\">&lt;/b2&gt;</it><it id=\"4\" pos=\"close\">&lt;/b3&gt;</it>",
			fmt.setContent(tf).toString(false));
	}
	
//TEMPORARY test	
	@Test
	public void testDisambiguityAnnotation () {
		GenericAnnotations anns = new GenericAnnotations();
		GenericAnnotation ga = anns.add(GenericAnnotationType.DISAMB);
		ga.setString(GenericAnnotationType.DISAMB_SOURCE, "src");
		ga.setString(GenericAnnotationType.DISAMB_GRANULARITY, GenericAnnotationType.DISAMB_GRANULARITY_ENTITY);
		TextFragment tf = new TextFragment();
		tf.append("Before ");
		tf.append(TagType.OPENING, GenericAnnotationType.GENERIC, anns);
		tf.append("the span");
		tf.append(TagType.CLOSING, GenericAnnotationType.GENERIC, anns);
		tf.append(" after.");
		assertEquals("Before <mrk mtype=\"x-its-disambiguation\"><g id=\"1\">the span</g></mrk> after.",
			fmt.setContent(tf).toString(true));
		
	}
	
	private TextFragment createTextFragment () {
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
	
	private TextFragment createMisOrderedTextFragment1 () {
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

	private TextFragment createMisOrderedTextFragment2 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.CLOSING, "b1", "</b1>");
		tf.append(TagType.OPENING, "b2", "<b2>");
		tf.append(TagType.CLOSING, "b2", "</b2>");
		tf.append(TagType.OPENING, "b3", "<b3>");
		return tf;
	}

	private TextFragment createMisOrderedComplexFragmentUnit () {
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
