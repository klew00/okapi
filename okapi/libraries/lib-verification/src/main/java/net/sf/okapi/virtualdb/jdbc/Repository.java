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

package net.sf.okapi.virtualdb.jdbc;

import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVRepository;
import net.sf.okapi.virtualdb.IVTextUnit;

public class Repository implements IVRepository {

	private IDBAccess db;
	
	public Repository (IDBAccess engine) {
		db = engine;
	}
	
	protected void finalize ()
		throws Throwable
	{
		close();
	}
	
	@Override
	public void close () {
		if ( db != null ) {
			db.close();
			db = null;
		}
	}
	
	@Override
	public Iterable<IVDocument> documents () {
		return db.documents();
	}

	@Override
	public IVDocument getDocument (String docId) {
		return db.getDocument(docId);
	}

	@Override
	public Iterable<IVItem> items () {
//		return new Iterable<IVItem>() {
//			@Override
//			public Iterator<IVItem> iterator() {
//				return new H2ItemIterator<IVItem>(this, null, false);
//			}
//		};
		return null;
	}

	@Override
	public Iterable<IVTextUnit> textUnits () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String importDocument (RawDocument rawDoc) {
		return db.importDocument(rawDoc);
	}

	@Override
	public void removeDocument (IVDocument doc) {
		db.removeDocument(doc);
	}
	
	@Override
	public IVDocument getFirstDocument() {
		return db.getFirstDocument();
	}

}
