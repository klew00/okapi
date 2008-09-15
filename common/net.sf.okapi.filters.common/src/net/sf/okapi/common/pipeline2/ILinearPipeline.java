/**
 * 
 */
package net.sf.okapi.common.pipeline2;

/**
 * @author HargraveJE
 *
 */
public interface ILinearPipeline {
	
	public void start();
	
	public void cancel();
	
	public void pause();
	
	public void resume();
	
	public void addPipleLineStep(IPipelineStep step);
	
	public void addPipleLineStep(IPipelineStep step, int numThreads);
}
