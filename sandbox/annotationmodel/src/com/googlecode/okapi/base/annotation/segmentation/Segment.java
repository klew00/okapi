package com.googlecode.okapi.base.annotation.segmentation;

import com.googlecode.okapi.base.annotation.IAnnotationFragment;
import com.googlecode.okapi.base.annotation.ResourceRange;

public class Segment implements IAnnotationFragment{
	
	private ResourceRange range;
	
	public String getId() {
		return null;
	}
	
	public ResourceRange getRange(){
		return range;
	}
	
	public void setRange(ResourceRange range){
		this.range = range;
	}
	
	
	
}
