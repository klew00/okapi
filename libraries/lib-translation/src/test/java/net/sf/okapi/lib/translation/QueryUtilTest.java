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
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryUtilTest {

	private QueryUtil qu;
	
	@Before
	public void setUp() {
		qu = new QueryUtil();
	}

	@Test
	public void testEmptyFragment () {
		TextFragment tf = new TextFragment();
		assertEquals("", qu.separateCodesFromText(tf));
		assertEquals("", qu.createNewFragmentWithCodes("").toString());
	}
	
	@Test
	public void testFragmentWithoutCodes () {
		TextFragment tf = new TextFragment("text");
		assertEquals("text", qu.separateCodesFromText(tf));
		assertEquals("new", qu.createNewFragmentWithCodes("new").toString());
	}
	
	@Test
	public void testFragmentWithCodes () {
		TextFragment tf = makeFragment();
		assertEquals("a & < > \" \' <b>bold</b> t <br/> z", tf.toString());
		assertEquals("a & < > \" \' bold t  z", qu.separateCodesFromText(tf));
		assertEquals("new<b></b><br/>", qu.createNewFragmentWithCodes("new").toString());
	}
	
	@Test
	public void testToHTML () {
		TextFragment tf = makeFragment();
		String htmlText = qu.toCodedHTML(tf);
		assertEquals("a &amp; &lt; > \" \' <s id='1'>bold</s> t <br id='2'/> z", htmlText);
	}
	
	@Test
	public void testFromSameHTML () {
		TextFragment tf = makeFragment();
		String htmlText = qu.toCodedHTML(tf);
		String codedText = qu.fromCodedHTML(htmlText, tf);
		TextFragment resFrag = new TextFragment(codedText, tf.getCodes());
		assertTrue(resFrag.compareTo(tf, false) == 0);
		assertTrue(resFrag.compareTo(tf, true) == 0);
	}
	
	@Test
	public void testFromModifiedHTML () {
		TextFragment tf = makeFragment();
		String htmlText = qu.toCodedHTML(tf);
		String codedText = qu.fromCodedHTML(htmlText, tf);
		codedText = codedText.toUpperCase();
		TextFragment resFrag = new TextFragment(codedText, tf.getCodes());
		assertEquals("A & < > \" \' <b>BOLD</b> T <br/> Z", resFrag.toString());
		
	}
	
	private TextFragment makeFragment () {
		TextFragment tf = new TextFragment("a & < > \" \' ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" t ");
		tf.append(TagType.PLACEHOLDER, null, "<br/>");
		tf.append(" z");
		return tf;
	}
}
