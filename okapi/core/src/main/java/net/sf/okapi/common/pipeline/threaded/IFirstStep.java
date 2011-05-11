package net.sf.okapi.common.pipeline.threaded;

import java.util.concurrent.BlockingQueue;

import net.sf.okapi.common.Event;

public interface IFirstStep {
	public void setInputQueue(BlockingQueue<Event> inputQueue);
}
