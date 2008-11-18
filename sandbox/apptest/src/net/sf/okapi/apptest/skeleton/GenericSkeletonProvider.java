package net.sf.okapi.apptest.skeleton;

public class GenericSkeletonProvider {
	
	public GenericSkeleton createSkeleton () {
		return new GenericSkeleton();
	}

	public GenericSkeleton createSkeleton (String data) {
		GenericSkeleton tmp = new GenericSkeleton();
		tmp.add(data);
		return tmp;
	}
	
	public GenericSkeleton createSkeleton (String data,
		boolean isReference)
	{
		GenericSkeleton tmp = new GenericSkeleton();
		tmp.add(data);
		tmp.getParts().get(0).setIsReferent(isReference);
		return tmp;
	}
	
}
