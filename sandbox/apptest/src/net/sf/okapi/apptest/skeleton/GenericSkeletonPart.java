package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.common.ISkeletonPart;
import net.sf.okapi.apptest.resource.BaseResource;

public class GenericSkeletonPart extends BaseResource implements ISkeletonPart {

	StringBuilder data;
	IResource parent;
	
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

	@Override
	public ISkeleton getSkeleton () {
		// Never used in this class
		// This is there only because it is part of the IResource interface
		assert(false);
		return null;
	}

	@Override
	public void setSkeleton (ISkeleton skeleton) {
		// Never used in this class
		// This is there only because it is part of the IResource interface
		assert(false);
	}

}
