/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.beans.v0;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class ParametersBean implements IPersistenceBean {

	private String data;
	
	@Override
	public <T> T get(Class<T> classRef) {
		IParameters params = null;
		try {
			params = (IParameters) ClassUtil.instantiateClass(classRef);
			params.fromString(data);
		} catch (InstantiationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		return classRef.cast(params);
	}

	@Override
	public void init(IPersistenceSession session) {
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof IParameters) {
			IParameters params = (IParameters) obj;
			data = params.toString();
		}
		return this;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
