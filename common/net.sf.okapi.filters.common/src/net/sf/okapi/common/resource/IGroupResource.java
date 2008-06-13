package net.sf.okapi.common.resource;

import java.util.List;

public interface IGroupResource extends ICommonResource {

	List<IBaseResource> getContent ();
	
	void add (IBaseResource resource);
	
	void remove (IBaseResource resource);

	String startToXML ();
	
	String endToXML ();
}
