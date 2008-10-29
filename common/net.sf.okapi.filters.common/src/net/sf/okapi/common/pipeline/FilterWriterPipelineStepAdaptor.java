package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilterWriter;

public class FilterWriterPipelineStepAdaptor extends BasePipelineStep {
	private IFilterWriter filterWriter;

	public FilterWriterPipelineStepAdaptor(IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	public void cancel() {
	}

	public String getName() {
		return filterWriter.getName();
	}
	
	@Override
	public FilterEvent handleEvent(FilterEvent event) {
		return filterWriter.handleEvent(event);		
	}

	public void pause() {
	}

	public void postprocess() {
		filterWriter.close();
	}

	public void preprocess() {
	}

	public void resume() {
	}
}
