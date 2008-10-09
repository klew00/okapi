package com.googlecode.okapi.base.annotation.skeleton;

import com.googlecode.okapi.base.annotation.AbstractAnnotationManager;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.ResourceId;

public class SkeletonAnnotationManager extends AbstractAnnotationManager<SkeletonAnnotation>{
	
	public static final String ID = "skeleton";
	public static final int VERSION = 1;
	
	public SkeletonAnnotation create(ResourceId resourceId) {
		return new SkeletonAnnotation(resourceId);
	}

	public String getId() {
		return ID;
	}

	public int getVersion() {
		return VERSION;
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] getAdapterList() {
		return new Class[]{
				DataPart.class
		};
	}
}
