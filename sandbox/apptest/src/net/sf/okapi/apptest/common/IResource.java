package net.sf.okapi.apptest.common;

public interface IResource {
	
	public static final int CREATE_EMPTY = 0;
	public static final int COPY_CONTENT = 0x01;
	public static final int COPY_PROPERTIES = 0x02;
	public static final int COPY_ALL = (COPY_CONTENT | COPY_PROPERTIES);

	/**
	 * Gets the ID of the resource. This ID is unique per extracted document, and may be 
	 * different from one extraction to the next.
	 * It has no correspondence in the source document ("IDs" coming from the source document
	 * are "Names" and not available for all resources).
	 * @return The id of this resource.
	 */
	public String getId ();
	
	/**
	 * Sets the ID of this resource.
	 * @param id The new ID value.
	 */
	public void setId (String id);
	
	/**
	 * Gets the skeleton object for this resource.
	 * @return The skeleton object for this resource or null if there is none.
	 */
	public ISkeleton getSkeleton ();
	
	/**
	 * Sets the skeleton object for this resource.
	 * @param skeleton The skeleton object to set.
	 */
	public void setSkeleton (ISkeleton skeleton);

	/**
	 * Gets the annotation object for a given class for this resource.
	 * @param type The class of the annotation object to retrieve.
	 * @return The annotation for the given class for this resource. 
	 */
	public <A> A getAnnotation (Class<? extends IAnnotation> type);

	/**
	 * Sets an annotation object or this resource.
	 * @param annotation The annotation object to set.
	 */
	public void setAnnotation (IAnnotation annotation);
	
}
