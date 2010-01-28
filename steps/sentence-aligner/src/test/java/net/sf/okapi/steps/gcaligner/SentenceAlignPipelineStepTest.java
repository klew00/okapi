package net.sf.okapi.steps.gcaligner;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SentenceAlignPipelineStepTest {	
	private Pipeline pipeline;
	private SentenceAlignerStep aligner;
	private EventObserver eventObserver;

	@Before
	public void setUp() throws Exception {
		// create pipeline
		pipeline = new Pipeline();
		eventObserver = new EventObserver();
		pipeline.addObserver(eventObserver);
		
		// add filter step
		IFilter filter = new PlainTextFilter();
		pipeline.addStep(new RawDocumentToFilterEventsStep(filter));
		
		// add aligner step
		aligner = new SentenceAlignerStep();
		
		aligner.setParameters(new Parameters());		
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.plaintext.PlainTextFilter");
		aligner.setFilterConfigurationMapper(fcMapper);		
		pipeline.addStep(aligner);
	
	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}
	
	@Test
	public void sentenceEnglishEnglishAlign() throws URISyntaxException {
		URL url = SentenceAlignPipelineStepTest.class.getResource("/src.txt");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH);
		t.setFilterConfigId("okf_plaintext");		
		aligner.setSecondInput(t);		
		aligner.setSourceLocale(LocaleId.ENGLISH);
		aligner.setTargetLocale(LocaleId.ENGLISH);
		
		pipeline.startBatch();		
		
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/src.txt"), "UTF-8", LocaleId.ENGLISH));

		pipeline.endBatch();			
		
		// test we observed the correct events
		List<Event> el = eventObserver.getResult();  
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		Event e = (el.remove(0));
		TextUnit tu = e.getTextUnit();
		assertNotNull(tu);
		assertEquals(EventType.TEXT_UNIT, e.getEventType());
		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}

	@Test
	public void sentenceAlignMultimatch() throws URISyntaxException {
		URL url = SentenceAlignPipelineStepTest.class.getResource("/trgMultimatch.txt");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.fromString("pt"));
		t.setFilterConfigId("okf_plaintext");
		
		aligner.setSecondInput(t);		
		aligner.setSourceLocale(LocaleId.ENGLISH);
		aligner.setTargetLocale(LocaleId.PORTUGUESE);
		
		pipeline.startBatch();				
		
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/srcMultimatch.txt"), "UTF-8", LocaleId.ENGLISH));

		pipeline.endBatch();
		
		// test we observed the correct events
		List<Event> el = eventObserver.getResult();  
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		Event e = (el.remove(0));
		TextUnit tu = e.getTextUnit();
		assertNotNull(tu);
		assertEquals(EventType.TEXT_UNIT, e.getEventType());
		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());

	}
}
