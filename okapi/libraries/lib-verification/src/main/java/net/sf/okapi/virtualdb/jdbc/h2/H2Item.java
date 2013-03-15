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

public abstract class H2Item extends H2Navigator implements IVItem {

	protected H2Document doc;
	protected String id;
	protected String name;
	protected String type;

	@Override
	public IVDocument getDocument () {
		return doc;
	}

	@Override
	public String getId () {
		return id;
	}

	@Override
	public String getName () {
		return name;
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
	
	public IVItem getFirstChild () {
		return doc.db.getItemFromItemKey(doc, firstChild);
	}

	@Override
	public String getType () {
		return type;
	}

}
