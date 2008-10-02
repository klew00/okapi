package com.googlecode.okapi.base.annotation.segmentation;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.okapi.base.annotation.IAnnotatable;
import com.googlecode.okapi.base.annotation.IAnnotationFragmentContainer;

public class SegmentationAnnotation implements IAnnotationFragmentContainer<Segment>, IAnnotatable{
	
	public List<Segment> fragments = new ArrayList<Segment>();
	public IAnnotatable target;
	
	public SegmentationAnnotation(IAnnotatable target){
		this.target = target;
	}
	
	public List<Segment> getFragments() {
		return fragments;
	}

	public IAnnotatable getTarget() {
		return target;
	}
	
	public String getId() {
		return getTarget().getId();
	}

}
