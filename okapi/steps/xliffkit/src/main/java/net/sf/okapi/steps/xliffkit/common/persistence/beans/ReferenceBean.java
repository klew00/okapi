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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class ReferenceBean extends PersistenceBean<Object> {

	private long reference;

	@Override
	protected Object createObject(IPersistenceSession session) {
		return null;
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
		if (obj == null) return;
		
		session.setRefIdForObject(this, this.getRefId()); // To find the ref parent's root
		
		long rid = session.getRefIdForObject(obj);
		if (rid != 0) {
			reference = rid;
			//session.setRefIdForObject(this, this.getRefId()); // To find the ref parent's root
			session.setReference(this.getRefId(), rid);
			return;
		}
		
		IPersistenceBean bean = session.createBean(ClassUtil.getClass(obj));
		session.cacheBean(obj, bean); // for a FactoryBean to hook up later
		reference = bean.getRefId();
		// session.setRefIdForObject(obj, bean.getRefId());
		session.setReference(this.getRefId(), bean.getRefId());		
	}

	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
		// TODO Auto-generated method stub
		
	}

	public void setReference(long reference) {
		this.reference = reference;
	}

	public long getReference() {
		return reference;
	}
}
