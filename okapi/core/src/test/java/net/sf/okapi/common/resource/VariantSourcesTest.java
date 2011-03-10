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
//    private ITextUnit tu1;



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
    private static final TextFragment NEW_FR_SOURCE = new TextFragment("New custom source text for French target");
    private static final TextFragment NEW_ES_SOURCE = new TextFragment("New custom source text for Spanish target");


    @Before
    public void setUp(){
        defaultSource = new TextContainer(DEFAULT_SOURCE);
        cs = new VariantSources(new TextUnit4(TU1, DEFAULT_SOURCE));

        DEFAULT_SOURCE_CONT = new TextContainer(DEFAULT_SOURCE);
        FR_SOURCE_CONT = new TextContainer(FR_SOURCE);
        ES_SOURCE_CONT = new TextContainer(ES_SOURCE);


        //TODO : are these needed?
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

        
    }


//    /**
//     * Indicates if the source text for the given target locale is empty.
//     * @param targetLocale the target locale for the source to check
//     * @return true if the source text (may be the default source) for the given
//     *         locale is empty, false otherwise
//     */
//    public boolean isSourceEmpty(LocaleId targetLocale);

    //null --> default
    //locale not in custom locales -->
    //locale in custom locales --> the locale


    @Test
    public void isSourceEmptyNull() {
        cs = createCustomSources(DEFAULT_SOURCE, EMPTY_SOURCE, null, null);

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for the "
                  + "default source when passed a null locale id",
                    cs.isSourceEmpty(null));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the custom source of the given target locale if a custom "
                 + "source exists",
                   cs.isSourceEmpty(locFR));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the default source if there is no custom source for the given"
                  + " target locale",
                    cs.isSourceEmpty(locES));
    }

    @Test
    public void isSourceEmptyForLocaleDefaultTrue() {
        cs = createCustomSources(EMPTY_SOURCE, FR_SOURCE, null, null);

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for the "
                 + "default source when passed a null locale id",
                   cs.isSourceEmpty(null));

        assertFalse("isSourceEmpty(LocaleId) should return the empty status for "
                  + "the custom source of the given target locale if a custom "
                  + "source exists",
                    cs.isSourceEmpty(locFR));

        assertTrue("isSourceEmpty(LocaleId) should return the empty status for "
                 + "the default source if there is no custom source for the given"
                 + " target locale",
                   cs.isSourceEmpty(locES));
    }

