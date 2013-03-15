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
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.virtualdb.IVDocument;

public class H2DocumentIterator implements Iterator<IVDocument> {

	private H2Access db;
	private List<Long> list;
	private int current = -1;
	
	public H2DocumentIterator (H2Access db) {
		this.db = db;
		list = db.getDocumentsKeys();
	}

	@Override
	public boolean hasNext () {
		if ( Util.isEmpty(list) ) return false;
		return current < list.size()-1;
	}

	@Override
	public IVDocument next () {
		return db.getDocument(list.get(++current));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("The method remove() is not supported.");
	}

//	public boolean hasPrevious () {
//		if ( Util.isEmpty(list) ) return false;
//		return current > 0;
//	}
//
//	public IVDocument previous () {
//		return db.getDocument(list.get(--current));
//	}

}
