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
============================================================================*/

package net.sf.okapi.common.resource;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TextUnit2Test {

    private static final LocaleId locFR = LocaleId.fromString("fr");
    private static final String TU1 = "tu1";
    private TextContainer tc1;
    ITextUnit tu1;

    @Before
    public void setUp(){
        tu1 = new TextUnit2(TU1);
        tc1 = new TextContainer("fr text");
    }

    @Test
    public void isEmptyTrue(){
        assertTrue("The TextUnit should be empty", tu1.isEmpty());
    }

    @Test
    public void isEmptyFalse(){
        tu1.setSource(tc1);
        assertFalse("The TextUnit should not be empty", tu1.isEmpty());
    }

    @Test
    public void toStringFromSource(){
        tu1.setSource(tc1);
        assertEquals("TextUnit.toString()",  "fr text", tu1.toString());
    }

	@Test
	public void getSetSource () {
		tu1.setSource(tc1);
		assertSame(tu1.getSource(), tc1);
	}

    @Test
    public void getTargetReturnsNewEmptyOnNoMatch(){
        assertNotNull("When there is no match a empty should be returned", tu1.getTarget_DIFF(locFR));
        assertEquals("", tu1.getTarget_DIFF(locFR).toString());
    }

	@Test
	public void getSetTarget () {
		tu1.setTarget(locFR, tc1);
		assertSame("The target should be TextContainer we just set", tc1, tu1.getTarget_DIFF(locFR));
	}

    @Test
    public void hasTargetNo(){
        assertFalse("No target should exist", tu1.hasTarget(locFR));
    }

    @Test
	public void hasTargetYes () {
		tu1.setTarget(locFR, tc1);
		assertTrue("TextUnit should now have a target", tu1.hasTarget(locFR));
	}

    @Test
	public void hasTargetCaseSensitive () {
		tu1.setTarget(locFR, tc1);
		// Language is now *not* case sensitive
		assertTrue(tu1.hasTarget(LocaleId.fromString("FR")));
		// Still: "fr" different from "fr-fr"
		assertTrue( ! tu1.hasTarget(LocaleId.fromString("fr-fr")));
	}

    @Test
    public void removeTarget() {
        tu1.setTarget(locFR, tc1);
        tu1.removeTarget(locFR);
        assertFalse("TextUnit should no longer have a target", tu1.hasTarget(locFR));
    }

    @Test
    public void createTargetCase1 () {
    	tu1 = createSegmentedTU();
        tu1.createTarget(locFR, false, IResource.COPY_ALL);
        assertEquals(tu1.getSource().toString(), tu1.getTarget_DIFF(locFR).toString());
        assertEquals(tu1.getSource().getSegments().count(), tu1.getTarget_DIFF(locFR).getSegments().count());
    }

    @Test
    public void createTargetCase2 () {
    	tu1 = createSegmentedTU();
        tu1.createTarget(locFR, false, IResource.COPY_SEGMENTS);
        assertEquals(tu1.getSource().getSegments().count(), tu1.getTarget_DIFF(locFR).getSegments().count());
        assertEquals(" a ", tu1.getTarget_DIFF(locFR).toString());
    }

    @Test
    public void createTargetCase3 () {
    	tu1 = createSegmentedTU();
        tu1.createTarget(locFR, false, IResource.COPY_CONTENT);
        assertEquals(1, tu1.getTarget_DIFF(locFR).getSegments().count());
        assertEquals("Part 1. a Part 2.", tu1.getTarget_DIFF(locFR).toString());
    }
    
    @Test
    public void loopThroughSegments () {
    	tu1 = createSegmentedTUAndTarget();
    	Segment trgSeg;
    	for ( Segment srcSeg : tu1.getSourceSegments() ) {
    		if ( srcSeg.id.equals("0") ) {
    			assertEquals("Part 1.", srcSeg.text.toString());
    			trgSeg = tu1.getTargetSegment(locFR, srcSeg.id, false);
    			assertEquals("Trg 1.", trgSeg.text.toString());
    		}
    		else {
    			assertEquals("Part 2.", srcSeg.text.toString());
    			trgSeg = tu1.getTargetSegment(locFR, srcSeg.id, false);
    			assertEquals("Trg 2.", trgSeg.text.toString());
    		}
    	}
    }

    @Test
    public void loopThroughSegmentsType2 () {
    	tu1 = createSegmentedTUAndTarget();
    	Segment trgSeg;
    	IAlignedSegments segs = tu1.getSegments();
    	for ( Segment srcSeg : segs ) {
    		if ( srcSeg.id.equals("0") ) {
    			assertEquals("Part 1.", srcSeg.text.toString());
    			trgSeg = segs.getCorrespondingTarget(srcSeg, locFR);
    			assertEquals("Trg 1.", trgSeg.text.toString());
    		}
    		else {
    			assertEquals("Part 2.", srcSeg.text.toString());
    			trgSeg = segs.getCorrespondingTarget(srcSeg, locFR);
    			assertEquals("Trg 2.", trgSeg.text.toString());
    		}
    	}
    }

    @Test
    public void createTargetSourceContentAndTargetContentSame(){
        tu1.setSource(tc1);
        tu1.createTarget(locFR, false, IResource.COPY_ALL);
        assertEquals("Target text vs Source Text", tu1.getSource().toString(), tu1.getTarget_DIFF(locFR).toString());
    }

    @Test
	public void createTargetDoesntAlreadyExist () {
		tu1.setSource(tc1);
		TextContainer tc2 = tu1.createTarget(locFR, false, IResource.COPY_ALL);
		assertSame("Target should be the same as returned from createTarget", tc2, tu1.getTarget_DIFF(locFR));
		assertNotSame("Target should have been cloned", tu1.getTarget_DIFF(locFR), tu1.getSource());
    }

    @Test
    public void createTargetAlreadyExistsDontOverwriteExisting () {
		// Do not override existing target
		tu1.setSource(tc1);
		TextContainer tc2 = new TextContainer("unique fr text");
		tu1.setTarget(locFR, tc2);
		tu1.createTarget(locFR, false, IResource.COPY_ALL);
		assertSame("Target should not have been modified", tc2, tu1.getTarget_DIFF(locFR));
    }

    @Test
    public void createTargetAlreadyExistsOverwriteExisting () {
        tu1.setSource(tc1);
        TextContainer tc2 = new TextContainer("unique fr text");
        tu1.setTarget(locFR, tc2);
        tu1.createTarget(locFR, true, IResource.COPY_ALL);
        assertNotSame("Target should not have been modified", tc2, tu1.getTarget_DIFF(locFR));
	}

    @Test
    public void createTargetEmptyOption () {
        tu1.setSource(tc1);
        tu1.createTarget(locFR, false, IResource.CREATE_EMPTY);
        assertTrue(tu1.hasTarget(locFR));
        assertEquals("Empty target created", "", tu1.getTarget_DIFF(locFR).toString());
	}
    
    @Test
    public void createTargetEmptyOptionOverwriteExisting () {
        tu1.setSource(tc1);
        TextContainer tc2 = new TextContainer("unique fr text");
        tu1.setTarget(locFR, tc2);
        tu1.createTarget(locFR, true, IResource.CREATE_EMPTY);
        assertTrue(tu1.hasTarget(locFR));
        assertEquals("Empty target created", "", tu1.getTarget_DIFF(locFR).toString());
	}
    
    @Test
    public void createTargetPropertiesOption () {
        tu1.setSource(tc1);
        tu1.getSource().setProperty(new Property("test", "value"));
        tu1.createTarget(locFR, false, IResource.COPY_PROPERTIES);
        assertTrue(tu1.hasTarget(locFR));
        assertEquals("Empty target created", "", tu1.getTarget_DIFF(locFR).toString());
        assertTrue(tu1.getTarget_DIFF(locFR).getProperty("test") != null);
	}
    
    @Test
    public void createTargetPropertiesOptionOverwriteExisting () {
        tu1.setSource(tc1);
        tu1.getSource().setProperty(new Property("test", "value"));
        TextContainer tc2 = new TextContainer("unique fr text");
        tu1.setTarget(locFR, tc2);
        tu1.createTarget(locFR, true, IResource.COPY_PROPERTIES);
        assertTrue(tu1.hasTarget(locFR));
        assertEquals("Empty target created", "", tu1.getTarget_DIFF(locFR).toString());
        assertTrue(tu1.getTarget_DIFF(locFR).getProperty("test") != null);
	}
    
    @Test
    public void createTargetPropertiesOptionNotOverwriteExisting () {
        tu1.setSource(tc1);
        tu1.getSource().setProperty(new Property("test", "value"));
        TextContainer tc2 = new TextContainer("unique fr text");
        tu1.setTarget(locFR, tc2);
        tu1.createTarget(locFR, false, IResource.COPY_PROPERTIES);
        assertTrue(tu1.hasTarget(locFR));
        assertEquals("unique fr text", tu1.getTarget_DIFF(locFR).toString());
        assertTrue(tu1.getTarget_DIFF(locFR).getProperty("test") == null);
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
		assertEquals(tu1.getTargetPropertyNames(locFR).size(), 0);
    }

    @Test
    public void getTargetPropertyNotFound() {
		tu1.setTarget(locFR, tc1);
        assertNull("Target shoudln't be found", tu1.getTargetProperty(locFR, "NAME"));
    }

    @Test
    public void getSetTargetProperty() {
        tu1.setTarget(locFR, tc1);
		Property p1 = new Property("name", "value", true);
		tu1.setTargetProperty(locFR, p1);
        assertSame("Properties should be the same", p1, tu1.getTargetProperty(locFR, "name"));
	}

    @Test
    public void getSegmentsTest () {
    	tu1.setSourceContent(new TextFragment("text"));
    	IAlignedSegments as = tu1.getSegments();
    	assertNotNull(as);
    }
    
    @Test
    public void removeSegmentsTest () {
    	ITextUnit tu = createSegmentedTU();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = as.getSource(0);
    	assertEquals("0", seg.id);
    	as.remove(seg);
    	seg = as.getSource(0);
    	assertEquals("s2", seg.id);
    }
    
    @Test
    public void removeSegmentsWithTargetTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg = as.getSource(0);
    	assertEquals("0", seg.id);
    	as.remove(seg);
    	ISegments segs = tu.getTargetSegments(locFR);
    	seg = segs.get(0);
    	assertEquals("s2", seg.id);
    }
    
    @Test
    public void insertSourceSegmentTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg1 = new Segment("newId");
    	as.insert(1, seg1);
    	Segment seg2 = as.getSource(1);
    	assertSame(seg1, seg2); // Check insertion
    	Segment seg3 = as.getSource(2);
    	assertEquals("s2", seg3.id); // Check old seg(1) was move down
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR);
    	assertEquals(seg1.id, seg4.id); // Check target was added
    }
    
    @Test
    public void insertSourceSegmentChangeIdTest () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	IAlignedSegments as = tu.getSegments();
    	Segment seg1 = new Segment("s2"); // "s2" id exists already
    	as.insert(1, seg1);
    	Segment seg2 = as.getSource(1);
    	assertSame(seg1, seg2);
    	assertEquals("1", seg2.id); // Id was changed to a valid one
    	Segment seg4 = as.getCorrespondingTarget(seg2, locFR);
    	assertEquals(seg1.id, seg4.id); // Check target was added with validated id
    }

    //	@Test
