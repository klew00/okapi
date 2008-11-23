package net.sf.okapi.apptest.common;

public interface IResource {
	
	public String getId ();
	
	public void setId (String id);
	
	public ISkeleton getSkeleton ();
	
	public void setSkeleton (ISkeleton skeleton);

	public IAnnotation getAnnotation (String name);
	
	public void setAnnotation (String name, IAnnotation object);

}
