package net.sf.okapi.filters.xml;

import net.sf.okapi.common.IParameters;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Resource extends net.sf.okapi.common.resource.Document {

	public Document          doc;
	public Node              srcNode;
	//public Node              trgNode;
	

	public Resource () {
		//TODO: params = new Parameters();
	}
	
	public IParameters getParameters () {
		return null; //TODO params;
	}

}
