package com.googlecode.okapi.base.annotation.comments;

import com.googlecode.okapi.base.annotation.AbstractAnnotationManager;
import com.googlecode.okapi.base.annotation.IAnnotationManager;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceId;
import com.googlecode.okapi.resource.TextFlow;

public class CommentsAnnotationManager extends AbstractAnnotationManager<CommentsAnnotation>{

	public static final String ID = "com.googlecode.okapi.base.annotation.comments";
	public static final int VERSION = 1;
	
	@SuppressWarnings("unchecked")
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
