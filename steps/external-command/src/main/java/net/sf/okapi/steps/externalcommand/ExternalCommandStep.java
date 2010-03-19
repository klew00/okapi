package net.sf.okapi.steps.externalcommand;

import net.sf.okapi.common.pipeline.BasePipelineStep;

public class ExternalCommandStep extends BasePipelineStep {

	@Override
	public String getDescription() {		
		return "Execute an external command line program.";
	}

	@Override
	public String getName() {
		return "Execute External Command";
	}
}
