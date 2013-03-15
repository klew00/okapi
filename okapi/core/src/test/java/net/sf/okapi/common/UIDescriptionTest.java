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

import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UIDescriptionTest {
	
	private TestClass ts;
	private ParametersDescription desc;

	class TestClass {
		private String text;
		private boolean flag;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		ts = new TestClass();
		desc = new ParametersDescription(ts);
		desc.add("text", "displayName", "shortDescription");
		desc.add("flag", "Flag", "A flag");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCheckboxPart () {
		// Using a boolean
		CheckboxPart part = new CheckboxPart(desc.get("flag"));
		assertEquals("flag", part.getName());
		assertEquals("Flag", part.getDisplayName());
		assertEquals("A flag", part.getShortDescription());
		// Using a String
		part = new CheckboxPart(desc.get("text"));
		assertEquals("text", part.getName());
		assertEquals("displayName", part.getDisplayName());
		assertEquals("shortDescription", part.getShortDescription());
	}
	
	@Test
	public void testTextInputPart () {
		TextInputPart part = new TextInputPart(desc.get("text"));
		part.setPassword(true);
		assertEquals("text", part.getName());
		assertEquals("displayName", part.getDisplayName());
		assertEquals("shortDescription", part.getShortDescription());
		assertEquals(false, part.isAllowEmpty());
		assertEquals(true, part.isPassword());
	}

	@Test
	public void testPathInputPart () {
		PathInputPart part = new PathInputPart(desc.get("text"), "title", true);
		assertEquals("text", part.getName());
		assertEquals("displayName", part.getDisplayName());
		assertEquals("shortDescription", part.getShortDescription());
		assertEquals("title", part.getBrowseTitle());
		assertEquals(true, part.isForSaveAs());
	}

	@Test
	public void testListSelectionPart () {
		String[] items = {"selection1", "selection2"};
		ListSelectionPart part = new ListSelectionPart(desc.get("text"), items);
		assertEquals("text", part.getName());
		assertEquals("displayName", part.getDisplayName());
		assertEquals("shortDescription", part.getShortDescription());
		String[] choices = part.getChoicesValues();
		assertNotNull(choices);
		assertEquals(2, choices.length);
		assertEquals("selection2", choices[1]);
	}

}
