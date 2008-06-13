package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public interface IResourceBuilder {

    public void startResource (IDocumentResource resource);

    public void endResource (IDocumentResource resource);
    
    public void startExtractionItem (IExtractionItem item);
    
    public void endExtractionItem (IExtractionItem item);
    
    public void startContainer (IGroupResource resource);
    
    public void endContainer (IGroupResource resource);

    public void skeletonContainer (ISkeletonResource resource);
}

