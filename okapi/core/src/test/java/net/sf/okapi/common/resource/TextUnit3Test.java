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

    private static final String DEFAULT_SOURCE = "Default source text";
    private static final String FR_SOURCE = "Custom source text for French target";
    private static final String ES_SOURCE = "Custom source text for Spanish target";
    private static final String DE_SOURCE = "Custom source text for German target";
    private static final String EMPTY_SOURCE = "";

    private static final String DEFAULT_SEG_1 = "first default segment.";
    private static final String DEFAULT_SEG_2 = "second default segment.";
    private static final String FR_SEG_1 = "first French segment.";
    private static final String FR_SEG_2 = "second French segment.";

    private static final String SEGMENT_2_ID = "s2";

    private TextFragment newDefaultFragment;
    private TextFragment newFRFragment;
    private TextFragment newESFragment;

    private TextContainer newDefaultContainer;
    private TextContainer newFRContainer;
    private TextContainer newESContainer;

    private Property dp1;
    private Property dp2;
    private Property fp1;
    private Property fp2;

    private IMultiSourceTextUnit mstu;

    private static final TextFragment NEW_DEFAULT_SOURCE = new TextFragment("New default source text");
    private static final TextFragment NEW_FR_SOURCE = new TextFragment("New custom source text for French target");
    private static final TextFragment NEW_ES_SOURCE = new TextFragment("New custom source text for Spanish target");

    public TextUnit3Test () {
    	fmt = new GenericContent();
    }

    @Before
    public void setUp(){
        tu1 = new TextUnit3(TU1);
        tc1 = new TextContainer("fr text");

        newDefaultFragment = new TextFragment("New default source text");
        newFRFragment = new TextFragment("New custom source text for French target");
        newESFragment = new TextFragment("New custom source text for Spanish target");

        newDefaultContainer = new TextContainer(newDefaultFragment);
        newFRContainer = new TextContainer(newFRFragment);
        newESContainer = new TextContainer(newESFragment);

        //properties
        dp1 = new Property("property1", "default_value", true);
        dp2 = new Property("property2", "default_value_2", true);
        fp1 = new Property("property1", "french_value", true);
        fp2 = new Property("property2", "french_value_2", true);

        mstu = null;
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


//// test methods influenced by active target locale switch:

//Note: methods with default source are to test that the existence of a custom
// source doesn't interfere with the default source.
//     * isEmpty()


    /*
     *
     */


    @Test
    public void isEmptyDefaultSource() {
        //make a text unit with empty default source and non-empty custom source
        mstu = createTUWithCustomSources(EMPTY_SOURCE, FR_SOURCE, null, null);

        mstu.setActiveTargetLocale(locFR);
        assertFalse("isEmpty() should return false when there is a non-empty custom source for the active target locale",
                    mstu.isEmpty());

        mstu.setActiveTargetLocale(null);
        assertTrue("isEmpty() should return true when the default source is empty and no target locale is active",
                   mstu.isEmpty());
    }

    @Test
    public void isEmptyCustomSource() {
        //make a text unit with non-empty defautl source and empty custom source
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, EMPTY_SOURCE, null, null);

        mstu.setActiveTargetLocale(locFR);
        assertTrue("isEmpty() should return true when there is an empty custom source for the active target locale",
                    mstu.isEmpty());

        mstu.setActiveTargetLocale(null);
        assertFalse("isEmpty() should return false when the default source is non-empty and no target locale is active",
                   mstu.isEmpty());
    }

 
//     * getSource()
//     * setSource(TextContainer)

    @Test
    public void getSetDefaultSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //check default source can be set properly
        mstu.setSource(newDefaultContainer);
        assertSame("the default source should be set and returned by setSource()"
                   + " and getSource() when no active target locale has been set",
                   mstu.getSource(), newDefaultContainer);

        //make sure custom source wasn't set
        mstu.setActiveTargetLocale(locFR);
        assertNotSame("custom source should not be affected by setSource() when "
                      + "no active locale has been set",
                      mstu.getSource(), newDefaultContainer);

        //check custom source can be set properly
        mstu.setSource(newFRContainer);
        assertSame("custom source should be set and returned by setSource() and "
                   + "getSource() when a non-null active locale is set",
                   mstu.getSource(), newFRContainer);

        //make sure default source wasn't reset
        mstu.setActiveTargetLocale(null);
        assertSame("default source should not be changed by setSource() when a "
                   + "non-null active locale is set",
                   mstu.getSource(), newDefaultContainer);
    }

    @Test
    public void getSetCustomSourceIndependence() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);

        mstu.setActiveTargetLocale(locFR);
        mstu.setSource(newFRContainer);
        assertSame("custom source should be set and returned by setSource() and "
                   + "getSource() when a non-null active locale is set",
                   mstu.getSource(), newFRContainer);

        mstu.setActiveTargetLocale(locES);
        assertNotSame("setSource() and getSource() should only act on the source"
                      + "for the currently active locale",
                      mstu.getSource(), newFRContainer);
        mstu.setSource(newESContainer);
        assertSame("custom source should be set and returned by setSource() and "
                   + "getSource() when a non-null active locale is set",
                   mstu.getSource(), newESContainer);

        mstu.setActiveTargetLocale(locFR);
        assertSame("setSource() and getSource() should only act on the source"
                   + "for the currently active locale",
                   mstu.getSource(), newFRContainer);
    }

