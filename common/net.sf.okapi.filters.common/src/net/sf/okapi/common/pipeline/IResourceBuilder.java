package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public interface IResourceBuilder {

    public void startResource(IResource resource);
    public void endResource(IResource resource);
    
    public void startExtractionItem(IExtractionItem extractionItem);
    public void endExtractionItem(IExtractionItem extractionItem);
    
    public void startContainer(IResourceContainer resourceContainer);
    public void endContainer(IResourceContainer resourceCntainer);
    
}

