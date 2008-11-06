package net.sf.okapi.apptest.common;

import net.sf.okapi.apptest.resource.GenericSkeletonPart;

public interface ISkeleton {

	public void add (GenericSkeletonPart part);

	public void add (String data);

	public void addStartElement (String name);
	
	public void addEndElement (String name);
	
	public void addAttribute (String name, String value);
	
	public void flush ();

}
