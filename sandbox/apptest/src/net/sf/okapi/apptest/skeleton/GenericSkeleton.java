package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.TextFragment;

public class GenericSkeleton implements ISkeleton, IResource {

	private ArrayList<GenericSkeletonPart> list;
	private int id;
	private String idText;
	
	public GenericSkeleton () {
		create(null, false);
	}

	public GenericSkeleton (String data) {
		create(data, false);
	}

	public GenericSkeleton (String data, boolean isReferent) {
		create(data, isReferent);
	}
	
	private void create (String data, boolean isReferent) {
		list = new ArrayList<GenericSkeletonPart>();
		id = 0;
		if ( data != null ) {
			add(data);
			list.get(0).setIsReferent(isReferent);
		}
	}

	/*
	public String toString (IWriterHelper writerHelper) {
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : list ) {
			tmp.append(part.toString(writerHelper));
		}
		return tmp.toString();
	}*/
	
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

	private String makeId() {
		idText = String.valueOf(++id);
		return idText;
	}

	public String getId () {
		return idText;
	}

	public ISkeleton getSkeleton() {
		// Not used.
		return null;
	}

	public void setId (String id) {
		// Not used, IDs are auto-incremented
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Not used.
	}
	
}
