package net.sf.okapi.filters.html.tests.integration;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Util;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.html.tests.TestUtils;
import net.sf.okapi.filters.tests.InputDocument;
import net.sf.okapi.filters.tests.RoundTripComparison;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExtractionComparisionTest {
	private HtmlFilter htmlFilter;
	private String[] testFileList;	
	private String root;

	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();		
		testFileList = TestUtils.getHtmlTestFiles();
		
		URL url = ExtractionComparisionTest.class.getResource("/324.html");
		root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/html/";
	}

	@After
	public void tearDown() throws Exception {		
	}
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {		
		RoundTripComparison rtc = new RoundTripComparison();
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		for (String f : testFileList) {
			list.add(new InputDocument(root + f, null));
		}
		assertTrue(rtc.executeCompare(htmlFilter, list, "UTF-8", "en", "en"));
	}
}
