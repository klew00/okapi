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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class GenericSkeletonBean extends PersistenceBean {

	private List<GenericSkeletonPartBean> parts = new ArrayList<GenericSkeletonPartBean>();
	
	public GenericSkeletonBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		if (obj instanceof GenericSkeleton) {
			GenericSkeleton skel = (GenericSkeleton) obj;
			
			for (GenericSkeletonPartBean partBean : parts)
				skel.add(partBean.getData());
		}
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(get(new GenericSkeleton()));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof GenericSkeleton) {
			GenericSkeleton skel = (GenericSkeleton) obj;
			
			for (GenericSkeletonPart part : skel.getParts()) {
				GenericSkeletonPartBean partBean = new GenericSkeletonPartBean(getSession());
				parts.add(partBean);
				partBean.set(part);
			}
		}
		return this;
	}

	public List<GenericSkeletonPartBean> getParts() {
		return parts;
	}

	public void setParts(List<GenericSkeletonPartBean> parts) {
		this.parts = parts;
	}
}
