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

/**
 * Groups in a single objects all the parameter descriptors associated with
 * a given object such as a step or a filter.
 */
public class ParametersDescription {

	private Object originalObject;
	private LinkedHashMap<String, ParameterDescriptor> descriptors;
	
	/**
	 * Creates a new ParametersDescription object for a given parent object.
	 * @param originalObject the object described. 
	 */
	public ParametersDescription (Object originalObject) {
		descriptors = new LinkedHashMap<String, ParameterDescriptor>();
		this.originalObject = originalObject;
	}
	
	/**
	 * Gets a map of all the parameter descriptors for this description. 
	 * @return a map of all parameter descriptors.
	 */
	public Map<String, ParameterDescriptor> getDescriptors () {
		return descriptors;
	}
	
	/**
	 * Gets the descriptor for a given parameter.
	 * @param name the name of the parameter to lookup.
	 * @return the descriptor for the given parameter.
	 */
	public ParameterDescriptor get (String name) {
		return descriptors.get(name);
	}
	
	/**
	 * Adds a descriptor to this description.
	 * @param name the name of the parameter.
	 * @param displayName the localizable display name for this parameter.
	 * @param shortDescription a short localizable description for this parameter. 
	 * @return the parameter descriptor created by the call.
	 */
	public ParameterDescriptor add (String name,
		String displayName,
		String shortDescription)
	{
		ParameterDescriptor desc = new ParameterDescriptor(name, originalObject,
			displayName, shortDescription);
		descriptors.put(name, desc);
		return desc;
	}

}
