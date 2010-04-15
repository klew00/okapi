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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class EndingBean extends PersistenceBean {

	private String id;
	private FactoryBean skeleton = new FactoryBean(getSession());
	private List<FactoryBean> annotations = new ArrayList<FactoryBean>();
	
	public EndingBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		if (obj instanceof Ending) {
			Ending en = (Ending) obj;
		
			en.setId(id);
			en.setSkeleton(skeleton.get(ISkeleton.class));
			
			for (FactoryBean annotationBean : annotations)
				en.setAnnotation(annotationBean.get(IAnnotation.class));
		}		
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {		
		return classRef.cast(get(new Ending(id)));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Ending) {
			Ending en = (Ending) obj;
			
			id = en.getId();
			skeleton.set(en.getSkeleton());
			
			for (IAnnotation annotation : en.getAnnotations()) {
				FactoryBean annotationBean = new FactoryBean(getSession());
				annotations.add(annotationBean);
				annotationBean.set(annotation);
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

	public FactoryBean getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(FactoryBean skeleton) {
		this.skeleton = skeleton;
	}

	public List<FactoryBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<FactoryBean> annotations) {
		this.annotations = annotations;
	}
}
