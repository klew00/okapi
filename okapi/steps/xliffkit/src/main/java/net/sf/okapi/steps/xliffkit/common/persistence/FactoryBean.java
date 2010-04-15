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
	
	private int reference;
	private String className;
	private Object content;	// Bean for the className
	
	public FactoryBean(IPersistenceSession session) {
		super(session);
	}

	@Override
	public <T> T get(T obj) {
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(validateContent() ? ((IPersistenceBean) content).get(classRef) : null);
	}
	
	private boolean validateContent() {
		if (content == null) return false;
		if (className == null) return false;
		
//		String cname = ClassUtil.getQualifiedClassName(content);
//		if (!className.equals(cname))			
//			content = JSONObjectConverter.convert(content, PersistenceMapper.getBeanClass(className));
//		if (content == null) return false;
//		cname = ClassUtil.getQualifiedClassName(content);
//		
//		return className.equals(cname);
		boolean res = content instanceof IPersistenceBean; 
		if (!res) {
			if (getSession() == null) return false;
			content = getSession().convert(content, BeanMapper.getBeanClass(className));
			res = content instanceof IPersistenceBean;
		}
		return res;
	}
	
	@Override
	public IPersistenceBean set(Object obj) {
		if (obj == null) return this;
		
		className = ClassUtil.getQualifiedClassName(obj);
		//System.out.println(className);
		
		//int rid = beenLookup.get(obj);
		int refId = this.getRefId(); 
		int rid = getSession().getRefIdForObject(obj);
		if (rid != 0) {
			reference = rid;
			getSession().setRefIdForObject(this, refId); // To find the ref parent's root
			getSession().setReference(refId, rid);
			content = null;			
			return this;
		}
		
		IPersistenceBean bean = BeanMapper.getBean(ClassUtil.getClass(obj), getSession());
		getSession().setRefIdForObject(obj, refId);
		reference = 0;
		content = bean;
		
		return (bean instanceof FactoryBean) ? this : bean.set(obj);		
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

	public void setReference(int reference) {
		this.reference = reference;
	}

	public int getReference() {
		return reference;
	}
}
