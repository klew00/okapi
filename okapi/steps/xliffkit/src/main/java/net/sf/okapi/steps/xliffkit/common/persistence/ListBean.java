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

package net.sf.okapi.steps.xliffkit.common.persistence;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ClassUtil;

public class ListBean extends PersistenceBean {

	private String className;
	List<FactoryBean> items = new ArrayList<FactoryBean>();

	@Override
	protected Object createObject(IPersistenceSession session) {
		Object res = null;
		try {
			res = ClassUtil.instantiateClass(className);
		} catch (InstantiationException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
		if (obj instanceof List<?>) {
			className = ClassUtil.getQualifiedClassName(obj);
			List<?> list = (List<?>) obj;
			for (Object item : list) {
				FactoryBean itemBean = new FactoryBean();
				itemBean.set(item, session);
				items.add(itemBean);
			}
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
		if (obj instanceof List<?>) {
			List<Object> list = (List<Object>) obj;
			for (IPersistenceBean itemBean : items) {
				Object item = itemBean.get(Object.class, session);
				list.add(item);
			}
		}		
	}

	public List<FactoryBean> getList() {
		return items;
	}

	public void setList(List<FactoryBean> items) {
		this.items = items;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
}
