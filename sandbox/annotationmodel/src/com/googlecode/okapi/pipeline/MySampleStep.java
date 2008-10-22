package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.builder.IDocumentParser;
import com.googlecode.okapi.resource.builder.ResourceEvent;
import com.googlecode.okapi.resource.builder.ResourceEvent.ResourceEventType;

public class MySampleStep extends AbstractPipelineStep{

	public MySampleStep(){
		super();
	}
	
	
	public MySampleStep(IDocumentParser input){
		super(input);
	}
	
	@Override
	public void handleEvent(ResourceEvent event) {
		if(event.type == ResourceEventType.StartTextFlow){
			TextFlow value = (TextFlow) event.value;
		}
	}

}
