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
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVSubDocument;
import net.sf.okapi.virtualdb.IVTextUnit;

public class H2SubDocument extends H2Navigator implements IVSubDocument {

	private H2Document doc;
	private String id;
	private String name;
	private String type;
	
	public H2SubDocument (long itemKey,
		H2Document doc,
		String id,
		String name,
		String type)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.doc = doc;
		itemType = ItemType.SUB_DOCUMENT;
	}
	
	@Override
	public String getId () {
		return id;
	}

	@Override
	public Ending getEndSubDocument () {
		throw new UnsupportedOperationException("getEndSubDocument");
	}

	@Override
	public StartSubDocument getStartSubDocument () {
		throw new UnsupportedOperationException("getStartSubDocument");
	}

	@Override
	public IVItem getFirstChild () {
		return doc.db.getItemFromItemKey(doc, firstChild);
	}

	@Override
	public Iterable<IVItem> items () {
		return new Iterable<IVItem>() {
			@Override
			public Iterator<IVItem> iterator() {
				return new H2ItemIterator<IVItem>(doc.db, doc, false);
			}
		}; 
	}
	
	@Override
	public Iterable<IVTextUnit> textUnits () {
		return new Iterable<IVTextUnit>() {
			@Override
			public Iterator<IVTextUnit> iterator() {
				return new H2ItemIterator<IVTextUnit>(doc.db, doc, true);
			}
		}; 
	}

	@Override
	public IVDocument getDocument () {
		return doc;
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public IVItem getNextSibling () {
		return doc.db.getItemFromItemKey(doc, next);
	}

	@Override
	public IVItem getParent () {
		return doc.db.getItemFromItemKey(doc, parent);
	}

	@Override
	public IVItem getPreviousSibling () {
		return doc.db.getItemFromItemKey(doc, previous);
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
