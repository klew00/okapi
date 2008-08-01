package net.sf.okapi.common.resource2;

public class SkeletonUnit implements IResource {

	private StringBuilder    data;
	protected String         id;
	private long             offset;
	private int              length;
	
	
	public SkeletonUnit () {
		offset = -1;
	}
	
	public String getID () {
		return id;
	}

	public boolean isEmpty () {
		if (  offset != -1 ) return (length==0);
		return (( data == null ) || ( data.length() == 0 ));
	}

	public void setID (String value) {
		id = value;
	}

	public void setData (String text) {
		data = new StringBuilder(text);
		offset = -1;
	}

	public void setData (long offset,
		int length)
	{
		if (( offset < 0 ) || ( length < 0 ))
			throw new RuntimeException("Offset and length must be positive values.");
		this.offset = offset;
		this.length = length;
		data = null;
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
	
	public boolean isOffsetBased () {
		return (offset != -1);
	}

}
