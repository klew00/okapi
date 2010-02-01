package net.sf.okapi.common.pipeline.integration;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.TextUnit;

class FindStringStep extends BasePipelineStep implements IPipelineStep {

	private String lookupString;
	private boolean found;
	private LocaleId locFR = LocaleId.fromString("FR");
	
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
	protected Event handleTextUnit(Event event) {
		String text = ((TextUnit)event.getResource()).getTarget(locFR).toString();		
		if (text.contains(lookupString)) {			
			found = true;
		}
		
		return event;
	};

	public boolean isFound() {		
		return found;
	}
}
