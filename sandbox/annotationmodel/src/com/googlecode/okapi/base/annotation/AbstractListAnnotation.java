package com.googlecode.okapi.base.annotation;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceId;

public abstract class AbstractListAnnotation<T> extends ArrayList<T> implements IListAnnotation<T>{

	private ResourceId target;
	
	public AbstractListAnnotation(ResourceId target) {
		this.target = target;
	}
	
	public List<T> get() {
		return this;
	}

	public ResourceId getTarget() {
		return target;
	}
	

}
