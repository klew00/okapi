package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
		pipeline = new LinearPipeline();
	}

	@Test
	public void runPipeline() {
		pipeline.addPipleLineStep(new Producer());
		pipeline.addPipleLineStep(new ConsumerProducer());
		pipeline.addPipleLineStep(new Consumer());

		System.out.println("START PIPELINE");
		while (pipeline.execute()) {
			System.out.println("Tick");
		}
		
	}

	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
	}

}