//     * setSourceContent(TextFragment)
    @Test
    public void setDefaultSourceContent() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //check default source can be set properly
        mstu.setSourceContent(newDefaultFragment);
        assertSame("default source content should be set by setSourceContent(TextFragment)"
                   + " when no active target locale is set",
                   mstu.getSource().getFirstContent(), newDefaultFragment);

        //make sure custom source wasn't set
        mstu.setActiveTargetLocale(locFR);
        assertNotSame("custom source content should not be set by setSourceContent(TextFragment)"
                      + " when no active target locale is set",
                      mstu.getSource().getFirstContent(), newDefaultFragment);

        //check custom source is set properly
        mstu.setSourceContent(newFRFragment);
        assertSame("custom source content should be set by setSourceContent(TextFragment)"
                   + " when its target locale is the active target locale",
                   mstu.getSource().getFirstContent(), newFRFragment);

        //make sure default source wasn't reset
        mstu.setActiveTargetLocale(null);
        assertNotSame("default source content should not be set by setSourceContent(TextFragment)"
                   + " when a non-null active target locale is set",
                   mstu.getSource().getFirstContent(), newFRFragment);
    }

    @Test
    public void setCustomSourceContentIndependence() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);

        mstu.setActiveTargetLocale(locFR);
        mstu.setSourceContent(newFRFragment);
        assertSame("custom source content should be set by setSourceContent(TextFragment)"
                   + " when its target locale is the active target locale",
                   mstu.getSource().getFirstContent(), newFRFragment);

        mstu.setActiveTargetLocale(locES);
        assertNotSame("custom source content should be set by setSourceContent(TextFragment)"
                   + " only when its target locale is the active target locale",
                   mstu.getSource(), newFRFragment);

        mstu.setSourceContent(newESFragment);
        assertSame("custom source content should be set by setSourceContent(TextFragment)"
                   + " when its target locale is the active target locale",
                   mstu.getSource().getFirstContent(), newESFragment);

        mstu.setActiveTargetLocale(locFR);
        assertNotSame("custom source content should not be set by setSourceContent(TextFragment)"
                     + " when its target locale is not the active target locale",
                     mstu.getSource().getFirstContent(), newESFragment);
    }


//     * createTarget(LocaleId, boolean, int)
    @Test
    public void createTargetFromCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.createTarget(locFR, true, ITextUnit.COPY_ALL);
        assertEquals("createTarget() should use the custom source if one exists"
                     + " for the given target locale",
                     FR_SOURCE, mstu.getTarget_DIFF(locFR).toString());
    }

    @Test
    public void createTargetFromDefaultSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.createTarget(locDE, true, ITextUnit.COPY_ALL);
        assertEquals("createTarget() should use the default source if no custom"
                     + " source exists for the given target locale",
                     DEFAULT_SOURCE, mstu.getTarget_DIFF(locDE).toString());
    }

//     * removeTarget(LocaleId) doesn't remove custom source
    @Test
    public void removeTargetWithCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createTarget(locFR, true, ITextUnit.COPY_ALL);
        mstu.removeTarget(locFR);

        assertEquals("custom source should not be removed when the target for "
                     + "the same locale is removed",
                     FR_SOURCE, mstu.getSource(locFR).toString());
    }
    


//     * getSegments (todo later)
    //TODO write tests for getSegments when I understand it

