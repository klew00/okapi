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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.annotation.IterableEnumeration;

/**
 * The target properties associated to a set of source properties
 * in a resource.
 */
public class TargetPropertiesAnnotation implements IAnnotation, Iterable<LocaleId> {

	private ConcurrentHashMap<LocaleId, LinkedHashMap<String, Property>> targets;

	/**
	 * Creates a new TargetPropertiesAnnotation object.
	 */
	public TargetPropertiesAnnotation () {
		targets = new ConcurrentHashMap<LocaleId, LinkedHashMap<String, Property>>();
	}

	/**
	 * Sets properties for a given target locale.
	 * @param locId Code of the target locale for this property.
	 * @param properties The properties to set.
	 */
	public void set (LocaleId locId,
		LinkedHashMap<String, Property> properties)
	{
		targets.put(locId, properties);
	}

	/**
	 * Gets the properties for a given target locale.
	 * @param locId Code of the target locale of the properties to retrieve. 
	 * @return The properties, or null if none has been found.
	 */
	public LinkedHashMap<String, Property> get (LocaleId locId) {
		return targets.get(locId);
	}

	/**
	 * Indicates if this annotation has any properties.
	 * @return True if this annotation counts at least one property.
	 */
	public boolean isEmpty () {
		return targets.isEmpty();
	}

	/**
	 * Gets a new iterator for this annotation.
	 */
	public Iterator<LocaleId> iterator () {
		IterableEnumeration<LocaleId> iterableLocales = new IterableEnumeration<LocaleId>(targets.keys());
		return iterableLocales.iterator();
	}

	/**
	 * Gets a set of the target locales available in this annotation.
	 * @return A set of the target locales in this annotation.
	 */
	public Set<LocaleId> getLocales () {
		return targets.keySet();
	}

}
