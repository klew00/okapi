package net.sf.okapi.apptest.resource;

public class Document2 extends BaseContainer {

	protected String language;
	protected String encoding;
	
	public String getLanguage () {
		return language;
	}
	
	public void setLanguage (String language) {
		this.language = language;
	}

	public String getEncoding () {
		return encoding;
	}
	
	public void setEncoding (String encoding) {
		this.encoding = encoding;
	}

}
