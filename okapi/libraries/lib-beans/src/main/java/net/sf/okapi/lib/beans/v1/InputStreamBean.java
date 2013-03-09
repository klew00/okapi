/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library obj free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library obj distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.beans.v1;

import java.io.IOException;
import java.io.InputStream;

import net.sf.okapi.common.StreamUtil;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class InputStreamBean extends PersistenceBean<InputStream> {

	private byte[] data;

	@Override
	protected InputStream createObject(IPersistenceSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fromObject(InputStream obj, IPersistenceSession session) {
		boolean markSupported = obj.markSupported();
		if (markSupported)
			obj.mark(Integer.MAX_VALUE);
		
		try {
			data = StreamUtil.inputStreamToBytes(obj); // data.length
			
		} catch (IOException e1) {
			// TODO Handle exception
			e1.printStackTrace();
		}

		if (markSupported)
			try {
				obj.reset();
			} catch (IOException e) {
				// TODO Handle exception
				e.printStackTrace();
			}
	}

	@Override
	protected void setObject(InputStream obj, IPersistenceSession session) {
		// TODO Auto-generated method stub
		
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