//	public void testGetSetSourceContent () {
//		TextFragment tf1 = new TextFragment("source text");
//		tu1.setSourceContent(tf1);
//		TextFragment tf2 = ((TextContainer)tu1.getSourceContent()).getContent();
//		//TODO: the tc is actually not the same!, because it uses insert()
//		// Do we need to 'fix' this? Probably.
//		//assertSame(tf1, tf2);
//        assertEquals("source content", tf1, tf2);
//    }
//
//	@Test
//	public void testGetSetTargetContent () {
//		TextFragment tf1 = new TextFragment("fr text");
//		tu1.setTargetContent(locFR, tf1);
//		TextFragment tf2 = tu1.getTargetContent(locFR);
//		//TODO: the tc is actually not the same!, because it uses insert()
//		// Do we need to 'fix' this? Probably.
//		//assertSame(tf1, tf2);
//        assertEquals("target content", tf1, tf2);
//	}

    private ITextUnit createSegmentedTU () {
    	ITextUnit tu = new TextUnit2("id", "Part 1.");
    	tu.getSource().getSegments().append(new Segment("s2", new TextFragment("Part 2.")), " a ");
    	return tu;
    }
    
    private ITextUnit createSegmentedTUAndTarget () {
    	ITextUnit tu = createSegmentedTU();
    	// Add the target segments
    	ISegments segs = tu.getTarget_DIFF(locFR).getSegments();
    	segs.get(0).text.append("Trg 1.");
    	segs.get(1).text.append("Trg 2.");
    	return tu;
    }
}
