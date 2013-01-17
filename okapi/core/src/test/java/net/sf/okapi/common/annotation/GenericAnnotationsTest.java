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

package net.sf.okapi.common.annotation;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.AnnotatedSpan;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextUnit;

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
	public void testConstructors () {
		GenericAnnotations anns = new GenericAnnotations();
		assertEquals(null, anns.getData());
		assertFalse(anns.hasAnnotation("type1"));
		
		anns = new GenericAnnotations(new GenericAnnotation("type1"));
		assertEquals(null, anns.getData());
		assertTrue(anns.hasAnnotation("type1"));
		String storage = anns.toString();
		anns = new GenericAnnotations(storage);
		assertEquals(null, anns.getData());
		assertTrue(anns.hasAnnotation("type1"));
	}

	@Test
	public void testToFromString () {
		GenericAnnotations anns1 = new GenericAnnotations(new GenericAnnotation("type1", "field1", "value1"));
		anns1.add("type2-no-fields");
		anns1.setData("dataText");
		String storage = anns1.toString();
		GenericAnnotations anns2 = new GenericAnnotations(storage);
		assertEquals("dataText", anns2.getData());
		assertEquals(2, anns2.getAllAnnotations().size());
		GenericAnnotation ga = anns2.getFirstAnnotation("type1");
		assertEquals("value1", ga.getString("field1"));
		ga = anns2.getFirstAnnotation("type2-no-fields");
		assertNotNull(ga);
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
	public void testAddAnnotationsOnTU () {
		GenericAnnotations anns1 = new GenericAnnotations();
		GenericAnnotation ann11 = anns1.add("type1");
		ann11.setString("name1", "v1");
		GenericAnnotation ann12 = anns1.add("type1");
		ann12.setString("name2", "v2-not-over");
		ITextUnit tu = new TextUnit("id");
		GenericAnnotations.addAnnotations(tu, anns1);

		GenericAnnotations res = tu.getAnnotation(GenericAnnotations.class);
		assertNotNull(res);
		assertEquals(res, anns1);
		
		GenericAnnotations anns2 = new GenericAnnotations();
		GenericAnnotation ann21 = anns2.add("type1");
		ann21.setString("name3", "v3");
		GenericAnnotation ann22 = anns2.add("type1");
		ann22.setString("name2", "another name2");
		
		GenericAnnotations.addAnnotations(tu, anns2);
		res = tu.getAnnotation(GenericAnnotations.class);
		assertNotNull(res);
		assertEquals(res, anns1);
		List<GenericAnnotation> list = res.getAnnotations("type1");
		assertEquals(4, list.size());
		GenericAnnotation ann = list.get(1);
		assertEquals("v2-not-over", ann.getString("name2"));
	}

	@Test
	public void testAddAnnotationsOnTC () {
		GenericAnnotations anns1 = new GenericAnnotations();
		GenericAnnotation ann11 = anns1.add("type1");
		ann11.setString("name1", "v1");
		GenericAnnotation ann12 = anns1.add("type1");
		ann12.setString("name2", "v2-not-over");
		TextContainer tc = new TextContainer();
		GenericAnnotations.addAnnotations(tc, anns1);

		GenericAnnotations res = tc.getAnnotation(GenericAnnotations.class);
		assertNotNull(res);
		assertEquals(res, anns1);
		
		GenericAnnotations anns2 = new GenericAnnotations();
		GenericAnnotation ann21 = anns2.add("type1");
		ann21.setString("name3", "v3");
		GenericAnnotation ann22 = anns2.add("type1");
		ann22.setString("name2", "another name2");
		
		GenericAnnotations.addAnnotations(tc, anns2);
		res = tc.getAnnotation(GenericAnnotations.class);
		assertNotNull(res);
		assertEquals(res, anns1);
		List<GenericAnnotation> list = res.getAnnotations("type1");
		assertEquals(4, list.size());
		GenericAnnotation ann = list.get(1);
		assertEquals("v2-not-over", ann.getString("name2"));
	}

	@Test
	public void testAddAnnotationsOnCode () {
		GenericAnnotations anns1 = new GenericAnnotations();
		GenericAnnotation ann11 = anns1.add("type1");
		ann11.setString("name1", "v1");
		GenericAnnotation ann12 = anns1.add("type1");
		ann12.setString("name2", "v2-not-over");
		Code code = new Code(TagType.PLACEHOLDER, "z");
		GenericAnnotations.addAnnotations(code, anns1);

		GenericAnnotations res = (GenericAnnotations)code.getAnnotation(GenericAnnotationType.GENERIC);
		assertNotNull(res);
		assertEquals(res, anns1);
		
		GenericAnnotations anns2 = new GenericAnnotations();
		GenericAnnotation ann21 = anns2.add("type1");
		ann21.setString("name3", "v3");
		GenericAnnotation ann22 = anns2.add("type1");
		ann22.setString("name2", "another name2");
		
		GenericAnnotations.addAnnotations(code, anns2);
		res = (GenericAnnotations)code.getAnnotation(GenericAnnotationType.GENERIC);
		assertNotNull(res);
		assertEquals(res, anns1);
		List<GenericAnnotation> list = res.getAnnotations("type1");
		assertEquals(4, list.size());
		GenericAnnotation ann = list.get(1);
		assertEquals("v2-not-over", ann.getString("name2"));
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
	public void testAddAll () {
		// First set
		GenericAnnotations anns1 = new GenericAnnotations();
		GenericAnnotation ann1 = anns1.add("A1-type1");
		ann1.setString("f1", "v1");
		ann1 = anns1.add("A1-type2");
		ann1.setString("f1", "v1");
		// second set
		GenericAnnotations anns2 = new GenericAnnotations();
		GenericAnnotation ann2 = anns2.add("A1-type1");
		ann2.setString("f1", "v1");
		ann2 = anns2.add("A1-type2");
		ann2.setString("f1", "v1");
		
		anns2.addAll(anns1);
		assertEquals(4, anns2.size());
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
		ann.setDouble("lqiSeverity", 50.0);
		ann.setBoolean("lqiEnabled", true);
		
		ann = anns.getAnnotations("its-lqi").get(0);
		assertEquals(50.0, ann.getDouble("lqiSeverity"), 0);
		assertEquals("typographical", ann.getString("lqiType"));
		assertEquals("Sentence without capitalization", ann.getString("lqiComment"));
		assertEquals("http://example.org/qaModel/v13", ann.getString("lqiProfileRef"));
		assertEquals(true, ann.getBoolean("lqiEnabled"));
	}
}