//    /**
//     * Creates a custom source for a given target locale, using the content of the
//     * default source.
//     *
//     * @param targetLocale the target locale that uses the new source
//     * @param overwriteExisting overwrites any existing source associated with
//     *                          the locale if true
//     * @return the newly created source, or the existing source if it was not
//     *         overwritten, or null if the
//     */
//    public TextContainer createCustomSource(LocaleId targetLocale, boolean overwriteExisting);
//    //TODO add creation options
//
//
//    /**
//     * Creates a custom source for a given locale, using the provided text.
//     * @param sourceText the text to include in this source
//     * @param targetLocale the target locale that uses the new source
//     * @param overwriteExisting overwrites any existing source associated with
//     *                          the locale if true
//     * @return the newly created source
//     */
//    public TextContainer createCustomSource(String sourceText,
//                                            LocaleId targetLocale,
//                                            boolean overwriteExisting);
//    //TODO add creation options
//
//
//    /**
//     * Gets the source object (a {@link TextContainer} object) used for the
//     * given target locale. Returns the default source object if the target
//     * locale has no custom source.
//     *
//     * @param targetLocale the target locale used by the source.
//     * @return the source used by the given locale. May be the default source.
//     */
//    public TextContainer getSource(LocaleId targetLocale);
//
//    /**
//     * Sets the source object to use for the given target locale. Any existing
//     * custom source object for the target locale will be overwritten.
//     *
//     * @param targetLocale the target locale that will use the given source object
//     * @param textContainer the source object to use for the given target locale
//     * @return the source object that has been set
//     */
//    public TextContainer setCustomSource(LocaleId targetLocale, TextContainer textContainer);
//
//    /**
//     * Removes any custom source used for the given target locale. Any associated
//     * targets will be associated with the default locale, which may lead to
//     * misalignment of segments between the target and default source.
//     *
//     * @param targetLocale the locale for which to remove custom source
//     */
//    public void removeCustomSource(LocaleId targetLocale);
//
//    /**
//     *
//     * @return the number of custom sources stored in this CustomSources object
//     */
//    public int count();
//
//    /**
//     *
//     * @return true if there are no custom sources in this custom sources object
//     */
//    public boolean empty();
//
//    /**
//     * Indicates whether there is a custom source for the given locale
//     *
//     * @param targetLocale the locale to check for custom sources
//     * @return true if there is a custom source object for the given locale,
//     *         false otherwise. Returns false if null target locale is given.
//     * @throws NullPointerException if targetLocale is null
//     */
//    public boolean hasCustomSource(LocaleId targetLocale) throws NullPointerException;
//
//    /**
//     * Sets the content of the custom source for the given target locale. Creates
//     * a custom source if one does not exist for the given locale. Replaces any
//     * existing content for the source of the given locale.
//     *
//     * TODO discuss adding an overwriteExisting flag
//     *
//     * @param targetLocale the locale for which to set the source content
//     *                     null to set for default source
//     * @param content the content to use for the source of the given locale
//     * @return the source content that was set.
//     * @throws NullPointerException if targetLocale is null
//     */
//    public TextFragment setCustomSourceContent(LocaleId targetLocale, TextFragment content) throws NullPointerException;
//
//    /**
//     * Creates a new {@link IAlignedSegments} object to access and manipulate the
//     * segments of this text unit for a given target locale.
//     *
//     * @param targetLocale the target locale for the segments
//     * @return a new {@link IAlignedSegments} object
//     */
//    public IAlignedSegments getSegments(LocaleId targetLocale);
//    //TODO consider the implications of using a locale with no custom source
//    //     as having multiple targets using default source could cause alignment
//    //     problems.
//
//    /**
//     * Gets the segments for the source used for the given target locale.
//     * Un-segmented content return a single segment.
//     *
//     * @param targetLocale the target locale for which to return source segments
//     * @return an object implementing ISegments for the source content.
//     */
//    public ISegments getSourceSegments(LocaleId targetLocale);
//
//    /**
//     * Gets the source segment for a given target locale and segment id.
//     * <p>If the segment does not exists, one is created if <code>createIfNeeded</code> is true.
//     * TODO discuss how to handle target locales that use the default source
//     *
//     * @param targetLocale the target locale for which to return a source segment
//     * @param segId the id of the segment to retrieve
//     * @param createIfNeeded true to append a segment at the end of the content
//     *                       and return it if the segment does not exist yet.
//     *                       False to return null when the segment does not exists.
//     * @return the retrieved or created segment, or null if none was found or created.
//     */
//    public Segment getSourceSegment(LocaleId targetLocale, String segId,
//                                    boolean createIfNeeded);
//
//
//    /**
//     * Lists all target locales for which there is a custom source
//     * Does not include a locale for the default source.
//     * @return a set of all target locales that use a custom source.
//     */
//    public Set<LocaleId> getTargetLocalesWithCustomSource();
//
//
//
//    //TODO consider adding COPY_OPTIONS to the following methods if there is any call for it
//
//    /**
//     * Copies a single property from one source version to another source version.
//     *
//     * @param from the target locale of the source in which the property is located,
//     *             null to use the default source
//     * @param to the target locale of the source to which the property will be copied,
//     *           null to use the default source
//     * @param propertyName the name of the property to copy
//     * @param overwriteExisting true to overwrite an existing property if present
//     */
//    public void propagateSourceProperty(LocaleId from, LocaleId to, String propertyName, boolean overwriteExisting);
//
//    /**
//     * Copies a single property from one source version to all other source versions.
//     *
//     * @param from the target locale of the source in which the property is located,
//     *             null to use the default source
//     * @param propertyName the name of the property to copy
//     * @param overwriteExisting true to overwrite an existing property if present
//     */
//    public void propagateSourceProperty(LocaleId from, String propertyName, boolean overwriteExisting);
//
//    /**
//     * Copies all properties from one source version to another source version.
//     *
//     * @param from the target locale of the source to copy properties from,
//     *             null to use the default source
//     * @param to the target locale of the source to copy properties to,
//     *           null to use the default source
//     * @param overwriteExisting true to overwrite any existing properties if present
//     */
//    public void propagateAllSourceProperties(LocaleId from, LocaleId to, boolean overwriteExisting);
//
//
//    /**
//     * Copies all properties from one source version to all other source versions.
//     *
//     * @param from the target locale of the source to copy properties from,
//     *             null to use the default source
//     * @param overwriteExisting true to overwrite any existing properties if present
//     */
//    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting);
//
//
//    /**
//     * Copies a single annotation from one source version to another source version.
//     *
//     * @param from the target locale of the source in which the annotation is located,
//     *             null to use the default source
//     * @param to the target locale of the source to which the annotation will be copied,
//     *           null to use the default source
//     * @param type the type of the annotation to copy
//     * @param overwriteExisting true to overwrite an existing annotation if present
//     */
//    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting);
//
//    /**
//     * Copies a single annotation from one source version to all other source versions.
//     *
//     * @param from the target locale of the source in which the annotation is located,
//     *             null to use the default source
//     * @param type the type of the annotation to copy
//     * @param overwriteExisting true to overwrite an existing annotation if present
//     */
//    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting);
//
//    /**
//     * Copies all annotations from one source version to another source version.
//     *
//     * @param from the target locale of the source to copy annotations from,
//     *             null to use the default source
//     * @param to the target locale of the source to which annotations will be copied,
//     *           null to use the default source
//     * @param overwriteExisting true to overwrite any existing annotation if present
//     */
//    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting);
//
//    /**
//     * Copies all annotations from one source version to all other source versions.
//     *
//     * @param from the target locale of the source to copy annotations from,
//     *             null to use the default source
//     * @param overwriteExisting true to overwrite any existing annotation if present
//     */
//    public void propagateAllSourceAnnotations(LocaleId from, boolean overwriteExisting);
//
//
//    //TODO decide whether to keep these 5 methods, or perhaps move them to INameable if appropriate
//
//    /**
//     * Gets the source property for a given name and target locale.
//     * @param targetLocale the target locale of the source
//     * @param name The name of the source property to retrieve.
//     * @return The property or null if it does not exist.
//     */
//    public Property getSourceProperty (LocaleId targetLocale, String name);
//
//    /**
//     * Sets a source property. If a property already exists it is overwritten.
//     * @param targetLocale the target locale of the source
//     * @param property The new property to set.
//     * @return The property that has been set.
//     */
//    public Property setSourceProperty (LocaleId targetLocale, Property property);
//
//    /**
//     * Removes a source property of a given name. If the property does not exists
//     * nothing happens.
//     * @param targetLocale the target locale of the source
//     * @param name The name of the property to remove.
//     */
//    public void removeSourceProperty (LocaleId targetLocale, String name);
//
//    /**
//     * Gets the names of all the source properties for this resource.
//     * @param targetLocale the target locale of the source
//     * @return All the names of the source properties for this resource.
//     */
//    public Set<String> getSourcePropertyNames (LocaleId targetLocale);
//
//    /**
//     * Indicates if a source property exists for a given name.
//     * @param targetLocale the target locale of the source
//     * @param name The name of the source property to query.
//     * @return True if a source property exists, false otherwise.
//     */
//    public boolean hasSourceProperty (LocaleId targetLocale, String name);








    @Test
    public void createCustomSourceNullLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any custom sources
        cs.createSource(null, true, CREATE_EMPTY);

        assertEquals("no custom source should be created when a null locale is "
                   + "passed to createCustomSource",
                     0, cs.getTargetLocalesWithCustomSource().size() );
    }

    @Test
    public void createCustomSourceUsesDefault() {
        cs = createCustomSources(DEFAULT_SOURCE, null, null, null);
        cs.createSource(locFR, true, COPY_ALL);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source if no source content is given",
                     DEFAULT_SOURCE, cs.getSource(locFR).toString());
    }

    @Test
    public void createCustomSourceAlreadyExistsOverwriteExisting() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.createSource(locFR, true, COPY_CONTENT);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source when overwriting a custom source if no source "
                   + "content is given",
                     DEFAULT_SOURCE, cs.getSource(locFR).toString());
    }
    @Test
    public void createCustomSourceAlreadyExistsDontOverwriteExisting() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.createSource(locFR, false, COPY_CONTENT);
        assertEquals("createCustomSource() should use the content of the default "
                   + "source when overwriting a custom source if no source "
                   + "content is given",
                     FR_SOURCE, cs.getSource(locFR).toString());
    }

    //TODO test each of the copy options for this method

    @Test
    public void createCustomSourceWithContentNullLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, null, null, null);

        //null locale should not create any custom sources
        cs.createSource(FR_SOURCE_CONT, null, true);

        assertEquals("no custom source should be created when a null locale is "
                   + "passed to createCustomSource",
                     0, cs.getTargetLocalesWithCustomSource().size() );

        assertEquals("the default source should not be changed by createCustomSource()",
                     DEFAULT_SOURCE, cs.getSource(null).toString());
    }

    @Test
    public void createCustomSourceWithContentAlreadyExistsOverwriteExisting() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.createSource(ES_SOURCE_CONT, locFR, true);
        assertEquals("createCustomSource() should overwrite custom source content "
                   + "when the overwriteExisting flag is true",
                     ES_SOURCE, cs.getSource(locFR).toString());
    }
    @Test
    public void createCustomSourceWithContentAlreadyExistsDontOverwriteExisting() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.createSource(ES_SOURCE_CONT, locFR, false);
        assertEquals("createCustomSource() should not overwrite custom source content "
                   + "when the overwriteExisting flag is false",
                     FR_SOURCE, cs.getSource(locFR).toString());
    }

    @Test
    public void getSourceByLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "given a null target locale",
                     DEFAULT_SOURCE, cs.getSource(null).toString());
        assertEquals("getSource(LocaleId) should return a custom source if one "
                   + "exists for the given target locale",
                     FR_SOURCE, cs.getSource(locFR).toString());
        assertEquals("getSource(LocaleId) should return the default source when "
                   + "there is no custom source for the given target locale",
                     DEFAULT_SOURCE, cs.getSource(locES).toString());
    }

    @Test(expected= IllegalArgumentException.class)
    public void setSourceNullLocaleThrowsException() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setSource(null, newDefaultContainer);
    }

    @Test
    public void setSourceReplacesExisting() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSource(locFR, newFRContainer);
        assertEquals("setSource() should replace any existing custom "
                   + "source for the given target locale",
                     NEW_FR_SOURCE, cs.getSource(locFR).toString());
    }

    @Test
    public void setSourceCreatesNew() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSource(locES, newESContainer);
        assertEquals("setSource() should create a new custom source if "
                   + "none exists for the given target locale",
                     NEW_ES_SOURCE, cs.getSource(locES).toString());
    }

    @Test(expected= IllegalArgumentException.class)
    public void removeSourceNullLocaleThrowsException() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.removeSource(null);
    }

    @Test
    public void removeSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.removeSource(locFR);
        assertEquals("removeSource(LocaleId) should remove any custom source "
                   + "for the given target locale",
                     DEFAULT_SOURCE, cs.getSource(locFR).toString());
    }



    @Test(expected= IllegalArgumentException.class)
    public void localeHasCustomSourceNullLocaleThrowsException() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.hasCustomSource(null);
    }

    @Test
    public void localeHasCustomSourceTrue() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertTrue("hasCustomSource(LocaleId) should return true if there is a "
                 + "custom source for the given target locale",
                   cs.hasCustomSource(locFR));
    }

    @Test
    public void localeHasCustomSourceFalse() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertFalse("hasCustomSource(LocaleId) should return false if there is no "
                  + "custom source for the given target locale",
                    cs.hasCustomSource(locES));
    }


    @Test(expected= IllegalArgumentException.class)
    public void setCustomSourceContentWithNullLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setSourceContent(null, newDefaultFragment);
    }

    @Test
    public void setCustomSourceContentNewSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setSourceContent(locES, newESFragment);
        assertEquals("setCustomSourceContent() should create a new custom source "
                   + "with the given content if none exists for the given target "
                   + "locale",
                     NEW_ES_SOURCE, cs.getSource(locES).toString());
    }

    @Test
    public void setCustomSourceContentExistingSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        cs.setSourceContent(locFR, newFRFragment);
        assertEquals("setSourceContent() should replace the content for "
                   + "the custom source of the given target locale",
                     NEW_FR_SOURCE, cs.getSource(locFR).toString());
    }


    @Test
    public void getSegmentsReturnsIAlignedSegments() {
        //This test is a reminder to get the IAlignedSegments implementation working
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

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
        cs = createCustomSegmentedSources();

        segs = cs.getSourceSegments(null);

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
        cs = createCustomSegmentedSources();

        segs = cs.getSourceSegments(locFR);

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
        cs = createCustomSegmentedSources();

        segs = cs.getSourceSegments(locES);

        //check that default segments are returned when no custom locale is present
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
        cs = createCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + " default soure if a null target locale is given",
                     DEFAULT_SEG_2, cs.getSourceSegment(null, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleCustomSource() {
        cs = createCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for a "
                     + " custom soure if one exists for the given target locale",
                     FR_SEG_2, cs.getSourceSegment(locFR, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getSourceSegmentByLocaleNoCustomSource() {
        cs = createCustomSegmentedSources();

        assertEquals("getSourceSegment() should return the given segment for the"
                     + "default soure if there is no custom source for the given target locale",
                     DEFAULT_SEG_2, cs.getSourceSegment(locES, SEGMENT_2_ID, true).toString());
    }

    @Test
    public void getTargetLocalesWithCustomSourceNone() {
        cs = createCustomSources(DEFAULT_SOURCE, null, null, null);

        assertEquals("getTargetLocalesWithCustomSource() should return an empty "
                   + "set if there are no locales with custom source",
                     0,
                     cs.getTargetLocalesWithCustomSource().size());
    }

    @Test
    public void getTargetLocalesWithCustomSourceOne() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);
        assertEquals("getTargetLocalesWithCustomSource() should return a set "
                   + "of each locale id that has an associated custom source",
                     1,
                     cs.getTargetLocalesWithCustomSource().size());
        assertTrue("getTargetLocalesWithCustomSource() should return the "
                 + "target locales that have an associated custom source",
                   cs.getTargetLocalesWithCustomSource().contains(locFR));
        assertFalse("getTargetLocalesWithCustomSource() should return only the "
                  + "target locales that have an associated custom source",
                    cs.getTargetLocalesWithCustomSource().contains(locES));
    }

    @Test
    public void getTargetLocalesWithCustomSourceSome() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        assertEquals("getTargetLocalesWithCustomSource() should return a set "
                   + "of each locale id that has an associated custom source",
                     2,
                     cs.getTargetLocalesWithCustomSource().size());
        assertTrue("getTargetLocalesWithCustomSource() should return all the "
                 + "target locales that have an associated custom source",
                   cs.getTargetLocalesWithCustomSource().contains(locFR));
        assertTrue("getTargetLocalesWithCustomSource() should return all the "
                 + "target locales that have an associated custom source",
                   cs.getTargetLocalesWithCustomSource().contains(locFR));
        assertFalse("getTargetLocalesWithCustomSource() should return only the "
                  + "target locales that have an associated custom source",
                    cs.getTargetLocalesWithCustomSource().contains(locDE));
    }

    @Test
    public void getSetSourcePropertyByLocaleNull() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to default
        cs.setSourceProperty(null, dp1);
        assertSame("setSourceProperty() should apply a property to the default"
                 + " source when given a null target locale",
                     dp1, cs.getSourceProperty(null, "property1"));

        //check property not on french
        assertNull("setSourceProperty() should not change properties for custom "
                 + "locales when a null target locale is given",
                   cs.getSourceProperty(locFR, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleCustomSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to french
        cs.setSourceProperty(locFR, fp1);

        assertSame("setSourceProperty() should set a property for the custom "
                 + "source if one exists for the given target locale",
                   fp1, cs.getSourceProperty(locFR, "property1"));
        assertNull("setSourceProperty() should not change properties for the "
                 + "default source when there is a custom source for the given "
                 + "target locale",
                   cs.getSourceProperty(null, "property1"));
    }

    @Test
    public void getSetSourcePropertyByLocaleNoCustomSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        //add property to spanish
        cs.setSourceProperty(locES, dp1);

        assertFalse("setSourceProperty() should not cause a new custom source "
                  + "to be created",
                    cs.hasCustomSource(locES));
        assertSame("properties of the default source should be set by "
                 + "setSourceProperty() when there is no custom source for the "
                 + "given target locale",
                   dp1, cs.getSourceProperty(null, "property1"));
        assertSame("properties of the default source should be returned when there "
                 + "is no custom source for the given target locale",
                   dp1, cs.getSourceProperty(locES, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNull() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(locFR, fp1);

        cs.removeSourceProperty(null, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when a null target locale is given",
                   cs.getSourceProperty(null, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from custom sources when a null target locale is given",
                      cs.getSourceProperty(locFR, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleCustomSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(locFR, fp1);

        cs.removeSourceProperty(locFR, "property1");
        assertNull("removeSourceProperty() should remove properties from a "
                 + "custom source when one exists for the given target locale",
                   cs.getSourceProperty(locFR, "property1"));

        assertNotNull("removeSourceProperty() should not remove properties "
                    + "from the default source when a custom source exists for "
                    + "the given target locale",
                      cs.getSourceProperty(null, "property1"));
    }


    @Test
    public void removeSourcePropertyByLocaleNoCustomSource() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(locFR, fp1);

        cs.removeSourceProperty(locES, "property1");
        assertNull("removeSourceProperty() should remove properties from the "
                 + "default source when no custom source exists for the given "
                 + "target locale",
                   cs.getSourceProperty(null, "property1"));
    }

    @Test
    public void getSourcePropertyNamesByLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(locFR, fp1);
        cs.setSourceProperty(locFR, fp2);

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when a null locale is given",
                     1, cs.getSourcePropertyNames(null).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for a custom source when one exists for the given locale",
                     2, cs.getSourcePropertyNames(locFR).size());

        assertEquals("getSourcePropertyNames() should return property names "
                   + "for the default source when there is no custom source for "
                   + "the given locale",
                     1, cs.getSourcePropertyNames(locES).size());
    }

    @Test
    public void getSourcePropertyNamesByLocaleNoProperties() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        assertEquals("getSourcePropertyNames() should return an empty set if "
                   + "there are no properties for the source of the given "
                   + "target locale",
                     0, cs.getSourcePropertyNames(locFR).size());
    }

    @Test
    public void hasSourcePropertyByLocale() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, null, null);

        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(locFR, fp2);

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                   cs.hasSourceProperty(null, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                 + "default source when a null target locale is given",
                    cs.hasSourceProperty(null, "property2"));

        assertFalse("hasSourceProperty() should check for properties of the "
                  + "custom source if one exists for the given target locale",
                    cs.hasSourceProperty(locFR, "property1"));
        assertTrue("hasSourceProperty() should check for properties of the "
                  + "custom source if one exists for the given target locale",
                   cs.hasSourceProperty(locFR, "property2"));

        assertTrue("hasSourceProperty() should check for properties of the "
                 + "default source if there is no custom source for the given "
                 + "target locale",
                   cs.hasSourceProperty(locES, "property1"));
        assertFalse("hasSourceProperty() should check for properties of the "
                  + "default source if there is no custom source for the given "
                  + "target locale",
                    cs.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateSourcePropertyFromDefaultToOne() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(null, dp1);
        cs.propagateSourceProperty(null, locFR, "property1", true);
        assertTrue("property should be copied from default source to the custom"
                 + " source of the given locale if present",
                   cs.hasSourceProperty(locFR, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromCustomToOne() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(locFR, dp1);
        cs.propagateSourceProperty(locFR, null, "property1", true);
        assertTrue("property should be copied from custom source to the default source",
                   cs.hasSourceProperty(null, "property1"));
        assertFalse("the property should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromDefaultToAll() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(null, dp1);
        cs.propagateSourceProperty(null, "property1", true);
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   cs.hasSourceProperty(locFR, "property1"));
        assertTrue("property should be copied from default source to all other"
                 + " sources",
                   cs.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateSourcePropertyFromCustomToAll() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(locFR, fp1);
        cs.propagateSourceProperty(locFR, "property1", true);
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   cs.hasSourceProperty(null, "property1"));
        assertTrue("property should be copied from french source to all other"
                 + " sources",
                   cs.hasSourceProperty(locES, "property1"));
    }

    @Test
    public void propagateAllSourcePropertiesFromDefaultToOne() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(null, dp2);
        cs.propagateAllSourceProperties(null, locFR, true);
        assertTrue("all properties should be copied from default source to the custom"
                 + " source of the given locale if present",
                   cs.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to the custom"
                 + " source of the given locale if present",
                   cs.hasSourceProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromCustomToOne() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(locFR, dp1);
        cs.setSourceProperty(locFR, dp2);
        cs.propagateAllSourceProperties(locFR, null, true);
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   cs.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from french source to the "
                 + "default source",
                   cs.hasSourceProperty(locFR, "property2"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property1"));
        assertFalse("properties should only be copied to the source for the "
                  + "specified target locale",
                    cs.hasSourceProperty(locES, "property2"));
    }

//TODO    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting);
    @Test
    public void propagateAllSourcePropertiesFromDefaultToAll() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(null, dp1);
        cs.setSourceProperty(null, dp2);
        cs.propagateAllSourceProperties(null, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locFR, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locFR, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locES, "property2"));
    }

    @Test
    public void propagateAllSourcePropertiesFromCustomToAll() {
        cs = createCustomSources(DEFAULT_SOURCE, FR_SOURCE, ES_SOURCE, null);
        cs.setSourceProperty(locFR, dp1);
        cs.setSourceProperty(locFR, dp2);
        cs.propagateAllSourceProperties(locFR, true);
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(null, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(null, "property2"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locES, "property1"));
        assertTrue("all properties should be copied from default source to "
                 + "all other sources",
                   cs.hasSourceProperty(locES, "property2"));
    }

//TODO    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting);
//TODO    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting);
//TODO    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting);
//TODO    public void propagateAllSourceAnnotations(LocaleId from, boolean overwriteExisting);


//TODO think about null pointer exceptions for each of the classes that take a LocaleId



    private VariantSources createCustomSegmentedSources() {

        VariantSources cSources = new VariantSources(new TextUnit4(TU1, DEFAULT_SEG_1));
        cSources.getSource(null).getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(DEFAULT_SEG_2)), " a ");
        cSources.createSource(new TextContainer(FR_SEG_1), locFR, true);
        cSources.getSource(locFR).getSegments().append(new Segment(SEGMENT_2_ID, new TextFragment(FR_SEG_2)), " b ");

        return cSources;
    }

    //utility method to create a set of custom sources with an associated default source
    private VariantSources createCustomSources(String defaultSourceString,
                                              String frenchSource,
                                              String spanishSource,
                                              String germanSource) {

        VariantSources cSources = new VariantSources(new TextUnit4(TU1, defaultSourceString) );
        if (frenchSource != null) cSources.createSource(new TextContainer(frenchSource), locFR, true);
        if (spanishSource != null) cSources.createSource(new TextContainer(spanishSource), locES, true);
        if (germanSource != null) cSources.createSource(new TextContainer(germanSource), locDE, true);

        return cSources;
    }

}
