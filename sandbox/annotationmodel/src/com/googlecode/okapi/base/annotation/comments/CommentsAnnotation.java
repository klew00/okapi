package com.googlecode.okapi.base.annotation.comments;

import com.googlecode.okapi.base.annotation.AbstractListAnnotation;
import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceId;

public class CommentsAnnotation extends AbstractListAnnotation<Comment>{

	public CommentsAnnotation(ResourceId target) {
		super(target);
	}
	

}
