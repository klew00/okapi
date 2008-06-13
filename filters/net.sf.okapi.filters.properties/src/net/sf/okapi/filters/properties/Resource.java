package net.sf.okapi.filters.properties;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.SkeletonResource;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	public boolean           endingLB;
	public String            lineBreak;
	public SkeletonResource  sklRes;
	public Parameters        params;
	
	public Resource () {
		sklRes = new SkeletonResource();
		params = new Parameters();
	}

	public IParameters getParameters () {
		return params;
	}
}
