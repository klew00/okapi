package net.sf.okapi.filters.regex;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.IBaseResource;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	public IBaseResource     currentRes;
	protected Parameters     params;
	

	public Resource () {
		params = new Parameters();
	}
	
	public IParameters getParameters () {
		return params;
	}

}
