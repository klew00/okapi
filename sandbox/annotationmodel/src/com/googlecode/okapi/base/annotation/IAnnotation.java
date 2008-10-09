package com.googlecode.okapi.base.annotation;

import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceId;

public interface IAnnotation<T> {
	
	public ResourceId getTarget();
	
	public T get();
	
}
