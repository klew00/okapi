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

package net.sf.okapi.filters.idml.tests;

import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.filters.idml.Simplifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SimplifierTest {

	Simplifier simp;
	GenericContent fmt;
	
	public SimplifierTest () {
		simp = new Simplifier();
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
		simp.simplify(tf);
//		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
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
		simp.simplify(tf);
//		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
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
		simp.simplify(tf);
//		assertEquals("<1>T1</1>", fmt.setContent(tf).toString());
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
		simp.simplify(tf);
//		assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
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
		simp.simplify(tf);
//		assertEquals("<1><2>T1</2>T2</1>", fmt.setContent(tf).toString());
	}

}
