package net.sf.okapi.common.pipeline.tests;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;

public class FileStepConsumer extends BasePipelineStep {

	@Override
	protected void handleFileResource(Event event) {
		System.out.println("EventType: " + event.getEventType().name());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IPipelineStep#cancel()
	 */
	public void cancel() {
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IPipelineStep#getName()
	 */
	public String getName() {
		return "FileStep2";
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IPipelineStep#postprocess()
	 */
	public void postprocess() {
		System.out.println(getName() + " postprocess");
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IPipelineStep#preprocess()
	 */
	public void preprocess() {
		System.out.println(getName() + " preprocess");	
	}
}
