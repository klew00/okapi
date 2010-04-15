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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import java.io.IOException;
import java.io.InputStream;

import net.sf.okapi.common.StreamUtil;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class InputStreamBean extends PersistenceBean {

	private byte[] data;
	
	public InputStreamBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof InputStream) {
			InputStream is = (InputStream) obj;
			boolean markSupported = is.markSupported();
			if (markSupported)
				is.mark(Integer.MAX_VALUE);
			
			try {
				data = StreamUtil.inputStreamToBytes(is); // data.length
				
			} catch (IOException e1) {
				// TODO Handle exception
				e1.printStackTrace();
			}

			if (markSupported)
				try {
					is.reset();
				} catch (IOException e) {
					// TODO Handle exception
					e.printStackTrace();
				}
		}
		return this;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

}
