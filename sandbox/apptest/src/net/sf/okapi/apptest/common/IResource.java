package net.sf.okapi.apptest.common;

public interface IResource {
	
	public static final int DO_NOTHING = 0;
	public static final int CREATE_EMPTY = 1;
	public static final int CREATE_CLONE = 2;
	
	public String getId ();
	
	public void setId (String id);
	
	public ISkeleton getSkeleton ();
	
	public void setSkeleton (ISkeleton skeleton);

	public <A> A getAnnotation (Class<? extends IAnnotation> type);

	public void setAnnotation (IAnnotation annotation);
	
}