//     * getSourceSegments()
    @Test
    public void getMultiSourceSegments() {
        ISegments segs;
        mstu = createTUWithCustomSegmentedSources();

        //check that default source segments match
        segs = mstu.getSourceSegments();
        assertEquals("getSegments() should return the segments for the default "
                     + "source when no active locale is set",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSegments() should return the segments for the default "
                     + "source when no active locale is set",
                     DEFAULT_SEG_2, segs.get(1).toString());

        //check that french source segments match
        mstu.setActiveTargetLocale(locFR);
        segs = mstu.getSourceSegments();
        assertEquals("getSegments() should return the segments for a custom "
                     + "source when one exists for the active locale",
                     FR_SEG_1, segs.get(0).toString());
        assertEquals("getSegments() should return the segments for a custom "
                     + "source when one exists for the active locale",
                     FR_SEG_2, segs.get(1).toString());

        //check that default segments are returned when no custom locale is present
        mstu.setActiveTargetLocale(locES);
        segs = mstu.getSourceSegments();
        assertEquals("getSegments() should return the segments for the default "
                     + "source when there is no custom source for the active locale",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSegments() should return the segments for the default "
                     + "source when there is no custom source for the active locale",
                     DEFAULT_SEG_2, segs.get(1).toString());
    }


//     * getSourceSegment(String, boolean)
    @Test
    public void getMultiSourceSegment() {
        mstu = createTUWithCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + " default soure if no target locale has been set",
                     DEFAULT_SEG_2, mstu.getSourceSegment(SEGMENT_2_ID, true).toString());

        mstu.setActiveTargetLocale(locFR);
        assertEquals("getSourceSegment() should return the given segment for a "
                     + " custom soure if one exists for the active target locale",
                     FR_SEG_2, mstu.getSourceSegment(SEGMENT_2_ID, true).toString());

        mstu.setActiveTargetLocale(locES);
        assertEquals("getSourceSegment() should return the given segment for the"
                     + "default soure if there is no custom source for the active target locale",
                     DEFAULT_SEG_2, mstu.getSourceSegment(SEGMENT_2_ID, true).toString());
    }
    
//     * getSourceProperty(String)
//     * setSourceProperty(Property)
    //TODO this test is too long, split it into default/custom, and custom independence?
    @Test
    public void getSetMultiSourceProperty() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        mstu.setSourceProperty(dp1);
        assertSame("setSourceProperty() should apply a property to the default"
                     + " source when no active target locale has been set",
                     dp1, mstu.getSourceProperty("property1"));

        //check property not on french
        mstu.setActiveTargetLocale(locFR);
        assertNull("a source property added when no active target locale has "
                   + "been set should not be added to custom sources",
                   mstu.getSourceProperty("property1"));
        
        //add property to french
        mstu.setSourceProperty(fp1);
        mstu.setSourceProperty(fp2);

        assertSame("a source property added when there is a custom source for"
                     + " the active target locale should be attached to the "
                     + "custom source",
                     fp1, mstu.getSourceProperty("property1"));
        assertSame("a source property added when there is a custom source for"
                     + " the active target locale should be attached to the "
                     + "custom source",
                     fp2, mstu.getSourceProperty("property2"));

        mstu.setActiveTargetLocale(null);
        assertSame("properties of the default source should not be modified "
                     + "by changes to properties of a custom source",
                     dp1, mstu.getSourceProperty("property1"));
        assertNull("a property applied to a custom source should not be "
                   + "automatically applied to the default source",
                   mstu.getSourceProperty("property2"));

        //change to spanish
        mstu.setActiveTargetLocale(locES);
        //check that property can be retrieved from default from here
        assertSame("properties of the default source should be returned by "
                     + "getProperty() when there is no custom source for the active "
                     + "target locale",
                     dp1, mstu.getSourceProperty("property1"));
        //check that property can be added to default from here
        mstu.setSourceProperty(dp2);

        mstu.setActiveTargetLocale(null);
        assertSame("setProperty() should set a property for the default source "
                   + "when no custom source exists for the active target locale",
                   dp2, mstu.getSourceProperty("property2"));
    }
    
    
//     * removeSourceProperty(String)
    @Test
    public void removeMultiSourceProperty() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        mstu.setSourceProperty(dp1);
        mstu.setSourceProperty(dp2);

        //add property to custom
        mstu.setActiveTargetLocale(locFR);
        mstu.setSourceProperty(fp1);
        mstu.setSourceProperty(fp2);

        
        //remove property from custom - check default not removed
        mstu.removeSourceProperty("property1");
        assertNull("removeSourceProperty() should remove properties from the "
                   + "custom source for the active target locale if one exists",
                   mstu.getSourceProperty("property1"));
        
        mstu.setActiveTargetLocale(null);
        assertNotNull("removeSourceProperty() should not remove properties from "
                      + "the default source when there is a custom source for the "
                      + "active target locale",
                      mstu.getSourceProperty("property1"));
        
        //remove property from default - check custom not removed
        mstu.removeSourceProperty("property2");
        assertNull("removeSourceProperty() should remove properties from the "
                   + "default source when the active target locale is null",
                   mstu.getSourceProperty("property2"));

        mstu.setActiveTargetLocale(locFR);
        assertNotNull("removeSourceProperty() should not remove properties from "
                      + "custom sources when the active target locale is null",
                      mstu.getSourceProperty("property2"));

        //check default property removed with no custom source for active target locale
        mstu.setActiveTargetLocale(locES);

        mstu.removeSourceProperty("property1");
        mstu.setActiveTargetLocale(null);
        assertNull("removeSourceProperty() should remove properties from the "
                   + "default source when there is no custom source for the "
                   + "active target locale",
                   mstu.getSourceProperty("property1"));
    }

