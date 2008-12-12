package net.sf.okapi.common.skeleton;

import net.sf.okapi.common.filters.ISkeleton;


public final class SkeletonFactory {	
	@SuppressWarnings("unchecked")
	public static final <S extends ISkeleton> S createSkeleton(S skeletonClass) {
		try {		
			return (S)Class.forName(skeletonClass.toString()).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}
}
