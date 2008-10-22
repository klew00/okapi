package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.pipeline.ResourceEvent.ResourceEventType;
import com.googlecode.okapi.resource.TextFlow;

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
