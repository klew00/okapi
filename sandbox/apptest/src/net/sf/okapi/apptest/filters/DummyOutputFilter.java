package net.sf.okapi.apptest.filters;

import net.sf.okapi.common.pipeline2.PipelineEvent;
import net.sf.okapi.common.pipeline2.PipelineEvent.PipelineEventType;
import net.sf.okapi.common.resource.TextUnit;

public class DummyOutputFilter implements IOutputFilter2 {
	
	public void process(PipelineEvent event) {
		switch ( (PipelineEventType)event.getEventType() ) {
		case FINISHED:
			System.out.println("done---");
			break;
		case TEXTUNIT:
			System.out.print("text-unit: ");
			System.out.println(((TextUnit)event.getData()).toString());
			break;
		}
	}

}
