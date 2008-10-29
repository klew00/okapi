package net.sf.okapi.common.simplepipeline;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.PipelineReturnValue;

public class SimplePipeline implements IPipeline {
	List<IPipelineStep> steps;
	IPipelineStep initialStep;
	boolean cancel = false;
	boolean pause = false;
	boolean stop = false;
	boolean first = true;

	public SimplePipeline() {
		steps = new ArrayList<IPipelineStep>();
	}

	public void addStep(IPipelineStep step) {
		if (first) {
			initialStep = step;
			first = false;
			return;
		} 
		steps.add(step);
	}

	public void cancel() {
		cancel = true;
	}

	public void execute() {
		// preprocess
		for (IPipelineStep step : steps) {
			if (cancel)
				return;
			step.preprocess();
		}

		while (!stop) {
			if (pause)
				continue;
			FilterEvent event = initialStep.handleEvent(null);
			for (IPipelineStep step : steps) {
				step.handleEvent(event);
			}
			if (event.getEventType() == FilterEventType.FINISHED) {
				stop = true;
			}			
		}

		// postprocess (cleanup)
		for (IPipelineStep step : steps) {
			step.postprocess();
		}
	}

	public PipelineReturnValue getState() {
		return null;
	}

	public void pause() {
		pause = true;
	}

	public void resume() {
		pause = false;
	}
}
