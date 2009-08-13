package net.sf.okapi.filters.html;

import java.io.InputStream;
import java.net.URISyntaxException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlFullFileTest {
	private HtmlFilter htmlFilter;
	private String[] testFileList;

	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();		
		testFileList = TestUtils.getHtmlTestFiles();
	}

	@After
	public void tearDown() {
		htmlFilter.close();
	}

	@Test
	public void testAllExternalFiles() throws URISyntaxException {
		@SuppressWarnings("unused")
		Event event = null;

		for (String f : testFileList) {			
			InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/" + f);
			htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
			while (htmlFilter.hasNext()) {
				event = htmlFilter.next();
			}
		}
	}

	@Test
	public void testNonwellformed() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/nonwellformed.specialtest");
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		while (htmlFilter.hasNext()) {
			@SuppressWarnings("unused")
			Event event = htmlFilter.next();
		}
	}
	
	@Test
	public void testEncodingShouldBeFound() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/withEncoding.html");
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		assertEquals("windows-1252", htmlFilter.getEncoding());
	}
	
	@Test
	public void testEncodingShouldBeFound2() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/W3CHTMHLTest1.html");
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		assertEquals("UTF-8", htmlFilter.getEncoding());		
	}

	@Test
	public void testOkapiIntro() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/okapi_intro_test.html");
		htmlFilter.open(new RawDocument(htmlStream, "windows-1252", "en"));
		boolean foundText = false;
		boolean first = true;
		String lastText = "";
		String firstText = "";
		while (htmlFilter.hasNext()) {
			Event event = htmlFilter.next();
			if (event.getEventType() == EventType.TEXT_UNIT) {
				TextUnit tu = (TextUnit) event.getResource();
				if (first) {
					first = false;
					firstText = tu.getSource().toString();
				}
				foundText = true;
				lastText = tu.getSource().toString();
			}
		}
		assertTrue(foundText);
		assertEquals("Okapi Framework", firstText);
		assertEquals("\u00A0", lastText);
	}

	@Test
	public void testSkippedScriptandStyleElements() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/testStyleScriptStylesheet.html");
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		boolean foundText = false;
		boolean first = true;
		String firstText = "";
		while (htmlFilter.hasNext()) {
			Event event = htmlFilter.next();
			if (event.getEventType() == EventType.TEXT_UNIT) {
				TextUnit tu = (TextUnit) event.getResource();
				if (first) {
					first = false;
					firstText = tu.getSource().toString();
				}
				foundText = true;
			}
		}
		assertTrue(foundText);
		assertEquals("First Text", firstText);
	}
}
