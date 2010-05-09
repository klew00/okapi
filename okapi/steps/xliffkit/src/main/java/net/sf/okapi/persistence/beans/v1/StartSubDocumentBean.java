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

package net.sf.okapi.persistence.beans.v1;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.persistence.IPersistenceSession;
import net.sf.okapi.persistence.beans.FactoryBean;

public class StartSubDocumentBean extends BaseNameableBean {

	private String parentId;
	private FactoryBean filterParams = new FactoryBean();

	@Override
	protected BaseNameable createObject(IPersistenceSession session) {
		return new StartSubDocument(parentId);
	}

	@Override
	protected void fromObject(BaseNameable obj, IPersistenceSession session) {
		super.fromObject(obj, session);
		
		if (obj instanceof StartSubDocument) {
			StartSubDocument ssd = (StartSubDocument) obj;
			
			parentId = ssd.getParentId();
			filterParams.set(ssd.getFilterParameters(), session);
		}
	}

	@Override
	protected void setObject(BaseNameable obj, IPersistenceSession session) {
		super.setObject(obj, session);
		
		if (obj instanceof StartSubDocument) {
			StartSubDocument ssd = (StartSubDocument) obj;
			
			ssd.setParentId(parentId);
			ssd.setFilterParameters(filterParams.get(IParameters.class, session));
		}
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public FactoryBean getFilterParams() {
		return filterParams;
	}

	public void setFilterParams(FactoryBean filterParams) {
		this.filterParams = filterParams;
	}
}