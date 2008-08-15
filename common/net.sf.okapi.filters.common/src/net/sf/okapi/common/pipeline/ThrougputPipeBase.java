package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public abstract class ThrougputPipeBase implements IResourceBuilder, IOutputPipe {

    private IResourceBuilder outputPipe;
    
    
    public void startResource(Document resource) {
        outputPipe.startResource(resource);
    }

    public void endResource(Document resource) {
        outputPipe.endResource(resource);        
    }

    public void startContainer(Group resource) {
        outputPipe.startContainer(resource);
    }

    public void endContainer(Group resource) {
        outputPipe.endContainer(resource);
    }

    public void startExtractionItem (TextUnit item) {
        outputPipe.startExtractionItem(item);
    }

    public void endExtractionItem(TextUnit item) {
        outputPipe.endExtractionItem(item);
    }

    public void skeletonContainer (SkeletonUnit resource) {
    	outputPipe.skeletonContainer(resource);
    }
    
    public void setOutput(IResourceBuilder inputBuilder) {
        this.outputPipe = inputBuilder;
    }

}
