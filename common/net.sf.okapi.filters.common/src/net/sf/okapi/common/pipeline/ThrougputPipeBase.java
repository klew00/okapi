package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public abstract class ThrougputPipeBase implements IResourceBuilder, IOutputPipe {

    private IResourceBuilder outputPipe;
    
    public void startResource(IResource resource) {
        outputPipe.startResource(resource);
    }

    public void endResource(IResource resource) {
        outputPipe.endResource(resource);        
    }

    public void startContainer(IResourceContainer resourceContainer) {
        outputPipe.startContainer(resourceContainer);
    }

    public void endContainer(IResourceContainer resourceCntainer) {
        outputPipe.endContainer(resourceCntainer);
    }

    //YS: See comment in IResourceBuilder
    public void startExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem) {
        outputPipe.startExtractionItem(sourceItem, targetItem);
    }

    public void endExtractionItem(IExtractionItem sourceItem,
    	IExtractionItem targetItem) {
        outputPipe.endExtractionItem(sourceItem, targetItem);
    }

    public void setOutput(IResourceBuilder inputBuilder) {
        this.outputPipe = inputBuilder;
    }

}
