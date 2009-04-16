package net.sf.okapi.filters.html.tests;

import java.io.IOException;
import java.io.InputStream;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlDetectBomTest {
	private HtmlFilter htmlFilter;
	
	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();		
	}

	@After
	public void tearDown() {
		htmlFilter.close();
	}

	@Test
	public void testDetectBom() throws IOException {
		InputStream htmlStream = HtmlDetectBomTest.class.getResourceAsStream("/ruby.html");		
		BOMNewlineEncodingDetector bomDetector = new BOMNewlineEncodingDetector(htmlStream);
		
		assertTrue(bomDetector.hasBom());
		assertTrue(bomDetector.hasUtf8Bom());
		assertFalse(bomDetector.hasUtf7Bom());
		
		
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", "en"));
		while (htmlFilter.hasNext()) {
			Event event = htmlFilter.next();
			if (event.getEventType() == EventType.START_DOCUMENT) {
				StartDocument sd = (StartDocument)event.getResource();
				assertTrue(sd.hasUTF8BOM());
				assertEquals("UTF-8", sd.getEncoding());
				assertEquals("en", sd.getLanguage());
				assertEquals("\r\n", sd.getLineBreak());
			}
		}
	}	
}
