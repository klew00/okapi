package net.sf.okapi.filters.ts.stax;

public class Attribute{
	String prefix;
	String localname;
	String value;
	
	public Attribute(String prefix, String localname, String value){
		this.prefix = prefix;
		this.localname = localname;
		this.value = value;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getLocalname() {
		return localname;
	}

	public void setLocalname(String localname) {
		this.localname = localname;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}