package net.sf.okapi.lib.xliff;

import static org.junit.Assert.*;

import org.junit.Test;
import org.oasisopen.xliff.v2.IAnnotation;

public class AnnotationTest {

	@Test
	public void testSimple () {
		IAnnotation anno = new Annotation("type1");
		assertEquals("type1", anno.getType());
		assertEquals(true, anno.getTranslate());
	}
	
	@Test
	public void testEquals () {
		assertTrue(new Annotation("t1").equals(new Annotation("t1")));
		assertFalse(new Annotation("t1").equals(new Annotation("t2")));
	}

}
