package net.sf.okapi.filters.its.html5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.TermsAnnotation;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextFragment;

import org.junit.Before;
import org.junit.Test;

public class HTML5FilterTest {

	private HTML5Filter filter;
	private GenericContent fmt;
	private String root;
	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;

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
		assertEquals("domA, dom2, domB, dom1, dom3", ga.getString(GenericAnnotationType.DOMAIN_VALUE));		
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
		assertEquals("[a-z]", ga.getString(GenericAnnotationType.ALLOWEDCHARS_VALUE));
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
		GenericAnnotation ga = tu.getAnnotation(GenericAnnotations.class).getFirstAnnotation(GenericAnnotationType.STORAGESIZE);
		assertEquals(10, (int)ga.getInteger(GenericAnnotationType.STORAGESIZE_SIZE));
		assertEquals("UTF-8", ga.getString(GenericAnnotationType.STORAGESIZE_ENCODING));
		assertEquals("lf", ga.getString(GenericAnnotationType.STORAGESIZE_LINEBREAK));
		tu = FilterTestDriver.getTextUnit(list, 3);
		ga = tu.getAnnotation(GenericAnnotations.class).getFirstAnnotation(GenericAnnotationType.STORAGESIZE);
		assertEquals(22, (int)ga.getInteger(GenericAnnotationType.STORAGESIZE_SIZE));
		assertEquals("ISO-8859-1", ga.getString(GenericAnnotationType.STORAGESIZE_ENCODING));
		assertEquals("lf", ga.getString(GenericAnnotationType.STORAGESIZE_LINEBREAK));
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
			+ "<script id='lqi1' type=application/its+xml>"
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
		assertEquals(10, res.get(0).getDouble(GenericAnnotationType.LQI_SEVERITY), 0);
		assertEquals(null, res.get(1).getDouble(GenericAnnotationType.LQI_SEVERITY));
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
		assertEquals(11, res.get(0).getDouble(GenericAnnotationType.LQI_SEVERITY), 0);
		assertEquals("uri", res.get(0).getString(GenericAnnotationType.LQI_PROFILEREF));
		assertEquals(false, res.get(0).getBoolean(GenericAnnotationType.LQI_ENABLED));
	}
	
	@Test
	public void testProvenanceStandoff () {
		String snippet = "<!DOCTYPE html><html lang=en><head><meta charset=utf-8><title>Title</title>"
			+ "<script id='prv1' type=application/its+xml>"
			+ "<its:provenanceRecords xml:id='prv1' xmlns:its='http://www.w3.org/2005/11/its' version='2.0'>"
			+ "<its:provenanceRecord person='p1' org='o1' tool='t1' provRef='ref1'/>"
			+ "<its:provenanceRecord personRef='pRef2' orgRef='oRef2' toolRef='tRef2'/>"
			+ "<its:provenanceRecord revPerson='revp3' revOrg='revo3' revTool='revt3'/>"
			+ "<its:provenanceRecord revPersonRef='revpRef4' revOrgRef='revoRef4' revToolRef='revtRef4'/>"
			+ "</its:provenanceRecords>"
			+ "</script>"
			+ "</head><body>"
			+ "<p its-provenance-records-ref='#prv1'>Text</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Text", tu.getSource().toString());
		GenericAnnotations anns = tu.getAnnotation(GenericAnnotations.class);
		assertNotNull(anns);
		List<GenericAnnotation> res = anns.getAnnotations(GenericAnnotationType.PROV);
		assertEquals(4, res.size());
		// first
		assertEquals("p1", res.get(0).getString(GenericAnnotationType.PROV_PERSON));
		assertEquals("o1", res.get(0).getString(GenericAnnotationType.PROV_ORG));
		assertEquals("t1", res.get(0).getString(GenericAnnotationType.PROV_TOOL));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.PROV_REVPERSON));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.PROV_REVORG));
		assertEquals(null, res.get(0).getString(GenericAnnotationType.PROV_REVTOOL));
		assertEquals("ref1", res.get(0).getString(GenericAnnotationType.PROV_PROVREF));
		// second
		assertEquals("REF:pRef2", res.get(1).getString(GenericAnnotationType.PROV_PERSON));
		assertEquals("REF:oRef2", res.get(1).getString(GenericAnnotationType.PROV_ORG));
		assertEquals("REF:tRef2", res.get(1).getString(GenericAnnotationType.PROV_TOOL));
		assertEquals(null, res.get(1).getString(GenericAnnotationType.PROV_REVPERSON));
		assertEquals(null, res.get(1).getString(GenericAnnotationType.PROV_REVORG));
		assertEquals(null, res.get(1).getString(GenericAnnotationType.PROV_REVTOOL));
		assertEquals(null, res.get(1).getString(GenericAnnotationType.PROV_PROVREF));
		// third
		assertEquals(null, res.get(2).getString(GenericAnnotationType.PROV_PERSON));
		assertEquals(null, res.get(2).getString(GenericAnnotationType.PROV_ORG));
		assertEquals(null, res.get(2).getString(GenericAnnotationType.PROV_TOOL));
		assertEquals("revp3", res.get(2).getString(GenericAnnotationType.PROV_REVPERSON));
		assertEquals("revo3", res.get(2).getString(GenericAnnotationType.PROV_REVORG));
		assertEquals("revt3", res.get(2).getString(GenericAnnotationType.PROV_REVTOOL));
		assertEquals(null, res.get(2).getString(GenericAnnotationType.PROV_PROVREF));
		// fourth
		assertEquals(null, res.get(3).getString(GenericAnnotationType.PROV_PERSON));
		assertEquals(null, res.get(3).getString(GenericAnnotationType.PROV_ORG));
		assertEquals(null, res.get(3).getString(GenericAnnotationType.PROV_TOOL));
		assertEquals("REF:revpRef4", res.get(3).getString(GenericAnnotationType.PROV_REVPERSON));
		assertEquals("REF:revoRef4", res.get(3).getString(GenericAnnotationType.PROV_REVORG));
		assertEquals("REF:revtRef4", res.get(3).getString(GenericAnnotationType.PROV_REVTOOL));
		assertEquals(null, res.get(3).getString(GenericAnnotationType.PROV_PROVREF));
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
		assertEquals(expected, FilterTestDriver.generateOutput(getEvents(snippet), locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	@Test
	public void testMinimalHTML5Output () {
		String snippet = "<!DOCTYPE html>\n<head><title>t1</title></head>t2";
		String expected = "<!DOCTYPE html>\n<html><head><title>t1</title></head><body>t2</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("t1", tu.getSource().toString());
		assertEquals(expected, FilterTestDriver.generateOutput(list, locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
	}
	
	@Test
	public void testAddITSAnnotations1 () {
		String snippet = "<!DOCTYPE html>\n<html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text1 text2 text3</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		addVariousAnnotations(tu);
		// Test the output
		String expected = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Title</title></head><body>"
			+ "<p>Text1 <span its-disambig-class-ref=\"classRefWith&quot;&#39;&amp;&lt;>[]{}\""
			+ " its-allowed-characters=\"[a-z]\""
			+ " its-term=\"yes\" its-term-confidence=\"0.123\" its-term-info=\"terminfo\""
			+ ">text2</span> text3</p>"
			+ "</body></html>";
		assertEquals(expected, FilterTestDriver.generateOutput(list, locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	@Test
	public void testAddITSAnnotations2 () {
		String snippet = "<!DOCTYPE html>\n<html lang=en><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text1 text2 text3</p>"
			+ "</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		addStandoffAnnotations(tu);
		// Test the output
		String expected = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Title</title></head><body>"
			+ "<p><span its-provenance-records-ref=\"#myProv\">Text1</span> "
			+ "<span its-loc-quality-issues-ref=\"#myLqi\""
			+ ">text2</span> text3</p>"
			+ "<script id=\"myLqi\" type=\"application/its+xml\">\n"
			+ "<its:locQualityIssues xmlns:its=\"http://www.w3.org/2005/11/its\" version=\"2.0\" xml:id=\"myLqi\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment1\"/>\n"
			+ "<its:locQualityIssue locQualityIssueSeverity=\"50.5\" locQualityIssueType=\"terminology\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment2\" locQualityIssueEnabled=\"no\"/>\n"
			+ "</its:locQualityIssues>\n</script>\n"
			+ "<script id=\"myProv\" type=\"application/its+xml\">"
			+ "<its:provenanceRecords xmlns:its=\"http://www.w3.org/2005/11/its\" version=\"2.0\" xml:id=\"myProv\">"
			+ "<its:provenanceRecord person=\"person1\"/><its:provenanceRecord orgRef=\"org1\"/>"
			+ "</its:provenanceRecords></script>"
			+ "</body></html>";
		assertEquals(expected, FilterTestDriver.generateOutput(list, locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	@Test
	public void testMinimalHTMLWithStandoff () {
		String snippet = "<!DOCTYPE html>\n<head><title>t1</title></head>Text1 text2 text3";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertEquals("Text1 text2 text3", tu.getSource().toString());
		addStandoffAnnotations(tu);
		String expected = "<!DOCTYPE html>\n<html><head><title>t1</title></head><body>"
			+ "<span its-provenance-records-ref=\"#myProv\">Text1</span> <span its-loc-quality-issues-ref=\"#myLqi\""
			+ ">text2</span> text3"
			+ "<script id=\"myLqi\" type=\"application/its+xml\">\n"
			+ "<its:locQualityIssues xmlns:its=\"http://www.w3.org/2005/11/its\" version=\"2.0\" xml:id=\"myLqi\">\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment1\"/>\n"
			+ "<its:locQualityIssue locQualityIssueSeverity=\"50.5\" locQualityIssueType=\"terminology\"/>\n"
			+ "<its:locQualityIssue locQualityIssueComment=\"comment2\" locQualityIssueEnabled=\"no\"/>\n"
			+ "</its:locQualityIssues>\n</script>\n"
			+ "<script id=\"myProv\" type=\"application/its+xml\">"
			+ "<its:provenanceRecords xmlns:its=\"http://www.w3.org/2005/11/its\" version=\"2.0\" xml:id=\"myProv\">"
			+ "<its:provenanceRecord person=\"person1\"/><its:provenanceRecord orgRef=\"org1\"/>"
			+ "</its:provenanceRecords></script>"
			+ "</body></html>";
		assertEquals(expected, FilterTestDriver.generateOutput(list, locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
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

	@Test
	public void testDATAContentOutput () {
		String snippet = "<!DOCTYPE html>\n<html lang=en translate=\"no\"><head><meta charset=utf-8><title>Title</title>"
			+ "<style>\n<!--\n.totrans { background-color: #FFFF00 }\n-->\n</style>\n"
			+ "<script type=\"application/its+xml\">\n"
			+ "<its:rules xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:h=\"http://www.w3.org/1999/xhtml\" version=\"2.0\">\n"
			+ "<its:translateRule selector=\"//h:*[@class='totrans']\" translate=\"yes\"/>\n"
			+ "</its:rules>\n</script>\n"
			+ "</head><body>"
			+ "<p>Text1<p class=\"totrans\">Text2</body></html>";
		ArrayList<Event> list = getEvents(snippet);
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertEquals("Text2", tu.getSource().toString());
		// Test the output
		String expected = "<!DOCTYPE html>\n<html lang=\"en\" translate=\"no\"><head><meta charset=\"UTF-8\"><title>Title</title>"
			+ "<style>\n<!--\n.totrans { background-color: #FFFF00 }\n-->\n</style>\n"
			+ "<script type=\"application/its+xml\">\n"
			+ "<its:rules xmlns:its=\"http://www.w3.org/2005/11/its\""
			+ " xmlns:h=\"http://www.w3.org/1999/xhtml\" version=\"2.0\">\n"
			+ "<its:translateRule selector=\"//h:*[@class='totrans']\" translate=\"yes\"/>\n"
			+ "</its:rules>\n</script>\n"
			+ "</head><body>"
			+ "<p>Text1</p><p class=\"totrans\">Text2</p></body></html>";
		assertEquals(expected, FilterTestDriver.generateOutput(list, locFR,
			filter.createSkeletonWriter(), filter.getEncoderManager()));
	}

	
	private void addStandoffAnnotations (ITextUnit tu) {
		// Content is "Text1 text2 text3"
		// Creates a target (no override)
		TextFragment tf = tu.createTarget(locFR, false, IResource.COPY_ALL).getFirstContent();

		// Add LQI annotations
		GenericAnnotations anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.LQI,
				GenericAnnotationType.LQI_COMMENT, "comment1"));
		anns.setData("myLqi");
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_TYPE, "terminology",
			GenericAnnotationType.LQI_SEVERITY, 50.5));
		anns.add(new GenericAnnotation(GenericAnnotationType.LQI,
			GenericAnnotationType.LQI_COMMENT, "comment2",
			GenericAnnotationType.LQI_ENABLED, false));
		// Set the annotations
		tf.annotate(6, 11, GenericAnnotationType.GENERIC, anns);
		
		// Add Prov annotations
		anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.PROV,
				GenericAnnotationType.PROV_PERSON, "person1"));
			anns.setData("myProv");
		anns.add(new GenericAnnotation(GenericAnnotationType.PROV,
				GenericAnnotationType.PROV_ORG, "REF:org1"));
		// Set the annotations
		tf.annotate(0, 5, GenericAnnotationType.GENERIC, anns);
	}
	
	private void addVariousAnnotations (ITextUnit tu) {
		// Creates a target
		TextFragment tf = tu.createTarget(locFR, false, IResource.COPY_ALL).getFirstContent();
		// Add a simple LQI annotation
		GenericAnnotations anns = new GenericAnnotations(
			new GenericAnnotation(GenericAnnotationType.DISAMB,
				GenericAnnotationType.DISAMB_GRANULARITY, GenericAnnotationType.DISAMB_GRANULARITY_ENTITY, // Default
				GenericAnnotationType.DISAMB_CLASS, "REF:classRefWith\"'&<>[]{}"));
		anns.add(new GenericAnnotation(GenericAnnotationType.ALLOWEDCHARS,
			GenericAnnotationType.ALLOWEDCHARS_VALUE, "[a-z]"));
		anns.add(new GenericAnnotation(GenericAnnotationType.TERM,
			GenericAnnotationType.TERM_INFO, "terminfo",
			GenericAnnotationType.TERM_CONFIDENCE, 0.123));
		tf.annotate(6, 11, GenericAnnotationType.GENERIC, anns);
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
