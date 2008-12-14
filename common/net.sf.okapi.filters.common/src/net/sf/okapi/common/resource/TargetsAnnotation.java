/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.resource;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.annotation.IterableEnumeration;

public class TargetsAnnotation implements IAnnotation, Iterable<String> {

	private ConcurrentHashMap<String, TextContainer> targets;

	public TargetsAnnotation() {
		targets = new ConcurrentHashMap<String, TextContainer>();
	}

	public void set (String language, TextContainer tt) {
		targets.put(language, tt);
	}

	public TextContainer get (String language) {
		return targets.get(language);
	}

	public boolean isEmpty() {
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
