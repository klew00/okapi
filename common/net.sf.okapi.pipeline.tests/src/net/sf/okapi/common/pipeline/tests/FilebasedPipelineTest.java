package net.sf.okapi.common.pipeline.tests;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.PipelineReturnValue;
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
		pipeline.addStep(new ConsumerProducer());
		pipeline.addStep(new Consumer());

		assertEquals(PipelineReturnValue.RUNNING, pipeline.getState());
		pipeline.process(new FileResource("<b>Test this resource</b>", "en"));	
		pipeline.destroy();
		assertEquals(PipelineReturnValue.DESTROYED, pipeline.getState());
	}
}
