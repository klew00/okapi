/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.BaseReferenceable;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.lib.persistence.IPersistenceBean;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;

import org.junit.Test;


public class TestBeans {

	@Test
	public void test1() {
		OkapiJsonSession session = new OkapiJsonSession();
		IPersistenceBean<BaseNameable> bean = session.createBean(BaseNameable.class);
		BaseNameable bn = new BaseNameable();
		bn.setId("the id");
		bn.setName("the name");
		bn.setType("the type");
		bean.set(bn, session);
		
		BaseNameable bn2 = bean.get(BaseNameable.class, session);
		assertEquals("the id", bn2.getId());
		assertEquals("the name", bn2.getName());
		assertEquals("the type", bn2.getType());
		
		IPersistenceBean<BaseReferenceable> bean2 = session.createBean(BaseReferenceable.class);
		BaseReferenceable br = new BaseReferenceable();
		br.setId("the id");
		br.setName("the name");
		br.setType("the type");
		bean2.set(br, session);
		
//		JSONPersistenceSession session = new JSONPersistenceSession(BaseReferenceableBean.class);
//		session.start((InputStream) null);
		BaseReferenceable br2 = bean2.get(BaseReferenceable.class, session);

		assertEquals("the id", br2.getId());
		assertEquals("the name", br2.getName());
		assertEquals("the type", br2.getType());		
	}
	
	// DEBUG @Test
	public void testObjectStream() throws FileNotFoundException, IOException {		
		File tempF = File.createTempFile("~temp", null);
		tempF.deleteOnExit();
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tempF));
		
		File outF = File.createTempFile("~temp", null);
		outF.deleteOnExit();
		OutputStream outStream = new FileOutputStream(outF);
				
		ITextUnit tu1 = TextUnitUtil.buildTU("source-text1" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu1.setSkeleton(new GenericSkeleton());
		tu1.setTarget(LocaleId.FRENCH, new TextContainer("french-text1"));
		tu1.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text1"));
		
		OkapiJsonSession session = new OkapiJsonSession();		
		IPersistenceBean<ITextUnit> tuBean = session.createBean(ITextUnit.class);
		
		os.writeObject(tuBean);
		
		session.start(outStream);
		session.end();
	}
	

}
