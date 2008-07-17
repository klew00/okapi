package net.sf.okapi.resource;

public abstract class ContentMarker implements IContent{

	private static final String EMPTY_STRING = "";

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getEquivText() {
		return EMPTY_STRING;
	}

}
