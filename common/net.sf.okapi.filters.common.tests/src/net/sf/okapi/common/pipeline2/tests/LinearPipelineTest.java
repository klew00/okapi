package net.sf.okapi.common.pipeline2.tests;

import net.sf.okapi.common.pipeline2.ILinearPipeline;
import net.sf.okapi.common.pipeline2.LinearPipeline;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;


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
		PipelineReturnValue state = pipeline.execute();
		//pipeline.cancel();
		while (state == PipelineReturnValue.RUNNING || state == PipelineReturnValue.PAUSED) {
			System.out.println("Tick");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			pipeline.resume();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			state = pipeline.execute();
		}
	}

	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
	}

}
