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

package net.sf.okapi.common;

import static org.junit.Assert.assertEquals;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.LCIDUtil;

import org.junit.Test;

public class LCIDUtilTest {

	@Test
	public void testLCID() {
		assertEquals(0x0419, LCIDUtil.getLCID("ru-ru"));
		assertEquals(0x0419, LCIDUtil.getLCID("ru-RU"));
		assertEquals(0x0419, LCIDUtil.getLCID("ru_RU"));
		assertEquals(0x0409, LCIDUtil.getLCID("en-us"));
		assertEquals(0x0448, LCIDUtil.getLCID(new LocaleId("or", "in")));
		assertEquals("or-IN", LCIDUtil.getTag(new LocaleId("or", "in")));
	}
}
