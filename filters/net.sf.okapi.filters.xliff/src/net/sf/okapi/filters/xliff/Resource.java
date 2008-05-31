package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.xliff.Parameters;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Resource extends net.sf.okapi.common.resource.ResourceBase {

	public Document          doc;
	public int               status;
	public Element           srcElem;
	public Element           trgElem;
	public Parameters        params;
	

	public Resource () {
		params = new Parameters();
	}
	
	public IParameters getParameters () {
		return params;
	}

}
