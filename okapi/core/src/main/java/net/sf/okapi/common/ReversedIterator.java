/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Create a reversed iterator for a list compatible with foreach. Credit: Nat on
 * http://stackoverflow.com/questions/1098117/can-one-do-a-for-each-loop-in-java-in-reverse-orders
 * 
 * @author HARGRAVEJE
 * 
 * @param <T>
 *            type of the list element
 */
public class ReversedIterator<T> implements Iterable<T> {
	private final List<T> original;

	public ReversedIterator(final List<T> original) {
		this.original = original;
	}

	public Iterator<T> iterator() {
		final ListIterator<T> i = original.listIterator(original.size());

		return new Iterator<T>() {
			public boolean hasNext() {
				return i.hasPrevious();
			}

			public T next() {
				return i.previous();
			}

			public void remove() {
				i.remove();
			}
		};
	}
	
	public static <T> ReversedIterator<T> reverse(List<T> original) {
        return new ReversedIterator<T>(original);
    }

}
