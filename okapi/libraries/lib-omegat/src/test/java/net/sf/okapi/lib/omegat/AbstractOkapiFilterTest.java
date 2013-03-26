package net.sf.okapi.lib.omegat;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractOkapiFilterTest {

	XLIFFFilter filter;
	
	@Before
	public void setUp() {
		filter = new XLIFFFilter();
	}

	@Test
	public void testComments () {
		ITextUnit tu = new TextUnit("i1");
		TextFragment tf = new TextFragment("a b cd e");
		GenericAnnotations anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.TERM,
				GenericAnnotationType.TERM_INFO, "info",
				GenericAnnotationType.TERM_CONFIDENCE, 12.34));
		anns.add(new GenericAnnotation(GenericAnnotationType.TA,
				GenericAnnotationType.TA_CLASS, "REF:classURI"));
		tf.annotate(2, 3, GenericAnnotationType.GENERIC, anns);
		
		tf.annotate(8, 10, GenericAnnotationType.GENERIC, new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.TA,
				GenericAnnotationType.TA_CLASS, "REF:classURI",
				GenericAnnotationType.TA_IDENT, "REF:identURI")));
		tu.setSource(new TextContainer(tf));
		
		String comments = filter.processComments(tu, tu.getSource().getSegments().get(0), null);
		assertEquals("Term: 'b' info Confidence=12.34\n"
			+ "TA: 'b' Class:REF:classURI\n"
			+ "TA: 'cd' Class:REF:classURI Ident:REF:identURI", comments);
	}

}
