package net.sf.okapi.apptest.common;

public interface IResource {
	
	public String getID ();
	
	public void setID (String id);
	
	public ISkeleton getSkeleton ();
	
	public void setSkeleton (ISkeleton skeleton);
}
