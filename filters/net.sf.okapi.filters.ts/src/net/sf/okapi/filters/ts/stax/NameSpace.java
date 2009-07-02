package net.sf.okapi.filters.ts.stax;

public class NameSpace{
	String prefix;
	String uri;
	
	public NameSpace(String prefix, String uri){
		this.prefix = prefix;
		this.uri = uri;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}