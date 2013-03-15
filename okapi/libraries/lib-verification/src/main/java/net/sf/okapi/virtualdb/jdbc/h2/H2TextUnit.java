/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVTextUnit;

public class H2TextUnit implements IVTextUnit {

	private long itemKey;
	private H2Document doc;
	private long parent;
	private long previous;
	private long next;
	private ITextUnit tu;
	
	public H2TextUnit (long itemKey,
		H2Document doc,
		String id,
		String name,
		String type)
	{
		super();
		tu = new TextUnit(id);
		this.itemKey = itemKey;
		this.doc = doc;
		tu.setName(name);
		tu.setType(type);
	}
	
	/**
	 * Fills the navigation pointers for this virtual text unit.
	 * @param parent the parent.
	 * @param previous the previous sibling.
	 * @param next the next sibling.
	 */
	void fillPointers(long parent,
		long previous,
		long next)
	{
		this.parent = parent;
		this.previous = previous;
		this.next = next;
	}

	/**
	 * Sets the text unit for this virtual text unit.
	 * @param tu the text unit to attach to this object.
	 */
	void setTextUnit (ITextUnit tu) {
		this.tu = tu;
	}

	@Override
	public ITextUnit getTextUnit () {
		return tu;
	}
	
	@Override
	public IVDocument getDocument () {
		return doc;
	}

	@Override
	public IVItem getParent () {
		return doc.db.getItemFromItemKey(doc, parent);
	}

	@Override
	public IVItem getNextSibling () {
		return doc.db.getItemFromItemKey(doc, next);
	}

	@Override
	public IVItem getPreviousSibling () {
		return doc.db.getItemFromItemKey(doc, previous);
	}

	@Override
	public IVItem getFirstChild () {
		// A virtual text unit cannot have children
		return null;
	}

	@Override
	public void save () {
		doc.db.saveTextUnit(this);
	}

	@Override
	public ItemType getItemType () {
		return ItemType.TEXT_UNIT;
	}

	@Override
	public String getId () {
		return tu.getId();
	}

	@Override
	public String getName () {
		return tu.getName();
	}

	@Override
	public String getType () {
		return tu.getType();
	}

	@Override
	public long getKey () {
		return itemKey;
	}

}
