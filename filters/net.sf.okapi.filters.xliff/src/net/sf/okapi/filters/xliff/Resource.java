package net.sf.okapi.filters.xliff;

import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.filters.xliff.Parameters;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	public Document          doc;
	public int               status;
	public Element           srcElem;
	public Element           trgElem;

	protected Parameters               params;
	protected boolean                  needTargetElement;
	protected ArrayList<IFragment>     inlineCodes;
	

	public Resource () {
		params = new Parameters();
		inlineCodes = new ArrayList<IFragment>();
	}
	
	public IParameters getParameters () {
		return params;
	}

}
