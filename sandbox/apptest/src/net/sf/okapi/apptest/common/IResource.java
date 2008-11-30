package net.sf.okapi.apptest.common;

public interface IResource {
	
	public static final int CREATE_EMPTY = 0;
	public static final int COPY_CONTENT = 0x01;
	public static final int COPY_PROPERTIES = 0x02;
	public static final int COPY_ALL = (COPY_CONTENT | COPY_PROPERTIES);

	public String getId ();
	
	public void setId (String id);
	
	public ISkeleton getSkeleton ();
	
	public void setSkeleton (ISkeleton skeleton);

	public <A> A getAnnotation (Class<? extends IAnnotation> type);

	public void setAnnotation (IAnnotation annotation);
	
}
