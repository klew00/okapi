package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.ISkeleton;

public interface ISkeletonProvider {

	public ISkeleton createSkeleton ();
	
	public ISkeleton createSkeleton (String data);
	
	public ISkeleton createSkeleton (String data,
		boolean isReference);
	
}
