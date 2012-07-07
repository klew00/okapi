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
import static org.junit.Assert.fail;
import net.sf.okapi.common.LCIDUtil.LCIDDescr;

import org.junit.Test;

public class LCIDUtilTest {

	@Test
	public void testLCID() {
		assertEquals(0x0419, LCIDUtil.getLCID("ru-ru"));
		assertEquals(0x0419, LCIDUtil.getLCID("ru-RU"));
		assertEquals(0x0419, LCIDUtil.getLCID("ru_RU"));
		assertEquals(0x0409, LCIDUtil.getLCID("en-us"));
		assertEquals(0x044c, LCIDUtil.getLCID("ml-IN"));
		assertEquals("ml-in", LCIDUtil.getTag(0x044c));
		assertEquals(0x0007, LCIDUtil.getLCID("de"));
		assertEquals(0x0009, LCIDUtil.getLCID("en"));		
		assertEquals(0x000a, LCIDUtil.getLCID("es"));
		assertEquals(0x000c, LCIDUtil.getLCID("fr"));
		assertEquals(0x0448, LCIDUtil.getLCID(new LocaleId("or", "in")));
		assertEquals("or-in", LCIDUtil.getTag(new LocaleId("or", "in")));
	}
	
	@Test
	public void testLookups() {
//		for (LCIDDescr descr : LCIDUtil.getTagLookup().values()) {
//			if (!LCIDUtil.getLcidLookup().containsKey(descr.tag)) {
//				fail("LcidLookup has no entry for " + descr.tag);
//			}
//		}
		for (LCIDDescr descr : LCIDUtil.getTagLookup().values()) {
			int lcid = descr.lcid;
			String tag = descr.tag;
			if (!LCIDUtil.getLcidLookup().containsKey(tag)) {
				fail(String.format("LcidLookup has no entry for 0x%04x (%s)", lcid, descr.tag));
			}
		}
	}
	
//  DEBUG @Test
	public void listLcidLookup() {
		for (String tag : LCIDUtil.getLcidLookup().keySet()) {
			LCIDDescr descr = LCIDUtil.getLcidLookup().get(tag);
			System.out.println(tag + ":   " + getDescrStr(descr));
		}
	}
	
	private String getDescrStr(LCIDDescr descr) {
		return String.format("Lang: %20s Reg: %20s lcid: 0x%04x tag: %s",
				descr.language, descr.region, descr.lcid, descr.tag);		
	}
}
