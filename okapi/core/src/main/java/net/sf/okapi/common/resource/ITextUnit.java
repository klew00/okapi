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

import net.sf.okapi.common.LocaleId;

/**
 * EXPERIMENTAL class. Do not use yet.
 *
 * Interface for the basic unit of extraction from a filter and also the resource
 * associated with the filter event TEXT_UNIT.
 *
 * The TextUnit object holds the extracted source text, all its properties and
 * annotations, and any target corresponding data.
 *
 * If adjustments to the source are required for different target locales, these
 * can be made using the {@link IVariantSources} interface, accessible through
 * {@link ITextUnit#getVariantSources()}
 *
 * Where adjusted sources exist, methods in this interface should have
 * their effects on the default source, however a specific implementation may
 * provide a method to select one of the variant sources that methods would then
 * use. The main source should always be used before such a method is called.
 *
 */
public interface ITextUnit extends INameable, IReferenceable {

    /**
     * Indicates if the source text of this TextUnit is empty.
     * @return true if the source text of this TextUnit is empty, false otherwise.
     */
    public boolean isEmpty ();

    /**
     * Gets the source object for this text unit (a {@link TextContainer} object).
     * @return the source object for this text unit.
     */
    public TextContainer getSource ();

    /**
     * Sets the source object for this TextUnit. Any existing source object is overwritten.
     * @param textContainer the source object to set.
     * @return the source object that has been set.
     */
    public TextContainer setSource (TextContainer textContainer);

    /**
     * Indicates whether there are any variant sources used in this text unit
     * @return true if there are one or more variant sources, false if there are none
     */
    public boolean hasVariantSource();

    /**
     * Returns the variant sources (an {@link IVariantSources} object) for this text unit
     * @return the variant sources used in this text unit
     */
    public IVariantSources getVariantSources();

    /**
     * Gets the target object for this text unit for a given locale.
     * If the target does not exists one is created automatically, and contains a copy
     * of the source with all segments empty.
     * <p>Calling this method is the same as calling <code>createTarget(locId, false, IResource.COPY_SEGMENTS)</code>
     * @param locId the locale to query.
     * @return the target object for this text unit for the given locale. Never returns null.
     * (Before M10 if the target did not exist none was created and the return was null).
     * @see #createTarget(LocaleId, boolean, int)
     */
    public TextContainer getTarget_DIFF (LocaleId locId);

    /**
     * Sets the target object for this text unit for a given locale.
     * <p>If the target does not exists, one is created.
     * Any existing content for the given locale is overwritten.
     * To set a target object based on the source, use the
     * {@link #createTarget(LocaleId, boolean, int)} method.
     * @param locId the target locale.
     * @param text the target content to set.
     * @return the target content that has been set.
     */
	public TextContainer setTarget (LocaleId locId,
		TextContainer text);

    /**
     * Removes a given target object from this text unit.
     * If the given locale does not exist in this text unit nothing happens.
     * Variant source for this target locale will not be affected.
     * @param locId the target locale to remove.
     */
    public void removeTarget (LocaleId locId);

    /**
     * Indicates if there is a target object for a given locale for this text unit.
     * @param locId the locale to query.
     * @return true if a target object exists for the given locale, false otherwise.
     */
    public boolean hasTarget (LocaleId locId);

    /**
     * Creates or get the target for this TextUnit.
     *
     * If a variant source exists for the target locale, creationOptions apply
     * to the variant source.
     *
     * @param locId the target locale.
     * @param overwriteExisting true to overwrite any existing target for the given locale.
     * False to not create a new target object if one already exists for the given locale.
     * @param creationOptions creation options:
     * <ul><li>CREATE_EMPTY: Create an empty target object.</li>
     * <li>COPY_CONTENT: Copy the text of the source (and any associated in-line code).</li>
     * <li>COPY_PROPERTIES: Copy the source properties.</li>
     * <li>COPY_SEGMENTS: Copy the source segmentation.</li>
     * <li>COPY_ALL: Same as (COPY_CONTENT|COPY_PROPERTIES|COPY_SEGMENTS).</li></ul>
     * @return the target object that was created, or retrieved.
     */
    public TextContainer createTarget (LocaleId locId,
                                       boolean overwriteExisting,
                                       int creationOptions);

	/**
	 * Sets the content of the source for this TextUnit.
	 * @param content the new content to set.
	 * @return the new content of the source for this TextUnit. 
	 */
	public TextFragment setSourceContent (TextFragment content);

	/**
	 * Sets the content of the target for a given locale for this TextUnit.
	 * <p>If the target does not exists, one is created.
	 * Any existing content for the given locale is overwritten.
	 * To set a target object based on the source, use the
	 * {@link #createTarget(LocaleId, boolean, int)} method.
	 * @param locId the locale to set.
	 * @param content the new content to set.
	 * @return the new content for the given target locale for this text unit. 
	 */
	public TextFragment setTargetContent (LocaleId locId,
		TextFragment content);

	/**
	 * Creates a new {@link IAlignedSegments} object to access and manipulate the segments of this text unit.
	 * @return a new {@link IAlignedSegments} object.
	 */
	IAlignedSegments getSegments ();
	
	/**
	 * Gets the segments for the source. Un-segmented content return a single segment.
	 * @return an object implementing ISegments for the source content. 
	 */
	public ISegments getSourceSegments ();

	/**
	 * Get the segments for a given target. Un-segmented content return a single segment.
	 * <p>If the target does not exists, one is created.
	 * @param trgLoc the locale of the target to retrieve.
	 * @return an object implementing ISegments for the given target content.
	 */
	public ISegments getTargetSegments (LocaleId trgLoc);

	/**
	 * Gets the source segment for a given segment id.
	 * <p>If the segment does not exists, one is created if <code>createIfNeeded</code> is true.
	 * @param segId the id of the segment to retrieve.
	 * @param createIfNeeded true to append a segment at the end of the content and return it 
	 * if the segment does not exist yet. False to return null when the segment does not exists.
	 * @return the found or created segment, or null.
	 */
	public Segment getSourceSegment (String segId,
		boolean createIfNeeded);
	
	/**
	 * Gets the segment for a given segment id in a given target.
	 * <p>If the target does not exists, one is created.
	 * <p>If the segment does not exists, one is created if <code>createIfNeeded</code> is true.
	 * @param trgLoc the target locale to look up.
	 * @param segId the id of the segment to retrieve.
	 * @param createIfNeeded true to append a segment at the end of the target content and 
	 * return it if the segment does not exist yet. False to return null when the segment
	 * does not exists.
	 * @return the found or created segment, or null.
	 */
	public Segment getTargetSegment (LocaleId trgLoc,
		String segId,
		boolean createIfNeeded);
	
}
