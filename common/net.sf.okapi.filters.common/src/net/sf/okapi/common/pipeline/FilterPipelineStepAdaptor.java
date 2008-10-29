package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;

public class FilterPipelineStepAdaptor extends BasePipelineStep {
	private IFilter filter;
	
	public FilterPipelineStepAdaptor(IFilter filter) {
		this.filter = filter;
	}
	
	public String getName() {		
		return filter.getName();
	}		

	@Override
	public FilterEvent handleEvent(FilterEvent event) {
		return filter.next();		
	}
	
	public void preprocess() {}

	public void postprocess() {
		filter.close();
	}
	
	public void cancel() {
		filter.cancel();
	}

	public void pause() {
	}

	public void resume() {
	}
}
