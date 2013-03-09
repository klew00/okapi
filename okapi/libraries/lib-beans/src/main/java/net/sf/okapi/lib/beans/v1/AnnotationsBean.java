/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class AnnotationsBean extends PersistenceBean<Iterable<IAnnotation>> {

	private List<FactoryBean> items = new ArrayList<FactoryBean>();
	
	@Override
	protected Iterable<IAnnotation> createObject(IPersistenceSession session) {
		return new Annotations();
	}

	@Override
	protected void fromObject(Iterable<IAnnotation> obj, IPersistenceSession session) {
		for (IAnnotation annotation : obj) {
			FactoryBean annotationBean = new FactoryBean();
			items.add(annotationBean);
			annotationBean.set(annotation, session);
		}
	}

	@Override
	protected void setObject(Iterable<IAnnotation> obj, IPersistenceSession session) {
		if (obj instanceof Annotations) { // Otherwise a read-only collection
			Annotations annots = (Annotations) obj; 
			for (FactoryBean annotationBean : items)
				annots.set(annotationBean.get(IAnnotation.class, session));
		}		
	}

	public List<FactoryBean> getItems() {
		return items;
	}

	public void setItems(List<FactoryBean> items) {
		this.items = items;
	}
}
