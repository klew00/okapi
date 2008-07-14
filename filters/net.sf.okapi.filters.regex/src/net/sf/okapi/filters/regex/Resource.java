package net.sf.okapi.filters.regex;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.ISkeletonResource;
import net.sf.okapi.common.resource.SkeletonResource;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	public ISkeletonResource sklRes;
	protected Parameters     params;
	

	public Resource () {
		sklRes = new SkeletonResource();
		params = new Parameters();
	}
	
	public IParameters getParameters () {
		return params;
	}

}
