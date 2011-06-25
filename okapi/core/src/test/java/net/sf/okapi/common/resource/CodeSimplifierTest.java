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

package net.sf.okapi.common.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Test;

public class CodeSimplifierTest {

	GenericContent fmt;
	CodeSimplifier simplifier;
	
	public CodeSimplifierTest () {
		simplifier = new CodeSimplifier();
		fmt = new GenericContent();
	}

	@Test
	public void testCodeReduction01 () {		
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.CLOSING, "a", "</a>");

			assertEquals("<1><2>T1</2></1>", fmt.setContent(tf).toString());
		// 1 + 2 -> 1
		// /2 + /1 -> /1
		simplifier.simplifyAll(tf, false);		
		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction02 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x", "<x/>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.CLOSING, "a", "</a>");
		
			assertEquals("<1><2/><3>T1</3></1>", fmt.setContent(tf).toString());
		// 1 + 2/ + 3 -> 1
		// /3 + /1 -> /1
		simplifier.simplifyAll(tf, false);		
		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction03 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "a", "</a>");
		
			assertEquals("<1><2/><3>T1</3><4/></1>", fmt.setContent(tf).toString());
		// 1 + 2/ + 3 -> 1
		// /3 + 4/ + /1 -> /1 
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction04 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");

			assertEquals("<1><2/><3>T1</3>T2</1>", fmt.setContent(tf).toString());
		// 1 + 2/ -> 1
		// 3 -> 2
		// /3 -> /2
		// /1 -> /1
		simplifier.simplifyAll(tf, false);		
		assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction05 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");

			assertEquals("<1><2><3>T1</3></2>T2</1>", fmt.setContent(tf).toString());
		// 1 -> 1
		// 2 + 3 -> 2
		// /3 + /2 -> /2
		// /1 -> /1
		simplifier.simplifyAll(tf, false);		
		assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction06 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");

			assertEquals("<1><2><3><4/>T1</3></2>T2</1><5/>", fmt.setContent(tf).toString());
		// 1 -> 1
		// 2 + 3 + 4/ -> 2
		// /3 + /2 -> /2
		// /1 + 5/ -> /1
		simplifier.simplifyAll(tf, false);		
		assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction07 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2/>T1<3><4><5/></4></3>T2</1><6/>", fmt.setContent(tf).toString());
		// 1 + 2/ -> 1
		// 3 + 4 + 5/ + /4 + /3 -> 2/
		// /1 + 6/ -> /1
		simplifier.simplifyAll(tf, false);				
		simplifier.simplifyEmptyOpeningClosing(tf);		
		assertEquals("<1>T1<2/>T2</1>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction08 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2/>T1<3><4><5/></4>T2</3>T3</1><6/>", fmt.setContent(tf).toString());
		// 1 + 2/ -> 1
		// 3 + 4 + 5/ + /4 -> 2
		// /3 -> /2
		// /1 + 6/ -> /1
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1<2>T2</2>T3</1>", fmt.setContent(tf).toString());
	}
			
	@Test
	public void testCodeReduction09 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.OPENING, "b", "<b>");		
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T2");		
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "c", "</c>");			
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2/>T1</1><3><4/>T2</3><5>T3</5><6/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1</1><3>T2</3><5>T3</5>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction10 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "a", "</a>");				
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "b", "</b>");		
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2/>T1</1><3><4><5/>T2</4>T3</3><6/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1</1><3><4>T2</4>T3</3>", fmt.setContent(tf).toString());
	}

	@Test
	public void testCodeReduction11 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "d", "<d>");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "d", "</d>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2><3/>T1</2><4><5><6/></5>T2</4>T3</1><7/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1><2>T1</2><4>T2</4>T3</1>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction12 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");

		assertEquals("<b1/>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction13 () {
		// Hanging opening code d
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "d", "<d>"); // no corresp. closing marker
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<b1/><b2/><3/>T1<4><5><6/></5>T2</4>T3<e1/><7/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1><b2/>T1<3>T2</3>T3</1>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction14 () {
		// Hanging closing code d
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");		
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.CLOSING, "d", "<d>"); // no corresp. opening marker
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1><2/>T1<3><4><5/></4>T2</3>T3</1><e7/><6/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1<2>T2</2>T3</1><e7/>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction15 () {
		// Hanging closing code d
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");		
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "d", "<d>"); // no corresp. opening marker
		tf.append(TagType.CLOSING, "a", "</a>");		
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<b1/><2/>T1<3><4><5/></4>T2</3>T3<e7/><e1/><6/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1<2>T2</2>T3<e7/></1>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction16 () {
		// Hanging closing code d
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.CLOSING, "d", "<d>"); // no corresp. opening marker
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<b1/><2/>T1<b3/><b4/><e7/><5/><e4/>T2<e3/>T3<e1/><6/>", fmt.setContent(tf).toString());
		
		simplifier.simplifyAll(tf, false);
		assertEquals("<1>T1<2><3><e7/></3>T2</2>T3</1>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction17 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		
			assertEquals("<1/>T1<2/>", fmt.setContent(tf).toString());
			
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<x1/>", res[0]);
		assertEquals("<x2/>", res[1]);
	}

	@Test
	public void testCodeReduction18 () {
		TextFragment tf = new TextFragment("T1");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T2");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		
			assertEquals("T1<1/>T2<2/>", fmt.setContent(tf).toString());
			
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1/>T2", fmt.setContent(tf).toString());
		assertTrue(res[0] == null);
		assertEquals("<x2/>", res[1]);
	}

	@Test
	public void testCodeReduction19 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T3");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");

			assertEquals("<1/>T1<2><3><4/></3>T2</2>T3<5/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>T3", fmt.setContent(tf).toString());
		assertEquals("<x1/>", res[0]);
		assertEquals("<x3/>", res[1]);
	}
	
	@Test
	public void testCodeReduction20 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T1");
		
			assertEquals("<1/><2/>T1", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<x1/><x2/>", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction21 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		
			assertEquals("<b1/><b2/>T1", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a><b>", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction22 () {
		// Spaces in-between codes
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(" ");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(" ");
		tf.append("T1");
		
			assertEquals("<1/> <2/> T1", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<x1/> <x2/> ", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction23 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.CLOSING, "a", "</a>");
		
			assertEquals("<1><2>T1</2></1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a><b>", res[0]);
		assertEquals("</b></a>", res[1]);
	}
	
	@Test
	public void testCodeReduction24 () {
		// Wrong sequence of closing tags (malformed tags)
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/><b2/>T1<e1/><e2/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a><b>", res[0]);
		assertEquals("</a></b>", res[1]);
	}
	
	@Test
	public void testCodeReduction25 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(" ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");		
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" ");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1> <2>T1</2> </1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a> <b>", res[0]);
		assertEquals("</b> </a>", res[1]);
	}
	
	@Test
	public void testCodeReduction26 () {
		// Wrong sequence of closing tags (malformed tags), nothing is removed
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(" ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(" ");
		tf.append("T1");
		tf.append(" ");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(" ");
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/> <b2/> T1 <e1/> <e2/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a> <b> ", res[0]);
		assertEquals(" </a> </b>", res[1]);
	}
	
	@Test
	public void testCodeReduction27 () {
		// Spaces in-between codes
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(" ");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(" ");
		tf.append("T1");
		
			assertEquals("<1/> <2/> T1", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<x1/> <x2/> ", res[0]);
	}

	@Test
	public void testCodeReduction28 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(" ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");		
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/> <2>T1</2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a> <b>", res[0]);
		assertEquals("</b>", res[1]);
	}
	
	@Test
	public void testCodeReduction29 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");		
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/><2>T1</2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a><b>", res[0]);
		assertEquals("</b>", res[1]);
	}
	
	@Test
	public void testCodeReduction30 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T2");		
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/>T1<2>T2</2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>", fmt.setContent(tf).toString());
		assertEquals("<a>", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction31 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");		
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1>T1</1>T2<e2/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("<1>T1</1>T2", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals("</a>", res[1]);
	}
	
	@Test
	public void testCodeReduction32 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(" ");
		tf.append("T1");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T2");		
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/> T1<2>T2</2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>", fmt.setContent(tf).toString());
		assertEquals("<a> ", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction33 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");		
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(" ");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1>T1</1>T2 <e2/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("<1>T1</1>T2", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(" </a>", res[1]);
	}
	
	@Test
	public void testCodeReduction34 () {
		TextFragment tf = new TextFragment();
		tf.append("T1");
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append("T3");		
						
			assertEquals("T1<1>T2</1>T3", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1>T2</1>T3", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction35 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T1");		
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/>T1<2></2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a>", res[0]);
		assertEquals("<b></b>", res[1]);
	}
	
	@Test
	public void testCodeReduction36 () {
		// Adjacent codes are not removed, because there's a space in-between
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T1");		
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(" ");
		tf.append(TagType.CLOSING, "b", "</b>");
				
			assertEquals("<b1/>T1<2> </2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<a>", res[0]);
		assertEquals("<b> </b>", res[1]);
	}
	
	@Test
	public void testCodeReduction37 () {
		// String is not trimmed by simplification
		TextFragment tf = new TextFragment();
		tf.append("   ");
		tf.append("T1");		
		tf.append("   ");
				
			assertEquals("   T1   ", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("   T1   ", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction38 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T1");		
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");
		tf.append(TagType.PLACEHOLDER, "x4", "<x4/>");
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1/><2/>T1<3/><4/><5>T2</5>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>", fmt.setContent(tf).toString());
		assertEquals("<x1/><x2/>", res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction39 () {
		TextFragment tf = new TextFragment();		
		tf.append("T1");		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
				
			assertEquals("T1<1/><2/><3>T2</3>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1>T2</1>", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction40 () {
		TextFragment tf = new TextFragment();		
		tf.append("T1");		
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
				
			assertEquals("T1<1>T2</1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1>T2</1>", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction41 () {
		TextFragment tf = new TextFragment();		
		tf.append("T1");		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
				
			assertEquals("T1<1/><2>T2</2>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1>T2</1>", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction42 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T1");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T3");
		tf.append(TagType.CLOSING, "c", "</c>");		
				
			assertEquals("<1>T1</1><2>T2</2><3>T3</3>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("<1>T1</1><3>T2</3><5>T3</5>", fmt.setContent(tf).toString());
		assertEquals(null, res[0]);
		assertEquals(null, res[1]);
	}
	
	@Test
	public void testCodeReduction43 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T1");		
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");
		tf.append(TagType.PLACEHOLDER, "x4", "<x4/>");
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.PLACEHOLDER, "x5", "<x5/>");
		tf.append(TagType.PLACEHOLDER, "x6", "<x6/>");
				
			assertEquals("<1/><2/>T1<3/><4/><5>T2</5><e8/><6/><7/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>", fmt.setContent(tf).toString());
		assertEquals("<x1/><x2/>", res[0]);
		assertEquals("</b><x5/><x6/>", res[1]);
	}
	
	@Test
	public void testCodeReduction44 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("   ");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append("T1");		
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");
		tf.append(TagType.PLACEHOLDER, "x4", "<x4/>");
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append("T2");
		tf.append("   ");
		tf.append(TagType.CLOSING, "a", "</a>");
		tf.append("   ");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.PLACEHOLDER, "x5", "<x5/>");
		tf.append("   ");
		tf.append(TagType.PLACEHOLDER, "x6", "<x6/>");
				
			assertEquals("<1/>   <2/>T1<3/><4/><5>T2   </5>   <e8/><6/>   <7/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2   </2>", fmt.setContent(tf).toString());
		assertEquals("<x1/>   <x2/>", res[0]);
		assertEquals("   </b><x5/>   <x6/>", res[1]);
	}
	
	@Test
	public void testCodeReduction45 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(" ");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("T1");
		tf.append("   ");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("   ");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1><2> <3>T1   <4/>T2</3>   </2></1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1   <2/>T2", fmt.setContent(tf).toString());
		assertEquals("<a><b> <c>", res[0]);
		assertEquals("</c>   </b></a>", res[1]);
	}
	
	@Test
	public void testCodeReduction46 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");		
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("T1");				
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("<2>T1</2>T2", fmt.setContent(tf).toString());
		assertEquals("<a>", res[0]);
		assertEquals("</a>", res[1]);
	}
	
	@Test
	public void testCodeReduction47 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		tf.append(TagType.PLACEHOLDER, "x3", "<x3/>");
		tf.append(TagType.PLACEHOLDER, "x4", "<x4/>");
				
			assertEquals("<1/><2/><3/><4/>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, false);
		assertEquals("<1/>", fmt.setContent(tf).toString());
		assertNull(res);
		assertEquals(1, tf.getCodes().size());
		assertEquals("x1", tf.getCode(0).getType());
	}
	
	@Test
	public void testCodeReduction48 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append(" ");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
				
			assertEquals("<1/> <2/>", fmt.setContent(tf).toString());
			assertEquals("<x1/> <x2/>", tf.toText());
		
		String[] res = simplifier.simplifyAll(tf, false);
		assertEquals("<1/>", fmt.setContent(tf).toString());
		assertNull(res);
		assertEquals(1, tf.getCodes().size());
		assertEquals("x1", tf.getCode(0).getType());
	}
	
	@Test
	public void testCodeReduction49 () {
		TextFragment tf = new TextFragment();		
		tf.append(TagType.OPENING, "a", "<a>");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append(" ");
		tf.append(TagType.OPENING, "c", "<c>");
		tf.append("   ");
		tf.append("T1");
		tf.append("   ");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T2");
		tf.append(TagType.CLOSING, "c", "</c>");
		tf.append("   ");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(TagType.CLOSING, "a", "</a>");
				
			assertEquals("<1><2> <3>   T1   <4/>T2</3>   </2></1>", fmt.setContent(tf).toString());
		
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1   <2/>T2", fmt.setContent(tf).toString());
		assertEquals("<a><b> <c>   ", res[0]);
		assertEquals("</c>   </b></a>", res[1]);
	}

	@Test
	public void testCodeReduction50 () {
		TextFragment tf = new TextFragment(".");
		tf.append(TagType.PLACEHOLDER, "x1", "<x/>");
		tf.append(TagType.PLACEHOLDER, "x2", "<x/>");
		assertEquals(".<1/><2/>", fmt.setContent(tf).toString());
		simplifier.simplifyAll(tf, true);		
		assertEquals(".", fmt.setContent(tf).toString());
	}

}
