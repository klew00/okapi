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

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Basic implementation of the {@link IContext} interface.
 */
public class BaseContext implements IContext {

	private Map<String, Object> properties;
	private Annotations annotations;

	/**
	 * Creates an empty context.
	 */
	public BaseContext () {
	}

	/**
	 * Creates a BaseContext object and copy a map of properties.
	 * @param properties the map of properties to copy.
	 */
	public BaseContext (Map<String, Object> properties) {
		this.properties = new LinkedHashMap<String, Object>(properties);
	}
	
	public String getString (String name) {
		if ( properties == null ) return null;
		return (String)properties.get(name);
	}
	
	public void setString (String name,
		String value)
	{
		if ( properties == null ) {
			properties = new LinkedHashMap<String, Object>();
		}
		properties.put(name, value);
	}

	public boolean getBoolean (String name) {
		if ( properties == null ) return false;
		return (Boolean)properties.get(name);
	}
	
	public void setBoolean (String name,
		boolean value)
	{
		if ( properties == null ) {
			properties = new LinkedHashMap<String, Object>();
		}
		properties.put(name, value);
	}
	
	public int getInteger (String name) {
		if ( properties == null ) return 0;
		return (Integer)properties.get(name);
	}
	
	public void setInteger (String name,
		int value)
	{
		if ( properties == null ) {
			properties = new LinkedHashMap<String, Object>();
		}
		properties.put(name, value);
	}
	
	public Object getObject (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}
	
	public void setObject (String name,
		Object value)
	{
		if ( properties == null ) {
			properties = new LinkedHashMap<String, Object>();
		}
		properties.put(name, value);
	}
	
	public void removeProperty (String name) {
		if ( properties != null ) {
			properties.remove(name);
		}
	}
	
	public Map<String, Object> getProperties () {
		if ( properties == null ) {
			properties = new LinkedHashMap<String, Object>();
		}
		return properties;
	}
	public void clearProperties () {
		if ( properties != null ) {
			properties.clear();
		}
	}
		
	public <A extends IAnnotation> A getAnnotation (Class<A> type) {
		if ( annotations == null ) return null;
		return annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	public void clearAnnotations () {
		if ( annotations != null ) {
			annotations.clear();
		}
	}

}
