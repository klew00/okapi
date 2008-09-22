package net.sf.okapi.common.pipeline2.tests;

import net.sf.okapi.common.pipeline2.ILinearPipeline;
import net.sf.okapi.common.pipeline2.LinearPipeline;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LinearPipelineTest {
	private ILinearPipeline pipeline;
	
	@Before
	public void setUp() {
		pipeline = new LinearPipeline(4);
	}
	
	@Test
	public void runPipeline() {
		pipeline.addPipleLineStep(new Producer());
		pipeline.addPipleLineStep(new ConsumerProducer());
		pipeline.addPipleLineStep(new Consumer());
		
		Thread pipelineThread = new Thread(pipeline);
		pipelineThread.start();
		
		pipeline.start();
		System.out.println("START PIPELINE");
	}
	
	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
		pipeline.cancel();
	}
	
}
