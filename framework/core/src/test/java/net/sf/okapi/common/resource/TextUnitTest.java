/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.resource;

import java.util.Set;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Test;
import static org.junit.Assert.*;

public class TextUnitTest {

	@Test
	public void testGetSetSource () {
		TextUnit tu1 = new TextUnit("tu1");
		assertEquals(tu1.isEmpty(), true);
		TextContainer tc1 = new TextContainer("source text");
		tu1.setSource(tc1);
		assertEquals(tu1.isEmpty(), false);
		assertEquals(tu1.toString(), "source text");
		tc1.append("+");
		assertEquals(tu1.toString(), "source text+");
		assertEquals(tu1.getSource().toString(), "source text+");
		assertSame(tu1.getSource(), tc1);
	}
	
	@Test
	public void testGetSetTarget () {
		TextUnit tu1 = new TextUnit("tu1");
		assertNull(tu1.getTarget("fr"));
		TextContainer tc1 = new TextContainer("fr text");
		tu1.setTarget("fr", tc1);
		assertNotNull(tu1.getTarget("fr"));
		assertEquals(tu1.getTarget("fr").toString(), "fr text");
		tc1.append("+");
		assertEquals(tu1.getTarget("fr").toString(), "fr text+");
	}
	
	@Test
	public void testHasTarget () {
		TextUnit tu1 = new TextUnit("tu1");
		assertEquals(tu1.hasTarget("fr"), false);
		TextContainer tc1 = new TextContainer("fr text");
		tu1.setTarget("fr", tc1);
		assertEquals(tu1.hasTarget("fr"), true);
		assertEquals(tu1.hasTarget("FR"), false);
		tu1.removeTarget("fr");
		assertEquals(tu1.hasTarget("fr"), false);
	}

	@Test
	public void testCreateTarget () {
		// Create from source
		TextUnit tu1 = new TextUnit("tu1");
		TextContainer tc1 = new TextContainer("source text");
		tu1.setSource(tc1);
		tu1.createTarget("fr", false, IResource.COPY_ALL);
		assertEquals(tu1.hasTarget("fr"), true);
		assertEquals(tu1.getTarget("fr").toString(), "source text");
		assertNotSame(tu1.getTarget("fr"), tu1.getSource());
		assertEquals(tu1.getTarget("fr").toString(), tu1.getSource().toString());
		// Do not override existing target
		tu1 = new TextUnit("tu1");
		tc1 = new TextContainer("source text");
		tu1.setSource(tc1);
		TextContainer tc2 = new TextContainer("fr text");
		tu1.setTarget("fr", tc2);
		assertEquals(tu1.getSource().toString(), "source text");
		assertEquals(tu1.getTarget("fr").toString(), "fr text");
		tu1.createTarget("fr", false, IResource.COPY_ALL);
		assertEquals(tu1.getTarget("fr").toString(), "fr text");
		// Override existing target
		tu1.createTarget("fr", true, IResource.COPY_ALL);
		assertEquals(tu1.getTarget("fr").toString(), "source text");
	}

	@Test
	public void testGetSetId () {
		TextUnit tu1 = new TextUnit("tu1");
		assertEquals(tu1.getId(), "tu1");
		tu1.setId("id2");
		assertEquals(tu1.getId(), "id2");
		tu1.setId(null);
		assertNull(tu1.getId());
	}
	
	@Test
	public void testGetSetMimeType () {
		TextUnit tu1 = new TextUnit("tu1");
		assertNull(tu1.getMimeType());
		tu1.setMimeType("test");
		assertEquals(tu1.getMimeType(), "test");
		tu1.setMimeType(null);
		assertNull(tu1.getMimeType());
	}
	
	@Test
	public void testGetSetProperties () {
		TextUnit tu1 = new TextUnit("tu1");
		Set<String> list = tu1.getPropertyNames();
		assertNotNull(list);
		assertEquals(list.size(), 0);
		Property p1 = new Property("name", "value", true);
		tu1.setProperty(p1);
		Property p2 = tu1.getProperty("NAME");
		assertNull(p2);
		p2 = tu1.getProperty("name");
		assertNotNull(p2);
		assertEquals(p2.getValue(), "value");
		assertTrue(p2.isReadOnly());
		p2.setValue("newValue");
		assertEquals(tu1.getProperty("name").getValue(), "newValue");
		assertSame(tu1.getProperty("name"), p1);
	}

