package net.sf.okapi.utilities.reverse;

import net.sf.okapi.common.pipeline.PipelineException;
import net.sf.okapi.common.pipeline.PipelineStep;
import net.sf.okapi.common.pipeline.PipelineStepStatus;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResourceBuilder;

public class ReverseUtility implements PipelineStep {

    public PipelineStepStatus execute(IResourceBuilder resourceBuilder)
			throws PipelineException {
    	 for (IExtractionItem item : resourceBuilder.getResource().getExtractionItems()) {
    		 String reverse = new StringBuffer(item.getContent()).reverse().toString();
    	     item.setContent(reverse);
		 }
		return PipelineStepStatus.DEFAULT;
	}

	public void finish(boolean success) throws PipelineException {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}

	public void prepare() throws PipelineException {
		// TODO Auto-generated method stub

	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}
}
