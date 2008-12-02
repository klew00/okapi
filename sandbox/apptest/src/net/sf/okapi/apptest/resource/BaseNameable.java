package net.sf.okapi.apptest.resource;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import net.sf.okapi.apptest.annotation.Annotations;
import net.sf.okapi.apptest.annotation.TargetPropertiesAnnotation;
import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;

public class BaseNameable implements IResource, INameable {

	protected String id;
	protected ISkeleton skeleton;
	protected String name;
	protected Hashtable<String, Property> properties;
	protected Annotations annotations;
	protected Hashtable<String, Property> sourceProperties;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotation == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	public Property getSourceProperty (String name) {
		if ( sourceProperties == null ) return null;
		return sourceProperties.get(name);
	}

	public Property setSourceProperty (Property property) {
		if ( sourceProperties == null ) sourceProperties = new Hashtable<String, Property>();
		sourceProperties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getSourcePropertyNames () {
		if ( sourceProperties == null ) sourceProperties = new Hashtable<String, Property>();
		return sourceProperties.keySet();
	}

	public Property getTargetProperty (String language,
		String name)
	{
		if ( annotations == null ) return null;
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return null;
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) return null;
		return trgProps.get(name);
	}

	public Property setTargetProperty (String language,
		Property property)
	{
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) {
			tpa.set(language, new Hashtable<String, Property>());
			trgProps = tpa.get(language);
		}
		trgProps.put(property.getName(), property);
		return property;
	}

	public Set<String> getTargetPropertyNames (String language) {
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) {
			tpa.set(language, new Hashtable<String, Property>());
			trgProps = tpa.get(language);
		}
		return trgProps.keySet();
	}

	public boolean hasTargetProperty (String language,
		String name)
	{
		if ( annotations == null ) return false;
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return false;
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) return false;
		return (trgProps.get(name) != null);
	}
		
	public Set<String> getTargetLanguages () {
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		return tpa.getLanguages();
	}

	public Property createTargetProperty (String language,
		String name,
		boolean overwriteExisting,
		int creationOptions)
	{
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) {
			tpa.set(language, new Hashtable<String, Property>());
			trgProps = tpa.get(language);
		}
		Property trgProp = trgProps.get(name);
		if (( trgProp == null ) || overwriteExisting ) {
			if ( creationOptions > CREATE_EMPTY ) {
				trgProp = new Property(name, "", false);
			}
			else { // Copy the source
				Property srcProp = getProperty(name); // Get the source
				if ( srcProp == null ) { // No corresponding source
					trgProp = new Property(name, "", false);
				}
				else { // Has a corresponding source
					trgProp = srcProp.clone();
				}
			}
			trgProps.put(name, trgProp); // Add the property to the list
		}
		return trgProp;
	}

}
