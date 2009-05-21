package net.sf.okapi.common;

import net.sf.okapi.common.annotation.IAnnotation;

public interface IContext {

	public String getString (String name);
	
	public void setString (String name,
		String value);

	public boolean getBoolean (String name);
	
	public void setBoolean (String name,
		boolean value);
	
	public int getInteger (String name);
	
	public void setInteger (String name,
		int value);
	
	public void clearProperties ();
	
	public <A> A getAnnotation (Class<? extends IAnnotation> type);

	public void setAnnotation (IAnnotation annotation);
	
	public void clearAnnotations ();

}
