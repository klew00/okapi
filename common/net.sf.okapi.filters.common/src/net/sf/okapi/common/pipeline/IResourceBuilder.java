package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public interface IResourceBuilder {

    public void startResource (Document resource);

    public void endResource (Document resource);
    
    public void startExtractionItem (TextUnit item);
    
    public void endExtractionItem (TextUnit item);
    
    public void startContainer (Group resource);
    
    public void endContainer (Group resource);

    public void skeletonContainer (SkeletonUnit resource);
}

