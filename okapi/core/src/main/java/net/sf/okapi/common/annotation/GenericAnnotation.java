/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

import java.security.InvalidParameterException;
import java.util.HashMap;

import net.sf.okapi.common.Util;

/**
 * Generic annotation allowing access with field names and multiple instance on the same object.
 */
public class GenericAnnotation {

	private static final String FIELD_SEPARATOR = "\u001e";
	private static final String PART_SEPARATOR = "\u001f";
	
	private String type;
	private HashMap<String, Object> map;
	
	/**
	 * Creates a new annotation for a given type.
	 * <p>Note that it is technically to have two annotations with the same type but different fields.
	 * This is a side effect of the capability to access the annotation in a generic way.
	 * The user is responsible for keeping consistent types of annotations.
	 * @param type the identifier of the type (cannot be null or empty).
	 */
	public GenericAnnotation (String type) {
		if ( Util.isEmpty(type) ) {
			throw new InvalidParameterException("The type of an annotation must not be null or empty.");
		}
		this.type = type;
	}
	
	@Override
	public GenericAnnotation clone () {
		GenericAnnotation obj = new GenericAnnotation("z");
		obj.fromString(this.toString());
		return obj;
	}
	
	/**
	 * Gets the type of annotation.
	 * @return the type identifier for the annotation.
	 */
	public String getType () {
		return type;
	}
	
	public String getString (String name) {
		if ( map == null ) return null;
		Object obj = map.get(name);
		if ( obj == null ) return null;
		if ( !(obj instanceof String) ) {
			throw new InvalidParameterException(String.format("The field '%s' is not a string.", name));
		}
		return (String)obj;
	}
	
	/**
	 * Sets a string field.
	 * @param name the name of the field to set.
	 * @param value the value to set, use null to remove the value.
	 */
	public void setString (String name,
		String value)
	{
		setObject(name, value);
	}

	public Boolean getBoolean (String name) {
		if ( map == null ) return null;
		Object obj = map.get(name);
		if ( obj == null ) return null;
		if ( !(obj instanceof Boolean) ) {
			throw new InvalidParameterException(String.format("The field '%s' is not a boolean.", name));
		}
		return (Boolean)obj;
	}

	public void setBoolean (String name,
		Boolean value)
	{
		setObject(name, value);
	}
	
	public Float getFloat (String name) {
		if ( map == null ) return null;
		Object obj = map.get(name);
		if ( obj == null ) return null;
		if ( !(obj instanceof Float) ) {
			throw new InvalidParameterException(String.format("The field '%s' is not a float.", name));
		}
		return (Float)obj;
	}

	public void setFloat (String name,
		Float value)
	{
		setObject(name, value);
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder(type); // Type saved first
		if ( map == null ) return sb.toString(); // No values
		for ( String name : map.keySet() ) {
			Object value = map.get(name);
			sb.append(FIELD_SEPARATOR);
			sb.append(name); // Name
			sb.append(PART_SEPARATOR);
			if ( value instanceof String ) sb.append('s');
			else if ( value instanceof Boolean ) sb.append('b');
			else if ( value instanceof Float ) sb.append('f');
			sb.append(PART_SEPARATOR);
			sb.append(value); // Value
		}
		return sb.toString();
	}
	
	public void fromString (String storage) {
		String[] fields = storage.split(FIELD_SEPARATOR, 0);
		type = fields[0];
		for ( int i=1; i<fields.length; i++ ) {
			String[] parts = fields[i].split(PART_SEPARATOR, -1);
			if ( parts[1].equals("s") ) {
				setString(parts[0], parts[2]);
			}
			else if ( parts[1].equals("b") ) {
				setBoolean(parts[0], parts[2].equals("true"));
			}
			else if ( parts[1].equals("f") ) {
				setFloat(parts[0], Float.parseFloat(parts[2]));
			}
			else {
				throw new RuntimeException(String.format("Unknow field type in annotation: '%s'", parts[1]));
			}
		}
	}

	private void setObject (String name,
		Object value)
	{
		if ( Util.isEmpty(name) ) {
			throw new InvalidParameterException("The field name must not be null or empty.");
		}
		// Create the map if needed
		if ( map == null ) map = new HashMap<String, Object>();
		// Remove or set the value
		if ( value == null ) map.remove(name);
		else map.put(name, value);
	}

}
