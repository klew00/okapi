package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.TextFragment;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	private int id;
	private String idText;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
		id = 0;
	}

	public GenericSkeleton (String data) {
		list = new ArrayList<GenericSkeletonPart>();
		if ( data != null ) add(data);
		id = 0;
	}

	public void add (String data) {
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(makeId(), data));
		else list.get(list.size()-1).append(data);
	}

	public void addRef (IResource referent) {
		GenericSkeletonPart part = new GenericSkeletonPart(makeId(), TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		list.add(part);
		// Then start a new part to avoid appending anything to the previous
		list.add(new GenericSkeletonPart(makeId(), ""));
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}

	public String getId () {
		return idText;
	}

	private String makeId() {
		idText = String.valueOf(++id);
		return idText;
	}

}