//     * getSourcePropertyNames()
    @Test
    public void getMultiSourcePropertyNames() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        mstu.setSourceProperty(dp1);

        //add property to custom
        mstu.setActiveTargetLocale(locFR);
        mstu.setSourceProperty(fp2);

        assertEquals("getSourcePropertyNames() should return property names for the "
                     + "custom source if one exists for the active target locale",
                     "property2", mstu.getSourcePropertyNames().toArray()[0]);

        mstu.setActiveTargetLocale(null);
        assertEquals("getSourcePropertyNames() should return property names for the "
                     + "default source when the active target locale is null",
                     "property1", mstu.getSourcePropertyNames().toArray()[0]);

        mstu.setActiveTargetLocale(locES);
        assertEquals("getSourcePropertyNames() should return property names for the "
                     + "default source when there is no custom source for the "
                     + "active target locale",
                     "property1", mstu.getSourcePropertyNames().toArray()[0]);
    }

//     * hasSourceProperty(String)
    @Test
    public void hasMultiSourceProperty() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        mstu.setSourceProperty(dp1);

        //add property to custom
        mstu.setActiveTargetLocale(locFR);
        mstu.setSourceProperty(fp2);

        assertFalse("hasSourceProperty() should only check for properties of the "
                    + "custom source if one exists for the active target locale",
                    mstu.hasSourceProperty("property1"));
        assertTrue("hasSourceProperty() should check for properties of the "
                   + "custom source if one exists for the active target locale",
                   mstu.hasSourceProperty("property2"));

        mstu.setActiveTargetLocale(null);
        assertTrue("hasSourceProperty() should check for properties of the "
                    + "default source when the active target locale is null",
                   mstu.hasSourceProperty("property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                    + "default source when the active target locale is null",
                    mstu.hasSourceProperty("property2"));

        mstu.setActiveTargetLocale(locES);
        assertTrue("hasSourceProperty() should check for properties of the "
                    + "default source when there is no custom source for the "
                    + "active target locale",
                   mstu.hasSourceProperty("property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                    + "default source when there is no custom source for the "
                    + "active target locale",
                    mstu.hasSourceProperty("property2"));
    }

//     * toString()
    @Test
    public void toStringMultiSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        assertEquals("toString() should return a string representation of the "
                     + "default source when no active target locale has been set",
                     DEFAULT_SOURCE, mstu.toString());

        mstu.setActiveTargetLocale(locFR);
        assertEquals("toString should return a string representation of a "
                     + "custom source when one exists for the active target locale",
                     FR_SOURCE, mstu.toString());

        mstu.setActiveTargetLocale(locES);
        assertEquals("toString() should return a string representation of the "
                     + "default source when there is no custom source for the "
                     + "active target locale",
                     DEFAULT_SOURCE, mstu.toString());
    }

    
    /*test methods in IMultiSourceTextUnit
     * 
     */
