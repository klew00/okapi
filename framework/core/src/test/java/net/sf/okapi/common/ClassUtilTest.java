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

package net.sf.okapi.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassUtilTest {

	@Test
	public void testGetPackageName() {
		
		assertEquals("", ClassUtil.getPackageName(null));
		assertEquals("net.sf.okapi.common", ClassUtil.getPackageName(this.getClass()));
		assertEquals("java.lang", ClassUtil.getPackageName((new String()).getClass()));
		// TODO test on a class w/o package info
	}
	
	@Test
	public void testExtractPackageName() {
		
		assertEquals("", ClassUtil.extractPackageName(null));
		assertEquals("", ClassUtil.extractPackageName(""));
		assertEquals("", ClassUtil.extractPackageName("aaa/bbb/ccc"));
		assertEquals("aaa.bbb", ClassUtil.extractPackageName("aaa.bbb.ccc"));
		assertEquals("net.sf.okapi.common", ClassUtil.extractPackageName("net.sf.okapi.common.ClassUtil"));
	}
	
	@Test
	public void testQualifyName() {
		
		assertEquals("", ClassUtil.qualifyName("", null));
		assertEquals("", ClassUtil.qualifyName("", ""));
		assertEquals("", ClassUtil.qualifyName("package", ""));
		assertEquals("", ClassUtil.qualifyName("", "class"));
		assertEquals("package.class", ClassUtil.qualifyName("package", "class"));
		assertEquals("package.class", ClassUtil.qualifyName("package.", "class"));
		assertEquals("package.class", ClassUtil.qualifyName("package_class", "package.class"));
		assertEquals(".class", ClassUtil.qualifyName("package.", ".class"));		
		assertEquals("java.lang.Integer", ClassUtil.qualifyName((new String()).getClass(), "Integer"));
		
		assertEquals("java.lang.Integer", ClassUtil.qualifyName(ClassUtil.extractPackageName(
				(new String()).getClass().getName()), "Integer"));
		
		assertEquals("net.sf.okapi.common.UtilTest", ClassUtil.qualifyName(ClassUtil.extractPackageName(
				this.getClass().getName()), "UtilTest"));
		
		assertEquals("net.sf.okapi.common.UtilTest", ClassUtil.qualifyName(this, "UtilTest"));
	}
}
