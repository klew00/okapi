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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.annotation.IterableEnumeration;

/**
 * Represents the target properties associated to a set of source properties in a resource.
 */
public class TargetPropertiesAnnotation implements IAnnotation, Iterable<String> {

	private ConcurrentHashMap<String, Hashtable<String, Property>> targets;

	public TargetPropertiesAnnotation () {
		targets = new ConcurrentHashMap<String, Hashtable<String, Property>>();
	}

	public void set (String locale, Hashtable<String, Property> properties) {
		targets.put(locale, properties);
	}

	public Hashtable<String, Property> get (String locale) {
		return targets.get(locale);
	}

	public boolean isEmpty () {
		return targets.isEmpty();
	}

	public Iterator<String> iterator () {
		IterableEnumeration<String> iterableLocales = new IterableEnumeration<String>(targets.keys());
		return iterableLocales.iterator();
	}

	public Set<String> getLanguages () {
		return targets.keySet();
	}
}
