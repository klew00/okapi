package net.sf.okapi.steps.diffleverage;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.sf.okapi.common.Event;
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
		el.get(0);
	}
}
