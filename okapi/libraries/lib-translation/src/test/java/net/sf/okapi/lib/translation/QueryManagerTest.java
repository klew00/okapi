/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.translation;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.IQuery;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryManagerTest {

	private QueryManager qm;
	private LocaleId locSrc = LocaleId.fromString("src");
	private LocaleId locTrg = LocaleId.fromString("trg");
	
	@Before
	public void setUp() {
		qm = new QueryManager();
	}

	@Test
	public void testLanguages () {
		qm.setLanguages(locSrc, locTrg);
		assertEquals(locSrc, qm.getSourceLanguage());
		assertEquals(locTrg, qm.getTargetLanguage());
	}

	@Test
	public void testResources () {
		DummyConnector conn = new DummyConnector();
		int resId = qm.addResource(conn, "ResNameTest");
		assertEquals("ResNameTest", qm.getName(resId));
		ResourceItem item = qm.getResource(resId);
		assertNotNull(item);
		IQuery q = qm.getInterface(resId);
		assertNotNull(q);
	}

}
