package net.sf.okapi.common.resource3.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.okapi.common.resource3.Code;
import net.sf.okapi.common.resource3.IRootContainer;
import net.sf.okapi.common.resource3.string.StringRootContainer;



/**
 * Corresponds to e.g. <source> or <target> in XLIFF. 
 */
public class TreeRootContainer extends ContainerBase implements IRootContainer {
	
	public List<SegmentContainer> getSegments(){
		List<SegmentContainer> segments = new ArrayList<SegmentContainer>();
		getSegmentsReqursive(this, segments);
		return Collections.unmodifiableList(segments);
	}
	
	private void getSegmentsReqursive(IContainer container, List<SegmentContainer> segments){
		for(IContent child : container){
			if(child instanceof SegmentContainer){
				segments.add((SegmentContainer)child);
				// TODO should we allow segments within segments?
			}
			else if(child instanceof IContainer){
				getSegmentsReqursive((IContainer)child, segments);
			}
		}
	}

	public List<Code> getCodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCodedText(String codedText, List<Code> codes) {
		// TODO Auto-generated method stub
		
	}
	public StringRootContainer getStringView() {
		// TODO Auto-generated method stub
		return null;
	}

	public TreeRootContainer getTreeView() {
		// TODO Auto-generated method stub
		return null;
	}

}
