package net.sf.okapi.apptest.common;

import java.util.Set;

import net.sf.okapi.apptest.resource.Property;

public interface INameable {

	public String getName () ;
	
	public void setName (String name);
	
	public Property getProperty (String name);

	public Property setProperty (Property property);
	
	public Set<String> getPropertyNames ();

	public Property getSourceProperty (String name);

	public Property setSourceProperty (Property property);
	
	public Set<String> getSourcePropertyNames ();

	public Property getTargetProperty (String language,
		String name);
	
	public Property setTargetProperty (String language,
		Property property);
	
	public Set<String> getTargetPropertyNames (String language);

	public Set<String> getTargetLanguages ();
	
	public boolean hasTargetProperty (String language,
			String name);

	public Property createTargetProperty (String language,
		String name,
		boolean overwriteExisting,
		int creationOptions);
	
}
