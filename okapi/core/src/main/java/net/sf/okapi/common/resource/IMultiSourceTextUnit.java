/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import java.util.Set;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * EXPERIMENTAL interface. Do not use yet.
 * 
 * Extends ITextUnit to allow the creation and manipulation of different source
 * versions for different locales.
 *
 * Custom sources are created using createCustomSource and removed using
 * removeCustomSource. Custom sources can be manipulated with methods from
 * ITextUnit after setting the target locale with setActiveTargetLocale. Methods
 * in this interface do not require the active target locale to be set.
 *
 * @author David Mason, dr.d.mason@gmail.com
 */
public interface IMultiSourceTextUnit extends ITextUnit {

    /**
     * Sets all this text unit's fields to values from the given text unit,
     * effectively making this text unit a clone of the original but with the
     * capacity to deal with multiple sources.
     *
     * @param original an ITextUnit object to copy
     * @throws NullPointerException if original is null
     */
    public void copyFromSingleSource(ITextUnit original) throws NullPointerException;

    /**
     * Sets the target locale for the source that will be accessed in the standard
     * ITextUnit methods. Default source will be used if no custom source is
     * associated with the given locale, or if the given locale is null.
     * @param targetLocale the locale for the source to be accessed
     */
    public void setActiveTargetLocale(LocaleId targetLocale);

    /**
     * Indicates if the source text for the given target locale is empty.
     * @param targetLocale the target locale for the source to check
     * @return true if the source text (may be the default source) for the given
     *         locale is empty, false otherwise
     */
    public boolean isSourceEmpty(LocaleId targetLocale);
    
    /**
     * Creates a custom source for a given target locale, using the content of the
     * default source.
     *
     * @param targetLocale the target locale that uses the new source
     * @param overwriteExisting overwrites any existing source associated with
     *                          the locale if true
     * @return the newly created source, or the existing source if it was not
     *         overwritten, or null if the
     */
    public TextContainer createCustomSource(LocaleId targetLocale, boolean overwriteExisting);
    //TODO add creation options


    /**
     * Creates a custom source for a given locale, using the provided text.
     * @param sourceText the text to include in this source
     * @param targetLocale the target locale that uses the new source
     * @param overwriteExisting overwrites any existing source associated with
     *                          the locale if true
     * @return the newly created source
     */
    public TextContainer createCustomSource(String sourceText,
                                            LocaleId targetLocale,
                                            boolean overwriteExisting);
    //TODO add creation options


    /**
     * Gets the source object (a {@link TextContainer} object) used for the
     * given target locale. Returns the default source object if the target
     * locale has no custom source.
     *
     * @param targetLocale the target locale used by the source.
     * @return the source used by the given locale. May be the default source.
     */
    public TextContainer getSource(LocaleId targetLocale);

    /**
     * Sets the source object to use for the given target locale. Any existing
     * custom source object for the target locale will be overwritten.
     * 
     * @param targetLocale the target locale that will use the given source object
     * @param textContainer the source object to use for the given target locale
     * @return the source object that has been set
     */
    public TextContainer setCustomSource(LocaleId targetLocale, TextContainer textContainer);

    /**
     * Removes any custom source used for the given target locale. Any associated
     * targets will be associated with the default locale, which may lead to
     * misalignment of segments between the target and default source.
     *
     * @param targetLocale the locale for which to remove custom source
     */
    public void removeCustomSource(LocaleId targetLocale);

    /**
     * Indicates whether there are any custom sources used in this text unit
     * @return true if there are one or more custom sources, false if there are none
     */
    public boolean hasCustomSource();

    /**
     * Indicates whether there is a custom source for the given locale
     *
     * @param targetLocale the locale to check for custom sources
     * @return true if there is a custom source object for the given locale,
     *         false otherwise. Returns false if null target locale is given.
     * @throws NullPointerException if targetLocale is null
     */
    public boolean hasCustomSource(LocaleId targetLocale) throws NullPointerException;

    /**
     * Sets the content of the custom source for the given target locale. Creates
     * a custom source if one does not exist for the given locale. Replaces any
     * existing content for the source of the given locale.
     *
     * TODO discuss adding an overwriteExisting flag
     *
     * @param targetLocale the locale for which to set the source content
     *                     null to set for default source
     * @param content the content to use for the source of the given locale
     * @return the source content that was set.
     * @throws NullPointerException if targetLocale is null
     */
    public TextFragment setCustomSourceContent(LocaleId targetLocale, TextFragment content) throws NullPointerException;

    /**
     * Creates a new {@link IAlignedSegments} object to access and manipulate the
     * segments of this text unit for a given target locale.
     *
     * @param targetLocale the target locale for the segments
     * @return a new {@link IAlignedSegments} object
     */
    public IAlignedSegments getSegments(LocaleId targetLocale);
    //TODO consider the implications of using a locale with no custom source
    //     as having multiple targets using default source could cause alignment
    //     problems.

    /**
     * Gets the segments for the source used for the given target locale.
     * Un-segmented content return a single segment.
     *
     * @param targetLocale the target locale for which to return source segments
     * @return an object implementing ISegments for the source content.
     */
    public ISegments getSourceSegments(LocaleId targetLocale);

