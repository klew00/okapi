package net.sf.okapi.common.pipeline2.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.okapi.common.pipeline2.ILinearPipeline;
import net.sf.okapi.common.pipeline2.IPipelineStep;
import net.sf.okapi.common.pipeline2.LinearPipeline;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LinearPipelineTest {

	@Before
	public void setUp() {

	}

	@Test
	public void runPipelineAsThread() {
		final ILinearPipeline pipeline = new LinearPipeline();

		Runnable runnable = new Runnable() {
			public void run() {
				pipeline.addPipleLineStep(new Producer());
				pipeline.addPipleLineStep(new ConsumerProducer());
				pipeline.addPipleLineStep(new Consumer());
				pipeline.execute();
			}
		};

		ExecutorService e = Executors.newSingleThreadExecutor();
		e.execute(runnable);

		boolean stop = false;
		while (!stop) {
			switch (pipeline.getState()) {
			case CANCELLED:
			case SUCCEDED:
			case FAILED:
			case INTERRUPTED:
				stop = true;
				break;

			default:
				// still running
				break;
			}
		}
	}

	// @Test
	public void runPipeline() {
		ILinearPipeline pipeline = new LinearPipeline();
		pipeline.addPipleLineStep(new Producer());
		pipeline.addPipleLineStep(new ConsumerProducer());
		pipeline.addPipleLineStep(new Consumer());

		System.out.println("START PIPELINE");
		pipeline.execute();
		while (pipeline.getState() == PipelineReturnValue.RUNNING || pipeline.getState() == PipelineReturnValue.PAUSED) {
			pipeline.pause();
			if (pipeline.getState() == PipelineReturnValue.PAUSED) {
				System.out.println("Paused");
			} else {
				System.out.println("Running");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pipeline.resume();
			if (pipeline.getState() == PipelineReturnValue.PAUSED) {
				System.out.println("Paused");
			} else {
				System.out.println("Running");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
	}

}
