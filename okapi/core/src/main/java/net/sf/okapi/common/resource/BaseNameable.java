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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
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
	protected LinkedHashMap<String, Property> properties;
	protected Annotations annotations;
	protected LinkedHashMap<String, Property> sourceProperties;
	
	@Override
	public String getId () {
		return id;
	}
	
	@Override
	public void setId (String id) {
		this.id = id;
	}

	@Override
	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	@Override
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
		if (skeleton != null) skeleton.setParent(this);
	}

	@Override
	public String getName () {
		return name;
	}
	
	@Override
	public void setName (String value) {
		name = value;
	}
	
	@Override
	public String getType () {
		return type;
	}
	
	@Override
	public void setType (String value) {
		type = value;
	}
	
	@Override
	public String getMimeType () {
		return mimeType;
	}
	
	@Override
	public void setMimeType (String value) {
		mimeType = value;
	}
	
	@Override
	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		if ( annotations == null ) return null;
		return annotationType.cast(annotations.get(annotationType) );
	}

	@Override
	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

	@Override
	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	@Override
	public Property setProperty (Property property) {
		if ( properties == null ) properties = new LinkedHashMap<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	@Override
	public void removeProperty (String name) {
		if ( properties != null ) {
			properties.remove(name);
		}
	}
	
	@Override
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new LinkedHashMap<String, Property>();
		return properties.keySet();
	}

	@Override
	public boolean hasProperty (String name) {
		if ( properties == null ) return false;
		return properties.containsKey(name);
	}

	@Override
	public Property getSourceProperty (String name) {
		if ( sourceProperties == null ) return null;
		return sourceProperties.get(name);
	}

	@Override
	public Property setSourceProperty (Property property) {
		if ( sourceProperties == null ) sourceProperties = new LinkedHashMap<String, Property>();
		sourceProperties.put(property.getName(), property);
		return property;
	}
	
	@Override
	public void removeSourceProperty (String name) {
		if ( sourceProperties != null ) {
			sourceProperties.remove(name);
		}
	}
	
	@Override
	public Set<String> getSourcePropertyNames () {
		if ( sourceProperties == null ) sourceProperties = new LinkedHashMap<String, Property>();
		return sourceProperties.keySet();
	}

	@Override
	public boolean hasSourceProperty (String name) {
		if ( sourceProperties == null ) return false;
		return sourceProperties.containsKey(name);
	}

	@Override
	public Property getTargetProperty (LocaleId locId,
		String name)
	{
		if ( annotations == null ) return null;
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return null;
		Map<String, Property> trgProps = tpa.get(locId);
		if ( trgProps == null ) return null;
		return trgProps.get(name);
	}

	@Override
	public Property setTargetProperty (LocaleId locId,
		Property property)
	{
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		Map<String, Property> trgProps = tpa.get(locId);
		if ( trgProps == null ) {
			tpa.set(locId, new LinkedHashMap<String, Property>());
			trgProps = tpa.get(locId);
		}
		trgProps.put(property.getName(), property);
		return property;
	}

	@Override
	public Set<String> getTargetPropertyNames (LocaleId locId) {
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		Map<String, Property> trgProps = tpa.get(locId);
		if ( trgProps == null ) {
			tpa.set(locId, new LinkedHashMap<String, Property>());
			trgProps = tpa.get(locId);
		}
		return trgProps.keySet();
	}

	@Override
	public void removeTargetProperty (LocaleId locId,
		String name)
	{
		if ( annotations != null ) {
			TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
			if ( tpa != null ) {
				Map<String, Property> trgProps = tpa.get(locId);
				trgProps.remove(name);
			}
		}
	}
	
	@Override
	public boolean hasTargetProperty (LocaleId locId,
		String name)
	{
		if ( annotations == null ) return false;
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) return false;
		Map<String, Property> trgProps = tpa.get(locId);
		if ( trgProps == null ) return false;
		return (trgProps.get(name) != null);
	}
		
	@Override
	public Set<LocaleId> getTargetLocales () {
		if ( annotations == null ) annotations = new Annotations();
		TargetPropertiesAnnotation tpa = annotations.get(TargetPropertiesAnnotation.class);
		if ( tpa == null ) {
			tpa = new TargetPropertiesAnnotation();
			annotations.set(tpa);
		}
		return tpa.getLocales();
	}

	@Override
	public Property createTargetProperty (LocaleId locId,
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
		Map<String, Property> trgProps = tpa.get(locId);
		if ( trgProps == null ) {
			tpa.set(locId, new LinkedHashMap<String, Property>());
			trgProps = tpa.get(locId);
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
	
	@Override
	public boolean isTranslatable () {
		return isTranslatable;
	}
	
	@Override
	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	@Override
	public boolean preserveWhitespaces () {
		return preserveWS;
	}
	
	@Override
	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	@Override
	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}

}
