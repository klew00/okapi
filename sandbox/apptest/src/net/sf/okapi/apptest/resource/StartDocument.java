package net.sf.okapi.apptest.resource;

public class StartDocument extends BaseNameable {

	protected String language;
	protected String encoding;
	protected boolean isMultilingual;
	
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
	
	public boolean isMultilingual () {
		return isMultilingual;
	}
	
	public void setIsMultilingual (boolean value) {
		isMultilingual = value;
	}

}
