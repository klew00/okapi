package net.sf.okapi.steps.copysourcetotarget;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextUnit;

public class CopySourceToTargetStep extends BasePipelineStep {
	private Parameters params;
	
	public CopySourceToTargetStep() {
		params = new Parameters();
	}
	
	public String getDescription() {		
		return "Copy the source segments to the specified target. Create the target if needed.";
	}

	public String getName() {
		return "Copy Source To Target";
	}
	
	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters)params;
	}
	
	@Override
	public Parameters getParameters() {
		return params;
	}
	
	@Override
	public void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		tu.createTarget(getParameters().targetLanguage, false, IResource.COPY_ALL);
	}	
}
