package net.sf.okapi.filters.regex;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.SkeletonResource;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	public SkeletonResource  sklRes;
	protected Parameters     params;
	

	public Resource () {
		sklRes = new SkeletonResource();
		params = new Parameters();
	}
	
	public IParameters getParameters () {
		return params;
	}

}
