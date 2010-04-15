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

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class GenericSkeletonPartBean extends PersistenceBean {

	private String data;
	private FactoryBean parent = new FactoryBean(getSession());
	private String locId;
	
	public GenericSkeletonPartBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		// IResource res = RefResolver.resolve(IResource.class, parent);
		return classRef.cast(get(new GenericSkeletonPart(data, parent.get(IResource.class), 
				new LocaleId(locId))));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof GenericSkeletonPart) {
			GenericSkeletonPart part = (GenericSkeletonPart) obj;
			
			data = part.toString();
//			IResource res = part.getParent();
//			if (res != null) {
//				parent = res.getId();
//				parentClass = ClassUtil.getQualifiedClassName(res);
//			}
			parent.set(part.getParent());
			LocaleId loc = part.getLocale();
			if (loc != null)
				locId = loc.toString();			
		}
		return this;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setLocId(String locId) {
		this.locId = locId;
	}

	public String getLocId() {
		return locId;
	}

	public FactoryBean getParent() {
		return parent;
	}

	public void setParent(FactoryBean parent) {
		this.parent = parent;
	}
}
