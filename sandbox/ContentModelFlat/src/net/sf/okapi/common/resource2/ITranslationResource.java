package net.sf.okapi.common.resource2;

public interface ITranslationResource extends IResource {
	
	public String getName ();
	
	public void setName (String value);
	
	public boolean isTranslatable ();
	
	public void setIsTranslatable (boolean value);
	
	public boolean preserveWhitespaces ();
	
	public void setPreserveWhitespaces (boolean value);
	
	public ITranslationResource getParent ();
	
	public void setParent (ITranslationResource value);

}
