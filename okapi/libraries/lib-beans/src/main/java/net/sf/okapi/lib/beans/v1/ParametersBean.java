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

package net.sf.okapi.lib.beans.v1;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class ParametersBean extends PersistenceBean<IParameters> {

	private String className;
	private String data;

	@Override
	protected IParameters createObject(IPersistenceSession session) {
		if (Util.isEmpty(className)) return null;
			
		IParameters obj = null;
		try {
			obj = (IParameters) ClassUtil.instantiateClass(className);			
		} catch (Exception e) {
			throw new RuntimeException(String.format("ParametersBean: cannot instantiate %s", className), e);
		}
		return obj;
	}

	@Override
	protected void fromObject(IParameters obj, IPersistenceSession session) {
		className = ClassUtil.getQualifiedClassName(obj);
		data = obj.toString();
	}

	@Override
	protected void setObject(IParameters obj, IPersistenceSession session) {
		obj.fromString(data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
