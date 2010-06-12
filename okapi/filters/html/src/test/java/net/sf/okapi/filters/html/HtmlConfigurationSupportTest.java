package net.sf.okapi.filters.html;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlConfigurationSupportTest {

	private HtmlFilter filter = new HtmlFilter();
	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	
	@Test
	public void test_collapse_whitespace () {
		String config = "collapse_whitespace: true";
		filter.setParameters(new Parameters(config));
		String snippet = "<p> t1  \nt2  </p>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1 t2", tu.getSource().toString());

		config = "collapse_whitespace: false";
		filter.setParameters(new Parameters(config));
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals(" t1  \nt2  ", tu.getSource().toString());
	}

	@Test
	public void test_PRESERVE_WHITESPACE () {
		String config = "pre: \n"
			+ "   ruleTypes: [PRESERVE_WHITESPACE]";
		filter.setParameters(new Parameters(config));
		String snippet = "<p> t1  \nt2  </p><pre> t3  \nt4  </pre>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1 t2", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals(" t3  \nt4  ", tu.getSource().toString());
	}

	@Test
	public void test_EXCLUDE () {
		String config = "pre: \n"
			+ "   ruleTypes: [EXCLUDE]";
		filter.setParameters(new Parameters(config));
		String snippet = "<pre>t1</pre><p>t2</p>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t2", tu.getSource().toString());
	}

	@Test
	public void test_INCLUDE () {
		String config = "pre: \n"
			+ "   ruleTypes: [EXCLUDE] \n"
			+ "b: \n"
			+ "   ruleTypes: [INCLUDE]";
		filter.setParameters(new Parameters(config));
		String snippet = "<pre>t1<b>t2</b>t3</pre><p>t4</p>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t2", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals("t4", tu.getSource().toString());
	}

	@Test
	public void test_ATTRIBUTE_ID () {
		String config = "id: \n"
			+ "   ruleTypes: [ATTRIBUTE_ID]";
		filter.setParameters(new Parameters(config));
		String snippet = "<p id='id1'>t1</p><pre id='id2'>t2</pre>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1", tu.getSource().toString());
//TODO: Fix support for ATTRIBUTE_ID		assertEquals("id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals("t2", tu.getSource().toString());
//TODO: Fix support for ATTRIBUTE_ID		assertEquals("id2", tu.getName());
	}

	@Test
	public void test_idAttributes () {
		String config = "p: \n"
			+ "   ruleTypes: [TEXTUNIT] \n"
			+ "   idAttribues: [id, 'xml:id']";
		filter.setParameters(new Parameters(config));
		String snippet = "<p id='id1'>t1</p><p xml:id='id2'>t2</p>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1", tu.getSource().toString());
//TODO: Fix support for idAttribues		assertEquals("id1", tu.getName());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals("t2", tu.getSource().toString());
//TODO: Fix support for idAttribues		assertEquals("id2", tu.getName());
	}

	@Test
	public void test_allElementsExcept () {
		String config = "alt: \n"
			+ "   ruleTypes: [ATTRIBUTE_TRANS] \n"
			+ "   allElementsExcept: [elem2, elem3]";
		filter.setParameters(new Parameters(config));
		String snippet = "<elem1 alt='t1'>t2</elem1><elem2 alt='t3'>t4</elem2><elem3 alt='t5'>t6</elem3>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1", tu.getSource().toString()); // alt
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals("t2", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 3);
		assertEquals("t4", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 4);
		assertEquals("t6", tu.getSource().toString());
	}

	@Test
	public void test_onlyTheseElements () {
		String config = "alt: \n"
			+ "   ruleTypes: [ATTRIBUTE_TRANS] \n"
			+ "   onlyTheseElements: [elem1, elem3]"; // only in elem1 and elem3, not elem2
		filter.setParameters(new Parameters(config));
		String snippet = "<elem1 alt='t1'>t2</elem1><elem2 alt='t3'>t4</elem2><elem3 alt='t5'>t6</elem3>";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1", tu.getSource().toString()); // alt of elem1
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 2);
		assertEquals("t2", tu.getSource().toString()); // elem1
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 3);
		assertEquals("t4", tu.getSource().toString()); // elem2
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 4);
		assertEquals("t5", tu.getSource().toString()); // alt of elem3
		tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 5);
		assertEquals("t6", tu.getSource().toString()); // elem3
	}
	
	@Test
	public void test_ATTRIBUTE_WRITABLE () {
		String config = "dir: \n"
			+ "   ruleTypes: [ATTRIBUTE_WRITABLE]\n"
			+ "p:\n"
			+ "   ruleTypes: [TEXTUNIT]";
		filter.setParameters(new Parameters(config));
		String snippet = "<p dir='rtl'>t1</p><pre dir='ltr'>t2</pre>";
		// p is defined as TEXTUNIT so the property is with the TU
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, locEN, locFR), 1);
		assertEquals("t1", tu.getSource().toString());
		assertNotNull(tu.getSource().getProperty("dir"));
		assertEquals("rtl", tu.getSource().getProperty("dir").getValue());
		// pre is not defined as TEXTUNIT so the property is with the skeleton in the previous document part
		DocumentPart dp = FilterTestDriver.getDocumentPart(getEvents(snippet, locEN, locFR), 2);
		assertNotNull(dp.getSourceProperty("dir"));
		assertEquals("ltr", dp.getSourceProperty("dir").getValue());
	}

	private ArrayList<Event> getEvents(String snippet,
		LocaleId srcLang,
		LocaleId trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang, trgLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
