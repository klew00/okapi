package net.sf.okapi.common.pipeline;

public interface IPipeline {

	public void execute();

	public PipelineReturnValue getState();

	public void cancel();

	public void pause();

	public void resume();

	public void addStep(IPipelineStep step);
}
