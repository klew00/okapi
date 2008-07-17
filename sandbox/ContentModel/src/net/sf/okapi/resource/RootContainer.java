package net.sf.okapi.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Corresponds to e.g. <source> or <target> in XLIFF. 
 */
public class RootContainer extends ContainerBase{
	
	public List<SegmentContainer> getSegments(){
		List<SegmentContainer> segments = new ArrayList<SegmentContainer>();
		getSegmentsReqursive(this, segments);
		return Collections.unmodifiableList(segments);
	}
	
	private void getSegmentsReqursive(IContentContainer container, List<SegmentContainer> segments){
		for(IContent child : container){
			if(child instanceof SegmentContainer){
				segments.add((SegmentContainer)child);
				// TODO should we allow segments within segments?
			}
			else if(child instanceof IContentContainer){
				getSegmentsReqursive((IContentContainer)child, segments);
			}
		}
	}
	
}
