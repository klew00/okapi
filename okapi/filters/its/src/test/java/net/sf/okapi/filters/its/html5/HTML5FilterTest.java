package net.sf.okapi.filters.its.html5;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.annotation.TermsAnnotation;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
	public void testLink () {
		ArrayList<Event> list = getEvents(new File(root+"test02.html"));
		ITextUnit tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("Text with <1><2/></1>.", fmt.setContent(tu.getSource().getFirstContent()).toString());
	}
	
	@Test
	public void testSimpleOutput () {
		String snippet = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=utf-8><title>Title</title></head><body>"
			+ "<p>Text <img alt=Text src=test.png>.</p>"
			+ "</body></html>";
		String expected = "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=\"utf-8\"><title>Title</title></head><body>"
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
