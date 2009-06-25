package net.sf.okapi.steps.copysourcetotarget;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextUnit;

public class CopySourceToTarget extends BasePipelineStep {
	Parameters params;
	
	public String getDescription() {		
		return "Copy the source segments to the specified target. Create the target if needed.";
	}

	public String getName() {
		return "Copy Source To Target";
	}
	
	@Override
	public Parameters getParameters() {
		return (Parameters)super.getParameters();
	}
	
	@Override
	public void handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		tu.createTarget(getParameters().targetLanguage, true, IResource.COPY_ALL);
	}	
}
