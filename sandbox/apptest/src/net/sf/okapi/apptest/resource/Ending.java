package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class Ending implements IResource {

	private String id;
	private ISkeleton skeleton;

	public Ending (String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}
}
