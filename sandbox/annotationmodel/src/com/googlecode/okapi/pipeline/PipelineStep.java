package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.resource.builder.IResourceBuilder;
import com.googlecode.okapi.resource.builder.ResourceEvent;

public abstract class PipelineStep implements IResourceBuilder{
	
	private IResourceBuilder input;
	
	public PipelineStep(IResourceBuilder input){
		this.input = input;
	}
	
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

}
