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

package net.sf.okapi.virtualdb.jdbc.h2;

import net.sf.okapi.virtualdb.IVItem.ItemType;

class H2Navigator {

	protected long key;
	protected long docKey;
	protected ItemType itemType;
	protected int level;
	protected long parent;
	protected long firstChild;
	protected long next;
	protected long previous;
	
	public H2Navigator () {
		// Default constructor
	}
	
	public H2Navigator (ItemType type,
		long key,
		long docKey,
		int level)
	{
		this.itemType = type;
		this.key = key;
		this.docKey = docKey;
		this.level = level;
		fillPointers(-1, -1, -1, -1);
	}

	public void fillPointers(long parent,
		long firstChild,
		long previous,
		long next)
	{
		this.parent = parent;
		this.firstChild = firstChild;
		this.previous = previous;
		this.next = next;
	}

	public ItemType getItemType () {
		return itemType;
	}
	
	public long getKey () {
		return key;
	}

}
