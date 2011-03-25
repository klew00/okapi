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

import static net.sf.okapi.common.IResource.*;
import net.sf.okapi.common.LocaleId;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class VariantSourcesTest {

    private static final LocaleId locFR = LocaleId.FRENCH;
    private static final LocaleId locES = LocaleId.SPANISH;
    private static final LocaleId locDE = LocaleId.GERMAN;
    private static final String TU1 = "tu1";
    private TextContainer defaultSource;



    private static final String DEFAULT_SOURCE = "Default source text";
    private static final String FR_SOURCE = "Variant source text for French target";
    private static final String ES_SOURCE = "Variant source text for Spanish target";
    private static final String DE_SOURCE = "Variant source text for German target";
    private static final String EMPTY_SOURCE = "";

    private static final String DEFAULT_SEG_1 = "first default segment.";
    private static final String DEFAULT_SEG_2 = "second default segment.";
    private static final String FR_SEG_1 = "first French segment.";
    private static final String FR_SEG_2 = "second French segment.";

    private static final String SEGMENT_2_ID = "s2";

    private TextFragment newDefaultFragment;
    private TextFragment newFRFragment;
    private TextFragment newESFragment;

    private TextContainer DEFAULT_SOURCE_CONT;
    private TextContainer FR_SOURCE_CONT;
    private TextContainer ES_SOURCE_CONT;

    private TextContainer newDefaultContainer;
    private TextContainer newFRContainer;
    private TextContainer newESContainer;

    private Property dp1;
    private Property dp2;
    private Property fp1;
    private Property fp2;

    private VariantSources cs;

    private static final TextFragment NEW_DEFAULT_SOURCE = new TextFragment("New default source text");
    private static final TextFragment NEW_FR_SOURCE = new TextFragment("New variant source text for French target");
    private static final TextFragment NEW_ES_SOURCE = new TextFragment("New variant source text for Spanish target");


    @Before
    public void setUp(){
        defaultSource = new TextContainer(DEFAULT_SOURCE);
        //cs = new VariantSources(new TextUnit4(TU1, DEFAULT_SOURCE));
        cs = new VariantSources(new TextContainer(DEFAULT_SOURCE));

        DEFAULT_SOURCE_CONT = new TextContainer(DEFAULT_SOURCE);
        FR_SOURCE_CONT = new TextContainer(FR_SOURCE);
        ES_SOURCE_CONT = new TextContainer(ES_SOURCE);


        //TODO : are these needed?
        newDefaultFragment = new TextFragment(NEW_DEFAULT_SOURCE);
        newFRFragment = new TextFragment(NEW_FR_SOURCE);
        newESFragment = new TextFragment(NEW_ES_SOURCE);

        newDefaultContainer = new TextContainer(newDefaultFragment);
        newFRContainer = new TextContainer(newFRFragment);
        newESContainer = new TextContainer(newESFragment);

        //properties
        dp1 = new Property("property1", "default_value", true);
        dp2 = new Property("property2", "default_value_2", true);
        fp1 = new Property("property1", "french_value", true);
        fp2 = new Property("property2", "french_value_2", true);

        
    }



    @Test
    public void isSourceEmptyNull() {
        cs = createVariantSources(DEFAULT_SOURCE, EMPTY_SOURCE, null, null);

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for the "
                  + "default source when passed a null locale id",
                    cs.isEmpty(null));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the variant source of the given target locale if a variant "
                 + "source exists",
                   cs.isEmpty(locFR));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the default source if there is no variant source for the given"
                  + " target locale",
                    cs.isEmpty(locES));
    }

    @Test
    public void isSourceEmptyForLocaleDefaultTrue() {
        cs = createVariantSources(EMPTY_SOURCE, FR_SOURCE, null, null);

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for the "
                 + "default source when passed a null locale id",
                   cs.isEmpty(null));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the variant source of the given target locale if a variant "
                  + "source exists",
                    cs.isEmpty(locFR));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the default source if there is no variant source for the given"
                 + " target locale",
                   cs.isEmpty(locES));
    }





    @Test
    public void createVariantSourceNullLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any variant sources
        cs.create(null, true, CREATE_EMPTY);

        assertEquals("no variant source should be created when a null locale is "
                   + "passed to createVariantSource",
                     0, cs.getLocales().size() );
    }

    @Test
    public void createVariantSourceUsesDefault() {
        cs = createVariantSources(DEFAULT_SOURCE, null, null, null);
        cs.create(locFR, true, COPY_ALL);
        assertEquals("createVariantSource() should use the content of the default "
                   + "source if no source content is given",
                     DEFAULT_SOURCE, cs.get(locFR).toString());
    }

    @Test
    public void createVariantSourceAlreadyExistsOverwriteExisting() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.create(locFR, true, COPY_CONTENT);
        assertEquals("createVariantSource() should use the content of the default "
                   + "source when overwriting a variant source if no source "
                   + "content is given",
                     DEFAULT_SOURCE, cs.get(locFR).toString());
    }
    @Test
    public void createVariantSourceAlreadyExistsDontOverwriteExisting() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.create(locFR, false, COPY_CONTENT);
        assertEquals("createVariantSource() should use the content of the default "
                   + "source when overwriting a variant source if no source "
                   + "content is given",
                     FR_SOURCE, cs.get(locFR).toString());
    }

    //TODO test each of the copy options for this method

    @Test
    public void createVariantSourceWithContentNullLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any variant sources
        cs.create(FR_SOURCE_CONT, null, true);

        assertEquals("no variant source should be created when a null locale is "
                   + "passed to createVariantSource",
                     0, cs.getLocales().size() );

        assertEquals("the default source should not be changed by createVariantSource()",
                     DEFAULT_SOURCE, cs.get(null).toString());
    }

    @Test
    public void createVariantSourceWithContentAlreadyExistsOverwriteExisting() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.create(ES_SOURCE_CONT, locFR, true);
        assertEquals("createVariantSource() should overwrite variant source content "
                   + "when the overwriteExisting flag is true",
                     ES_SOURCE, cs.get(locFR).toString());
    }
    @Test
    public void createVariantSourceWithContentAlreadyExistsDontOverwriteExisting() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.create(ES_SOURCE_CONT, locFR, false);
        assertEquals("createVariantSource() should not overwrite variant source content "
                   + "when the overwriteExisting flag is false",
                     FR_SOURCE, cs.get(locFR).toString());
    }

    @Test
    public void getSourceByLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "given a null target locale",
                     DEFAULT_SOURCE, cs.get(null).toString());
        assertEquals("getSource(LocaleId) should return a variant source if one "
                   + "exists for the given target locale",
                     FR_SOURCE, cs.get(locFR).toString());
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "there is no variant source for the given target locale",
                     DEFAULT_SOURCE, cs.get(locES).toString());
    }

    @Test(expected= IllegalArgumentException.class)
    public void setSourceNullLocaleThrowsException() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.set(null, newDefaultContainer);
    }

    @Test
    public void setSourceReplacesExisting() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.set(locFR, newFRContainer);
        assertEquals("setSource() should replace any existing variant "
                   + "source for the given target locale",
                     NEW_FR_SOURCE, cs.get(locFR).toString());
    }

    @Test
    public void setSourceCreatesNew() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.set(locES, newESContainer);
        assertEquals("setSource() should create a new variant source if "
                   + "none exists for the given target locale",
                     NEW_ES_SOURCE, cs.get(locES).toString());
    }

    @Test(expected= IllegalArgumentException.class)
    public void removeSourceNullLocaleThrowsException() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.remove(null);
    }

    @Test
    public void removeSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.remove(locFR);
        assertEquals("removeSource(LocaleId) should remove any variant source "
                   + "for the given target locale",
                     DEFAULT_SOURCE, cs.get(locFR).toString());
    }



    @Test(expected= IllegalArgumentException.class)
    public void localeHasVariantSourceNullLocaleThrowsException() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.hasVariant(null);
    }

    @Test
    public void localeHasVariantSourceTrue() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertTrue("hasVariantSource(LocaleId) should return true if there is a "
                 + "variant source for the given target locale",
                   cs.hasVariant(locFR));
    }

    @Test
    public void localeHasVariantSourceFalse() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertFalse("hasVariantSource(LocaleId) should return false if there is no "
                  + "variant source for the given target locale",
                    cs.hasVariant(locES));
    }


    @Test(expected= IllegalArgumentException.class)
    public void setVariantSourceContentWithNullLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setContent(null, newDefaultFragment);
    }

    @Test
    public void setVariantSourceContentNewSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setContent(locES, newESFragment);
        assertEquals("setVariantSourceContent() should create a new variant source "
                   + "with the given content if none exists for the given target "
                   + "locale",
                     NEW_ES_SOURCE, cs.get(locES).toString());
    }

    @Test
    public void setVariantSourceContentExistingSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setContent(locFR, newFRFragment);
        assertEquals("setSourceContent() should replace the content for "
                   + "the variant source of the given target locale",
                     NEW_FR_SOURCE, cs.get(locFR).toString());
    }


    @Test
    public void getSegmentsReturnsIAlignedSegments() {
        //This test is a reminder to get the IAlignedSegments implementation working
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //throws a 'not implemented' exception
        try {
            cs.getSegments(locFR);
        } catch (UnsupportedOperationException e) {
            fail("IAlignedSegments should be implemented");
        }
    }

