package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.TextFragment;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
	}

	public GenericSkeleton (String data) {
		list = new ArrayList<GenericSkeletonPart>();
		if ( data != null ) add(data);
	}

	public void add (String data) {
		GenericSkeletonPart part = new GenericSkeletonPart(data);
		list.add(part);
	}

	public void append (String data) {
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(data));
		else list.get(list.size()-1).append(data);
	}

	public void addRef (IResource referent) {
		GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		list.add(part);
		// Then start a new part to avoid appending anything to the previous
		list.add(new GenericSkeletonPart(""));
	}
	
	public void addRef (IResource referent,
		String language)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		part.language = language;
		list.add(part);
		// Then start a new part to avoid appending anything to the previous
		list.add(new GenericSkeletonPart(""));
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}

}
