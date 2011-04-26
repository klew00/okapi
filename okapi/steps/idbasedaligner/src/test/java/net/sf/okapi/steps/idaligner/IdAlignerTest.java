package net.sf.okapi.steps.idaligner;

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
import net.sf.okapi.filters.properties.PropertiesFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class IdAlignerTest {
	private Pipeline pipeline;
	private IdBasedAlignerStep aligner;
	private EventObserver eventObserver;

	@Before
	public void setUp() throws Exception {
		// create pipeline
		pipeline = new Pipeline();
		eventObserver = new EventObserver();
		pipeline.addObserver(eventObserver);

		// add filter step
		IFilter filter = new PropertiesFilter();
		pipeline.addStep(new RawDocumentToFilterEventsStep(filter));

		// add aligner step
		aligner = new IdBasedAlignerStep();

		Parameters p = new Parameters();
		p.setGenerateTMX(false);
		aligner.setParameters(p);
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.properties.PropertiesFilter");
		aligner.setFilterConfigurationMapper(fcMapper);
		pipeline.addStep(aligner);

	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}

	@Test
	public void IdEnglishEnglishAlign() throws URISyntaxException {
		URL url = IdAlignerTest.class
				.getResource("/messages_en-brief.properties");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH);
		t.setFilterConfigId("okf_properties");
		aligner.setSecondInput(t);
		aligner.setSourceLocale(LocaleId.ENGLISH);
		aligner.setTargetLocale(LocaleId.ENGLISH);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream(
				"/messages_en-brief.properties"), "UTF-8", LocaleId.ENGLISH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());

		Event tue = el.remove(0);
		assertEquals("Cancel", tue.getResource().toString());
		assertEquals("key.Cancel", tue.getTextUnit().getName());

		tue = el.remove(0);
		assertEquals("Unable to communicate with the <b>server</b>.", tue
				.getResource().toString());
		assertEquals("key.server", tue.getTextUnit().getName());

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}

	@Test
	public void IdSourceTargetAlign() throws URISyntaxException {
		URL url = IdAlignerTest.class
				.getResource("/messages_de-brief.properties");
		RawDocument t = new RawDocument(url.toURI(), "ASCII", LocaleId.ENGLISH);
		t.setFilterConfigId("okf_properties");
		aligner.setSecondInput(t);
		aligner.setSourceLocale(LocaleId.ENGLISH);
		aligner.setTargetLocale(LocaleId.GERMAN);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream(
				"/messages_de-brief.properties"), "ASCII", LocaleId.GERMAN));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());

		Event tue = el.remove(0);
		assertTrue(tue.getTextUnit().hasTarget(LocaleId.GERMAN));
		assertEquals("Abbrechen", tue.getTextUnit().getTarget(LocaleId.GERMAN)
				.toString());

		tue = el.remove(0);
		assertTrue(tue.getTextUnit().hasTarget(LocaleId.GERMAN));
		assertEquals(
				"Es konnte keine Verbindung zum <b>Server</b> aufgebaut werden.",
				tue.getTextUnit().toString());

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}

}
