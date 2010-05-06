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

package net.sf.okapi.persistence.beans.v1;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.AltTransAnnotation;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.persistence.IPersistenceSession;
import net.sf.okapi.persistence.PersistenceBean;

public class AltTransAnnotationBean extends PersistenceBean<AltTransAnnotation> {
	
	private List<AltTransBean> list = new LinkedList<AltTransBean>();
	
	@Override
	protected AltTransAnnotation createObject(IPersistenceSession session) {
		return new AltTransAnnotation();
	}

	@Override
	protected void fromObject(AltTransAnnotation obj,
			IPersistenceSession session) {
		obj.startIteration();
		while (obj.moveToNext()) {
			AltTransBean bean = new AltTransBean();
			list.add(bean);
			
			LocaleId srcLang = obj.getSourceLanguage();
			LocaleId trgLang = obj.getTargetLanguage();
			
			if (srcLang != null)
				bean.setSrcLang(srcLang.toString());
			else
				bean.setSrcLang(null);
			
			if (trgLang != null)
				bean.setTrgLang(trgLang.toString());
			else
				bean.setTrgLang(null);
			
			TextUnitBean tub = new TextUnitBean();
			tub.set(obj.getEntry(), session);
			bean.setTu(tub);
		}
	}

	@Override
	protected void setObject(AltTransAnnotation obj, IPersistenceSession session) {
		for (AltTransBean bean : list) {
			TextUnitBean tub = bean.getTu();
			if (tub == null) continue;
			
			TextUnit tu = tub.get(TextUnit.class, session);
			if (tu == null) continue;
			
			LocaleId srcLang = null;
			LocaleId trgLang = null;
			
			String srcl = bean.getSrcLang();
			String trgl = bean.getTrgLang();
			
			if (!Util.isEmpty(srcl))
				srcLang = new LocaleId(srcl);
			
			if (!Util.isEmpty(trgl))
				trgLang = new LocaleId(trgl);
			
			obj.addNew(srcLang, tu.getSource());			 
			obj.setTarget(trgLang, tu.getTarget(trgLang));
		}		
	}

	public void setList(List<AltTransBean> list) {
		this.list = list;
	}

	public List<AltTransBean> getList() {
		return list;
	}

}
