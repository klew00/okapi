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

package net.sf.okapi.lib.beans.v0;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.lib.beans.v0.FactoryBean;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class PropertyBean implements IPersistenceBean {

	private String name;
	private String value;
	private boolean isReadOnly;
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	
	@Override
	public void init(IPersistenceSession session) {
	}

	@Override
	public <T> T get(Class<T> classRef) {
		Property prop = new Property(name, value, isReadOnly);
		
		for (FactoryBean annotationBean : annotations)
			prop.setAnnotation(annotationBean.get(IAnnotation.class));
		
		return classRef.cast(prop);
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Property) {
			Property prop = (Property) obj;
			name = prop.getName();
			value = prop.getValue();
			isReadOnly = prop.isReadOnly();
			
			// TODO Property.getAnnotations()
			//annotations.write(tu.ge)
		}
		return this;
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

}
