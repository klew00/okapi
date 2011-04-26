/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class AltTranslationsAnnotationBean extends PersistenceBean<AltTranslationsAnnotation> {

	private List<AltTranslationBean> list = new LinkedList<AltTranslationBean>();
	
	@Override
	protected AltTranslationsAnnotation createObject(IPersistenceSession session) {
		return new AltTranslationsAnnotation();
	}

	@Override
	protected void fromObject(AltTranslationsAnnotation obj,
			IPersistenceSession session) {
		for (AltTranslation annot : obj) {
			AltTranslationBean bean = new AltTranslationBean();
			list.add(bean);
			bean.set(annot, session);
		}		
	}

	@Override
	protected void setObject(AltTranslationsAnnotation obj,
			IPersistenceSession session) {
		for (AltTranslationBean bean : list) {
			AltTranslation annot = bean.get(AltTranslation.class, session);
			
			ITextUnit tunit = annot.getEntry();
			TextFragment src = null; 
			TextFragment trg = null;
			
			if (tunit != null) {
				src = tunit.getSource().getSegments().getFirstContent(); 
				trg = tunit.getTarget(annot.getTargetLocale()).getSegments().getFirstContent();
			}			
			obj.add(annot.getSourceLocale(), annot.getTargetLocale(), null,	src, trg, 
					annot.getType(), annot.getScore(), annot.getOrigin());
		}		
	}

	public void setList(List<AltTranslationBean> list) {
		this.list = list;
	}

	public List<AltTranslationBean> getList() {
		return list;
	}

}
