/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import static org.junit.Assert.*;
import net.sf.okapi.common.annotation.GenericAnnotation;

import org.junit.Test;

public class GenericAnnotationTest {

	@Test
	public void testString () {
		GenericAnnotation ann = new GenericAnnotation("type1");
		assertEquals("type1", ann.getType());
		ann.setString("f1", "v1");
		assertEquals("v1", ann.getString("f1"));
		ann.setString("f1", "v2");
		assertEquals("v2", ann.getString("f1"));
	}

	@Test
	public void testBoolean () {
		GenericAnnotation ann = new GenericAnnotation("type1");
		assertEquals("type1", ann.getType());
		ann.setBoolean("f1", true);
		assertEquals(true, ann.getBoolean("f1"));
		ann.setBoolean("f1", false);
		assertEquals(false, ann.getBoolean("f1"));
	}

	@Test
	public void testStorage () {
		GenericAnnotation ann1 = new GenericAnnotation("type1");
		ann1.setBoolean("fb1", true);
		ann1.setString("fs1", "string1");
		ann1.setBoolean("fb2", false);
		ann1.setString("fs2", "");
		ann1.setString("fs3", " \t ");
		String buf = ann1.toString();
		
		GenericAnnotation ann2 = new GenericAnnotation("tmp");
		ann2.fromString(buf);
		assertEquals("type1", ann2.getType());
		assertEquals(true, ann2.getBoolean("fb1"));
		assertEquals(false, ann2.getBoolean("fb2"));
		assertEquals("string1", ann2.getString("fs1"));
		assertEquals("", ann2.getString("fs2"));
		assertEquals(" \t ", ann2.getString("fs3"));
	}

	@Test
	public void testClone () {
		GenericAnnotation ann1 = new GenericAnnotation("type1");
		ann1.setString("f1", "v1");
		ann1.setBoolean("f2", true);
		
		GenericAnnotation ann2 = ann1.clone();
		assertEquals(ann2.getType(), ann1.getType());
		assertFalse(ann2.getType()==ann1.getType());
		assertEquals(ann2.getString("f1"), ann1.getString("f1"));
		assertFalse(ann2.getString("f1")==ann1.getString("f1"));
		assertEquals(ann2.getBoolean("f2"), ann1.getBoolean("f2"));
	}
}
