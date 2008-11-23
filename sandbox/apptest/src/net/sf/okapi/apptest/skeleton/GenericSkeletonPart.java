package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.IResource;

public class GenericSkeletonPart {

	StringBuilder data;
	IResource parent;
	String id;
	
	public GenericSkeletonPart (String id,
		String data)
	{
		this.data = new StringBuilder(data);
		this.id = id;
	}

	@Override
	public String toString () {
		return data.toString();
	}

	public void append (String data) {
		this.data.append(data);
	}

}
