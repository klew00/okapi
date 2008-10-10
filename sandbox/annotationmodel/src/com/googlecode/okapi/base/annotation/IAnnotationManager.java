package com.googlecode.okapi.base.annotation;

import com.googlecode.okapi.base.apapters.IAdapterFactory;
import com.googlecode.okapi.resource.ResourceId;

public interface IAnnotationManager<T extends IAnnotation<?>> extends IAdapterFactory{

	public T get(ResourceId resourceId);
	
	public T create(ResourceId resourceId);
	
	public T getOrCreate(ResourceId resourceId);

	public String getId();
	
	public int getVersion();
}

