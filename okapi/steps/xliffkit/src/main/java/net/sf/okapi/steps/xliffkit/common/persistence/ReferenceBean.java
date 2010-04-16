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

import net.sf.okapi.common.ClassUtil;

public class ReferenceBean extends PersistenceBean {

	private int reference;
	
	public ReferenceBean(IPersistenceSession session) {
		super(session);
	}

	@Override
	public <T> T get(T obj) {
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		return null;
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj == null) return this;
		
		int rid = getSession().getRefIdForObject(obj);
		if (rid != 0) {
			setReference(rid);
			getSession().setRefIdForObject(this, this.getRefId()); // To find the ref parent's root
			getSession().setReference(this.getRefId(), rid);
			return this;
		}
		
		IPersistenceBean bean = getSession().createBean(ClassUtil.getClass(obj));
		getSession().cacheBean(obj, bean);
		setReference(bean.getRefId());
		getSession().setRefIdForObject(obj, bean.getRefId());
		getSession().setReference(this.getRefId(), bean.getRefId());
		
		return this;
	}

	public void setReference(int reference) {
		this.reference = reference;
	}

	public int getReference() {
		return reference;
	}

}
