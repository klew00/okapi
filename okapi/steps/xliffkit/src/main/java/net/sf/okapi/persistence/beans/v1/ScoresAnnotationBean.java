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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.ScoreInfo;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.persistence.IPersistenceSession;
import net.sf.okapi.persistence.PersistenceBean;

public class ScoresAnnotationBean extends PersistenceBean<ScoresAnnotation> {

	private List<ScoreInfoBean> list = new ArrayList<ScoreInfoBean>();
	
	@Override
	protected ScoresAnnotation createObject(IPersistenceSession session) {
		return new ScoresAnnotation();
	}

	@Override
	protected void fromObject(ScoresAnnotation obj, IPersistenceSession session) {
		for (ScoreInfo scoreInfo : obj.getList()) {
			ScoreInfoBean bean = new ScoreInfoBean();
			list.add(bean);
			bean.set(scoreInfo, session);
		}
	}

	@Override
	protected void setObject(ScoresAnnotation obj, IPersistenceSession session) {
		for (ScoreInfoBean bean : list)
			obj.getList().add(bean.get(ScoreInfo.class, session));
	}

	public void setList(List<ScoreInfoBean> list) {
		this.list = list;
	}

	public List<ScoreInfoBean> getList() {
		return list;
	}

}
