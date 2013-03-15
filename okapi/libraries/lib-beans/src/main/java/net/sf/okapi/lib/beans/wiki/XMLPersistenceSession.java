/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.beans.wiki;

import java.io.InputStream;
import java.io.OutputStream;

import net.sf.okapi.lib.persistence.IPersistenceBean;
import net.sf.okapi.lib.persistence.PersistenceSession;

public abstract class XMLPersistenceSession extends PersistenceSession {

	@Override
	protected void endReading(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void endWriting(OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected <T extends IPersistenceBean<?>> T readBean(Class<T> beanClass,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void startReading(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startWriting(OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeBean(IPersistenceBean<?> bean, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends IPersistenceBean<?>> T convert(Object obj,
			Class<T> expectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() {
		// TODO Auto-generated method stub
		return null;
	}
}
