package net.sf.okapi.common.pipeline.tests;


import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.IPipeline;

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
	public void runPipeline() {
		IPipeline pipeline = new Pipeline();
		pipeline.addStep(new FileStepProducer());
		pipeline.addStep(new FileStepConsumer());
		pipeline.addStep(new Consumer());

		System.out.println("START PIPELINE");
		pipeline.execute();		
	}

}
