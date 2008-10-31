package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.events.Event;


public abstract class AbstractPipelineStep implements IResourceProcessor{
	
	private IDocumentParser input;
	
	public AbstractPipelineStep(IDocumentParser input){
		this.input = input;
	}
	
	public AbstractPipelineStep(){}
	
	public final boolean hasNext() {
		return input.hasNext();
	}
	
	public final Event next() {
		Event event = input.next();
		handleEvent(event);
		return event;
	}
	
	public abstract void handleEvent(Event event);
	
	public final void close() {
		input.close();
	}

	public void setInput(IDocumentParser input){
		this.input = input;
	}
}
