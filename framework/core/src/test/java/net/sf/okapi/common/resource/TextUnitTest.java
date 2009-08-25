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

import net.sf.okapi.common.IResource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TextUnitTest {

    TextUnit tu1;
    private static final String FR = "fr";
    private TextContainer tc1;
    private static final String TU1 = "tu1";

    @Before
    public void setUp(){
        tu1 = new TextUnit(TU1);
        tc1 = new TextContainer("fr text");
    }

    @Test
    public void isEmptyTrue(){
        assertTrue("The TextUnit should be empty", tu1.isEmpty());
    }

    @Test
    public void isEmptyFlase(){
        tu1.setSource(tc1);
        assertFalse("The TextUnit should not be empty", tu1.isEmpty());
    }

    @Test
    public void toStringFromSource(){
        tu1.setSource(tc1);
        assertEquals("TextUnit.toString()",  "fr text", tu1.toString());
    }

	@Test
	public void testGetSetSource () {
		tu1.setSource(tc1);
		assertSame(tu1.getSource(), tc1);
	}

    @Test
    public void getTargetReturnsNullOnNoMatch(){
        assertNull("When there is no match a null should be returned", tu1.getTarget(FR));
    }

	@Test
	public void testGetSetTarget () {
		tu1.setTarget(FR, tc1);
		assertSame("The target should be TextContainer we just set", tc1, tu1.getTarget(FR));
	}

    @Test
    public void testHasTargetNo(){
        assertFalse("No target should exist", tu1.hasTarget(FR));
    }

    @Test
	public void testHasTargetYes () {
		tu1.setTarget(FR, tc1);
		assertTrue("TextUnit should now have a target", tu1.hasTarget(FR));
		assertEquals(tu1.hasTarget("FR"), false);
	}

    @Test
	public void testHasTargetCaseSensitive () {
		tu1.setTarget(FR, tc1);
		assertFalse("FR target is not set", tu1.hasTarget("FR"));
	}

    @Test
    public void testRemoveTarget(){
        tu1.setTarget(FR, tc1);
        tu1.removeTarget(FR);
        assertFalse("TextUnit should no longer have a target", tu1.hasTarget(FR));
    }

    @Test
    public void createTargetSourceContentAndTargetContentSame(){
        tu1.setSource(tc1);
        tu1.createTarget(FR, false, IResource.COPY_ALL);
        assertEquals("Target text vs Source Text", tu1.getSource().toString(), tu1.getTarget(FR).toString());
    }

    @Test
	public void createTargetDoesntAlreadyExist () {
		tu1.setSource(tc1);
		TextContainer tc2 = tu1.createTarget(FR, false, IResource.COPY_ALL);
		assertSame("Target should be the same as returned from createTarget", tc2, tu1.getTarget(FR));
		assertNotSame("Target should have been cloned", tu1.getTarget(FR), tu1.getSource());
    }

    @Test
    public void createTargetAlreadyExistsDontOverwriteExisting () {
		// Do not override existing target
		tu1.setSource(tc1);
		TextContainer tc2 = new TextContainer("unique fr text");
		tu1.setTarget(FR, tc2);
		tu1.createTarget(FR, false, IResource.COPY_ALL);
		assertSame("Target should not have been modified", tc2, tu1.getTarget(FR));
    }

    @Test
    public void createTargetAlreadyExistsOverwriteExisting () {
        tu1.setSource(tc1);
        TextContainer tc2 = new TextContainer("unique fr text");
        tu1.setTarget(FR, tc2);
        tu1.createTarget(FR, true, IResource.COPY_ALL);
        assertNotSame("Target should not have been modified", tc2, tu1.getTarget(FR));
	}

	@Test
	public void getSetId () {
		assertEquals(tu1.getId(), TU1);
		tu1.setId("id2");
		assertEquals(tu1.getId(), "id2");
	}
	
	@Test
	public void getSetMimeType () {
		assertNull(tu1.getMimeType());
		tu1.setMimeType("test");
		assertEquals(tu1.getMimeType(), "test");
	}
	
	@Test
	public void propertiesInitialization() {
        //TODO: move properties initialization to constructor and create appropriate test
		assertEquals("Should be empty", 0, tu1.getPropertyNames().size());
    }

    @Test
    public void getPropertyReturnsDoesntExist() {
		assertNull("returns null when no property exists", tu1.getProperty("NAME"));
    }

    @Test
    public void getSetProperty() {
		Property p1 = new Property("name", "value", true);
		tu1.setProperty(p1);
		assertSame("should return the same property", p1, tu1.getProperty("name"));
	}

	@Test
	public void sourcePropertiesInitialization () {
		//TODO: move properties initialization to constructor and create appropriate test
        assertEquals("Should be empty", 0, tu1.getSourcePropertyNames().size());
    }

    @Test
    public void getSourcePropertyDoesntExist() {
		assertNull("returns null when no property exists", tu1.getSourceProperty("NAME"));
    }

    @Test
    public void getSetSourcePropertyFound() {
		Property p1 = new Property("name", "value", true);
		tu1.setSourceProperty(p1);
		assertSame("Should be the same object", p1, tu1.getSourceProperty("name"));
    }

	@Test
	public void targetPropertiesInitialization() {
		//TODO: move properties initialization to constructor maybe and create appropriate test 
		assertEquals(tu1.getTargetPropertyNames(FR).size(), 0);
    }

    @Test
    public void getTargetPropertyNotFound() {
		tu1.setTarget(FR, tc1);
        assertNull("Target shoudln't be found", tu1.getTargetProperty(FR, "NAME"));
    }

    @Test
    public void getSetTargetProperty() {
        tu1.setTarget(FR, tc1);
		Property p1 = new Property("name", "value", true);
		tu1.setTargetProperty(FR, p1);
        assertSame("Properties should be the same", p1, tu1.getTargetProperty(FR, "name"));
	}
	
	@Test
	public void testGetSetSourceContent () {
		TextFragment tf1 = new TextFragment("source text");
		tu1.setSourceContent(tf1);
		TextFragment tf2 = ((TextContainer)tu1.getSourceContent()).getContent();
		//TODO: the tc is actually not the same!, because it uses insert()
		// Do we need to 'fix' this? Probably.
		//assertSame(tf1, tf2);
        assertEquals("source content", tf1, tf2);
    }

	@Test
	public void testGetSetTargetContent () {
		TextFragment tf1 = new TextFragment("fr text");
		tu1.setTargetContent(FR, tf1);
		TextFragment tf2 = tu1.getTargetContent(FR);
		//TODO: the tc is actually not the same!, because it uses insert()
		// Do we need to 'fix' this? Probably.
		//assertSame(tf1, tf2);
        assertEquals("target content", tf1, tf2);
	}

}
