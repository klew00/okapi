package net.sf.okapi.common.resource;

public interface IFragment {

	String toString ();
	
	IFragment clone ();
	
	boolean isText ();
	
	String toXML (IPart parent);

}
