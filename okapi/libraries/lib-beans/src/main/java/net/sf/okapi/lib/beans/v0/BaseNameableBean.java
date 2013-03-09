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

import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.lib.beans.v0.FactoryBean;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class BaseNameableBean implements IPersistenceBean{

	private String id;
	private FactoryBean skeleton = new FactoryBean();
	private String name;
	private String type;
	private String mimeType;
	private boolean isTranslatable;
	private boolean preserveWS;
	private List<PropertyBean> properties = new ArrayList<PropertyBean>();
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private List<PropertyBean> sourceProperties = new ArrayList<PropertyBean>();
	
	@Override
	public <T> T get(Class<T> classRef) {
		BaseNameable bn = new BaseNameable();
		
		bn.setId(id);
		bn.setSkeleton(skeleton.get(ISkeleton.class));
		bn.setName(name);
		bn.setType(type);
		bn.setMimeType(mimeType);
		bn.setIsTranslatable(isTranslatable);
		bn.setPreserveWhitespaces(preserveWS);
		
		for (PropertyBean prop : properties)
			bn.setProperty(prop.get(Property.class));
		
		for (FactoryBean annotationBean : annotations)
			bn.setAnnotation(annotationBean.get(IAnnotation.class));
		
		for (PropertyBean prop : sourceProperties)
			bn.setSourceProperty(prop.get(Property.class));
		
		return classRef.cast(bn);
	}

	@Override
	public void init(IPersistenceSession session) {
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof BaseNameable) {
			BaseNameable bn = (BaseNameable) obj;
			
			id = bn.getId();
			skeleton.set(bn.getSkeleton());
			name = bn.getName();
			type = bn.getType();
			mimeType = bn.getMimeType();
			isTranslatable = bn.isTranslatable();
			preserveWS = bn.preserveWhitespaces();
			
			for (String propName : bn.getPropertyNames()) {
				PropertyBean propBean = new PropertyBean();
				propBean.set(bn.getProperty(propName));
				properties.add(propBean);
			}
			
			// TODO BaseNamable.getAnnotations()
			//annotations.set(bn.getAnnotations());
			
			for (String propName : bn.getSourcePropertyNames()) {
				PropertyBean propBean = new PropertyBean();
				propBean.set(bn.getSourceProperty(propName));
				sourceProperties.add(propBean);
			}
		}		
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isTranslatable() {
		return isTranslatable;
	}

	public void setTranslatable(boolean isTranslatable) {
		this.isTranslatable = isTranslatable;
	}

	public boolean isPreserveWS() {
		return preserveWS;
	}

	public void setPreserveWS(boolean preserveWS) {
		this.preserveWS = preserveWS;
	}

	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public List<PropertyBean> getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(List<PropertyBean> sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

	public FactoryBean getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(FactoryBean skeleton) {
		this.skeleton = skeleton;
	}

}
