package net.sf.okapi.apptest.resource;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.ISkeleton;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
	}

	public GenericSkeleton (GenericSkeletonPart part) {
		list = new ArrayList<GenericSkeletonPart>();
		list.add(part);
	}

	public void add (GenericSkeletonPart part) {
		list.add(part);
	}
	
	public void add (String data) {
		list.add(new GenericSkeletonPart(data));
	}
	
	public void append (String data) {
		if ( list.size() == 0 ) add(data);
		else list.get(list.size()-1).append(data);
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}
}
