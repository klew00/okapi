/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.util.Map;

import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Common set of methods to provide context information.
 */
public interface IContext {

	/**
	 * Gets a string property of this context.
	 * @param name the name of the property to retrieve.
	 * @return the value of the property or null if it not defined.
	 */
	public String getString (String name);
	
	/**
	 * Sets a string property for this context. If the property is already
	 * defined its value will be overwritten.
	 * @param name the name of the property to set.
	 * @param value the value to set. 
	 */
	public void setString (String name,
		String value);

	/**
	 * Gets a boolean property of this context.
	 * @param name the name of the property to retrieve.
	 * @return the value of the property or false if it is not defined.
	 */
	public boolean getBoolean (String name);
	
	/**
	 * Sets a boolean property for this context. If the property is already
	 * defined its value will be overwritten.
	 * @param name the name of the property to set.
	 * @param value the value to set. 
	 */
	public void setBoolean (String name,
		boolean value);
	
	/**
	 * Gets an integer property of this context.
	 * @param name the name of the property to retrieve.
	 * @return the value of the property or 0 if it is not defined.
	 */
	public int getInteger (String name);
	
	/**
	 * Sets an integer property for this context. If the property is already
	 * defined its value will be overwritten.
	 * @param name the name of the property to set.
	 * @param value the value to set. 
	 */
	public void setInteger (String name,
		int value);
	
	/**
	 * Gets an object property of this context.
	 * @param name the name of the property to retrieve.
	 * @return the value of the property or null if it is not defined.
	 */
	public Object getObject (String name);
	
	/**
	 * Sets an object property for this context. If the property is already
	 * defined its value will be overwritten.
	 * @param name the name of the property to set.
	 * @param value the value to set. 
	 */
	public void setObject (String name,
		Object value);
	
	/**
	 * Removes a given property from this context. If the property does not exist
	 * nothing happens.
	 * @param name the name of the property to remove.
	 */
	public void removeProperty (String name);
	
	/**
	 * Gets the map of the existing properties for this context.
	 * @return the map of the properties for this context. May be empty but not null.
	 */
	public Map<String, Object> getProperties ();
	
	/**
	 * Removes all properties from this context.
	 */
	public void clearProperties ();
	
	/**
	 * Gets the annotation of a given type for this context.
	 * @param type the type of the annotation to retrieve.
	 * @return the annotation for the given type, or null if it is not defined.
	 */
	public <A extends IAnnotation> A getAnnotation (Class<A> type);

	/**
	 * Sets an annotation for this context.
	 * @param annotation the annotation to set. If one of this type
	 * already exists it will be overwritten. 
	 */
	public void setAnnotation (IAnnotation annotation);
	
	/**
	 * Removes all annotations from this context.
	 */
	public void clearAnnotations ();

}
