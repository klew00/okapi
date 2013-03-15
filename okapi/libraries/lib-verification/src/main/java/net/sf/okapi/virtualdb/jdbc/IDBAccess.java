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

import java.io.InputStream;

import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVRepository.OpeningMode;

public interface IDBAccess {

	public enum RepositoryType {
		INMEMORY,
		LOCAL,
		REMOTE
	}
	
	public void open (String name,
		OpeningMode mode);

	public void open (String name);
	
	public void create (String name);
	
	public void close () ;
	
	public void delete ();

	public String importDocument (RawDocument rawDoc);
	
	public long importDocumentReturnKey (RawDocument rawDoc);
	
	public void removeDocument (IVDocument doc);
	
//	public IVDocument getDocument (String docId);

	public IVDocument getDocument (long key);

	public Iterable<IVDocument> documents ();

	public IVDocument getFirstDocument();

	public void saveExtraData1 (InputStream inputStream);

	public InputStream loadExtraData1 ();
}
