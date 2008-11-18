package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class Ending implements IResource {

	protected String id;
	protected ISkeleton skeleton;
	
	public Ending (String id) {
		this.id = id;
	}

	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}

}
