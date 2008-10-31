package com.googlecode.okapi.resource;

import com.googlecode.okapi.base.apapters.IAdaptable;

public interface Resource<T extends ResourceId> extends IAdaptable, Immutable{

	public T getId();
	
}
