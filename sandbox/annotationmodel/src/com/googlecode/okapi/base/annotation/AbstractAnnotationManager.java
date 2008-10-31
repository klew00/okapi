package com.googlecode.okapi.base.annotation;

import java.util.Map;

import com.googlecode.okapi.dom.Resource;
import com.googlecode.okapi.resource.ResourceId;

public abstract class AbstractAnnotationManager<T extends IAnnotation<?>> implements IAnnotationManager<T>{

	Map<ResourceId, T> annotations; 
	
	public T get(ResourceId resourceId) {
		return annotations.get(resourceId);
	}
	
	public T getOrCreate(ResourceId resourceId) {
		T annotation = get(resourceId);
		if(annotation == null){
			annotation = create(resourceId);
		}
		return annotation;
	}
	
	public <A> A getAdapter(Object adaptableObject, Class<A> adapterType) {
		if(adaptableObject instanceof Resource<?>){
			Resource<?> res = (Resource<?>) adaptableObject;
			return (A) get(res.getId());
		}
		return null;
	}

}
