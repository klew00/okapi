/**
 * 
 */
package net.sf.okapi.common.pipeline2;

import java.util.concurrent.BlockingQueue;

/**
 * @author HargraveJE
 *
 */
public interface IConsumer {

	public void setConsumerQueue(BlockingQueue<PipelineEvent> consumerQueue);
	
}
