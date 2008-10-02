package com.googlecode.okapi.base.annotation;

import java.util.Map;

public interface IAnnotationContainer<T extends IAnnotation>{
	
	public Class<?>[] getTargets();
	
	public String getId();

	public Map<String,T> getAnnotations();
	
	public T getAnnotation(String resourceId);
	
}
