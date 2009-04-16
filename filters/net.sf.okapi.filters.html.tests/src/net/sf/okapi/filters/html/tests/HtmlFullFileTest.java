package net.sf.okapi.filters.html.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MemMappedCharSequence;
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
		
		// read all files in the test html directory
		URL url = HtmlFullFileTest.class.getResource("/simpleTest.html");
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".html") || name.endsWith(".hml");
			}
		};
		testFileList = dir.list(filter);
	}

	@After
	public void tearDown() {
		htmlFilter.close();
	}

	@Test
	public void testAll() throws URISyntaxException {
		for (String f : testFileList) {
			InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/" + f);
			htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
			try {
				while (htmlFilter.hasNext()) {
					@SuppressWarnings("unused")
					Event event = htmlFilter.next();
				}				
			} catch (Exception e) {
				//System.err.println("Error for file: " + f + ": " + e.toString());
				throw new RuntimeException("Error for file: " + f + ": " + e.toString());
			}
		}
	}

	@Test
	public void testNonwellformed() {
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/nonwellformed.specialtest");
		MemMappedCharSequence charSequence = new MemMappedCharSequence(htmlStream, "UTF-8");
		htmlFilter.open(new RawDocument(charSequence, "en"));
		while (htmlFilter.hasNext()) {
			@SuppressWarnings("unused")
			Event event = htmlFilter.next();
		}
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
				TextUnit tu = (TextUnit)event.getResource();
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
				TextUnit tu = (TextUnit)event.getResource();
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
