/**
 * 
 */
package net.sf.okapi.common.pipeline2;

/**
 * @author HargraveJE
 *
 */
public interface ILinearPipeLine {
	
	public void start();
	
	public void cancel();
	
	public void pause();
	
	public void resume();
	
	public void addPipleLineStep(IPipeLineStep step);
	
	public void addPipleLineStep(IPipeLineStep step, int numThreads);
}
