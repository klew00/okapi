package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.resource.builder.IDocumentParser;
import com.googlecode.okapi.resource.builder.IResourceProcessor;
import com.googlecode.okapi.resource.builder.ResourceEvent;

public abstract class AbstractPipelineStep implements IResourceProcessor{
	
	private IDocumentParser input;
	
	public AbstractPipelineStep(IDocumentParser input){
		this.input = input;
	}
	
	public AbstractPipelineStep(){}
	
	public final boolean hasNext() {
		return input.hasNext();
	}
	
	public final ResourceEvent next() {
		ResourceEvent event = input.next();
		handleEvent(event);
		return event;
	}
	
	public abstract void handleEvent(ResourceEvent event);
	
	public final void close() {
		input.close();
	}

	public void setInput(IDocumentParser input){
		this.input = input;
	}
}
