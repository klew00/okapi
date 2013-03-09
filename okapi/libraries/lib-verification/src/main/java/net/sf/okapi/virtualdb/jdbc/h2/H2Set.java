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

import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVSet;
import net.sf.okapi.virtualdb.IVTextUnit;

public class H2Set implements IVSet {

//	private H2Access db;
	private String id;
	private String name;
	private String type;
	
	public H2Set (H2Access access,
		String id,
		String name,
		String type)
	{
		//db = access;
		this.id = id;
		this.name = name;
		this.type = type;
	}

	@Override
	public String getId () {
		return id;
	}
	
	@Override
	public IVItem getFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemType getItemType() {
		return null;
	}

	@Override
	public Iterable<IVItem> items() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IVTextUnit> textUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVDocument getDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public IVItem getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVItem getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVItem getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType () {
		return type;
	}

	@Override
	public void save () {
		// No modifiable data to save
	}

	@Override
	public long getKey () {
		return -1L; // TODO
	}

}
