package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public abstract class ThrougputPipeBase implements IResourceBuilder, IOutputPipe {

    private IResourceBuilder outputPipe;
    
    
    public void startResource(IDocumentResource resource) {
        outputPipe.startResource(resource);
    }

    public void endResource(IDocumentResource resource) {
        outputPipe.endResource(resource);        
    }

    public void startContainer(IGroupResource resource) {
        outputPipe.startContainer(resource);
    }

    public void endContainer(IGroupResource resource) {
        outputPipe.endContainer(resource);
    }

    public void startExtractionItem (IExtractionItem item) {
        outputPipe.startExtractionItem(item);
    }

    public void endExtractionItem(IExtractionItem item) {
        outputPipe.endExtractionItem(item);
    }

    public void skeletonContainer (ISkeletonResource resource) {
    	outputPipe.skeletonContainer(resource);
    }
    
    public void setOutput(IResourceBuilder inputBuilder) {
        this.outputPipe = inputBuilder;
    }

}
