package net.sf.okapi.steps.tests.integration;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.TextUnit;

class FindStringStep extends BasePipelineStep implements IPipelineStep {
	private String lookupString;
	private boolean found;
	
	public FindStringStep(String lookupString) {
		super();
		this.lookupString = lookupString;
		found = false;
	}

	public String getDescription() {		
		return "Finds a string in TextUnits";
	}

	public String getName() {
		return "Find String";
	}
	
	@Override
	protected void handleTextUnit(Event event) {
		String text = ((TextUnit)event.getResource()).getTarget("fr").toString();		
		if (text.contains(lookupString)) {			
			found = true;
		}
	};

	public boolean isFound() {		
		return found;
	}
}
