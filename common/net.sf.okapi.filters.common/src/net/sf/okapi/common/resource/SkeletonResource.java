package net.sf.okapi.common.resource;

import net.sf.okapi.common.Util;

public class SkeletonResource implements ISkeletonResource {

	private StringBuilder    data;
	private String           id;
	private long             offset;
	private int              length;
	
	
	public SkeletonResource () {
		data = null;
		offset = -1;
	}
	
	public int getKind () {
		return KIND_SKELETON;
	}

	@Override
	public String toString () {
		//TODO: Implement SkeletonResource.toString()
		if ( isOffsetBased() ) throw new RuntimeException("SkeletonResource.toString not fully implemented");
		if ( data == null ) return "";
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
		offset = -1;
	}

	public void appendData (String text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}
	
	public void appendData (StringBuilder text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}
	
	public boolean isEmpty () {
		if (  offset > -1 ) return false;
		return (( data == null ) || ( data.length() == 0 ));
	}

	public void setSkeleton (long offset,
		int length)
	{
		this.offset = offset;
		this.length = length;
		data = null;
	}
	
	public boolean isOffsetBased () {
		return (offset != -1);
	}
	
	public String toXML () {
		//TODO: Implement SkeletonResource.toXML()
		if ( isOffsetBased() ) throw new RuntimeException("SkeletonResource.toXML not fully implemented");
		if ( data == null ) return "<skl id=\"" + id + "\"></skl>";
		return "<skl id=\"" + id + "\">"
			+ Util.escapeToXML(data.toString(), 0, false) + "</skl>";
	}

}
