package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.filters.ISkeletonProvider;

public class GenericSkeletonProvider implements ISkeletonProvider {
	
	public ISkeleton createSkeleton () {
		return new GenericSkeleton();
	}

	public ISkeleton createSkeleton (String data) {
		GenericSkeleton tmp = new GenericSkeleton();
		tmp.add(data);
		return tmp;
	}
	
	public ISkeleton createSkeleton (String data,
		boolean isReference)
	{
		GenericSkeleton tmp = new GenericSkeleton();
		tmp.add(data);
		tmp.getParts().get(0).setIsReference(isReference);
		return tmp;
	}
	
}
