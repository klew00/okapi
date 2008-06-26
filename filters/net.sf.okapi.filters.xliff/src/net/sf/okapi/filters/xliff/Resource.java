package net.sf.okapi.filters.xliff;

import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.filters.xliff.Parameters;

public class Resource extends net.sf.okapi.common.resource.DocumentResource {

	protected Parameters               params;
	protected boolean                  needTargetElement;
	protected ArrayList<IFragment>     srcCodes;
	protected ArrayList<IFragment>     trgCodes;
	

	public Resource () {
		params = new Parameters();
		srcCodes = new ArrayList<IFragment>();
		trgCodes = new ArrayList<IFragment>();		
	}
	
	public IParameters getParameters () {
		return params;
	}

}
