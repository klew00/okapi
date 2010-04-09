package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;

public class DummyCustomEventStep extends BasePipelineStep {

	private boolean isDone = false;


	public String getDescription() {
		return "Dummy step for testing";
	}

	public String getName() {
		return "DummyStep";
	}
	
	protected Event handleCustom(Event event) {			
		isDone = true;
		return event;
	}
	
	public boolean isDone() {
		return isDone;
	}
}
