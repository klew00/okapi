package net.sf.okapi.filters.xliff;

import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.filters.xliff.Parameters;

public class Resource extends net.sf.okapi.common.resource.Document {

	private static final long serialVersionUID = 1L;

	protected Parameters          params;
	protected boolean             needTargetElement;
	protected ArrayList<Code>     srcCodes;
	protected ArrayList<Code>     trgCodes;
	

	public Resource () {
		params = new Parameters();
		srcCodes = new ArrayList<Code>();
		trgCodes = new ArrayList<Code>();		
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

}
