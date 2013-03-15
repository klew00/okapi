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

package net.sf.okapi.lib.beans.v1;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class DocumentBean extends PersistenceBean<Document> {

	private AnnotationsBean annotations = new AnnotationsBean();
	private String id;
	private List<FactoryBean> documentResources = new ArrayList<FactoryBean>();

	@Override
	protected Document createObject(IPersistenceSession session) {
		return new Document();
	}

	@Override
	protected void fromObject(Document obj, IPersistenceSession session) {
		annotations.set(obj.getAnnotations(), session);
		
		id = obj.getId();
		
		for (IResource res : obj) {
			FactoryBean resBean = new FactoryBean();
			resBean.set(res, session);
			documentResources.add(resBean);
		}						
	}

	@Override
	protected void setObject(Document obj, IPersistenceSession session) {
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
		
		obj.setId(id);
		
		for (FactoryBean docPropBean : documentResources)
			obj.addResource(docPropBean.get(IResource.class, session));
	}
	
	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
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
