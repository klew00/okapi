package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class UtilityStep extends BasePipelineStep {

	private IFilterDrivenUtility utility;

	public UtilityStep (IFilterDrivenUtility utility) {
		this.utility = utility;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		//TODO: IFilterWriter handleEvent should probably return its own event
		utility.handleEvent(event);
		return event;
	}
	
	public void cancel () {
		// Cancel needed here
	}

	public String getName () {
		return utility.getName();
	}

	public void pause () {
	}

	public void postprocess () {
		utility.finish();
	}

	public void preprocess () {
	}

	public void resume() {
	}
	
}
