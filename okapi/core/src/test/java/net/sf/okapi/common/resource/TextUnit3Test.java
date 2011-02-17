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
import net.sf.okapi.common.filterwriter.GenericContent;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/*
 * Copied from TextUnit2Test and just changed the name so far
 */
public class TextUnit3Test {

    private static final LocaleId locFR = LocaleId.FRENCH;
    private static final LocaleId locES = LocaleId.SPANISH;
    private static final LocaleId locDE = LocaleId.GERMAN;
    private static final String TU1 = "tu1";
    private TextContainer tc1;
    private ITextUnit tu1;
    private GenericContent fmt;

    public TextUnit3Test () {
    	fmt = new GenericContent();
    }

    @Before
    public void setUp(){
        tu1 = new TextUnit3(TU1);
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
        assertEquals("[] a []", fmt.printSegmentedContent(tu1.getTarget_DIFF(locFR), true));
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
    public void loopThroughSegmentsWithoutTargets () {
    	ITextUnit tu = createSegmentedTU();
    	tu.createTarget(locES, true, IResource.CREATE_EMPTY); // Not a copy of the source
    	Segment trgSeg;
    	for ( Segment srcSeg : tu.getSourceSegments() ) {
    		if ( srcSeg.id.equals("0") ) {
    			assertEquals("Part 1.", srcSeg.text.toString());
    			trgSeg = tu.getTargetSegment(locFR, srcSeg.id, true); // FR
    			assertEquals("", trgSeg.text.toString());
    		}
    		else {
    			assertEquals("Part 2.", srcSeg.text.toString());
    			trgSeg = tu.getTargetSegment(locES, srcSeg.id, false); // ES
    			assertNull(trgSeg);
    		}
    	}
        assertEquals("[Part 1.] a [Part 2.]", fmt.printSegmentedContent(tu.getSource(), true));
        assertTrue(tu.hasTarget(locFR));
        assertEquals("[] a []", fmt.printSegmentedContent(tu.getTarget_DIFF(locFR), true));
        assertTrue(tu.hasTarget(locES));
        assertEquals("[]", fmt.printSegmentedContent(tu.getTarget_DIFF(locES), true));
    }

    @Test
    public void getSourceSegments () {
    	ITextUnit tu = createSegmentedTU();
    	ISegments segs = tu.getSourceSegments();
    	assertNotNull(segs);
    	assertEquals(2, segs.count());
    }

    @Test
    public void getExistingTargetSegments () {
    	ITextUnit tu = createSegmentedTUAndTarget();
    	ISegments segs = tu.getTargetSegments(locFR);
    	assertNotNull(segs);
    	assertEquals(2, segs.count());
    	assertEquals("Trg 1.", segs.get(0).toString());
    }

    @Test
    public void getNonExistingTargetSegments () {
    	ITextUnit tu = createSegmentedTU();
    	ISegments segs = tu.getTargetSegments(locES);
    	assertNotNull(segs);
    	assertEquals(2, segs.count());
    	assertEquals("", segs.get(0).toString());
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


    @Test
    public void returnDefaultSourceForNewLocale() {
        TextUnit3 tu3 = createDualSourceTU();
        ITextUnit tu = tu3;

        tu3.setActiveLocale(locDE);
        assertEquals("Default source should be used when the active locale does not have a custom source",
                     "Default source", tu.getSource().toString());
    }

    @Test
    public void returnCorrectSourceForLocale() {
        TextUnit3 tu3 = createDualSourceTU();
        ITextUnit tu = tu3;

        assertEquals("Default source should be used when locale has not been set",
                     "Default source", tu.getSource().toString());

        tu3.setActiveLocale(locFR);
        assertEquals("Custom source should be used when locale is set to one with custom source",
                     "Source for fr", tu.getSource().toString());

        tu3.setActiveLocale(null);
        assertEquals("Default source should be used when locale has been set to null",
                     "Default source", tu.getSource().toString());

    }

    @Test
    public void changeCorrectSourceForLocale() {
        TextUnit3 tu3 = createDualSourceTU();
        ITextUnit tu = tu3;

        tu.setSourceContent(new TextFragment("New default source"));
        assertEquals("Default source should be modified when no locale is set",
                     "New default source", tu.getSource().toString());

        tu3.setActiveLocale(locFR);
        assertEquals("Custom source should not have been changed when its locale was not active",
                     "Source for fr", tu.getSource().toString());

        tu.setSourceContent(new TextFragment("New fr source"));
        assertEquals("Custom source should have been changed when its locale was active",
                     "New fr source", tu.getSource().toString());

        tu3.setActiveLocale(null);
        assertEquals("Default source should not have been modified when a custom source locale was active",
                     "New default source", tu.getSource().toString());
    }

    @Test
    public void newSourceDuplicatesDefaultSourceWhenNotSpecified() {
        TextUnit3 tu3 = new TextUnit3("id", "Original default source");
        tu3.createSource(locFR, true);
        
        //change source to make sure it has been copied and is not just referencing it
        tu3.setSourceContent(new TextFragment("New default source"));
        tu3.setActiveLocale(locFR);

        assertEquals("new custom sources should use content from the default source if none is specified",
                     "Original default source", tu3.getSource().toString());
    }

    @Test
    public void targetsUseCustomSourceWhenAvailable() {
        TextUnit3 tu3 = createDualSourceTUAndTargets();

        assertEquals("New targets should copy default source when there is no custom source for their locale",
                     "Default source", tu3.getTarget_DIFF(locDE).toString());
        assertEquals("New targets should copy custom source when it is available for their locale",
                     "Source for fr", tu3.getTarget_DIFF(locFR).toString());
    }

    @Test
    public void customSourceDeletedWithTarget() {
        TextUnit3 tu3 = createDualSourceTUAndTargets();
        tu3.setActiveLocale(locFR);
        assertEquals("Custom source should be present when it has been created",
                     "Source for fr", tu3.getSource().toString());
        tu3.removeTarget(locFR);
        assertEquals("Custom source should be removed when the target for the locale is removed",
                     "Default source", tu3.getSource().toString());
    }

    @Test
    public void multipleSourcesAndTargetsAreIndependent() {
        TextUnit3 tu3 = createMultiSourceTUAndTargets();
        //TODO
    }





    private TextUnit3 createDualSourceTU() {
        TextUnit3 tu = new TextUnit3("id", "Default source");
        tu.createSource("Source for fr", locFR, true);
        return tu;
    }

    private TextUnit3 createDualSourceTUAndTargets() {
        TextUnit3 tu = createDualSourceTU();
        tu.createTarget(locDE, true, IResource.COPY_ALL); //should use default source
        tu.createTarget(locFR, true, IResource.COPY_ALL); //should use fr source
        return tu;
    }

    private TextUnit3 createMultiSourceTU() {
        TextUnit3 tu = createDualSourceTU();
        tu.createSource("Source for es", locES, true);
        return tu;
    }

    private TextUnit3 createMultiSourceTUAndTargets() {
        TextUnit3 tu = createDualSourceTUAndTargets();
        tu.createSource("Source for es", locES, true);
        tu.createTarget(locES, true, IResource.COPY_ALL); //should use es source
        return tu;
    }





    
    private ITextUnit createSegmentedTU () {
    	ITextUnit tu = new TextUnit3("id", "Part 1.");
    	tu.getSource().getSegments().append(new Segment("s2", new TextFragment("Part 2.")), " a ");
    	return tu;
    }

    private ITextUnit createSegmentedTUAndTarget () {
    	ITextUnit tu = createSegmentedTU();
    	// Add the target segments
    	ISegments segs = tu.getTarget_DIFF(locFR).getSegments();
    	segs.get(0).text.append("Trg 1.");
    	segs.get(1).text.append("Trg 2.");
    	segs = tu.getTarget_DIFF(locES).getSegments();
    	segs.get(0).text.append("Objetivo 1.");
    	segs.get(1).text.append("Objetivo 2.");
    	return tu;
    }
}
