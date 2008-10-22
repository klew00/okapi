package com.googlecode.okapi.resource;

import com.googlecode.okapi.base.apapters.IAdaptable;

public interface Resource<T extends ResourceId> extends IAdaptable{

	public T getId();
	
}
