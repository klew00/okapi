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

package net.sf.okapi.common.annotation;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 
 * Wrap an Enumeration and make it compatible with new Iterators. Useful for
 * dealing with old style API's.
 * 
 * @param <T>
 */
public class IterableEnumeration<T> implements Iterable<T> {
	private final Enumeration<T> en;

	public IterableEnumeration(Enumeration<T> en) {
		this.en = en;
	}

	// return an adaptor for the Enumeration
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			public boolean hasNext() {
				return en.hasMoreElements();
			}

			public T next() {
				return en.nextElement();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <T> Iterable<T> make(Enumeration<T> en) {
		return new IterableEnumeration<T>(en);
	}
}
