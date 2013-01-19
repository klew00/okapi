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

package net.sf.okapi.lib.persistence;

import net.sf.okapi.common.ClassUtil;

public abstract class PersistenceBean<PutCoreClassHere> implements IPersistenceBean<PutCoreClassHere> {
	
	private long refId = 0;
	private boolean busy = false;

	protected abstract PutCoreClassHere createObject(IPersistenceSession session);
	protected abstract void setObject(PutCoreClassHere obj, IPersistenceSession session);
	protected abstract void fromObject(PutCoreClassHere obj, IPersistenceSession session);
	
	public PersistenceBean() {
		super();
		refId = ReferenceResolver.generateRefId();
	}
	
	@Override
	public long getRefId() {
		return refId;
	}

	@Override
	public void setRefId(long refId) {
		this.refId = refId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> classRef, IPersistenceSession session) {
		if (busy) {
			throw new RuntimeException(String.format("PersistenceBean: recursive get() in %s", 
					ClassUtil.getQualifiedClassName(this.getClass())));
		}
		
		// Try to get one created by ReferenceBean.createObject()
		PutCoreClassHere obj = (PutCoreClassHere) session.getObject(refId); 
		if (obj == null) {			
//			if (busy) {
//				Class<?> objRef = session.getObjectClass((Class<? extends IPersistenceBean<?>>) this.getClass());
//				if (objRef == classRef)
//					throw new RuntimeException(String.format("PersistenceBean: recursive object creation in %s.%s", 
//						ClassUtil.getQualifiedClassName(this.getClass()),
//						"createObject()"));
//				else {
//					IPersistenceBean<?> proxy = session.getProxy(classRef);
//					if (proxy != null)
//						obj = (PutCoreClassHere) proxy.get(classRef, session);
//				}
//			}
//			else {
				busy = true; // recursion protection
				try {
					obj = createObject(session);
					// System.out.println(refId + ":  " + obj);
				}
				finally {
					busy = false;
				}
			//}			
		}					
		if (obj != null && refId != 0) { // not for proxies
			session.setRefIdForObject(obj, refId);		
			setObject(obj, session);
		}		
		return classRef.cast(obj);
	}
	
	@Override
	public IPersistenceBean<PutCoreClassHere> set(PutCoreClassHere obj, IPersistenceSession session) {
		if (obj != null)
			fromObject(obj, session);
		
		return this;
	}		
}
