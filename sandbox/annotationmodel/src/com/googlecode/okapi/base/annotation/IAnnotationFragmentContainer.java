package com.googlecode.okapi.base.annotation;

import java.util.List;

public interface IAnnotationFragmentContainer<T extends IAnnotationFragment> extends IAnnotation{
	
	public List<T> getFragments();
	
	
}
