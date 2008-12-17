/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.resource.tests;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import junit.framework.*;

public class TextFragmentTest extends TestCase {

	public void testConstructors () {
		TextFragment tf1 = new TextFragment();
		assertTrue(tf1.isEmpty());
		assertNotNull(tf1.toString());
		assertNotNull(tf1.getCodedText());
		tf1 = new TextFragment("text");
		assertFalse(tf1.isEmpty());
		TextFragment tf2 = new TextFragment(tf1);
		assertEquals(tf1.toString(), tf2.toString());
		assertNotSame(tf1, tf2);
	}
	
	public void testAppend () {
		TextFragment tf1 = new TextFragment();
		tf1.append('c');
		assertEquals(tf1.toString(), "c");
		tf1 = new TextFragment();
		tf1.append("string");
		assertEquals(tf1.toString(), "string");
		tf1.append('c');
		assertEquals(tf1.toString(), "stringc");
		TextFragment tf2 = new TextFragment();
		tf2.append(tf1);
		assertEquals(tf2.toString(), "stringc");
		assertNotSame(tf1, tf2);
		assertFalse(tf1.hasCode());
		// Test with in-line codes
		tf1 = new TextFragment();
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		assertTrue(tf1.hasCode());
		Code code = tf1.getCode(0);
		assertEquals(code.getData(), "<br/>");
	}
	
	public void testInsert () {
		TextFragment tf1 = new TextFragment();
		tf1.insert(0, new TextFragment("[ins1]"));
		assertEquals(tf1.toString(), "[ins1]");
		tf1.insert(4, new TextFragment("ertion"));
		assertEquals(tf1.toString(), "[insertion1]");
		tf1.insert(0, new TextFragment("<"));
		assertEquals(tf1.toString(), "<[insertion1]");
		tf1.insert(13, new TextFragment(">"));
		assertEquals(tf1.toString(), "<[insertion1]>");
		tf1.insert(-1, new TextFragment("$"));
		assertEquals(tf1.toString(), "<[insertion1]>$");
		// Test with in-line codes
		tf1 = new TextFragment();
		tf1.insert(0, new TextFragment("abc"));
		TextFragment tf2 = new TextFragment();
		tf2.append(TagType.PLACEHOLDER, "br", "<br/>");
		tf1.insert(1, tf2);
		Code code = tf1.getCode(0);
		assertEquals(code.getData(), "<br/>");
	}

}
