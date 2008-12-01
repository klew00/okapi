package net.sf.okapi.apptest.common;

public interface IReferenceable {

	/**
	 * Sets the flag indicating if this resource is a referent (i.e. is referred to by 
	 * another resource) or not.
	 * @param value True if the resource is a referent, false if it is not.
	 */
	public void setIsReferent (boolean value);
	
	/**
	 * Indicates if this resource is a referent (i.e. is referred to by another resource)
	 * or not.
	 * @return True if this resource is a referent, false if it is not.
	 */
	public boolean isReferent ();

}
