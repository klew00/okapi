package com.googlecode.okapi.base.annotation.comments;

import com.googlecode.okapi.base.annotation.AbstractAnnotationManager;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.ResourceId;

public class CommentsAnnotationManager extends AbstractAnnotationManager<CommentsAnnotation>{

	public static final String ID = "com.googlecode.okapi.base.annotation.comments";
	public static final int VERSION = 1;
	
	public Class<?>[] getAdapterList() {
		return new Class[]{
				DocumentPart.class
		};
	}
	
	public CommentsAnnotation create(ResourceId resourceId) {
		return new CommentsAnnotation(resourceId);
	}
	
	public String getId() {
		return ID;
	}
	
	public int getVersion() {
		return VERSION;
	}
}
