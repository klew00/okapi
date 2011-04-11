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
import static org.junit.Assert.assertTrue;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.CodeSimplifier;
import net.sf.okapi.common.resource.TextFragment;
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
	public void testCodeReduction1 () {		
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
	public void testCodeReduction2 () {
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
	public void testCodeReduction3 () {
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
	public void testCodeReduction4 () {
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
	public void testCodeReduction5 () {
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
	public void testCodeReduction6 () {
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
	public void testCodeReduction7 () {
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
	public void testCodeReduction8 () {
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
	public void testCodeReduction8_2 () {
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
	public void testCodeReduction8_3 () {
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
	public void testCodeReduction8_4 () {
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
	public void testCodeReduction8_5 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "a", "<a>");

		assertEquals("<b1/>", fmt.setContent(tf).toString());
	}
	
	@Test
	public void testCodeReduction8_6 () {
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
	public void testCodeReduction8_7 () {
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
	public void testCodeReduction8_8 () {
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
	public void testCodeReduction8_9 () {
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
	public void testCodeReduction9 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1", fmt.setContent(tf).toString());
		assertEquals("<x1/>", res[0]);
		assertEquals("<x2/>", res[1]);
	}

	@Test
	public void testCodeReduction10 () {
		TextFragment tf = new TextFragment("T1");
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T2");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<1/>T2", fmt.setContent(tf).toString());
		assertTrue(res[0] == null);
		assertEquals("<x2/>", res[1]);
	}

	@Test
	public void testCodeReduction11 () {
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

		String[] res = simplifier.simplifyAll(tf, true);
		assertEquals("T1<2>T2</2>T3", fmt.setContent(tf).toString());
		assertEquals("<x1/>", res[0]);
		assertEquals("<x3/>", res[1]);
	}
}
