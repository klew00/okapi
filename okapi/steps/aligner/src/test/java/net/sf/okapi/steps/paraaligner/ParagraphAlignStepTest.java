package net.sf.okapi.steps.paraaligner;

import java.net.URISyntaxException;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.EventObserver;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParagraphAlignStepTest {
	private Pipeline pipeline;
	private ParagraphAlignerStep aligner;
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
		aligner = new ParagraphAlignerStep();

		Parameters p = new Parameters();
		aligner.setParameters(p);
		
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
	public void englishToEnglishAlign() throws URISyntaxException {
	}
}
