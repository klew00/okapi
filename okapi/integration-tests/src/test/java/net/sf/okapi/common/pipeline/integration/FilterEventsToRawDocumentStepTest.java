package net.sf.okapi.common.pipeline.integration;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.steps.common.FilterEventsToRawDocumentStep;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public class FilterEventsToRawDocumentStepTest {

	private FilterEventsToRawDocumentStep eventToDoc;
	private String htmlSnippet;
	private HtmlFilter htmlFilter;
	private LocaleId locEN = LocaleId.fromString("EN");

    @Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();
		htmlSnippet = "<p>This is a <i>test</i> snippet</p>";		
	}

	@After
	public void tearDown() throws Exception {
		htmlFilter.close();
	}

	@Test
	public void htmlEventsToRawDocumentWithUserURI() throws IOException {
		Event event = null;
		eventToDoc = new FilterEventsToRawDocumentStep();
		
		RawDocument rawDoc = new RawDocument(htmlSnippet, locEN);
		File tmpFile = File.createTempFile("FilterEventsToRawDocumentStepTest", ".tmp");
		eventToDoc.setOutputURI(tmpFile.toURI());
		eventToDoc.setOutputEncoding("UTF-8");
		
		htmlFilter.open(rawDoc);
		while (htmlFilter.hasNext()) {
			event = eventToDoc.handleEvent(htmlFilter.next());
		}
		htmlFilter.close();

		// last event should be RawDocument
		assertTrue(event.getEventType() == EventType.RAW_DOCUMENT);
		// Get the EventsToRawDocumentStep output and compare it to our input
		assertEquals(htmlSnippet, convertRawDocumentToString((RawDocument)event.getResource()));
		eventToDoc.destroy();
	}

	@Test
	public void htmlEventsToRawDocument() throws IOException {
		Event event = null;		
		eventToDoc = new FilterEventsToRawDocumentStep();
		RawDocument rawDoc = new RawDocument(htmlSnippet, locEN);
		eventToDoc.setOutputEncoding("UTF-8");

		htmlFilter.open(rawDoc);
		while ( htmlFilter.hasNext() ) {
			event = eventToDoc.handleEvent(htmlFilter.next());
		}
		htmlFilter.close();

		// last event should be RawDocument
		assertTrue(event.getEventType() == EventType.RAW_DOCUMENT);
		// Get the EventsToRawDocumentStep output and compare it to our input
		assertEquals(htmlSnippet, convertRawDocumentToString((RawDocument)event.getResource()));
		eventToDoc.destroy();
	}

	private String convertRawDocumentToString(RawDocument d) throws IOException {		
		int c;
		StringWriter sw = new StringWriter();
		Reader r = d.getReader(); 
		while ( true ) {
			c = r.read();			
			if (c == -1) break;
			sw.append((char) c);
		}
		d.getReader().close();
		return sw.toString();
	}
}
