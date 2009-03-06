package net.sf.okapi.common.pipeline.tests;

import java.net.URISyntaxException;

import net.sf.okapi.common.pipeline.FileResourceInitialPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.resource.FileResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilebasedPipelineTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void runPipeline() throws URISyntaxException {
		IPipeline pipeline = new Pipeline();
		pipeline.addStep(new FileResourceInitialPipelineStepAdaptor(new FileResource("<b>Test this resource</b>", "text/html", "en")));
		pipeline.addStep(new ConsumerProducer());
		pipeline.addStep(new Consumer());

		System.out.println("START PIPELINE");		
		pipeline.process();		
		pipeline.destroy();
		System.out.println("CLEANUP PIPELINE");		
	}
}
