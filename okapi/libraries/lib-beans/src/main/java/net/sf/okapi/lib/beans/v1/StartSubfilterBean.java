/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

import net.sf.okapi.common.encoder.IEncoder;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class StartSubfilterBean extends StartGroupBean {

	private StartDocumentBean startDoc = new StartDocumentBean();
	private FactoryBean parentEncoder = new FactoryBean();
	
	@Override
	protected StartSubfilter createObject(IPersistenceSession session) {
		return new StartSubfilter(getId(), startDoc.get(StartDocument.class, session),
				parentEncoder.get(IEncoder.class, session));
	}

	@Override
	protected void setObject(BaseNameable obj, IPersistenceSession session) {
		super.setObject(obj, session);
	}

	@Override
	protected void fromObject(BaseNameable obj, IPersistenceSession session) {
		super.fromObject(obj, session);
		
		if (obj instanceof StartSubfilter) {
			StartSubfilter ssf = (StartSubfilter) obj;			
			startDoc.set(ssf.getStartDoc(), session);
		}
	}

	public StartDocumentBean getStartDoc() {
		return startDoc;
	}

	public void setStartDoc(StartDocumentBean startDoc) {
		this.startDoc = startDoc;
	}

	public FactoryBean getParentEncoder() {
		return parentEncoder;
	}

	public void setParentEncoder(FactoryBean parentEncoder) {
		this.parentEncoder = parentEncoder;
	}
}
