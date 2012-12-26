package net.sf.okapi.filters.its.html5;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.TermsAnnotation;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HTML5FilterTest {

	private HTML5Filter filter;
	private GenericContent fmt;
	private String root;
	private LocaleId locEN = LocaleId.fromString("en");

	@Before
	public void setUp() {
		filter = new HTML5Filter();
		fmt = new GenericContent();
		root = TestUtil.getParentDir(this.getClass(), "/test01.html");
	}

	@Test
	public void testSimpleRead () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text in <span>bold</span>."
			+ "<p>Text in <i>italics</i>."
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("Title", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text in <1>bold</1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertNotNull(tu);
		assertEquals("Text in <1>italics</1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testTranslateLocally () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text in <span translate=no>code</span>.</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text in <1><2/></1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testTranslateOverridenByRule () {
		ArrayList<Event> list = getEvents(new File(root+"test01.html"));
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu); // Para should be first because the rules associated to test1.html override meta[keywords] as translatable
		assertEquals("This is a <1>motherboard</1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testTranslateAttribute () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text <img src=test.png alt=Text>.</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertNotNull(tu);
		assertEquals("Text <1/>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testPreserveSpace () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title></head><body>"
				+ "<pre> text  \t\t <b>  etc.  </b>\t </pre>"
				+ "<p> text  \t\t <b>  etc.  </b>\t </p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals(" text  \t\t <1>  etc.  </1>\t ", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals(" text <1> etc. </1> ", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testDomain () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title>"
			+ "<meta name='dcterms.subject' content='domA, dom2, domB'>"
			+ "<meta name='keywords' content='dom1, dom2, dom3'>"
			+ "</head>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("Title", fmt.setContent(tu.getSource().getFirstContent()).toString());
		GenericAnnotation ga = tu.getAnnotation(GenericAnnotations.class).getFirstAnnotation(GenericAnnotationType.DOMAIN);
		assertEquals("domA, dom2, domB, dom1, dom3", ga.getString(GenericAnnotationType.DOMAIN_LIST));		
	}

	@Test
	public void testRulesInScripts () {
		String snippet = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=utf-8><title>Title</title>"
			+ "<script type=application/its+xml>"
			+ "<its:rules xmlns:its='http://www.w3.org/2005/11/its' version='2.0' "
			+ "xmlns:h='http://www.w3.org/1999/xhtml'>"
			+ "<its:translateRule selector='//h:title' translate='no'/>"
			+ "</its:rules>"
			+ "</script>"
			+ "</head><body>"
			+ "<p>text</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testLocaleFilterLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p its-locale-filter-list='de'>Text 1</p>"
			+ "<p its-locale-filter-list='FR'>Text 2</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text 2", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testIdValueLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p id='n1'>Text 1</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("n1", tu.getName());
	}

	@Test
	public void testAllowedChars () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p its-allowed-characters='[a-z]'>text</p>"
			+ "<pre>text</pre>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		GenericAnnotation ga = tu.getAnnotation(GenericAnnotations.class).getFirstAnnotation(GenericAnnotationType.ALLOWEDCHARS);
		assertEquals("[a-z]", ga.getString(GenericAnnotationType.ALLOWEDCHARS_PATTERN));
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals(null, tu.getAnnotation(GenericAnnotations.class));
	}
	
	@Test
	public void testStorageSizeLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<ul>"
			+ "<li its-storage-size='10' its-storage-encoding='UTF-8'>1234567890-Extra</li>"
			+ "<li its-storage-size='22' its-storage-encoding='ISO-8859-1'>abcdefghij-Extra</li>"
			+ "</ul>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("10\tUTF-8\tlf", tu.getProperty(Property.ITS_STORAGESIZE).getValue());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals("22\tISO-8859-1\tlf", tu.getProperty(Property.ITS_STORAGESIZE).getValue());
	}
	
	@Test
	public void testTerminologyLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<dl><dt its-term=yes its-term-info-ref='some URI'>motherboard</dt><dd>Some text</dd></dl>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("motherboard", fmt.setContent(tu.getSource().getFirstContent()).toString());
		TermsAnnotation ta = tu.getSource().getAnnotation(TermsAnnotation.class);
		assertNotNull(ta);
		assertEquals("motherboard", ta.getTerm(0));
		assertEquals("REF:some URI", ta.getInfo(0));
	}
	
	@Test
	public void testLocNoteLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p its-loc-note='note'>text</p>"
			+ "<p its-loc-note-ref='note ref'>text</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("note", tu.getProperty(Property.NOTE).getValue());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals("REF:note ref", tu.getProperty(Property.NOTE).getValue());
	}
	
	@Test
	public void testWithinTextLocal () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text1 <span>inside</span> text2</p>"
			+ "<p>Text3 <span its-within-text='no'>not-within</span> text4</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Text1 <1>inside</1> text2", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals("Text3 ", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 4);
		assertEquals("not-within", fmt.setContent(tu.getSource().getFirstContent()).toString());
		tu = FilterTestDriver.getTextUnit(list, 5);
		assertEquals(" text4", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}

	@Test
	public void testGlobalLocQualityIssues () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title>"
			+ "<script type=application/its+xml>"
			+ "<its:rules xmlns:its='http://www.w3.org/2005/11/its' version='2.0' "
			+ "xmlns:h='http://www.w3.org/1999/xhtml'>"
			+ "<its:locQualityIssueRule selector='//h:p/@title' locQualityIssueComment='comment'/>"
			+ "</its:rules>"
			+ "</script>"
			+ "</head><body>"
			+ "<p title='Text'>text paragraph</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Text", tu.getSource().toString());
		GenericAnnotations anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		List<GenericAnnotation> res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(1, res.size());
		assertEquals("comment", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.LQI_TYPE));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.LQI_SEVERITY));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.LQI_PROFILEREF));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.LQI_ISSUESREF));
		assertEquals(true, res.get(0).getBoolean(GenericAnnotationType.LQI_ENABLED));
	}
	
	@Test
	public void testLocQualityIssuesExternalXMLStandoff () {
		ArrayList<Event> list = getEvents(new File(root+"lqi-test1.html"));
		
		// First paragraph
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Paragraph 1", tu.getSource().toString());
		GenericAnnotations anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		List<GenericAnnotation> res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(2, res.size());
		assertEquals("lqi3-comment1", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("lqi3-comment2", res.get(1).getString(GenericAnnotationType.LQI_COMMENT));
		
		// Attribute of paragraph 2
		tu = FilterTestDriver.getTextUnit(list, 3);
		assertEquals("Text", tu.getSource().toString());
		anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(2, res.size());
		assertEquals("lqi1-comment1", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("lqi1-comment2", res.get(1).getString(GenericAnnotationType.LQI_COMMENT));
		
		// Paragraph 2
		tu = FilterTestDriver.getTextUnit(list, 4);
		assertEquals("Paragraph 2", tu.getSource().toString());
		anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(2, res.size());
		assertEquals("lqi2-comment1", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("lqi2-comment2", res.get(1).getString(GenericAnnotationType.LQI_COMMENT));
	}

	@Test
	public void testStandofftLocQualityIssues () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title>"
			+ "<script id='lqi1'>"
			+ "<its:locQualityIssues xml:id='lqi1' xmlns:its='http://www.w3.org/2005/11/its'>"
			+ "<its:locQualityIssue locQualityIssueType='misspelling' locQualityIssueComment='comment1' locQualityIssueSeverity='10'/>"
			+ "<its:locQualityIssue locQualityIssueComment='comment2' locQualityIssueEnabled='no' locQualityIssueProfileRef='uri'/>"
			+ "</its:locQualityIssues>"
			+ "</script>"
			+ "</head><body>"
			+ "<p its-loc-quality-issues-ref='#lqi1'>Bad text</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Bad text", tu.getSource().toString());
		GenericAnnotations anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		List<GenericAnnotation> res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(2, res.size());
		assertEquals("comment1", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("comment2", res.get(1).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("misspelling", res.get(0).getString(GenericAnnotationType.LQI_TYPE));
		assertEquals(null, res.get(1).getString(GenericAnnotationType.LQI_TYPE));
		assertEquals(10, res.get(0).getFloat(GenericAnnotationType.LQI_SEVERITY), 0);
		assertEquals(null, res.get(1).getFloat(GenericAnnotationType.LQI_SEVERITY));
		assertEquals(true, res.get(0).getBoolean(GenericAnnotationType.LQI_ENABLED));
		assertEquals(false, res.get(1).getBoolean(GenericAnnotationType.LQI_ENABLED));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.LQI_PROFILEREF));
		assertEquals("uri", res.get(1).getString(GenericAnnotationType.LQI_PROFILEREF));
	}
	
	@Test
	public void testLocalLocQualityIssues () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title>"
			+ "</head><body>"
			+ "<p its-loc-quality-issue-type='misspelling' its-loc-quality-issue-severity='11'"
			+ " its-loc-quality-issue-comment='note' its-loc-quality-issue-profile-ref='uri'"
			+ " its-loc-quality-issue-enabled='false'"
			+ ">Bad text</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Bad text", tu.getSource().toString());
		GenericAnnotations anns = tu.getSource().getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		List<GenericAnnotation> res = anns.getAnnotations(GenericAnnotationType.LQI);
		assertEquals(1, res.size());
		assertEquals("note", res.get(0).getString(GenericAnnotationType.LQI_COMMENT));
		assertEquals("misspelling", res.get(0).getString(GenericAnnotationType.LQI_TYPE));
		assertEquals(11, res.get(0).getFloat(GenericAnnotationType.LQI_SEVERITY), 0);
		assertEquals("uri", res.get(0).getString(GenericAnnotationType.LQI_PROFILEREF));
		assertEquals(false, res.get(0).getBoolean(GenericAnnotationType.LQI_ENABLED));
	}
	
	@Test
	public void testLink () {
		ArrayList<Event> list = getEvents(new File(root+"test02.html"));
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text with <1><2/></1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testSimpleOutput () {
		String snippet = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=UTF-8><title>Title</title></head><body>"
			+ "<p>Text <img alt=Text src=test.png>.</p>"
			+ "</body></html>";
		String expected = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Title</title></head><body>"
			+ "<p>Text <img alt=\"Text\" src=\"test.png\">.</p>"
			+ "</body></html>";
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet),
			filter.getEncoderManager(), locEN));
	}
	
	
	@Test
	public void testOpenTwice () throws URISyntaxException {
		File file = new File(root+"test01.html");
		RawDocument rawDoc = new RawDocument(file.toURI(), "UTF-8", locEN);
		filter.open(rawDoc);
		filter.close();
		filter.open(rawDoc);
		filter.close();
	}
	
	private ArrayList<Event> getEvents (String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN, LocaleId.FRENCH));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

	private ArrayList<Event> getEvents (File file) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(file.toURI(), "UTF-8", locEN));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
