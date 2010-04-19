/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

public class FactoryBean extends PersistenceBean {
	
	private long reference;
	private String className;
	private Object content;	// Bean for the className
	
	@Override
	protected Object createObject(IPersistenceSession session) {
		return null;
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
	}

	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
	}

	@Override
	public <T> T get(T obj, IPersistenceSession session) {
		return super.get(obj, session); // To handle refId
	}
	
	@Override
	public <T> T get(Class<T> classRef, IPersistenceSession session) {
		return classRef.cast(validateContent(session) ? ((IPersistenceBean) content).get(classRef, session) : null);
	}
	
	private boolean validateContent(IPersistenceSession session) {
		if (content == null) return false;
		if (className == null) return false;
		
		boolean res = content instanceof IPersistenceBean; 
		if (!res) {
			if (session == null) return false;
			content = session.convert(content, BeanMapper.getBeanClass(className));
			res = content instanceof IPersistenceBean;
		}
		return res;
	}
	
	@Override
	public IPersistenceBean set(Object obj, IPersistenceSession session) {
		if (obj == null) return this;
		
		className = ClassUtil.getQualifiedClassName(obj); // stored for get()
		
//		long rid = session.getRefIdForObject(obj);
//		if (rid != 0) {
//			reference = rid;
//			session.setRefIdForObject(this, this.getRefId()); // To find the ref parent's root
//			session.setReference(this.getRefId(), rid);
//			return this;
//		}
		
		IPersistenceBean bean = session.uncacheBean(obj); // get a bin created earlier here or in a ReferenceBean
		if (bean == null) {
			bean = session.createBean(ClassUtil.getClass(obj));
			// session.cacheBean(obj, bean);
		}
		session.setRefIdForObject(obj, bean.getRefId());
		//session.setRefIdForObject(this, this.getRefId()); // To find the ref parent's root
		reference = 0;
		content = bean;
		
		return (bean instanceof FactoryBean) ? this : bean.set(obj, session);		
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	public void setReference(long reference) {
		this.reference = reference;
	}

	public long getReference() {
		return reference;
	}
}
