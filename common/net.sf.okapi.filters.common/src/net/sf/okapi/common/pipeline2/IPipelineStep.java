package net.sf.okapi.common.pipeline2;

import java.util.concurrent.Callable;

public interface IPipelineStep extends Callable<PipelineReturnValue> {

	public String getName();
	
	void initialize() throws InterruptedException;
	
	PipelineReturnValue process() throws InterruptedException;
	
	void finish() throws InterruptedException;

	void pause();

	void resume();
}
