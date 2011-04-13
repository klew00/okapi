package net.sf.okapi.steps.common.codesimplifier;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * !!! It's important to include this step in a pipeline before any source-copying or leveraging steps, because it can modify 
 * codes in the source, and target codes will easily get desynchronized with their sources.
 * The best place for this step -- right after the filter.  
 */
@UsingParameters(Parameters.class)
public class CodeSimplifierStep extends BasePipelineStep {
	
	private Parameters params;

	public CodeSimplifierStep() {
		super();
		params = new Parameters();
	}
	
	public String getDescription() {
		return "*Smartly* (code-type-awaredly) merges adjacent inline codes in the source part of a text unit. " +
				"Also where possible, moves leading and trailing codes of the TU source to the skeleton."
			+ " Expects: filter events. Sends back: filter events.";
	}

	public String getName() {
		return "Code Simplifier";
	}
	
	@Override
	public IParameters getParameters() {
		return params;
	}
	
	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters) params;
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();
		TextUnitUtil.simplifyCodes(tu, params.getRemoveLeadingTrailingCodes());
		
		return super.handleTextUnit(event);
	}
}
