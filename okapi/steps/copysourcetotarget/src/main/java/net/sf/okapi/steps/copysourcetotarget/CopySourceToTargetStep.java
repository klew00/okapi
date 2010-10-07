package net.sf.okapi.steps.copysourcetotarget;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.TextUnit;

@UsingParameters(Parameters.class)
public class CopySourceToTargetStep extends BasePipelineStep {
	private Parameters params;
	private LocaleId targetLocale;

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
		this.params = (Parameters) params;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@Override
	public Parameters getParameters() {
		return params;
	}

	@Override
	public Event handleTextUnit(Event event) {
		TextUnit tu = event.getTextUnit();

		// initialize the copy options
		int copyOptions = IResource.CREATE_EMPTY;

		if (params.isCopyContent()) {
			copyOptions |= IResource.COPY_CONTENT;
		}
		if (params.isCopyProperties()) {
			copyOptions |= IResource.COPY_PROPERTIES;
		}

		tu.createTarget(targetLocale, params.isOverwriteExisting(), copyOptions);
		return event;
	}
}