//TODO    public void copyFromSingleSource(ITextUnit original) {
    @Test(expected= NullPointerException.class)
    public void copyFromSingleSourceNull() {
        mstu = new TextUnit3("");
        
        //exception expected
        mstu.copyFromSingleSource(null);
    }

    @Test
    public void copyFromSingleSourceCopiesAll() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createTarget(locFR, true, IResource.COPY_ALL);
        mstu.setProperty(dp1);
        
        ITextUnit tu = createTUWithCustomSources(ES_SOURCE, null, null, null);
        tu.createTarget(locES, true, IResource.COPY_ALL);
        tu.setProperty(fp2);

        //TODO test that annotations are copied
        //TODO test that source and target properties and annotations are copied
        
        mstu.copyFromSingleSource(tu);
        
        assertEquals("default source should be copied from the ITextUnit",
                     ES_SOURCE, mstu.getSource().toString());
        
        assertFalse("custom sources from the multi-source text unit should not"
                  + " be kept when copying from a text unit",
                    mstu.hasCustomSource());
        
        assertFalse("targets from the multi-source text unit should not be kept "
                  + "when copying from a text unit",
                    mstu.hasTarget(locFR));
        assertTrue("targets from the text unit should be copied to the multi-source"
                 + " text unit",
                   mstu.hasTarget(locES));

        assertFalse("properties from the multi-source text unit should not be kept "
                  + "when copying from a text unit",
                    mstu.hasProperty("property1"));
        assertTrue("properties from the text unit should be copied to the multi-source"
                 + " text unit",
                   mstu.hasProperty("property2"));
    }

    


    







    @Test
    public void isSourceEmptyForLocaleCustomTrue() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, EMPTY_SOURCE, null, null);

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for the "
                  + "default source when passed a null locale id",
                    mstu.isSourceEmpty(null));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the custom source of the given target locale if a custom "
                 + "source exists",
                   mstu.isSourceEmpty(locFR));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the default source if there is no custom source for the given"
                  + " target locale",
                    mstu.isSourceEmpty(locES));
    }

    @Test
    public void isSourceEmptyForLocaleDefaultTrue() {
        mstu = createTUWithCustomSources(EMPTY_SOURCE, FR_SOURCE, null, null);

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for the "
                 + "default source when passed a null locale id",
                   mstu.isSourceEmpty(null));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the custom source of the given target locale if a custom "
                  + "source exists",
                    mstu.isSourceEmpty(locFR));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the default source if there is no custom source for the given"
                 + " target locale",
                   mstu.isSourceEmpty(locES));
    }

    @Test
    public void createCustomSourceNullLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any custom sources
        mstu.createCustomSource(null, true);

        assertEquals("no custom source should be created when a null locale is "
                   + "passed to createCustomSource",
                     0, mstu.getTargetLocalesWithCustomSource().size() );
    }

    @Test
    public void createCustomSourceUsesDefault() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, null, null, null);
        mstu.createCustomSource(locFR, true);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source if no source content is given",
                     DEFAULT_SOURCE, mstu.getSource(locFR).toString());
    }

    @Test
    public void createCustomSourceAlreadyExistsOverwriteExisting() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createCustomSource(locFR, true);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source when overwriting a custom source if no source "
                   + "content is given",
                     DEFAULT_SOURCE, mstu.getSource(locFR).toString());
    }
    @Test
    public void createCustomSourceAlreadyExistsDontOverwriteExisting() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createCustomSource(locFR, false);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source when overwriting a custom source if no source "
                   + "content is given",
                     FR_SOURCE, mstu.getSource(locFR).toString());
    }

    @Test
    public void createCustomSourceWithContentNullLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any custom sources
        mstu.createCustomSource(FR_SOURCE, null, true);

        assertEquals("no custom source should be created when a null locale is "
                   + "passed to createCustomSource",
                     0, mstu.getTargetLocalesWithCustomSource().size() );

        assertEquals("the default source should not be changed by createCustomSource()",
                     DEFAULT_SOURCE, mstu.getSource().toString());
    }

    @Test
    public void createCustomSourceWithContentAlreadyExistsOverwriteExisting() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createCustomSource(ES_SOURCE, locFR, true);
        assertEquals("createCustomSource() should overwrite custom source content "
                   + "when the overwriteExisting flag is true",
                     ES_SOURCE, mstu.getSource(locFR).toString());
    }
    @Test
    public void createCustomSourceWithContentAlreadyExistsDontOverwriteExisting() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.createCustomSource(ES_SOURCE, locFR, false);
        assertEquals("createCustomSource() should not overwrite custom source content "
                   + "when the overwriteExisting flag is false",
                     FR_SOURCE, mstu.getSource(locFR).toString());
    }

    @Test
    public void getSourceByLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "given a null target locale",
                     DEFAULT_SOURCE, mstu.getSource(null).toString());
        assertEquals("getSource(LocaleId) should return a custom source if one "
                   + "exists for the given target locale",
                     FR_SOURCE, mstu.getSource(locFR).toString());
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "there is no custom source for the given target locale",
                     DEFAULT_SOURCE, mstu.getSource(locES).toString());
    }

    @Test
    public void setCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertNull("setCustomSource() should not set any source if a null target "
                 + "locale is given",
                   mstu.setCustomSource(null, newDefaultContainer));
        assertEquals("setCustomSource() should not set any source if a null target "
                 + "locale is given",
                   DEFAULT_SOURCE, mstu.getSource().toString());

        mstu.setCustomSource(locFR, newFRContainer);
        assertEquals("setCustomSource() should replace any existing custom "
                   + "source for the given target locale",
                     NEW_FR_SOURCE, mstu.getSource(locFR).toString());

        mstu.setCustomSource(locES, newESContainer);
        assertEquals("setCustomSource() should create a new custom source if "
                   + "none exists for the given target locale",
                     NEW_ES_SOURCE, mstu.getSource(locES).toString());
    }

    @Test
    public void removeCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.removeCustomSource(null);
        assertEquals("removeCustomSource(null) should not remove the default source",
                     DEFAULT_SOURCE, mstu.getSource().toString());

        mstu.removeCustomSource(locFR);
        assertEquals("removeCustomSource(LocaleId) should remove any custom source "
                   + "for the given target locale",
                     DEFAULT_SOURCE, mstu.getSource(locFR).toString());
    }

    @Test
    public void hasCustomSourceFalse() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, null, null, null);
        //before adding
        assertFalse("hasCustomSource() should return false before any custom "
                  + "sources have been added",
                    mstu.hasCustomSource());

        //after removing
        mstu.createCustomSource(locFR, true);
        mstu.removeCustomSource(locFR);
        assertFalse("hasCustomSource() should return false after the last "
                  + "custom source has been removed",
                    mstu.hasCustomSource());
    }

    @Test
    public void hasCustomSourceTrue() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertTrue("hasCustomSource() should return true if this text unit has "
                 + "one or more custom sources",
                   mstu.hasCustomSource());
    }

    @Test(expected= NullPointerException.class)
    public void localeHasCustomSourceNullLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        
        //should throw null pointer exception
        mstu.hasCustomSource(null);
    }

    @Test
    public void localeHasCustomSourceTrue() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertTrue("hasCustomSource(LocaleId) should return true if there is a "
                 + "custom source for the given target locale",
                   mstu.hasCustomSource(locFR));
    }

    @Test
    public void localeHasCustomSourceFalse() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertFalse("hasCustomSource(LocaleId) should return false if there is no "
                  + "custom source for the given target locale",
                    mstu.hasCustomSource(locES));
    }


    @Test(expected= NullPointerException.class)
    public void setCustomSourceContentWithNullLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //should throw NullPointerException
        mstu.setCustomSourceContent(null, newDefaultFragment);
    }

    @Test
    public void setCustomSourceContentNewSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.setCustomSourceContent(locES, newESFragment);
        assertEquals("setCustomSourceContent() should create a new custom source "
                   + "with the given content if none exists for the given target "
                   + "locale",
                     NEW_ES_SOURCE, mstu.getSource(locES).toString());
    }

    @Test
    public void setCustomSourceContentExistingSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        mstu.setCustomSourceContent(locFR, newFRFragment);
        assertEquals("setCustomSourceContent() should replace the content for "
                   + "the custom source of the given target locale",
                     NEW_FR_SOURCE, mstu.getSource(locFR).toString());
    }




