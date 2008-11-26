package net.sf.okapi.apptest.resource;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

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
	protected Hashtable<String, Property> properties; // Source properties
	protected Annotations annotations;
	
	public BaseNameable () {
		annotations = new Annotations();
	}
	
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
	
	public Property getProperty (String name) {
		if ( name == null ) throw new InvalidParameterException();
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public void setProperty (Property property) {
		if ( property == null ) throw new InvalidParameterException();
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
	}
	
	public Iterator<String> propertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet().iterator();
	}

	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

	public boolean hasTargetProperty (String language,
		String name)
	{
		if ( name == null ) throw new InvalidParameterException();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return false;
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) return false;
		return (trgProps.get(name) != null);
	}
	
	public Property getTargetProperty (String language,
		String name)
	{
		// Assumes it exists
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		Map<String, Property> trgProps = tpa.get(language);
		return trgProps.get(name);
	}

	public Property getTargetProperty (String language,
		String name,
		int creationOptions)
	{
		if ( name == null ) throw new InvalidParameterException();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			if ( creationOptions > DO_NOTHING ) {
				tpa = new TargetPropertiesAnnotation();
				annotations.set(tpa);
			}
			else return null;
		}
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) {
			if ( creationOptions == DO_NOTHING ) {
				return null;
			}
			else { // Else: create the properties list
				tpa.set(language, new Hashtable<String, Property>());
				trgProps = tpa.get(language);
			}
		}
		Property trgProp = trgProps.get(name);
		if ( trgProp == null ) {
			if ( creationOptions == DO_NOTHING ) {
				return null;
			}
			else { // Else: create the property
				Property srcProp = getProperty(name); // Get the source
				if ( srcProp == null ) { // No corresponding source
					trgProp = new Property(name, "", true);
				}
				else { // Has a corresponding source
					if ( creationOptions > CREATE_EMPTY ) {
						trgProp = new Property(name, srcProp.getValue(), srcProp.isWriteable());
					}
					else {
						trgProp = new Property(name, "", srcProp.isWriteable());
					}
				}
				trgProps.put(name, trgProp); // Add the property to the list
			}
		}
		return trgProp;
	}
	
	public void setTargetProperty (String language,
		Property property)
	{
		//TODO
		if ( property == null ) throw new InvalidParameterException();
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
	}

	public Iterator<String> targetPropertyNames (String language) {
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return null;
		Map<String, Property> trgProps = tpa.get(language);
		if ( trgProps == null ) return null;
		return trgProps.keySet().iterator();
	}

}
