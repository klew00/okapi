/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common.resource;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Implements a nameable resource.
 */
public class BaseNameable implements INameable {

	protected String id;
	protected ISkeleton skeleton;
	protected String name;
	protected String type;
	protected String mimeType;
	protected boolean isTranslatable = true; // Default for all resources
	protected boolean preserveWS = false; // Default for all resources
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
	
	public void setName (String value) {
		name = value;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String value) {
		type = value;
	}
	
	public String getMimeType () {
		return mimeType;
	}
	
	public void setMimeType (String value) {
		mimeType = value;
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
	
	public void removeProperty (String name) {
		if ( properties != null ) {
			properties.remove(name);
		}
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	public boolean hasProperty (String name) {
		if ( properties == null ) return false;
		return properties.containsKey(name);
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
	
	public void removeSourceProperty (String name) {
		if ( sourceProperties != null ) {
			sourceProperties.remove(name);
		}
	}
	
	public Set<String> getSourcePropertyNames () {
		if ( sourceProperties == null ) sourceProperties = new Hashtable<String, Property>();
		return sourceProperties.keySet();
	}

	public boolean hasSourceProperty (String name) {
		if ( sourceProperties == null ) return false;
		return sourceProperties.containsKey(name);
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

	public void removeTargetProperty (String language,
		String name)
	{
		if ( annotations != null ) {
			TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
			if ( tpa != null ) {
				Map<String, Property> trgProps = tpa.get(language);
				trgProps.remove(name);
			}
		}
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
			if ( creationOptions > IResource.CREATE_EMPTY ) {
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
	
	public boolean isTranslatable () {
		return isTranslatable;
	}
	
	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public boolean preserveWhitespaces () {
		return preserveWS;
	}
	
	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

}
