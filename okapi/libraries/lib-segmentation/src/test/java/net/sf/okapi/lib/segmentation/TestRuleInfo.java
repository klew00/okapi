/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestRuleInfo {

	private RuleInfo info;
	
	@Test
	public void testQuotedAreas() {
		info = new RuleInfo(
				"ds\\Qjk\\[h]sdk\\Ej[h[s]]d[sdjh ss]dk\\[jhs s\\]dkj");
			   //012 3456 7890123 456789012345678901234 5678901 2345
			   //0           1          2         3          4
						
		assertFalse(info.isQuotedArea(0));
		assertFalse(info.isQuotedArea(1));
		assertFalse(info.isQuotedArea(2));
		assertFalse(info.isQuotedArea(3));
		assertTrue(info.isQuotedArea(4));
		assertTrue(info.isQuotedArea(12));
		assertFalse(info.isQuotedArea(13));
		assertFalse(info.isQuotedArea(14));
	}
	
	@Test
	public void testSetAreas() {
		info = new RuleInfo(
				"ds\\Qjk\\[h]sdk\\Ej[h[s]]d[sdjh ss]dk\\[jhs [s\\]d]kj");
  			   //012 3456 7890123 456789012345678901234 5678901 234567
			   //0           1          2         3          4
		
		assertFalse(info.isSetArea(0));
		assertFalse(info.isSetArea(1));
		assertFalse(info.isSetArea(2));
		assertFalse(info.isSetArea(3));
		assertFalse(info.isSetArea(4));
		assertFalse(info.isSetArea(5));
		assertFalse(info.isSetArea(6));
		assertFalse(info.isSetArea(7));
		assertTrue(info.isSetArea(8));
		assertFalse(info.isSetArea(9));
		assertFalse(info.isSetArea(10));
		assertFalse(info.isSetArea(11));
		assertFalse(info.isSetArea(12));
		assertFalse(info.isSetArea(13));
		assertFalse(info.isSetArea(14));
		assertFalse(info.isSetArea(15));
		assertFalse(info.isSetArea(16));
		assertTrue(info.isSetArea(17));
		assertTrue(info.isSetArea(18));
		assertTrue(info.isSetArea(19));
		assertTrue(info.isSetArea(20)); // top level sets only
		assertFalse(info.isSetArea(21));
		assertFalse(info.isSetArea(22));
		assertFalse(info.isSetArea(23));
		assertTrue(info.isSetArea(24));
		assertTrue(info.isSetArea(30));
		assertFalse(info.isSetArea(31));
		assertFalse(info.isSetArea(35));
		assertFalse(info.isSetArea(36));
		assertFalse(info.isSetArea(37));
		assertTrue(info.isSetArea(41));
		assertTrue(info.isSetArea(42));
		assertTrue(info.isSetArea(43));
		assertTrue(info.isSetArea(44));
		assertFalse(info.isSetArea(45));
		assertFalse(info.isSetArea(46));
		assertFalse(info.isSetArea(47));
	}
	
	@Test
	public void testWbGroup() {
		String st = "abcdef";
		Matcher m = Pattern.compile("bc(.{0})d(.{0})e").matcher(st);
		if (m.find()) {
			assertEquals(3, m.start(1));
			assertEquals(4, m.start(2));
		}
		else fail();
	}
	
}
