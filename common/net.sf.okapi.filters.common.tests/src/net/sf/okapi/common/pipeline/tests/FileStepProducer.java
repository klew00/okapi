package net.sf.okapi.common.pipeline.tests;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MemMappedCharSequence;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.FileResource;

public class FileStepProducer extends BasePipelineStep {
	private boolean hasNext = true;
	
	@Override
	public Event handleEvent(Event event) {	
		if (!hasNext) {			
			return null;
		}
		
		FileResource r = new FileResource(new MemMappedCharSequence("Test a file event"), "plain/txt", "en");
		Event e = new Event(EventType.FILE_RESOURCE, r);
		hasNext = false;
		return e;
	}
	
	public boolean hasNext() {
		return hasNext;
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
		return "FileStep1";
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
