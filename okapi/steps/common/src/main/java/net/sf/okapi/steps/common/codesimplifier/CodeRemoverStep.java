package net.sf.okapi.steps.common.codesimplifier;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * Removes all inline codes from the source and all target {@link TextUnit}s.
 * @author hargraveje
 */
public class CodeRemoverStep extends BasePipelineStep {
	@Override
	public String getName() {		
		return "Inline Code Remover Step";
	}

	@Override
	public String getDescription() {
		return "Remove all inline codes from each TextUnit";
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();
		TextUnitUtil.removeCodes(tu, true);		
		return event;
	}
}
