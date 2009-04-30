package net.sf.okapi.common.pipeline.tests;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipeline.EventsToRawDocumentStep;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.html.HtmlFilter;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventsToRawDocumentStepTest {
	private EventsToRawDocumentStep eventToDoc;
	private String htmlSnippet;
	private HtmlFilter htmlFilter;

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
	public void htmlEventsToRawDocument() throws IOException {
		Event event = null;
		eventToDoc = new EventsToRawDocumentStep();

		htmlFilter.open(new RawDocument(htmlSnippet, "en"));
		while (htmlFilter.hasNext()) {
			event = eventToDoc.handleEvent(htmlFilter.next());
		}

		// last event should be RawDocument
		assertTrue(event.getEventType() == EventType.RAW_DOCUMENT);
		// Get the EventsToRawDocumentStep output and compare it to our input
		assertEquals(htmlSnippet, convertRawDocumentToString((RawDocument)event.getResource()));
		eventToDoc.destroy();
	}

	@Test
	public void htmlEventsToRawDocumentWithUserURI() throws IOException {
		Event event = null;
		eventToDoc = new EventsToRawDocumentStep(File.createTempFile("EventsToRawDocumentStepTest", ".tmp").toURI());

		htmlFilter.open(new RawDocument(htmlSnippet, "en"));
		while (htmlFilter.hasNext()) {
			event = eventToDoc.handleEvent(htmlFilter.next());
		}

		// last event should be RawDocument
		assertTrue(event.getEventType() == EventType.RAW_DOCUMENT);
		// Get the EventsToRawDocumentStep output and compare it to our input
		assertEquals(htmlSnippet, convertRawDocumentToString((RawDocument)event.getResource()));
		eventToDoc.destroy();
	}

	private String convertRawDocumentToString(RawDocument d) throws IOException {		
		int c = -1;
		StringWriter sw = new StringWriter();
		while ((c = d.getReader().read()) != -1) {
			sw.append((char) c);
		}
		d.getReader().close();
		return sw.toString();
	}
}