//TODO    public IAlignedSegments getSegments(LocaleId targetLocale);





    @Test
    public void getSourceSegmentsByLocaleNull() {
        ISegments segs;
        cs = createVariantSegmentedSources();

        segs = cs.getSegments(null);

        //check that default source segments match
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when a null target locale is given",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when a null target locale is given",
                     DEFAULT_SEG_2, segs.get(1).toString());
    }

    @Test
    public void getSourceSegmentsByLocaleVariantSource() {
        ISegments segs;
        cs = createVariantSegmentedSources();

        segs = cs.getSegments(locFR);

        //check that french source segments match
        assertEquals("getSourceSegments() should return the segments for a variant "
                     + "source when one exists for the given locale",
                     FR_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for a variant "
                     + "source when one exists for the given locale",
                     FR_SEG_2, segs.get(1).toString());
    }

    @Test
    public void getSourceSegmentsByLocaleNoVariantSource() {
        ISegments segs;
        cs = createVariantSegmentedSources();

        segs = cs.getSegments(locES);

        //check that default segments are returned when no variant locale is present
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when there is no variant source for the given locale",
                     DEFAULT_SEG_1, segs.get(0).toString());
        assertEquals("getSourceSegments() should return the segments for the default "
                     + "source when there is no variant source for the given locale",
                     DEFAULT_SEG_2, segs.get(1).toString());
    }






