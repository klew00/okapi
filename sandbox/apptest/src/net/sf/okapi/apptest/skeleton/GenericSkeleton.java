package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.TextFragment;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	private boolean createNew = true;
	
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
		createNew = false;
	}

	public void append (String data) {
		if (( createNew ) || ( list.size() == 0 )) {
			add(data);
		}
		else {
			list.get(list.size()-1).append(data);
		}
	}

	/**
	 * Adds a reference to the resource itself to the skeleton.
	 * @param referent Resource object.
	 * @param language Language or null if the reference is to the source.
	 */
	public void addRef (IResource referent,
		String language)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		part.language = language;
		list.add(part);
		// Flag that the next append() should start a new part
		createNew = true;
	}

	/**
	 * Adds a reference to the skeleton.
	 * @param Id of the referenced resource.
	 * @param propName Property name or null if the reference is to the text.
	 * @param language Language or null if the reference is to the source.
	 */
	public void addRef (String refId,
		String propName,
		String language)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(
			TextFragment.makeRefMarker(refId, propName));
		part.language = language;
		list.add(part);
		// Flag that the next append() should start a new part
		createNew = true;
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}

}
