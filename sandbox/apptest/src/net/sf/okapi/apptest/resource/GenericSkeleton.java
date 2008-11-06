package net.sf.okapi.apptest.resource;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.ISkeleton;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	private boolean inStartElement = false;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
	}

	public GenericSkeleton (GenericSkeletonPart part) {
		list = new ArrayList<GenericSkeletonPart>();
		list.add(part);
	}

	public void add (GenericSkeletonPart part) {
		flush();
		list.add(part);
	}

	public void add (String data) {
		flush();
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(data));
		else list.get(list.size()-1).append(data);
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
		flush();		
		addWithoutCheck(String.format("</%s>", name));		
	}

	public void addStartElement(String name) {
		flush();
		addWithoutCheck(String.format("<%s", name));		
	}
	
	public void flush () {
		if ( inStartElement ) {
			addWithoutCheck(">");
			inStartElement = false;
		}
	}

	private void addWithoutCheck (String data) {
		if ( list.size() == 0 ) list.add(new GenericSkeletonPart(data));
		else list.get(list.size()-1).append(data);
	}
	
}
