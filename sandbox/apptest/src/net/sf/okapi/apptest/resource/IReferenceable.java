package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;

public interface IReferenceable extends IResource {

	public void setIsReference (boolean value);
	
	public boolean isReference ();
	
}
