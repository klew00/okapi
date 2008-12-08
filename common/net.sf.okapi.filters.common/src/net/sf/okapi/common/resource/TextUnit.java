/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.resource;

import java.util.Hashtable;
import java.util.Set;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.filters.IEncoder;
import net.sf.okapi.common.filters.ISkeleton;

public class TextUnit implements IResource, INameable, IReferenceable {

	private String id;
	private boolean isReferent;
	private String name;
	private String type;
	private boolean isTranslatable;
	private ISkeleton skeleton;
	private Hashtable<String, Property> properties;
	private Annotations annotations;
	private TextContainer source;
	private IEncoder encoder;

	/**
	 * Creates a new TextUnit object.
	 * @param id The ID of this resource.
	 */
	public TextUnit (String id) {
		create(id, null, false, null);
	}

	/**
	 * Creates a new TextUnit object.
	 * @param id The ID of this resource.
	 * @param sourceText The initial text of the source.
	 */
	public TextUnit (String id,
		String sourceText)
	{
		create(id, sourceText, false, null);
	}

	/**
	 * Creates a new TextUnit object.
	 * @param id The ID of this resource.
	 * @param sourceText The initial text of the source.
	 * @param isReferent Indicates if this resource is a referent (i.e. is referred to
	 * by another resource) or not.
	 */
	public TextUnit (String id,
		String sourceText,
		boolean isReferent)
	{
		create(id, sourceText, isReferent, null);
	}

	/**
	 * Creates a new TextUnit object.
	 * @param id The ID of this resource.
	 * @param sourceText The initial text of the source.
	 * @param isReferent Indicates if this resource is a referent (i.e. is referred to
	 * by another resource) or not.
	 * @param encoder The encoder to use for escaping characters and string for this
	 * TextUnit.
	 */
	public TextUnit (String id,
		String sourceText,
		boolean isReferent,
		IEncoder encoder)
	{
		create(id, sourceText, isReferent, encoder);
	}

	private void create (String id,
		String sourceText,
		boolean isReferent,
		IEncoder encoder)
	{
		annotations = new Annotations();
		this.id = id;
		this.isReferent = isReferent;
		this.encoder = encoder;
		source = new TextContainer();
		if ( sourceText != null ) {
			source.text.append(sourceText);
		}
	}

	@Override
	public String toString () {
		return source.toString();
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

	public String getType () {
		return type;
	}
	
	public void setType (String value) {
		type = value;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
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
		if ( source.properties == null ) return null;
		return source.properties.get(name);
	}

	public Property setSourceProperty (Property property) {
		return source.setProperty(property);
	}
	
	public Set<String> getSourcePropertyNames () {
		return source.getPropertyNames();
	}
	
	public Property getTargetProperty (String language,
		String name)
	{
		TextContainer tc = getTarget(language);
		if ( tc == null ) return null;
		return tc.getProperty(name);
	}

	public Property setTargetProperty (String language,
		Property property)
	{
		return createTarget(language, false, IResource.CREATE_EMPTY).setProperty(property);
	}

	public Set<String> getTargetPropertyNames (String language) {
		TextContainer tc = createTarget(language, false, IResource.CREATE_EMPTY);
		if ( tc.properties == null ) {
			tc.properties = new Hashtable<String, Property>(); 
		}
		return tc.properties.keySet();
	}

	public boolean hasTargetProperty (String language,
		String name)
	{
		TextContainer tc = getTarget(language);
		if ( tc == null ) return false;
		return (tc.getProperty(name) != null);
	}

	public Set<String> getTargetLanguages () {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		return ta.getLanguages();
	}

	public Property createTargetProperty (String language,
		String name,
		boolean overwriteExisting,
		int creationOptions)
	{
		// Get the target or create an empty one
		TextContainer tc = createTarget(language, false, CREATE_EMPTY);
		// Get the property if it exists
		Property prop = tc.getProperty(name);
		// If it does not exists or if we overwrite: create a new one
		if (( prop == null ) || overwriteExisting ) {
			// Get the source property
			prop = source.getProperty(name);
			if ( prop == null ) {
				// If there is no source, create an empty property
				return tc.setProperty(new Property(name, "", false));
			}
			else { // If there is a source property
				// Create a copy, empty or not depending on the options
				if ( creationOptions == CREATE_EMPTY ) {
					return tc.setProperty(new Property(name, "", prop.isReadOnly()));
				}
				else {
					return tc.setProperty(prop.clone());
				}
			}
		}
		return prop;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}
	
	public void setIsTranslatable (boolean value) {
		isTranslatable = value;
	}

	public boolean isReferent () {
		return isReferent;
	}

	public void setIsReferent (boolean value) {
		isReferent = value;
	}

	/**
	 * Gets the source object for this TextUnit.
	 * @return The source object for this TextUnit.
	 */
	public TextContainer getSource () {
		return source;
	}
	
	/**
	 * Sets the source object for this TextUnit. Any existing source object is overwritten.
	 * @param textContainer The source object to set.
	 * @return The source object that has been set.
	 */
	public TextContainer setSource (TextContainer textContainer) {
		source = textContainer;
		return source;
	}
	
	/**
	 * Gets the target object for this TextUnit for a given language.
	 * @param language The language to query.
	 * @return The target object for this TextUnit for the given language, or null if
	 * it does not exist.
	 */
	public TextContainer getTarget (String language) {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) return null;
		return ta.get(language);
	}