//TODO    public IAlignedSegments getSegments(LocaleId targetLocale);





    @Test
    public void getSourceSegmentsByLocaleNull() {
        ISegments segs;
        mstu = createTUWithCustomSegmentedSources();

        segs = mstu.getSourceSegments(null);

        //check that default source segments match
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when a null target locale is given",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when a null target locale is given",
                     DEFAULT_SEG_2, segs.get(1).toString());
    }

    @Test
    public void getSourceSegmentsByLocaleCustomSource() {
        ISegments segs;
        mstu = createTUWithCustomSegmentedSources();

        segs = mstu.getSourceSegments(locFR);

        //check that french source segments match
        assertEquals("getSourceSegments() should return the segments for a custom "
                     + "source when one exists for the given locale",
                     FR_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for a custom "
                     + "source when one exists for the given locale",
                     FR_SEG_2, segs.get(1).toString());
    }

    @Test
    public void getSourceSegmentsByLocaleNoCustomSource() {
        ISegments segs;
        mstu = createTUWithCustomSegmentedSources();

        segs = mstu.getSourceSegments(locES);

        //check that default segments are returned when no custom locale is present
        mstu.setActiveTargetLocale(locES);
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when there is no custom source for the given locale",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when there is no custom source for the given locale",
                     DEFAULT_SEG_2, segs.get(1).toString());
    }






