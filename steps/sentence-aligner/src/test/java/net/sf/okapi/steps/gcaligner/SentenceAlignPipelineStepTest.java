package net.sf.okapi.steps.gcaligner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.lib.segmentation.SegmentationTest;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SentenceAlignPipelineStepTest {	
	private Pipeline pipeline;
	private SentenceAlignerStep aligner;

	@Before
	public void setUp() throws Exception {
		// create pipeline
		pipeline = new Pipeline();
		
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
	}
}