	/**
	 * Sets the target object for this TextUnit for a given language.
	 * Any existing target object for the given language is overwritten.
	 * To set a target object based on the source, use the 
	 * {@link #createTarget(String, boolean, int)} method.
	 * @param language The target language. 
	 * @param text The target object to set.
	 * @return The target object that has been set.
	 */
	public TextContainer setTarget (String language,
		TextContainer text)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		ta.set(language, text);
		return text;
	}
	
	/**
	 * Indicates if there is a target object for a given language for this TextUnit. 
	 * @param language The language to query.
	 * @return True if a target object exists for the given language, false otherwise.
	 */
	public boolean hasTarget (String language) {
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) return false;
		return (ta.get(language) != null);
	}
	
	/**
	 * Creates or get the target for this TextUnit.
	 * @param language The target language.
	 * @param overwriteExisting True to overwrite any existing target for the given language.
	 * False to not create a new target object if one already exists for the given language. 
	 * @param creationOptions Creation options:
	 * <ul><li>CREATE_EMPTY: Create an empty target object.</li>
	 * <li>COPY_CONTENT: Copy the text of the source (and any associated in-line code).</li>
	 * <li>COPY_PROPERTIES: Copy the source properties.</li>
	 * <li>COPY_ALL: Same as (COPY_CONTENT|COPY_PROPERTIES).</li></ul>
	 * @return The target object that was created, or retrieved. 
	 */
	public TextContainer createTarget (String language,
		boolean overwriteExisting,
		int creationOptions)
	{
		TargetsAnnotation ta = annotations.get(TargetsAnnotation.class);
		if ( ta == null ) {
			ta = new TargetsAnnotation();
			annotations.set(ta);
		}
		TextContainer tc = ta.get(language);
		if (( tc == null ) || overwriteExisting ) {
			tc = new TextContainer();
			if ( (creationOptions & COPY_CONTENT) == COPY_CONTENT ) {
				TextFragment tf = getSourceContent().clone();
				tc.setContent(tf);
			}
			if ( (creationOptions & COPY_PROPERTIES) == COPY_PROPERTIES ) {
				if ( source.properties != null ) {
					tc.properties = new Hashtable<String, Property>();
					for ( Property prop : source.properties.values() ) {
						tc.properties.put(prop.getName(), prop.clone()); 
					}
				}
			}
			ta.set(language, tc);
		}
		return tc;
	}

	/**
	 * Gets the content of the source for this TextUnit.
	 * @return The content of the source for this TextUnit.
	 */
	public TextFragment getSourceContent () {
		return source;
	}
	
	public TextFragment setSourceContent (TextFragment content) {
		source.setContent(content);
		return source;
	}

	/**
	 * Gets the content of the target for a given language for this TextUnit.
	 * @param language The language to query.
	 * @return The content of the target for the given language for this TextUnit.
	 */
	public TextFragment getTargetContent (String language) {
		TextContainer tc = getTarget(language);
		if ( tc == null ) return null;
		return tc.getContent();
	}
	
	public TextFragment setTargetContent (String language,
		TextFragment content)
	{
		TextContainer tc = createTarget(language, false, CREATE_EMPTY);
		tc.setContent(content);
		return tc;
	}

	public IEncoder getEncoder () {
		return encoder;
	}
	
	public void setEncoder (IEncoder encoder) {
		this.encoder = encoder;
	}	

	/**
	 * Indicates if the source text of this TextUnit is empty.
	 * @return True if the source text of this TextUnit is empty, false otherwise.
	 */
	public boolean isEmpty () {
		return (( source.text == null ) || ( source.text.length() == 0 ));
	}
}
