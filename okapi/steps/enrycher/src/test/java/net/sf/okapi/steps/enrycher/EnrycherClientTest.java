package net.sf.okapi.steps.enrycher;

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.steps.enrycher.EnrycherStep.Insertion;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnrycherClientTest {

	@Test
	public void parametersTest () {
		EnrycherClient ec = new EnrycherClient();
		assertNotNull(ec.getParameters());
	}

	// Comment out the test annotation before committing
	/*@Test
	public void manual () {
		EnrycherClient ec = new EnrycherClient();
		ec.setLocale(LocaleId.ENGLISH);
		
		// Detection (use the s element to avoid the change in the p element)
		String res = ec.processContent("<p id=\"1\"><s>CMS</s></p>");
		System.out.println(res);
		System.out.println("<p\n id=\"1\"><s><span\n its-disambig-ident-ref=\"http://dbpedia.org/resource/CMS\"\n its-disambig-granularity=\"entity\"\n its-disambig-class-ref=\"http://schema.org/Thing\">CMS</span></s></p>\n\n");
		assertEquals("<p\n id=\"1\"><s><span\n its-disambig-ident-ref=\"http://dbpedia.org/resource/CMS\"\n its-disambig-granularity=\"entity\"\n its-disambig-class-ref=\"http://schema.org/Thing\">CMS</span></s></p>\n\n", res);

		// Nothing to annotate
		res = ec.processContent("<p id=\"1\"><s>something</s></p>");
		System.out.println(res);
		System.out.println("<p\n id=\"1\"><s>something</s></p>\n\n");
		assertEquals("<p\n id=\"1\"><s>something</s></p>\n\n", res);
	}*/
	
	@Test
	public void synchToFirstInsertionPosition() {
		TextFragment tf = new TextFragment("<p>hello sweet Paris summer</p>");
		Source source = new Source("<p>hello <span its-disambig-ident-ref=\"http://purl.org/vocabularies/princeton/wn30/synset-sweet-adjective-1.rdf\" its-disambig-granularity=\"lexicalConcept\">sweet</span> <span its-disambig-ident-ref=\"http://dbpedia.org/resource/Paris\" its-disambig-granularity=\"entity\" its-disambig-class-ref=\"http://schema.org/Place\">Paris</span> summer</p>");

		assertEquals(9, new EnrycherStep().getInsertionPosition(0,tf.toString(), 0, source.toString(), 9));
	}

	@Test
	public void getItsSpans() {
		Source source = new Source("hello <span its-disambig-ident-ref=\"http://purl.org/vocabularies/princeton/wn30/synset-sweet-adjective-1.rdf\" its-disambig-granularity=\"lexicalConcept\">sweet</span> <span its-disambig-ident-ref=\"http://dbpedia.org/resource/Paris\" its-disambig-granularity=\"entity\" its-disambig-class-ref=\"http://schema.org/Place\">Paris</span> summer");
		
		List<Element> itsSpans = new EnrycherStep().getItsElements(source);
		
		//--check no spans
		assertEquals(2, itsSpans.size());
		
		//--check correct open and closing pos
		assertEquals(6, itsSpans.get(0).getBegin());		
		assertEquals(164, itsSpans.get(0).getEnd());		
		assertEquals(165, itsSpans.get(1).getBegin());		
		assertEquals(325, itsSpans.get(1).getEnd());
	}
	
	@Test
	public void genericAnnotations() {
		TextFragment tf = new TextFragment("hello sweet Paris summer");		
		Source source = new Source("hello <span its-disambig-ident-ref=\"http://purl.org/vocabularies/princeton/wn30/synset-sweet-adjective-1.rdf\" its-disambig-granularity=\"lexicalConcept\">sweet</span> <span its-disambig-ident-ref=\"http://dbpedia.org/resource/Paris\" its-disambig-granularity=\"entity\" its-disambig-class-ref=\"http://schema.org/Place\">Paris</span> summer");
		
		EnrycherStep dummy = new EnrycherStep();
		
		List<Element> itsSpans = dummy.getItsElements(source);
		List<Insertion> insertions = dummy.getInsertions(tf.toString(), source.toString(), itsSpans);

		assertEquals(2, insertions.size());
		
		Insertion ins = insertions.get(0);
		List<GenericAnnotation> gas = ins.genAnn.getAnnotations(GenericAnnotationType.DISAMB);
		GenericAnnotation ga = gas.get(0);
		
		//--check first GenericAnnotation
		assertEquals("REF:http://purl.org/vocabularies/princeton/wn30/synset-sweet-adjective-1.rdf", ga.getString(GenericAnnotationType.DISAMB_IDENT));
		assertEquals("lexicalConcept", ga.getString(GenericAnnotationType.DISAMB_GRANULARITY));
		
		ins = insertions.get(1);
		gas = ins.genAnn.getAnnotations(GenericAnnotationType.DISAMB);
		ga = gas.get(0);
		
		//--check second GenericAnnotation
		assertEquals("REF:http://dbpedia.org/resource/Paris", ga.getString(GenericAnnotationType.DISAMB_IDENT));
		assertEquals("entity", ga.getString(GenericAnnotationType.DISAMB_GRANULARITY));
		assertEquals("REF:http://schema.org/Place", ga.getString(GenericAnnotationType.DISAMB_CLASS));

		//--TextFragment before
		assertEquals("hello sweet Paris summer", tf.toString());

		dummy.annotateFragment(tf, insertions);
		
		//--TextFragment after
		assertEquals("hello "+(char)TextFragment.MARKER_OPENING + TextFragment.toChar(0) + "sweet" 
			+ (char)TextFragment.MARKER_CLOSING+TextFragment.toChar(1) +" " + (char)TextFragment.MARKER_OPENING + TextFragment.toChar(2) 
			+ "Paris" + (char)TextFragment.MARKER_CLOSING + "" + TextFragment.toChar(3) + " summer", tf.toString());
	}

	//TODO: In progress
	/*@Test
	public void jerichoTest() {

		TextFragment tf1 = new TextFragment("hello");
		tf1.append(new Code(TagType.PLACEHOLDER, "dummy"));
		tf1.append(" sweet Paris ");
		tf1.append(new Code(TagType.PLACEHOLDER, "dummy2"));
		tf1.append("summer");

		EnrycherStep es = new EnrycherStep();
		System.out.println(es.toCodedHTML(tf1));

		TextFragment tf = new TextFragment("hello sweet Paris summer");
		String source_org = new String("hello sweet Paris summer");
		Source source = new Source("hello <span its-disambig-ident-ref=\"http://purl.org/vocabularies/princeton/wn30/synset-sweet-adjective-1.rdf\" its-disambig-granularity=\"lexicalConcept\">sweet</span> <span its-disambig-ident-ref=\"http://dbpedia.org/resource/Paris\" its-disambig-granularity=\"entity\" its-disambig-class-ref=\"http://schema.org/Place\">Paris</span> summer");

		List<Element> spans = source.getAllElements("span");

		int offset=0;

		for (Element span : spans) {

			int inner = span.getEndTag().getBegin() - span.getStartTag().getEnd();
			System.out.println("enclosed text: " + inner);

			System.out.println(source_org.substring(span.getStartTag().getBegin()-offset, span.getStartTag().getBegin()+ inner-offset));
			System.out.println("annotate from: " + (span.getStartTag().getBegin()-offset) + "\tto: " + (span.getStartTag().getBegin()+ inner-offset));
			tf.annotate(span.getStartTag().getBegin()-offset, span.getStartTag().getBegin()+ inner-offset, "test", new InlineAnnotation("first annotation"));

			System.out.println(tf.getCodedText());
			System.out.println(tf.getCodedText().length());

			int length = span.getStartTag().getEnd()-span.getStartTag().getBegin();

			System.out.println(span.getTextExtractor().toString());   

			//offset is the length of the starttag + length of the end tag
			offset = (span.getStartTag().getEnd()-span.getStartTag().getBegin()) + (span.getEndTag().getEnd()-span.getEndTag().getBegin());
			offset += -4;

			System.out.println("offset: " + offset);

		}
	}*/
	
	@Test
	public void testToCodedHTML() {
		TextFragment tf = new TextFragment("hello <br />sweet Paris summer");
		
		EnrycherStep dummy = new EnrycherStep();
		System.out.println(dummy.toCodedHTML(tf));
	}
}