//TODO tests for createIfNeeded?
// public Segment getSourceSegment(LocaleId targetLocale, String segId,
//                                    boolean createIfNeeded);

    @Test
    public void getSourceSegmentByLocaleNull() {
        mstu = createTUWithCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + " default soure if a null target locale is given",
                     DEFAULT_SEG_2, mstu.getSourceSegment(null, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleCustomSource() {
        mstu = createTUWithCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for a "
                     + " custom soure if one exists for the given target locale",
                     FR_SEG_2, mstu.getSourceSegment(locFR, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleNoCustomSource() {
        mstu = createTUWithCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + "default soure if there is no custom source for the given target locale",
                     DEFAULT_SEG_2, mstu.getSourceSegment(locES, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getTargetLocalesWithCustomSourceNone() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, null, null, null);

        assertEquals("getTargetLocalesWithCustomSource() should return an empty "
                   + "set if there are no locales with custom source",
                     0,
                     mstu.getTargetLocalesWithCustomSource().size());
    }

    @Test
    public void getTargetLocalesWithCustomSourceOne() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getTargetLocalesWithCustomSource() should return a set "
                   + "of each locale id that has an associated custom source",
                     1,
                     mstu.getTargetLocalesWithCustomSource().size());
        assertTrue("getTargetLocalesWithCustomSource() should return the "
                 + "target locales that have an associated custom source",
                   mstu.getTargetLocalesWithCustomSource().contains(locFR));
        assertFalse("getTargetLocalesWithCustomSource() should return only the "
                  + "target locales that have an associated custom source",
                    mstu.getTargetLocalesWithCustomSource().contains(locES));
    }

    @Test
    public void getTargetLocalesWithCustomSourceSome() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        assertEquals("getTargetLocalesWithCustomSource() should return a set "
                   + "of each locale id that has an associated custom source",
                     2,
                     mstu.getTargetLocalesWithCustomSource().size());
        assertTrue("getTargetLocalesWithCustomSource() should return all the "
                 + "target locales that have an associated custom source",
                   mstu.getTargetLocalesWithCustomSource().contains(locFR));
        assertTrue("getTargetLocalesWithCustomSource() should return all the "
                 + "target locales that have an associated custom source",
                   mstu.getTargetLocalesWithCustomSource().contains(locFR));
        assertFalse("getTargetLocalesWithCustomSource() should return only the "
                  + "target locales that have an associated custom source",
                    mstu.getTargetLocalesWithCustomSource().contains(locDE));
    }
    
    @Test
    public void getSetSourcePropertyByLocaleNull() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        mstu.setSourceProperty(null, dp1);
        assertSame("setSourceProperty() should apply a property to the default"
                 + " source when given a null target locale",
                     dp1, mstu.getSourceProperty(null, "property1"));

        //check property not on french
        assertNull("setSourceProperty() should not change properties for custom "
                 + "locales when a null target locale is given",
                   mstu.getSourceProperty(locFR, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to french
        mstu.setSourceProperty(locFR, fp1);

        assertSame("setSourceProperty() should set a property for the custom "
                 + "source if one exists for the given target locale",
                   fp1, mstu.getSourceProperty(locFR, "property1"));
        assertNull("setSourceProperty() should not change properties for the "
                 + "default source when there is a custom source for the given "
                 + "target locale",
                   mstu.getSourceProperty(null, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleNoCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to spanish
        mstu.setSourceProperty(locES, dp1);

        assertFalse("setSourceProperty() should not cause a new custom source "
                  + "to be created",
                    mstu.hasCustomSource(locES));
        assertSame("properties of the default source should be set by "
                 + "setSourceProperty() when there is no custom source for the "
                 + "given target locale",
                   dp1, mstu.getSourceProperty(null, "property1"));
        assertSame("properties of the default source should be returned when there "
                 + "is no custom source for the given target locale",
                   dp1, mstu.getSourceProperty(locES, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNull() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.setSourceProperty(null, dp1);
        mstu.setSourceProperty(locFR, fp1);

        mstu.removeSourceProperty(null, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when a null target locale is given",
                   mstu.getSourceProperty(null, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from custom sources when a null target locale is given",
                      mstu.getSourceProperty(locFR, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.setSourceProperty(null, dp1);
        mstu.setSourceProperty(locFR, fp1);

        mstu.removeSourceProperty(locFR, "property1");
        assertNull("removeSourceProperty() should remove properties from a "
                 + "custom source when one exists for the given target locale",
                   mstu.getSourceProperty(locFR, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from the default source when a custom source exists for "
                    + "the given target locale",
                      mstu.getSourceProperty(null, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNoCustomSource() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.setSourceProperty(null, dp1);
        mstu.setSourceProperty(locFR, fp1);

        mstu.removeSourceProperty(locES, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when no custom source exists for the given "
                 + "target locale",
                   mstu.getSourceProperty(null, "property1"));
    }

    @Test
    public void getSourcePropertyNamesByLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.setSourceProperty(null, dp1);
        mstu.setSourceProperty(locFR, fp1);
        mstu.setSourceProperty(locFR, fp2);

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when a null locale is given",
                     1, mstu.getSourcePropertyNames(null).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for a custom source when one exists for the given locale",
                     2, mstu.getSourcePropertyNames(locFR).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when there is no custom source for "
                   + "the given locale",
                     1, mstu.getSourcePropertyNames(locES).size());
    }

    @Test
    public void getSourcePropertyNamesByLocaleNoProperties() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        assertEquals("getSourcePropertyNames() should return an empty set if "
                   + "there are no properties for the source of the given "
                   + "target locale",
                     0, mstu.getSourcePropertyNames(locFR).size());
    }

    @Test
    public void hasSourcePropertyByLocale() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        mstu.setSourceProperty(null, dp1);
        mstu.setSourceProperty(locFR, fp2);

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                   mstu.hasSourceProperty(null, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                    mstu.hasSourceProperty(null, "property2"));

        assertFalse("hasSourceProperty() should check for properties of the "
                  + "custom source if one exists for the given target locale",
                    mstu.hasSourceProperty(locFR, "property1"));
        assertTrue("hasSourceProperty() should check for properties of the "
                  + "custom source if one exists for the given target locale",
                   mstu.hasSourceProperty(locFR, "property2"));

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source if there is no custom source for the given "
                 + "target locale",
                   mstu.hasSourceProperty(locES, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                  + "default source if there is no custom source for the given "
                  + "target locale",
                    mstu.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateSourcePropertyFromDefaultToOne() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(dp1);
        mstu.propagateSourceProperty(null, locFR, "property1", true);
        assertTrue("property should be copied from default source to the custom"
                 + " source of the given locale if present",
                   mstu.hasSourceProperty(locFR, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromCustomToOne() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(locFR, dp1);
        mstu.propagateSourceProperty(locFR, null, "property1", true);
        assertTrue("property should be copied from custom source to the default source",
                   mstu.hasSourceProperty(null, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property1"));
    }
    
    @Test
    public void propagateSourcePropertyFromDefaultToAll() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(dp1);
        mstu.propagateSourceProperty(null, "property1", true);
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   mstu.hasSourceProperty(locFR, "property1"));
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   mstu.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromCustomToAll() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(locFR, fp1);
        mstu.propagateSourceProperty(locFR, "property1", true);
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   mstu.hasSourceProperty(null, "property1"));
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   mstu.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateAllSourcePropertiesFromDefaultToOne() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(dp1);
        mstu.setSourceProperty(dp2);
        mstu.propagateAllSourceProperties(null, locFR, true);
        assertTrue("all properties should be copied from default source to the custom"
                 + " source of the given locale if present",
                   mstu.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to the custom"
                 + " source of the given locale if present",
                   mstu.hasSourceProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromCustomToOne() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(locFR, dp1);
        mstu.setSourceProperty(locFR, dp2);
        mstu.propagateAllSourceProperties(locFR, null, true);
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   mstu.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   mstu.hasSourceProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    mstu.hasSourceProperty(locES, "property2"));
    }

//TODO    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting);
    @Test
    public void propagateAllSourcePropertiesFromDefaultToAll() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(dp1);
        mstu.setSourceProperty(dp2);
        mstu.propagateAllSourceProperties(null, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locFR, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromCustomToAll() {
        mstu = createTUWithCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        mstu.setSourceProperty(locFR, dp1);
        mstu.setSourceProperty(locFR, dp2);
        mstu.propagateAllSourceProperties(locFR, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(null, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(null, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   mstu.hasSourceProperty(locES, "property2"));
    }

//TODO    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting);
//TODO    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting);
//TODO    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting);
//TODO    public void propagateAllSourceAnnotations(LocaleId from, boolean overwriteExisting);


//TODO think about null pointer exceptions for each of the classes that take a LocaleId


    
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

    private IMultiSourceTextUnit createTUWithCustomSegmentedSources() {
        TextUnit3 tu = new TextUnit3(TU1, DEFAULT_SEG_1);
        tu.getSource().getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(DEFAULT_SEG_2)), " a ");

        tu.createCustomSource(FR_SEG_1, locFR, true);
        tu.getSource(locFR).getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(FR_SEG_2)), " b ");

        return tu;
    }

    //utility method to create text units with various source configurations
    private IMultiSourceTextUnit createTUWithCustomSources(String defaultSource,
                                                           String frenchSource,
                                                           String spanishSource,
                                                           String germanSource) {
        TextUnit3 tu = new TextUnit3(TU1, defaultSource);
        if (frenchSource != null) tu.createCustomSource(frenchSource, locFR, true);
        if (spanishSource != null) tu.createCustomSource(spanishSource, locES, true);
        if (germanSource != null) tu.createCustomSource(germanSource, locDE, true);

        return tu;
    }
}
