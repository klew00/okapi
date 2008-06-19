package net.sf.okapi.common.resource;

import net.sf.okapi.common.Util;

public class SkeletonResource implements ISkeletonResource {

	public StringBuilder     data;

	private String           id;
	
	
	public SkeletonResource () {
		data = new StringBuilder();
	}
	
	public int getKind() {
		return KIND_SKELETON;
	}

	@Override
	public String toString () {
		return data.toString();
	}

	public String getID () {
		if ( id == null ) return "";
		return id;
	}

	public void setID (String newId) {
		id = newId;
	}

	public void setData (String text) {
		data = new StringBuilder(text);
	}

	public String toXML () {
		return "<skl id=\"" + id + "\">"
			+ Util.escapeToXML(data.toString(), 0, false) + "</skl>";
	}

}
