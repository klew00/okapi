package net.sf.okapi.common.skeleton;

import net.sf.okapi.common.resource.IReferenceable;

class Referent {
	
	IReferenceable ref;
	int count;
	
	public Referent (IReferenceable value) {
		ref = value;
		count = value.getReferenceCount();
	}

}
