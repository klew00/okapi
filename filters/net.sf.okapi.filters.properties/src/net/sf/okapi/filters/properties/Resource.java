package net.sf.okapi.filters.properties;

public class Resource extends net.sf.okapi.common.resource.ResourceBase {

	public boolean           endingLB;
	public StringBuilder     buffer;
	public Parameters        params;
	
	public Resource () {
		buffer = new StringBuilder();
		params = new Parameters();
	}
}
