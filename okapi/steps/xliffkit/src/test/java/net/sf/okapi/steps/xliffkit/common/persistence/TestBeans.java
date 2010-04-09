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

package net.sf.okapi.steps.xliffkit.common.persistence;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.BaseReferenceable;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.BaseNameableBean;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.BaseReferenceableBean;

import org.junit.Test;


public class TestBeans {

	@Test
	public void test1() {
		BaseNameableBean bean = new BaseNameableBean();
		BaseNameable bn = new BaseNameable();
		bn.setId("the id");
		bn.setName("the name");
		bn.setType("the type");
		bean.set(bn);
		
		BaseNameable bn2 = bean.get(new BaseNameable());
		assertEquals("the id", bn2.getId());
		assertEquals("the name", bn2.getName());
		assertEquals("the type", bn2.getType());
		
		BaseReferenceableBean bean2 = new BaseReferenceableBean();		
		BaseReferenceable br = new BaseReferenceable();
		br.setId("the id");
		br.setName("the name");
		br.setType("the type");
		bean2.set(bn);
		
//		JSONPersistenceSession session = new JSONPersistenceSession(BaseReferenceableBean.class);
//		session.start((InputStream) null);
		BaseReferenceable br2 = bean2.get(new BaseReferenceable());

		assertEquals("the id", br2.getId());
		assertEquals("the name", br2.getName());
		assertEquals("the type", br2.getType());		
	}
}
