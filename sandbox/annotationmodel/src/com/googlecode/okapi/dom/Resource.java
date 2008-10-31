package com.googlecode.okapi.dom;

import com.googlecode.okapi.dom.apapters.IAdaptable;
import com.googlecode.okapi.resource.ResourceId;

public interface Resource<T extends ResourceId> extends IAdaptable, Immutable{

	public T getId();
	
}
