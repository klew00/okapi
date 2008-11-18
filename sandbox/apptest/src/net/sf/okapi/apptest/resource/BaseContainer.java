package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class BaseContainer implements IResource {

	protected String id;
	protected ISkeleton skeleton;
	protected String name;
	
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

	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
}
