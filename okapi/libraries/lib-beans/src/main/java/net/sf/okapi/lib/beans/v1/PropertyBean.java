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

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class PropertyBean extends PersistenceBean<Property> {

	private String name;
	private String value;
	private boolean isReadOnly;
	private AnnotationsBean annotations = new AnnotationsBean();

	@Override
	protected Property createObject(IPersistenceSession session) {
		return new Property(name, value, isReadOnly);
	}

	@Override
	protected void fromObject(Property obj, IPersistenceSession session) {
			name = obj.getName();
			value = obj.getValue();
			isReadOnly = obj.isReadOnly();
			
			annotations.set(obj.getAnnotations(), session);
	}

	@Override
	protected void setObject(Property obj, IPersistenceSession session) {
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
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