//TODO tests for createIfNeeded?
// public Segment getSegment(LocaleId targetLocale, String segId,
//                                    boolean createIfNeeded);

    @Test
    public void getSourceSegmentByLocaleNull() {
        cs = createVariantSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + " default soure if a null target locale is given",
                     DEFAULT_SEG_2, cs.getSegment(null, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleVariantSource() {
        cs = createVariantSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for a "
                     + " variant soure if one exists for the given target locale",
                     FR_SEG_2, cs.getSegment(locFR, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleNoVariantSource() {
        cs = createVariantSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + "default soure if there is no variant source for the given target locale",
                     DEFAULT_SEG_2, cs.getSegment(locES, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getTargetLocalesWithVariantSourceNone() {
        cs = createVariantSources(DEFAULT_SOURCE, null, null, null);

        assertEquals("getTargetLocalesWithVariantSource() should return an empty "
                   + "set if there are no locales with variant source",
                     0,
                     cs.getLocales().size());
    }

    @Test
    public void getTargetLocalesWithVariantSourceOne() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getTargetLocalesWithVariantSource() should return a set "
                   + "of each locale id that has an associated variant source",
                     1,
                     cs.getLocales().size());
        assertTrue("getTargetLocalesWithVariantSource() should return the "
                 + "target locales that have an associated variant source",
                   cs.getLocales().contains(locFR));
        assertFalse("getTargetLocalesWithVariantSource() should return only the "
                  + "target locales that have an associated variant source",
                    cs.getLocales().contains(locES));
    }

    @Test
    public void getTargetLocalesWithVariantSourceSome() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        assertEquals("getTargetLocalesWithVariantSource() should return a set "
                   + "of each locale id that has an associated variant source",
                     2,
                     cs.getLocales().size());
        assertTrue("getTargetLocalesWithVariantSource() should return all the "
                 + "target locales that have an associated variant source",
                   cs.getLocales().contains(locFR));
        assertTrue("getTargetLocalesWithVariantSource() should return all the "
                 + "target locales that have an associated variant source",
                   cs.getLocales().contains(locFR));
        assertFalse("getTargetLocalesWithVariantSource() should return only the "
                  + "target locales that have an associated variant source",
                    cs.getLocales().contains(locDE));
    }

    @Test
    public void getSetSourcePropertyByLocaleNull() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        cs.setProperty(null, dp1);
        assertSame("setSourceProperty() should apply a property to the default"
                 + " source when given a null target locale",
                     dp1, cs.getProperty(null, "property1"));

        //check property not on french
        assertNull("setSourceProperty() should not change properties for variant "
                 + "locales when a null target locale is given",
                   cs.getProperty(locFR, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleVariantSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to french
        cs.setProperty(locFR, fp1);

        assertSame("setSourceProperty() should set a property for the variant "
                 + "source if one exists for the given target locale",
                   fp1, cs.getProperty(locFR, "property1"));
        assertNull("setSourceProperty() should not change properties for the "
                 + "default source when there is a variant source for the given "
                 + "target locale",
                   cs.getProperty(null, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleNoVariantSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to spanish
        cs.setProperty(locES, dp1);

        assertFalse("setSourceProperty() should not cause a new variant source "
                  + "to be created",
                    cs.hasVariant(locES));
        assertSame("properties of the default source should be set by "
                 + "setSourceProperty() when there is no variant source for the "
                 + "given target locale",
                   dp1, cs.getProperty(null, "property1"));
        assertSame("properties of the default source should be returned when there "
                 + "is no variant source for the given target locale",
                   dp1, cs.getProperty(locES, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNull() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setProperty(null, dp1);
        cs.setProperty(locFR, fp1);

        cs.removeProperty(null, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when a null target locale is given",
                   cs.getProperty(null, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from variant sources when a null target locale is given",
                      cs.getProperty(locFR, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleVariantSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setProperty(null, dp1);
        cs.setProperty(locFR, fp1);

        cs.removeProperty(locFR, "property1");
        assertNull("removeSourceProperty() should remove properties from a "
                 + "variant source when one exists for the given target locale",
                   cs.getProperty(locFR, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from the default source when a variant source exists for "
                    + "the given target locale",
                      cs.getProperty(null, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNoVariantSource() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setProperty(null, dp1);
        cs.setProperty(locFR, fp1);

        cs.removeProperty(locES, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when no variant source exists for the given "
                 + "target locale",
                   cs.getProperty(null, "property1"));
    }

    @Test
    public void getSourcePropertyNamesByLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setProperty(null, dp1);
        cs.setProperty(locFR, fp1);
        cs.setProperty(locFR, fp2);

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when a null locale is given",
                     1, cs.getPropertyNames(null).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for a variant source when one exists for the given locale",
                     2, cs.getPropertyNames(locFR).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when there is no variant source for "
                   + "the given locale",
                     1, cs.getPropertyNames(locES).size());
    }

    @Test
    public void getSourcePropertyNamesByLocaleNoProperties() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        assertEquals("getSourcePropertyNames() should return an empty set if "
                   + "there are no properties for the source of the given "
                   + "target locale",
                     0, cs.getPropertyNames(locFR).size());
    }

    @Test
    public void hasSourcePropertyByLocale() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setProperty(null, dp1);
        cs.setProperty(locFR, fp2);

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                   cs.hasProperty(null, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                    cs.hasProperty(null, "property2"));

        assertFalse("hasSourceProperty() should check for properties of the "
                  + "variant source if one exists for the given target locale",
                    cs.hasProperty(locFR, "property1"));
        assertTrue("hasSourceProperty() should check for properties of the "
                  + "variant source if one exists for the given target locale",
                   cs.hasProperty(locFR, "property2"));

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source if there is no variant source for the given "
                 + "target locale",
                   cs.hasProperty(locES, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                  + "default source if there is no variant source for the given "
                  + "target locale",
                    cs.hasProperty(locES, "property2"));
    }

    @Test
    public void propagateSourcePropertyFromDefaultToOne() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(null, dp1);
        cs.propagateProperty(null, locFR, "property1", true);
        assertTrue("property should be copied from default source to the variant"
                 + " source of the given locale if present",
                   cs.hasProperty(locFR, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromVariantToOne() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(locFR, dp1);
        cs.propagateProperty(locFR, null, "property1", true);
        assertTrue("property should be copied from variant source to the default source",
                   cs.hasProperty(null, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromDefaultToAll() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(null, dp1);
        cs.propagateProperty(null, "property1", true);
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   cs.hasProperty(locFR, "property1"));
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   cs.hasProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromVariantToAll() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(locFR, fp1);
        cs.propagateProperty(locFR, "property1", true);
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   cs.hasProperty(null, "property1"));
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   cs.hasProperty(locES, "property1"));
    }

    @Test
    public void propagateAllSourcePropertiesFromDefaultToOne() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(null, dp1);
        cs.setProperty(null, dp2);
        cs.propagateAllProperties(null, locFR, true);
        assertTrue("all properties should be copied from default source to the variant"
                 + " source of the given locale if present",
                   cs.hasProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to the variant"
                 + " source of the given locale if present",
                   cs.hasProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromVariantToOne() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(locFR, dp1);
        cs.setProperty(locFR, dp2);
        cs.propagateAllProperties(locFR, null, true);
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   cs.hasProperty(locFR, "property1"));
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   cs.hasProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasProperty(locES, "property2"));
    }

//TODO    public void propagateAllProperties(LocaleId from, boolean overwriteExisting);
    @Test
    public void propagateAllSourcePropertiesFromDefaultToAll() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(null, dp1);
        cs.setProperty(null, dp2);
        cs.propagateAllProperties(null, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locFR, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromVariantToAll() {
        cs = createVariantSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setProperty(locFR, dp1);
        cs.setProperty(locFR, dp2);
        cs.propagateAllProperties(locFR, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(null, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(null, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasProperty(locES, "property2"));
    }

//TODO    public <A extends IAnnotation> void propagateAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting);
//TODO    public <A extends IAnnotation> void propagateAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting);
//TODO    public void propagateAllAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting);
//TODO    public void propagateAllAnnotations(LocaleId from, boolean overwriteExisting);


//TODO think about null pointer exceptions for each of the classes that take a LocaleId



    private VariantSources createVariantSegmentedSources() {

        VariantSources cSources = new VariantSources(new TextContainer(DEFAULT_SEG_1));
        cSources.get(null).getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(DEFAULT_SEG_2)), " a ");
        cSources.create(new TextContainer(FR_SEG_1), locFR, true);
        cSources.get(locFR).getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(FR_SEG_2)), " b ");

        return cSources;
    }

    //utility method to create a set of variant sources with an associated default source
    private VariantSources createVariantSources(String defaultSourceString,
                                              String frenchSource,
                                              String spanishSource,
                                              String germanSource) {

        VariantSources cSources = new VariantSources(new TextContainer(defaultSourceString) );
        if (frenchSource != null) cSources.create(new TextContainer(frenchSource), locFR, true);
        if (spanishSource != null) cSources.create(new TextContainer(spanishSource), locES, true);
        if (germanSource != null) cSources.create(new TextContainer(germanSource), locDE, true);

        return cSources;
    }

}
