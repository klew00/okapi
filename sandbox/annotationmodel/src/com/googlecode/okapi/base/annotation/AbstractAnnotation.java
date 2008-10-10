package com.googlecode.okapi.base.annotation;

import com.googlecode.okapi.resource.ResourceId;

public abstract class AbstractAnnotation<T> implements IAnnotation<T>{

	private ResourceId target;
	
	public AbstractAnnotation(ResourceId target) {
		this.target = target;
	}
	
	public ResourceId getTarget() {
		return target;
	}

}
