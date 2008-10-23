/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.threadedpipeline.tests;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.threadedpipeline.BasePipelineStep;
import net.sf.okapi.common.threadedpipeline.IConsumer;
import net.sf.okapi.common.threadedpipeline.IPipelineEvent;
import net.sf.okapi.common.threadedpipeline.IProducer;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

/**
 * @author HargraveJE
 * 
 */
public class ConsumerProducer extends BasePipelineStep implements IConsumer, IProducer {
	private BlockingQueue<IPipelineEvent> producerQueue;
	private BlockingQueue<IPipelineEvent> consumerQueue;

	public void setConsumerQueue(BlockingQueue<IPipelineEvent> consumerQueue) {
		this.consumerQueue = consumerQueue;
	}

	public void setProducerQueue(BlockingQueue<IPipelineEvent> producerQueue) {
		this.producerQueue = producerQueue;
	}

	public String getName() {
		return "ProducerConsumer";
	}

	public void finish() throws InterruptedException {
	}

	public void initialize() throws InterruptedException {

	}

	public PipelineReturnValue process() throws InterruptedException {
		IPipelineEvent event = consumerQueue.take();
		producerQueue.add(event);
		if (event.getEventType() == FilterEvent.FilterEventType.FINISHED) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}
}
