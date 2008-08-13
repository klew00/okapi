package net.sf.okapi.common.resource;

public class SkeletonUnit implements IContainable {

	private StringBuilder    data;
	protected String         id;
	private long             offset;
	private int              length;


	public SkeletonUnit () {
		offset = -1;
	}
	
	public SkeletonUnit (String id,
		String data)
	{
		this.id = id;
		setData(data);
	}
	
	public SkeletonUnit (String id,
		int offset,
		int length)
	{
		this.id = id;
		setData(offset, length);
	}
	
	@Override
	public String toString () {
		//TODO: Modify for real output, this is test only
		if ( isEmpty() ) return "";
		else if ( isOffsetBased() ) return "[offset-based]";
		else if ( data == null ) return "";
		else return data.toString();
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

	/**
	 * Sets the skeleton's data as a string. This method overrides any existing
	 * data. Use {@link #appendData(String)} or {@link #appendData(StringBuilder)}
	 * to add data to the existing one.
	 * @param text The data to set.
	 */
	public void setData (String text) {
		data = new StringBuilder(text);
		offset = -1;
	}

	/**
	 * Sets the skeleton data in the form of an offset and a length. This method
	 * overrides any existing data.
	 * @param offset The offset position.
	 * @param length The length starting from the offset.
	 */
	public void setData (long offset,
		int length)
	{
		if (( offset < 0 ) || ( length < 0 ))
			throw new RuntimeException("Offset and length must be positive values.");
		this.offset = offset;
		this.length = length;
		data = null;
	}

	/**
	 * Appends skeleton data to the object. If previous data where set using
	 * {@link #setData(long, int)} before, this new data overrides the offset
	 * information. In short: you cannot have both direct data and offset at the
	 * same time. 
	 * @param text The data to set.
	 */
	public void appendData (String text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}
	
	/**
	 * Appends skeleton data to the object. If previous data where set using
	 * {@link #setData(long, int)} before, this new data overrides the offset
	 * information. In short: you cannot have both direct data and offset at the
	 * same time. 
	 * @param text The data to set.
	 */
	public void appendData (StringBuilder text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}

	/**
	 * Indicates if the current skeleton's data are offset-based or string.
	 * @return True if the data are offset-based, false if there is no offset
	 * information set.
	 */
	public boolean isOffsetBased () {
		return (offset != -1);
	}

}
