/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.xliff;

import java.util.ArrayList;
import java.util.Iterator;

public class Unit implements Iterable<Segment> {
	
	private ArrayList<Segment> list;
	private String id;

	public Unit (String id) {
		this.id = id;
		list = new ArrayList<Segment>();
	}
	
	@Override
    public Iterator<Segment> iterator() {
		return new Iterator<Segment>() {
			int current = 0;

			@Override
			public void remove () {
				throw new UnsupportedOperationException("The method remove() not supported.");
			}

			@Override
			public Segment next () {
				return (Segment)list.get((++current)-1);
			}

			@Override
			public boolean hasNext () {
				return ( !list.isEmpty() && ( current < list.size() ));
			}
		};
	};

	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public Unit add (Segment segment) {
		list.add(segment);
		return this;
	}

}
