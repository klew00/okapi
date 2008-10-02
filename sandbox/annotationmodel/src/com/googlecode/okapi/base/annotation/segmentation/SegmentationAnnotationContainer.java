package com.googlecode.okapi.base.annotation.segmentation;

import com.googlecode.okapi.base.annotation.AnnotationContainerBase;
import com.googlecode.okapi.base.annotation.IAnnotatable;
import com.googlecode.okapi.resource.ResourceDescriptor;

public class SegmentationAnnotationContainer extends AnnotationContainerBase<SegmentationAnnotation>{

	private static final String id = "com.googlecode.okapi.base.annotations.segmentation";
	
	public String getId() {
		return id;
	}

	public Class<? extends IAnnotatable>[] getTargets() {
		return new Class[]{
				IAnnotatable.class,
				ResourceDescriptor.class
		};
		
		
	}

}
