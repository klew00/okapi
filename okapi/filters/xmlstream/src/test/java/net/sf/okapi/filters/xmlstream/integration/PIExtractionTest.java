package net.sf.okapi.filters.xmlstream.integration;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.filters.xmlstream.XmlStreamFilter;

import org.junit.Before;
import org.junit.Test;

public class PIExtractionTest {

	private XmlStreamFilter xmlStreamFilter;	
	private String ditaRoot;
	private LocaleId locEN = LocaleId.ENGLISH;
	
	@Before
	public void setUp() throws Exception {
		xmlStreamFilter = new XmlStreamFilter();	
		xmlStreamFilter.setParametersFromURL(XmlStreamFilter.class.getResource("dita.yml"));
		URL ditaUrl = DitaExtractionComparisionTest.class.getResource("/bookmap-readme.dita");				
		ditaRoot = Util.getDirectoryName(ditaUrl.toURI().getPath()) + File.separator;
	}
	
	@Test
	public void testDoubleExtraction() throws URISyntaxException, MalformedURLException {
		RoundTripComparison rtc = new RoundTripComparison();
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(ditaRoot + "PI-Problem.xml", null));
		assertTrue(rtc.executeCompare(xmlStreamFilter, list, "UTF-8", locEN, locEN));
	}
	
	@Test
	public void testDoubleExtraction2() throws URISyntaxException, MalformedURLException {
		RoundTripComparison rtc = new RoundTripComparison();
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(ditaRoot + "PI-Problem2.dita", null));
		assertTrue(rtc.executeCompare(xmlStreamFilter, list, "UTF-8", locEN, locEN));
	}
}