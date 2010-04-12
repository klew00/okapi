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

import net.sf.okapi.common.resource.BaseReferenceable;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;

public class BaseReferenceableBean extends BaseNameableBean {

	private int refCount;
	private String parentId;
	
	@Override
	public <T> T get(T obj) {		
		obj = super.get(obj);
		
		if (obj instanceof BaseReferenceable) {						
			
			BaseReferenceable br = (BaseReferenceable) obj;
			br.setReferenceCount(refCount);
			br.setParentId(parentId);			
		}
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {		
		return classRef.cast(get(new BaseReferenceable()));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		super.set(obj);
		
		if (obj instanceof BaseReferenceable) {
			BaseReferenceable br = (BaseReferenceable) obj;
			
			refCount = br.getReferenceCount();
			parentId = br.getParentId();
		}
		
		return this;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}