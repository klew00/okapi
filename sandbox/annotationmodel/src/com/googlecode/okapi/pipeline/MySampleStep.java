package com.googlecode.okapi.pipeline;

import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.builder.IResourceBuilder;
import com.googlecode.okapi.resource.builder.ResourceEvent;
import com.googlecode.okapi.resource.builder.ResourceEvent.ResourceEventType;

public class MySampleStep extends PipelineStep{

	public MySampleStep(IResourceBuilder input){
		super(input);
	}
	
	@Override
	public void handleEvent(ResourceEvent event) {
		if(event.type == ResourceEventType.StartTextFlow){
			TextFlow value = (TextFlow) event.value;
		}
	}

}
