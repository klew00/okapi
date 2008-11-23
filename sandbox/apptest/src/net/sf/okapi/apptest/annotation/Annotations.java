package net.sf.okapi.apptest.annotation;

import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.apptest.common.IAnnotation;

public class Annotations {
	
	private ConcurrentHashMap<Class<? extends IAnnotation>, IAnnotation> annotations;

	public Annotations () {
		annotations = new ConcurrentHashMap<Class<? extends IAnnotation>, IAnnotation>();
	}
	
	public void set (IAnnotation annotation) {
		annotations.put(annotation.getClass(), annotation);
	}
		
	@SuppressWarnings("unchecked")
	public <A> A get (Class<? extends IAnnotation> annotationType) {
		return (A) annotations.get(annotationType);
	}
}