	@Test
	public void testGetSetSourceProperties () {
		TextUnit tu1 = new TextUnit("tu1");
		Set<String> list = tu1.getSourcePropertyNames();
		assertNotNull(list);
		assertEquals(list.size(), 0);
		Property p1 = new Property("name", "value", true);
		tu1.setSourceProperty(p1);
		Property p2 = tu1.getSourceProperty("NAME");
		assertNull(p2);
		p2 = tu1.getSourceProperty("name");
		assertNotNull(p2);
		assertEquals(p2.getValue(), "value");
		assertTrue(p2.isReadOnly());
		p2.setValue("newValue");
		assertEquals(tu1.getSourceProperty("name").getValue(), "newValue");
		assertSame(tu1.getSourceProperty("name"), p1);
		// Test copy by cloning
		TextUnit tu2 = new TextUnit("tu2");
		tu2.setSource(tu1.getSource().clone());
		Property p3 = tu2.getSourceProperty("name");
		assertEquals(p3.getValue(), p2.getValue());
		assertNotSame(p3, p2);
	}

	@Test
	public void testGetSetTargetProperties () {
		TextUnit tu1 = new TextUnit("tu1");
		Set<String> list = tu1.getTargetPropertyNames("fr");
		assertNotNull(list);
		assertEquals(list.size(), 0);
		tu1.setTarget("fr", new TextContainer("fr text"));
		list = tu1.getTargetPropertyNames("fr");
		assertNotNull(list);
		assertEquals(list.size(), 0);
		Property p1 = new Property("name", "value", true);
		tu1.setTargetProperty("fr", p1);
		Property p2 = tu1.getTargetProperty("fr", "NAME");
		assertNull(p2);
		p2 = tu1.getTargetProperty("fr", "name");
		assertNotNull(p2);
		assertEquals(p2.getValue(), "value");
		assertTrue(p2.isReadOnly());
		p2.setValue("newValue");
		assertEquals(tu1.getTargetProperty("fr", "name").getValue(), "newValue");
		assertSame(tu1.getTargetProperty("fr", "name"), p1);
		// Test copy by cloning
		TextUnit tu2 = new TextUnit("tu2");
		tu2.setTarget("fr", tu1.getTarget("fr").clone());
		Property p3 = tu2.getTargetProperty("fr", "name");
		assertEquals(p3.getValue(), p2.getValue());
		assertNotSame(p3, p2);
	}
	
	@Test
	public void testGetSetSourceContent () {
		TextUnit tu1 = new TextUnit("tu1");
		TextFragment tf1 = new TextFragment("source text");
		tu1.setSourceContent(tf1);
		TextFragment tf2 = tu1.getSourceContent();
		//TODO: the tc is actually not the same!, because it uses insert()
		// Do we need to 'fix' this? Probably.
		//assertSame(tf1, tf2);
		tf1 = new TextFragment("source text");
		tf2 = tu1.setSourceContent(tf1);
		//assertSame(tf1, tf2);
		assertEquals(tu1.getSourceContent().toString(), "source text");
		assertEquals(tu1.getSource().toString(), "source text");
		TextContainer tc1 = tu1.getSource();
		tc1.append("+");
		assertEquals(tu1.getSourceContent().toString(), "source text+");
		assertEquals(tu1.getSource().toString(), "source text+");
		tf2.append("$");
		assertEquals(tu1.getSource().toString(), "source text+$");
	}

	@Test
	public void testGetSetTargetContent () {
		TextUnit tu1 = new TextUnit("tu1");
		TextFragment tf1 = new TextFragment("fr text");
		tu1.setTargetContent("fr", tf1);
		TextFragment tf2 = tu1.getTargetContent("fr");
		//TODO: the tc is actually not the same!, because it uses insert()
		// Do we need to 'fix' this? Probably.
		//assertSame(tf1, tf2);
		tf1 = new TextFragment("fr text");
		tf2 = tu1.setTargetContent("fr", tf1);
		//assertSame(tf1, tf2);
		assertEquals(tu1.getTargetContent("fr").toString(), "fr text");
		assertEquals(tu1.getTarget("fr").toString(), "fr text");
		TextContainer tc1 = tu1.getTarget("fr");
		tc1.append("+");
		assertEquals(tu1.getTargetContent("fr").toString(), "fr text+");
		assertEquals(tu1.getTarget("fr").toString(), "fr text+");
		tf2.append("$");
		assertEquals(tu1.getTarget("fr").toString(), "fr text+$");
	}

}
