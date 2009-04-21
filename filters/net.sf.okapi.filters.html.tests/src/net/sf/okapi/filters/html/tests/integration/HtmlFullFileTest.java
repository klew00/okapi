package net.sf.okapi.filters.html.tests.integration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlFullFileTest {
	private IFilter htmlFilter;
	private String[] testFileList;

	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();

		// read all files in the test html directory
		URL url = HtmlFullFileTest.class.getResource("/simpleTest.html");
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".html") || name.endsWith(".htm");
			}
		};
		testFileList = dir.list(filter);
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
			System.out.println(f);
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

	// @Test
	public void printEvents() {
		htmlFilter = new HtmlFilter();
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/msg00058.html");
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		try {
			while (htmlFilter.hasNext()) {
				Event event = htmlFilter.next();
				if (event.getEventType() == EventType.TEXT_UNIT) {
					assertTrue(event.getResource() instanceof TextUnit);
				} else if (event.getEventType() == EventType.DOCUMENT_PART) {
					assertTrue(event.getResource() instanceof DocumentPart);
				} else if (event.getEventType() == EventType.START_GROUP || event.getEventType() == EventType.END_GROUP) {
					assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
				}
				System.out.print(event.getEventType().toString() + ": ");
				if (event.getResource() != null) {
					if (event.getResource() instanceof DocumentPart) {
						System.out.println(((DocumentPart) event.getResource()).getSourcePropertyNames());
					} else {
						System.out.println(event.getResource().toString());
					}
					if (event.getResource().getSkeleton() != null) {
						System.out.println("\tSkeketon: " + event.getResource().getSkeleton().toString());
					}
				}
			}
			htmlFilter.close();
		} catch (Exception e) {
			int x = 1;
		}
	}
}
