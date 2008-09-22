/**
 * 
 */
package net.sf.okapi.common.pipeline2;

import java.util.concurrent.BlockingQueue;

/**
 * @author HargraveJE
 *
 */
public interface IProducer {
	
	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue);	
}
