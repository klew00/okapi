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

package net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.FactoryBean;

public class PropertyBean extends PersistenceBean<Property> {

	private String name;
	private String value;
	private boolean isReadOnly;
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();

	@Override
	protected Property createObject(IPersistenceSession session) {
		return new Property(name, value, isReadOnly);
	}

	@Override
	protected void fromObject(Property obj, IPersistenceSession session) {
			name = obj.getName();
			value = obj.getValue();
			isReadOnly = obj.isReadOnly();
			
			for (IAnnotation annotation : obj.getAnnotations()) {
				FactoryBean annotationBean = new FactoryBean();
				annotations.add(annotationBean);
				annotationBean.set(annotation, session);
			}
	}

	@Override
	protected void setObject(Property obj, IPersistenceSession session) {
		for (FactoryBean annotationBean : annotations)
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public String getName() {
		return name;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
}
