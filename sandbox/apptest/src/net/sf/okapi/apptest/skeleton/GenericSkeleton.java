package net.sf.okapi.apptest.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.resource.TextFragment;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	private boolean inStartElement = false;
	private int id;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
		id = 0;
	}

	public void add (String data) {
		closeStartElement();
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(makeId(), data));
		else list.get(list.size()-1).append(data);
	}

	public void addRef (String refId) {
		closeStartElement();
		list.add(new GenericSkeletonPart(makeId(), TextFragment.makeRefMarker(refId)));
		list.add(new GenericSkeletonPart(makeId(), ""));
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}

	public void addAttribute (String name, String value) {
		if ( !inStartElement ) {
			throw new RuntimeException("Call to addAttribute done outside a start element tag.");
		}
		addWithoutCheck(String.format(" %s=\"%s\"", name, value));
	}

	public void addEndElement(String name) {
		closeStartElement();		
		addWithoutCheck(String.format("</%s>", name));		
	}

	public void addStartElement(String name) {
		closeStartElement();
		addWithoutCheck(String.format("<%s", name));		
	}
	
	public void closeStartElement () {
		if ( inStartElement ) {
			addWithoutCheck(">");
			inStartElement = false;
		}
	}

	private String makeId() {
		return String.valueOf(++id);
	}
	
	private void addWithoutCheck (String data) {
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(makeId(), data));
		else list.get(list.size()-1).append(data);
	}
	
}
