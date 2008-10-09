package com.googlecode.okapi.base.annotation.skeleton;

import com.googlecode.okapi.base.annotation.AbstractAnnotation;
import com.googlecode.okapi.resource.ResourceId;

public class SkeletonAnnotation extends AbstractAnnotation<Skeleton>{

	Skeleton skl;
	
	public SkeletonAnnotation(ResourceId target) {
		super(target);
		skl = new Skeleton();
	}
	
	public Skeleton get() {
		return skl;
	}
	
	

}
