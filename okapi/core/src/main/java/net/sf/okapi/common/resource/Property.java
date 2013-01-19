/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Represents a read-only or a modifiable property associated with a resource.
 * For example the HREF attribute of the element A in HTML would be a property.
 * Note that translatable data (such as the text of an attribute ALT of an IMG element in HTML)
 * must be stored in {@link TextUnit} rather that Property.
 */
public class Property {
	
	public static final String ENCODING = "encoding";
	public static final String LANGUAGE = "language";
	public static final String APPROVED = "approved"; 
	public static final String NOTE = "note"; 
	public static final String TRANSNOTE = "transNote"; 
	public static final String COORDINATES = "coordinates"; 

	private final String name;
	private String value;
	private final boolean isReadOnly;
	protected Annotations annotations;

	/**
	 * Creates a new property object with a name, a vale and its read-only flag.
	 * @param name the name of the property (case-sensitive).
	 * @param value the value of the property.
	 * @param isReadOnly true if the property cannot be modified using the filter, false if you
	 * can modify the value in the output document.
	 */
	public Property (String name, String value, boolean isReadOnly) {
		this.name = name;
		this.value = value;
		this.isReadOnly = isReadOnly;
	}
	
	/**
	 * Creates a new read-only property object with a name and a value.
	 * @param name the name of the property (case-sensitive)
	 * @param value the value of the property.
	 */
	public Property (String name, String value) {
		this(name, value, true);
	}
	
	/**
	 * Gets the string representation of this property. This is the same as its value.
	 * @return the value of the property.
	 */
	@Override
	public String toString () {
		return value;
	}
	
	/**
	 * Clones this property.
	 * @return a new property object that is a copy of this one.
	 */
	public Property clone () {
		Property prop = new Property(name, value, isReadOnly);
		//TODO: copy annotations?? prop.annotations = annotations.clone();
		return prop;
	}

	/**
	 * Gets the name of this property.
	 * @return the name of this property.
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Gets the value of this property.
	 * @return the value of this property.
	 */
	public String getValue () {
		return value;
	}

	/**
	 * Gets the boolean value of this property. Use this helper method to get a boolean from the 
	 * value of this property. The values "true" and "yes" (in any case) returns true, any other 
	 * value returns false. No verification is done to see if the value is really boolean or not.
	 * @return true is the property value is "true", "yes" (case-insensitive), false otherwise.
	 */
	public boolean getBoolean () {
		if ( value == null ) return false;
		return (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"));
	}
	
	/**
	 * Sets a new value for this property.
	 * @param value the new value to set.
	 */
	public void setValue (String value) {
		this.value = value;
	}
	
	/**
	 * Indicates if this property is read-only.
	 * <p>All property can be changed, but the ones flagged as read-only
	 * cannot be modified when re-writing the document from where they have been extracted.
	 * @return true if the property cannot be modified in the filter output, false if it can be modified.
	 */
	public boolean isReadOnly () {
		return isReadOnly;
	}
	
	/**
	 * Gets the annotation of a given type for this property.
	 * @param type the type of annotation to retrieve.
	 * @return the annotation, or null if this property has no annotation of the requested type.
	 */
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	/**
	 * Sets an annotation for this property. 
	 * @param annotation the annotation object to set.
	 */
	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

	public Annotations getAnnotations() {
		return (annotations == null) ? new Annotations() : annotations;
	}

}
