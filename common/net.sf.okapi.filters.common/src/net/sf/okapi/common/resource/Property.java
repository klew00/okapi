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

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Represents a read-only or a modifiable property associated with a resource.
 * For example the HREF attribute of the element A in HTML would be a property.
 * Note that translatable data (such as the text of an attribute ALT of an IMG element in HTML)
 * must be stored in {@link TextUnit} rather that Property.
 */
public class Property {
	
	private final String name;
	private String value;
	private final boolean isReadOnly;
	protected Annotations annotations;

	/**
	 * Creates a new property object with a name, a vale and its read-only flag.
	 * @param name Name of the property (case-sensitive).
	 * @param value Value of the property.
	 * @param isReadOnly True if the property is not modifiable, false if it can be modified.
	 */
	public Property (String name, String value, boolean isReadOnly) {
		this.name = name;
		this.value = value;
		this.isReadOnly = isReadOnly;
	}
	
	/**
	 * Creates a new read-only property object with a name and a value.
	 * @param name The name of the property (case-sensitive)
	 * @param value The value of the property.
	 */
	public Property (String name, String value) {
		this(name, value, true);
	}
	
	/**
	 * Gets the string representation of this property. This is the same as its value.
	 * @return The value of the property.
	 */
	@Override
	public String toString () {
		return value;
	}
	
	/**
	 * Clones this property.
	 * @return A new property object that is a copy of this one.
	 */
	public Property clone () {
		Property prop = new Property(name, value, isReadOnly);
		//TODO: copy annotations?? prop.annotations = annotations.clone();
		return prop;
	}

	/**
	 * Gets the name of this property.
	 * @return The name of this property.
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Gets the value of this property.
	 * @return The value of this property.
	 */
	public String getValue () {
		return value;
	}
	
	/**
	 * Sets a new value for this property.
	 * @param value The new value to set.
	 */
	public void setValue (String value) {
		this.value = value;
	}
	
	/**
	 * Indicates if this property is read-only.
	 * @return True if the property is not modifiable, false if it can be modified.
	 */
	public boolean isReadOnly () {
		return isReadOnly;
	}
	
	
	/**
	 * Gets the annotation of a given type for this property.
	 * @param type The type of annotation to retrieve.
	 * @return The annotation, or null if this property has no annotation of the requested type.
	 */
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	/**
	 * Sets an annotation for this property. 
	 * @param annotation The annotation object to set.
	 */
	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

}
