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

package net.sf.okapi.lib.beans.v1;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TargetPropertiesAnnotation;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class TargetPropertiesAnnotationBean extends PersistenceBean<TargetPropertiesAnnotation> {

	private ConcurrentHashMap<String, LinkedHashMap<String, PropertyBean>> targets = 
		new ConcurrentHashMap<String, LinkedHashMap<String, PropertyBean>>();

	@Override
	protected TargetPropertiesAnnotation createObject(IPersistenceSession session) {
		return new TargetPropertiesAnnotation();
	}

	@Override
	protected void fromObject(TargetPropertiesAnnotation obj, IPersistenceSession session) {
		for (LocaleId locId : obj) {
			LinkedHashMap<String, PropertyBean> propBeans = new LinkedHashMap<String, PropertyBean>();
			LinkedHashMap<String, Property> props = obj.get(locId);
			
			for (String key : props.keySet()) {
				Property prop = props.get(key);
				PropertyBean propBean = new PropertyBean();
				propBean.set(prop, session);
				propBeans.put(key, propBean);
			}								
			targets.put(locId.toString(), propBeans);
		}
	}

	@Override
	protected void setObject(TargetPropertiesAnnotation obj, IPersistenceSession session) {
		for (String locTag : targets.keySet()) {
			LinkedHashMap<String, PropertyBean> propBeans = targets.get(locTag);
			LinkedHashMap<String, Property> props = new LinkedHashMap<String, Property>();
			
			for (String key : propBeans.keySet()) {
				PropertyBean propBean = propBeans.get(key);
				Property prop = propBean.get(Property.class, session);
				props.put(key, prop);
			}
			
			obj.set(new LocaleId(locTag), props);
		}
	}
}
