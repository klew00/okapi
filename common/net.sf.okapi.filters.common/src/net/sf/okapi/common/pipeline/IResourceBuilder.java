package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public interface IResourceBuilder {

    public void startResource(IResource resource);
    public void endResource(IResource resource);
    
    //YS: Added targetItem to try out. Maybe instead the target
    // can be obtained from the source?
    public void startExtractionItem (IExtractionItem item);
    
    public void endExtractionItem(IExtractionItem item);
    
    public void startContainer(IResourceContainer resourceContainer);
    public void endContainer(IResourceContainer resourceCntainer);
    
}

