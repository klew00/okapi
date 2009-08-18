package net.sf.okapi.common.pipeline.integration;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.PipelineContext;
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
		eventToDoc = new FilterEventsToRawDocumentStep();
		
		RawDocument rawDoc = new RawDocument(htmlSnippet, "en");
		rawDoc.setFilterConfigId("okf_html");
		// FIXME: pipeline tests should not depend on pipeline driver
		BatchItemContext bic = new BatchItemContext(rawDoc, null, "UTF-8");
		PipelineContext c = new PipelineContext();
		eventToDoc.setContext(c);
		c.setBatchItemContext(bic);

		htmlFilter.open(rawDoc);
		while ( htmlFilter.hasNext() ) {
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
		eventToDoc = new FilterEventsToRawDocumentStep();
		
		File tmpFile = File.createTempFile("FilterEventsToRawDocumentStepTest", ".tmp");
		RawDocument rawDoc = new RawDocument(htmlSnippet, "en");
		rawDoc.setFilterConfigId("okf_html");
		BatchItemContext bic = new BatchItemContext(rawDoc, tmpFile.toURI(), "UTF-8");
		// FIXME: pipeline tests should not depend on pipeline driver
		PipelineContext c = new PipelineContext();
		eventToDoc.setContext(c);
		c.setBatchItemContext(bic);
		
		htmlFilter.open(rawDoc);
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
		int c;
		StringWriter sw = new StringWriter();
		Reader r = d.getReader(); 
		while (true) {
			c = r.read();			
			if (c == -1) break;							
			sw.append((char) c);			
		}
		d.getReader().close();
		return sw.toString();
	}
}
