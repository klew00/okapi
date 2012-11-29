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

import java.util.List;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.AnnotatedSpan;
import net.sf.okapi.common.resource.TextFragment;

import org.junit.Test;

public class GenericAnnotationsTest {

	@Test
	public void testAddAndRemove () {
		GenericAnnotations anns = new GenericAnnotations();
		assertFalse(anns.hasAnnotation("type1"));
		GenericAnnotation ann = anns.add("type1");
		assertTrue(anns.hasAnnotation("type1"));
		anns.remove(ann);
		assertFalse(anns.hasAnnotation("type1"));
	}

	@Test
	public void testSeveral () {
		GenericAnnotations anns = new GenericAnnotations();
		GenericAnnotation ann1 = anns.add("type1");
		ann1.setString("name", "v1");
		GenericAnnotation ann2 = anns.add("type1");
		ann2.setString("name", "v2");
		List<GenericAnnotation> list = anns.getAnnotations("type1");
		assertEquals(2, list.size());
		assertEquals("v2", list.get(1).getString("name"));
	}

	@Test
	public void testInContent () {
		GenericAnnotations anns = new GenericAnnotations();
		TextFragment tf = new TextFragment("This is a test.");
		                                 // 012345678901234
		tf.annotate(10, 14, "lqi", anns);
		List<AnnotatedSpan> list = tf.getAnnotatedSpans("lqi");
		assertEquals(1, list.size());
		assertEquals("test", list.get(0).span.toString());
	}

	@Test
	public void testStorage () {
		GenericAnnotations anns1 = new GenericAnnotations();
		GenericAnnotation ann = anns1.add("type1");
		ann.setString("fs1", "value1");
		ann.setBoolean("fb1", true);
		ann = anns1.add("type1");
		ann.setString("fs1bis", "value1bis");
		ann.setBoolean("fb1bis", false);
		ann = anns1.add("type2");
		ann.setString("fs2", "value2");
		ann.setBoolean("fb2", false);
		anns1.add("typeNoData"); // Annotation with no data
		String buf = anns1.toString();
		
		GenericAnnotations anns2 = new GenericAnnotations(buf);
		List<GenericAnnotation> list = anns2.getAnnotations("type1");
		assertEquals("value1", list.get(0).getString("fs1"));
		assertEquals(true, list.get(0).getBoolean("fb1"));
		assertEquals("value1bis", list.get(1).getString("fs1bis"));
		assertEquals(false, list.get(1).getBoolean("fb1bis"));
		assertEquals("value2", anns2.getAnnotations("type2").get(0).getString("fs2"));
		assertEquals(false, anns2.getAnnotations("type2").get(0).getBoolean("fb2"));
		assertEquals(1, anns2.getAnnotations("typeNoData").size());
	}

	@Test
	public void testITS_LQI () {
		GenericAnnotations anns = new GenericAnnotations();
		GenericAnnotation ann = anns.add("its-lqi");
		ann.setString("lqiType", "typographical");
		ann.setString("lqiComment", "Sentence without capitalization");
		ann.setString("lqiProfileRef", "http://example.org/qaModel/v13");
		ann.setFloat("lqiSeverity", 50.0f);
		ann.setBoolean("lqiEnabled", true);
		
		ann = anns.getAnnotations("its-lqi").get(0);
		assertEquals(50.0, ann.getFloat("lqiSeverity"), 0);
		assertEquals("typographical", ann.getString("lqiType"));
		assertEquals("Sentence without capitalization", ann.getString("lqiComment"));
		assertEquals("http://example.org/qaModel/v13", ann.getString("lqiProfileRef"));
		assertEquals(true, ann.getBoolean("lqiEnabled"));
	}
}