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

public abstract class PersistenceBean implements IPersistenceBean {
	
	private long refId = 0;

	protected abstract Object createObject(IPersistenceSession session);
	protected abstract void setObject(Object obj, IPersistenceSession session);
	protected abstract void fromObject(Object obj, IPersistenceSession session);
	
	@Override
	public long getRefId() {
		if (refId == 0)
			refId = ReferenceResolver.generateRefId();
		
		return refId;
	}

	@Override
	public void setRefId(long refId) {
		this.refId = refId;
	}

	@Override
	public <T> T get(T obj, IPersistenceSession session) {
		if (obj != null)
			session.setRefIdForObject(obj, refId);		
		setObject(obj, session);
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef, IPersistenceSession session) {
		return classRef.cast(get(createObject(session), session));
	}
	
	@Override
	public IPersistenceBean set(Object obj, IPersistenceSession session) {
		fromObject(obj, session);		
		return this;
	}		
}
