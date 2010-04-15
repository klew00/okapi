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

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TargetPropertiesAnnotation;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class TargetPropertiesAnnotationBean extends PersistenceBean {

	private ConcurrentHashMap<String, Hashtable<String, PropertyBean>> targets = 
		new ConcurrentHashMap<String, Hashtable<String, PropertyBean>>();
	
	public TargetPropertiesAnnotationBean(IPersistenceSession session) {
		super(session);
	}

	@Override
	public <T> T get(T obj) {
		if (obj instanceof TargetPropertiesAnnotation) {
			TargetPropertiesAnnotation annot = (TargetPropertiesAnnotation) obj;
		
			for (String locTag : targets.keySet()) {
				Hashtable<String, PropertyBean> propBeans = targets.get(locTag);
				Hashtable<String, Property> props = new Hashtable<String, Property>();
				
				for (String key : propBeans.keySet()) {
					PropertyBean propBean = propBeans.get(key);
					Property prop = propBean.get(Property.class);
					props.put(key, prop);
				}
				
				annot.set(new LocaleId(locTag), props);
			}
		}
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(get(new TargetPropertiesAnnotation()));
	}
	
	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof TargetPropertiesAnnotation) {
			TargetPropertiesAnnotation annot = (TargetPropertiesAnnotation) obj;
			
			for (LocaleId locId : annot) {
				Hashtable<String, PropertyBean> propBeans = new Hashtable<String, PropertyBean>();
				Hashtable<String, Property> props = annot.get(locId);
				
				for (String key : props.keySet()) {
					Property prop = props.get(key);
					PropertyBean propBean = new PropertyBean(getSession());
					propBean.set(prop);
					propBeans.put(key, propBean);
				}								
				targets.put(locId.toString(), propBeans);
			}
		}
		return this;
	}
}
