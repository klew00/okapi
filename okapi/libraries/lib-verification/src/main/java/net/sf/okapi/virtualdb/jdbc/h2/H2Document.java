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

import java.util.Iterator;

import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVTextUnit;

public class H2Document extends H2Navigator implements IVDocument {

	H2Access db;
	private String id;
	private String name;
	private String type;
	private H2Document selfDoc;
	
	public H2Document (H2Access access,
		long key,
		String id,
		String name,
		String type)
	{
		db = access;
		this.key = key;
		this.id = id;
		this.name = name;
		this.type = type;
		selfDoc = this;
	}
	
	public void fillPointers (long parent,
		long firstChild,
		long previous,
		long next)
	{
		this.parent = parent;
		this.firstChild = firstChild;
		this.previous = previous;
		this.next = next;
	}

	@Override
	public String getId () {
		return id;
	}

	@Override
	public Ending getEndDocument () {
		throw new UnsupportedOperationException("getEndDocument");
	}

	@Override
	public IVItem getItem (String extractionId) {
		return db.getItemFromExtractionId(this, extractionId);
	}

	@Override
	public IVItem getItem (long key) {
		return db.getItemFromItemKey(this, key);
	}

	@Override
	public StartDocument getStartDocument () {
		throw new UnsupportedOperationException("getStartDocument");
	}

	@Override
	public IVTextUnit getTextUnit (String extractionId) {
		IVItem item = db.getItemFromExtractionId(this, extractionId);
		if ( item instanceof IVTextUnit ) {
			return (IVTextUnit)item;
		}
		return null;
	}

	@Override
	public IVItem getFirstChild () {
		return db.getItemFromItemKey(this, firstChild);
	}

	@Override
	public ItemType getItemType () {
		return IVItem.ItemType.DOCUMENT;
	}

	@Override
	public Iterable<IVItem> items () {
		return new Iterable<IVItem>() {
			@Override
			public Iterator<IVItem> iterator() {
				return new H2ItemIterator<IVItem>(db, selfDoc, false);
			}
		}; 
	}
	
	@Override
	public Iterable<IVTextUnit> textUnits () {
		return new Iterable<IVTextUnit>() {
			@Override
			public Iterator<IVTextUnit> iterator() {
				return new H2ItemIterator<IVTextUnit>(db, selfDoc, true);
			}
		}; 
	}

	@Override
	public IVDocument getDocument () {
		return this;
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public IVItem getNextSibling () {
		return db.getItemFromItemKey(this, next);
	}

	@Override
	public IVItem getParent () {
		// The parent of a document is always null
		return null;
	}

	@Override
	public IVItem getPreviousSibling () {
		return db.getItemFromItemKey(this, previous);
	}

	@Override
	public String getType () {
		return type;
	}

	@Override
	public void save () {
		// No modifiable data to save
	}

}
