/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class TextContainerBean extends PersistenceBean<TextContainer> {
	
	private List<PropertyBean> properties = new ArrayList<PropertyBean>();
	private AnnotationsBean annotations = new AnnotationsBean();
	//private List<TextPartBean> parts = new ArrayList<TextPartBean>();
	private List<FactoryBean> parts = new ArrayList<FactoryBean>();
	private boolean segApplied;

	@Override
	protected TextContainer createObject(IPersistenceSession session) {
		List<TextPart> pts = new ArrayList<TextPart>(); 
		for (FactoryBean partBean : parts) {
			TextPart part = partBean.get(TextPart.class, session);
			if (part != null)
				pts.add(part);
		}
		return new TextContainer(pts.toArray(new TextPart[] {}));
	}

	@Override
	protected void fromObject(TextContainer obj, IPersistenceSession session) {
		for (String propName : obj.getPropertyNames()) {
			PropertyBean propBean = new PropertyBean();
			propBean.set(obj.getProperty(propName), session);
			properties.add(propBean);
		}
		
		annotations.set(obj.getAnnotations(), session);
		
		for (int i = 0; i < obj.count(); i++) {
			TextPart part = obj.get(i);
//			TextPartBean partBean = part.isSegment() ? 
//					(SegmentBean) session.createBean(part.getClass()) : 
//						(TextPartBean) session.createBean(part.getClass());
			FactoryBean partBean = new FactoryBean();
			parts.add(partBean);
			partBean.set(part, session);
		}
		
		segApplied = obj.hasBeenSegmented();
	}

	@Override
	protected void setObject(TextContainer obj, IPersistenceSession session) {
		for (PropertyBean prop : properties)
			obj.setProperty(prop.get(Property.class, session));
		
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
		
		obj.setHasBeenSegmentedFlag(segApplied);
	}

	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
		this.annotations = annotations;
	}

	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

//	public List<TextPartBean> getParts() {
//		return parts;
//	}
//
//	public void setParts(List<TextPartBean> parts) {
//		this.parts = parts;
//	}

	public List<FactoryBean> getParts() {
		return parts;
	}

	public void setParts(List<FactoryBean> parts) {
		this.parts = parts;
	}

	public boolean isSegApplied() {
		return segApplied;
	}

	public void setSegApplied(boolean segApplied) {
		this.segApplied = segApplied;
	}
}
