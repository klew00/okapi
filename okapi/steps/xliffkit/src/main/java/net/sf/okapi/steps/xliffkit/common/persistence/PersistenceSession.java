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

package net.sf.okapi.steps.xliffkit.common.persistence;

import java.io.InputStream;
import java.io.OutputStream;

public class PersistenceSession implements IPersistenceSession {

	@Override
	public void cacheBean(Object obj, IPersistenceBean bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends IPersistenceBean> T convert(Object obj,
			Class<T> expectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistenceBean createBean(Class<?> classRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T deserialize(Class<T> classRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getItemClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRefIdForObject(Object obj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serialize(Object obj, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRefIdForObject(Object obj, long refId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReference(long parentRefId, long childRefId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(OutputStream outStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public IPersistenceBean uncacheBean(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionState getState() {
		// TODO Auto-generated method stub
		return null;
	}

}
