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

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class ParametersBean extends PersistenceBean {

	private String className;
	private String data;
	
	public ParametersBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		if (obj instanceof IParameters) {
			IParameters params = (IParameters) obj;			
			params.fromString(data);
		}
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {
		IParameters params = null;
		try {
			params = (IParameters) ClassUtil.instantiateClass(className);			
		} catch (Exception e) {
			throw new RuntimeException(String.format("ParametersBean: cannot instantiate %s", ClassUtil.getClassName(classRef)), e);
		}		
		if (params != null)
			params.fromString(data);
		
		return classRef.cast(get(params));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof IParameters) {
			IParameters params = (IParameters) obj;
			className = ClassUtil.getQualifiedClassName(obj);
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
