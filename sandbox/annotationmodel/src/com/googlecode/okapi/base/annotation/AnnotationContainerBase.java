package com.googlecode.okapi.base.annotation;

import java.util.HashMap;
import java.util.Map;

public abstract class AnnotationContainerBase<T extends IAnnotation> implements IAnnotationContainer<T>{

	private Map<String, T> annotations = new HashMap<String, T>();
	
	public T getAnnotation(String resourceId) {
		return annotations.get(resourceId);
	}

	public Map<String, T> getAnnotations() {
		return annotations;
	}

}
