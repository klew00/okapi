package net.sf.okapi.common.resource;

import net.sf.okapi.common.resource.TextFragment.TagType;

public class Code {

	protected TagType   tagType;
	protected int       id;
	protected String    type;
	protected String    data;
	protected boolean   hasSubflow;


	public Code (TagType tagType, String type, String data) {
		id = -1;
		this.tagType = tagType;
		if ( type == null ) type = "null";
		this.type = type;
		this.data = data;
	}
	
	public Code clone () {
		Code clone = new Code(tagType, type, data);
		clone.id = id;
		return clone;
	}
	
	public TagType getTagType () {
		return tagType;
	}
	
	public void setTagType (TagType value) {
		tagType = value;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String value) {
		if ( value == null ) type = "null";
		else type = value;
	}
	
	/**
	 * Gets the raw data for the code. This does not build a string
	 * with sub-flows content.
	 * @return The raw data of the code.
	 */
	public String getData () {
		return data;
	}
	
	public void setData (String value) {
		data = value;
	}
	
	public int getID () {
		return id;
	}
	
	public void setID (int value) {
		id = value;
	}

	public boolean hasSubflow () {
		return hasSubflow;
	}
	
	public void setHasSubflow (boolean value) {
		hasSubflow = value;
	}

}
