package net.sf.okapi.common.filters;

public class InputDocument {

	public String path;
	public String paramFile;
	
	public InputDocument (String path,
		String paramFile)
	{
		this.paramFile = paramFile;
		this.path = path;
	}

}
