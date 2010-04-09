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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;

public class DocumentBean implements IPersistenceBean {

	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	private String id;
	private List<FactoryBean> documentResources = new ArrayList<FactoryBean>();
	
	@Override
	public <T> T get(T obj) {		
		if (obj instanceof Document) {
			Document doc = (Document) obj;
			
			for (FactoryBean annotationBean : annotations)
				doc.setAnnotation(annotationBean.get(IAnnotation.class));
			
			doc.setId(id);
			
			for (FactoryBean docPropBean : documentResources)
				doc.addResource(docPropBean.get(IResource.class));
		}
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(get(new Document()));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Document) {
			Document doc = (Document) obj;
			
			for (IAnnotation annotation : doc.getAnnotations()) {
				FactoryBean annotationBean = new FactoryBean();
				annotations.add(annotationBean);
				annotationBean.set(annotation);
			}
			
			id = doc.getId();
			
			for (IResource res : doc) {
				FactoryBean resBean = new FactoryBean();
				resBean.set(res);
				documentResources.add(resBean);
			}						
		}
		return this;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FactoryBean> getDocumentResources() {
		return documentResources;
	}

	public void setDocumentResources(List<FactoryBean> documentResources) {
		this.documentResources = documentResources;
	}
}
