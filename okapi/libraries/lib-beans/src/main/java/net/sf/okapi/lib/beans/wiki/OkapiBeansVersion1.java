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

package net.sf.okapi.lib.beans.wiki;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.lib.beans.v1.EventBean;
import net.sf.okapi.lib.beans.v1.PropertyBean;
import net.sf.okapi.lib.beans.v1.RawDocumentBean;
import net.sf.okapi.lib.beans.v1.TextUnitBean;
import net.sf.okapi.lib.persistence.BeanMapper;
import net.sf.okapi.lib.persistence.IVersionDriver;

public class OkapiBeansVersion1 implements IVersionDriver {

	public static final String VERSION = "OKAPI 1.0";
	
	@Override
	public String getVersionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerBeans(BeanMapper beanMapper) {
		beanMapper.registerBean(Event.class, EventBean.class);		
		beanMapper.registerBean(ITextUnit.class, TextUnitBean.class);
		beanMapper.registerBean(RawDocument.class, RawDocumentBean.class);
		beanMapper.registerBean(Property.class, PropertyBean.class);
	}
}
