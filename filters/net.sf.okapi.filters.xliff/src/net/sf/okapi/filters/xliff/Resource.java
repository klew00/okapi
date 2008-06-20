package net.sf.okapi.filters.xliff;

import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.filters.xliff.Parameters;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

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