    /**
     * Gets the source segment for a given target locale and segment id.
     * <p>If the segment does not exists, one is created if <code>createIfNeeded</code> is true.
     * TODO discuss how to handle target locales that use the default source
     *
     * @param targetLocale the target locale for which to return a source segment
     * @param segId the id of the segment to retrieve
     * @param createIfNeeded true to append a segment at the end of the content
     *                       and return it if the segment does not exist yet.
     *                       False to return null when the segment does not exists.
     * @return the retrieved or created segment, or null if none was found or created.
     */
    public Segment getSourceSegment(LocaleId targetLocale, String segId,
                                    boolean createIfNeeded);


    /**
     * Lists all target locales for which there is a custom source
     * Does not include a locale for the default source.
     * @return a set of all target locales that use a custom source.
     */
    public Set<LocaleId> getTargetLocalesWithCustomSource();


    
    //TODO consider adding COPY_OPTIONS to the following methods if there is any call for it

    /**
     * Copies a single property from one source version to another source version.
     *
     * @param from the target locale of the source in which the property is located,
     *             null to use the default source
     * @param to the target locale of the source to which the property will be copied,
     *           null to use the default source
     * @param propertyName the name of the property to copy
     * @param overwriteExisting true to overwrite an existing property if present
     */
    public void propagateSourceProperty(LocaleId from, LocaleId to, String propertyName, boolean overwriteExisting);
    
    /**
     * Copies a single property from one source version to all other source versions.
     *
     * @param from the target locale of the source in which the property is located,
     *             null to use the default source
     * @param propertyName the name of the property to copy
     * @param overwriteExisting true to overwrite an existing property if present
     */
    public void propagateSourceProperty(LocaleId from, String propertyName, boolean overwriteExisting);

    /**
     * Copies all properties from one source version to another source version.
     *
     * @param from the target locale of the source to copy properties from,
     *             null to use the default source
     * @param to the target locale of the source to copy properties to,
     *           null to use the default source
     * @param overwriteExisting true to overwrite any existing properties if present
     */
    public void propagateAllSourceProperties(LocaleId from, LocaleId to, boolean overwriteExisting);


    /**
     * Copies all properties from one source version to all other source versions.
     *
     * @param from the target locale of the source to copy properties from,
     *             null to use the default source
     * @param overwriteExisting true to overwrite any existing properties if present
     */
    public void propagateAllSourceProperties(LocaleId from, boolean overwriteExisting);

    
    /**
     * Copies a single annotation from one source version to another source version.
     *
     * @param from the target locale of the source in which the annotation is located,
     *             null to use the default source
     * @param to the target locale of the source to which the annotation will be copied,
     *           null to use the default source
     * @param type the type of the annotation to copy
     * @param overwriteExisting true to overwrite an existing annotation if present
     */
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, LocaleId to, Class<A> type, boolean overwriteExisting);

    /**
     * Copies a single annotation from one source version to all other source versions.
     *
     * @param from the target locale of the source in which the annotation is located,
     *             null to use the default source
     * @param type the type of the annotation to copy
     * @param overwriteExisting true to overwrite an existing annotation if present
     */
    public <A extends IAnnotation> void propagateSourceAnnotation(LocaleId from, Class<A> type, boolean overwriteExisting);

    /**
     * Copies all annotations from one source version to another source version.
     *
     * @param from the target locale of the source to copy annotations from,
     *             null to use the default source
     * @param to the target locale of the source to which annotations will be copied,
     *           null to use the default source
     * @param overwriteExisting true to overwrite any existing annotation if present
     */
    public void propagateAllSourceAnnotations(LocaleId from, LocaleId to, boolean overwriteExisting);

    /**
     * Copies all annotations from one source version to all other source versions.
     *
     * @param from the target locale of the source to copy annotations from,
     *             null to use the default source
     * @param overwriteExisting true to overwrite any existing annotation if present
     */
    public void propagateAllSourceAnnotations(LocaleId from, boolean overwriteExisting);


    //TODO decide whether to keep these 5 methods, or perhaps move them to INameable if appropriate

    /**
     * Gets the source property for a given name and target locale.
     * @param targetLocale the target locale of the source
     * @param name The name of the source property to retrieve.
     * @return The property or null if it does not exist.
     */
    public Property getSourceProperty (LocaleId targetLocale, String name);

    /**
     * Sets a source property. If a property already exists it is overwritten.
     * @param targetLocale the target locale of the source
     * @param property The new property to set.
     * @return The property that has been set.
     */
    public Property setSourceProperty (LocaleId targetLocale, Property property);

    /**
     * Removes a source property of a given name. If the property does not exists
     * nothing happens.
     * @param targetLocale the target locale of the source
     * @param name The name of the property to remove.
     */
    public void removeSourceProperty (LocaleId targetLocale, String name);

    /**
     * Gets the names of all the source properties for this resource.
     * @param targetLocale the target locale of the source
     * @return All the names of the source properties for this resource.
     */
    public Set<String> getSourcePropertyNames (LocaleId targetLocale);

    /**
     * Indicates if a source property exists for a given name.
     * @param targetLocale the target locale of the source
     * @param name The name of the source property to query.
     * @return True if a source property exists, false otherwise.
     */
    public boolean hasSourceProperty (LocaleId targetLocale, String name);




    //TODO consider methods such as:
    //     - appending text to all sources at once
    //     - comparing sources to determine which are identical
    //           to count unique sources
    //           to modify all identical sources at once

}
