package net.sf.okapi.filters.xml;

import net.sf.okapi.common.IParameters;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Resource extends net.sf.okapi.common.resource.ResourceBase {

	public Document          doc;
	public int               status;
	public Element           srcElem;
	public Element           trgElem;
	

	public Resource () {
		//TODO: params = new Parameters();
	}
	
	public IParameters getParameters () {
		return null; //TODO params;
	}

}
