package net.sf.okapi.common.resource3;

public class Code {

	protected int       type;
	protected int       id;
	protected String    label;
	protected String    data;

	public Code (int type, String label, String data) {
		id = -1;
		this.type = type;
		this.label = label;
		this.data = data;
	}
	
	public int getType () {
		return type;
	}
	
	public void setType (int value) {
		type = value;
	}
	
	public String getLabel () {
		return label;
	}
	
	public void setLabel (String value) {
		label = value;
	}
	
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

}
