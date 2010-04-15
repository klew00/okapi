package net.sf.okapi.steps.diffleverage;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiffLeverageStepTest {
	private Pipeline pipeline;
	private DiffLeverageStep diffLeverage;
	private EventObserver eventObserver;

	@Before
	public void setUp() throws Exception {
		// create pipeline
		pipeline = new Pipeline();
		eventObserver = new EventObserver();
		pipeline.addObserver(eventObserver);

		// add filter step
		IFilter filter = new POFilter();
		pipeline.addStep(new RawDocumentToFilterEventsStep(filter));

		// add DiffLeverage step
		diffLeverage = new DiffLeverageStep();

		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.po.POFilter");
		diffLeverage.setFilterConfigurationMapper(fcMapper);
		pipeline.addStep(diffLeverage);

	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}

	@Test
	public void diffLeverageSimplePOFiles() throws URISyntaxException {
		URL url = DiffLeverageStepTest.class.getResource("/Test_en_fr_old.po");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH);
		t.setFilterConfigId("okf_po");
		diffLeverage.setSecondInput(t);		
		diffLeverage.setTargetLocale(LocaleId.FRENCH);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/Test_en_fr_new.po"), "UTF-8",
				LocaleId.ENGLISH, LocaleId.FRENCH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();			
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		
		assertEquals(EventType.NO_OP, el.remove(0).getEventType());
		assertEquals(EventType.NO_OP, el.remove(0).getEventType());
		assertEquals(EventType.NO_OP, el.remove(0).getEventType());
		assertEquals(EventType.NO_OP, el.remove(0).getEventType());
		
		assertEquals(EventType.DOCUMENT_PART, el.remove(0).getEventType());
		
		Event tue1 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue1.getEventType());
		Assert.assertNotNull(tue1.getTextUnit().getAnnotation(DiffLeverageAnnotation.class));
		
		Event tue2 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue2.getEventType());
		Assert.assertNull(tue2.getTextUnit().getAnnotation(DiffLeverageAnnotation.class));
		
		Event tue3 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		Assert.assertNotNull(tue3.getTextUnit().getAnnotation(DiffLeverageAnnotation.class));
		
		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}
}
