/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.tests;

import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.ParameterDescriptor;
import net.sf.okapi.common.ParametersDescription;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParametersTest {
	
	class TestClass {
		private String text;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testDefaults () {
		DummyParameters params = new DummyParameters();
		assertTrue(params.paramBool1);
		assertEquals(params.paramInt1, 123);
		assertEquals(params.paramStr1, "test");
		params.reset();
		assertTrue(params.paramBool1);
		assertEquals(params.paramInt1, 123);
		assertEquals(params.paramStr1, "test");
	}

	@Test
	public void testLoadParametersFromString () {
		String snippet = "#v1\nparamBool1.b=false\nparamInt1.i=456";
		DummyParameters params = new DummyParameters();
		params.fromString(snippet);
		assertFalse(params.paramBool1);
		assertEquals(params.paramInt1, 456);
		assertEquals(params.paramStr1, "test"); // Default
	}

	@Test
	public void testWhitespaces () {
		String snippet = "#v1\nparamBool1.b  =  true  \nparamInt1.i  =  456 \nparamStr1  = AB  C  ";
		DummyParameters params = new DummyParameters();
		params.fromString(snippet);
		assertTrue(params.paramBool1);
		assertEquals(params.paramInt1, 456);
		assertEquals(params.paramStr1, " AB  C  "); // WS count
	}

	@Test
	public void testLoadParametersFromWindowsFile () throws URISyntaxException {
		DummyParameters params = new DummyParameters();
		URL url = ParametersTest.class.getResource("/ParamTest01.txt");
		params.load(url.toURI(), false);
		assertFalse(params.paramBool1);
		assertEquals(789, params.paramInt1);
		assertEquals(params.paramStr1, "TestOK");
	}

	@Test
	public void testParameterDescriptor () {
		TestClass ts = new TestClass();
		ParameterDescriptor pd = new ParameterDescriptor("text", String.class,
			ts, "displayName", "shortDescription");
		assertEquals("displayName", pd.getDisplayName());
		assertEquals("shortDescription", pd.getShortDescription());
		assertEquals(String.class, pd.getType());
		assertEquals("text", pd.getName());
		assertEquals(ts, pd.getParent());
		assertNotNull(pd.getReadMethod());
		assertNotNull(pd.getWriteMethod());
	}

	@Test
	public void testParametersDescription () {
		TestClass ts = new TestClass();
		ParametersDescription desc = new ParametersDescription(ts);
		desc.add("text", String.class, "displayName", "shortDescription");
		ParameterDescriptor pd = desc.get("text");
		assertEquals(1, desc.getDescriptors().size());
		assertEquals(pd, desc.getDescriptors().get("text"));
	}

}
