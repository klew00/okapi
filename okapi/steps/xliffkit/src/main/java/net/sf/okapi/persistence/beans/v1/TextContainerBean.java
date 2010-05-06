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

package net.sf.okapi.persistence.beans.v1;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.persistence.IPersistenceSession;
import net.sf.okapi.persistence.PersistenceBean;
import net.sf.okapi.persistence.beans.FactoryBean;

public class TextContainerBean extends PersistenceBean<TextContainer> {
	
	private List<PropertyBean> properties = new ArrayList<PropertyBean>();
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private List<TextPartBean> parts = new ArrayList<TextPartBean>();
	private boolean segApplied;

	@Override
	protected TextContainer createObject(IPersistenceSession session) {
		return new TextContainer();
	}

	@Override
	protected void fromObject(TextContainer obj, IPersistenceSession session) {
		for (String propName : obj.getPropertyNames()) {
			PropertyBean propBean = new PropertyBean();
			propBean.set(obj.getProperty(propName), session);
			properties.add(propBean);
		}
		
		for (IAnnotation annotation : obj.getAnnotations()) {
			FactoryBean annotationBean = new FactoryBean();
			annotations.add(annotationBean);
			annotationBean.set(annotation, session);
		}
		
		for (int i = 0; i < obj.count(); i++) {
			TextPartBean partBean = (TextPartBean) session.createBean(obj.get(i).getClass());
			parts.add(partBean);
			partBean.set(obj.get(i), session);
		}
		
		segApplied = obj.hasBeenSegmented();
	}

	@Override
	protected void setObject(TextContainer obj, IPersistenceSession session) {
		for (PropertyBean prop : properties)
			obj.setProperty(prop.get(Property.class, session));
		
		for (FactoryBean annotationBean : annotations)
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
		
		for (TextPartBean partBean : parts)
			obj.insert(obj.count(), partBean.get(TextPart.class, session));
		
		obj.setHasBeenSegmentedFlag(segApplied);
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

	public List<TextPartBean> getParts() {
		return parts;
	}

	public void setParts(List<TextPartBean> parts) {
		this.parts = parts;
	}

	public boolean isSegApplied() {
		return segApplied;
	}

	public void setSegApplied(boolean segApplied) {
		this.segApplied = segApplied;
	}
}
