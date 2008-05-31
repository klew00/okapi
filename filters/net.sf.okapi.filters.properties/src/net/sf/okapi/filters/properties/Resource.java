package net.sf.okapi.filters.properties;

import net.sf.okapi.common.IParameters;

public class Resource extends net.sf.okapi.common.resource.ResourceBase {

	public boolean           endingLB;
	public String            lineBreak;
	public StringBuilder     buffer;
	public Parameters        params;
	
	public Resource () {
		buffer = new StringBuilder();
		params = new Parameters();
	}

	public IParameters getParameters () {
		return params;
	}
}
