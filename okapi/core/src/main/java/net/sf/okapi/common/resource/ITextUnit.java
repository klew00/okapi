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

import java.util.List;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;

public interface ITextUnit extends INameable, IReferenceable {

	/**
	 * Adds a segment at the end of the source content.
	 * Empty corresponding segments are created in all target in this text unit.
	 * @param srcSeg the source segment to add.
	 */
	public void addSegment (Segment srcSeg);
	
	/**
	 * Adds a segment at the end of the source content, along with a corresponding target segment.
	 * Empty corresponding segments are created in all other targets in this text unit.
	 * @param srcSeg the source segment to add.
	 * @param trgSeg the corresponding segment for the given target. 
	 * @param trgLoc the locale id of the target.
	 */
	public void addSegment (Segment srcSeg,
		Segment trgSeg,
		LocaleId trgLoc);
	
	//addSegment(int index, Segment srcSeg) - inserts the given segment at the specified position in the list, empty target segments are created at the same position in targets 
	//addSegment(int index, Segment srcSeg, Segment trgSeg, LocaleId? trgLoc) 
	//setSrcSegment(int index, Segment src) - replaces the segment at given index with the given segment 
	//Maybe not: setSrcSegment(int index, TextFragment? tf) - replaces the content of the segment at index with the given TF 
	
	//setTrgSegment(int index, Segment src, LocaleId? trgLoc) - same for the target segment 
	//setTrgSegment(int index, TextFragment? tf, LocaleId? trgLoc)

	/**
	 * Removes the given segment and its corresponding matches from the source and all the targets.
	 * @param seg the segment to remove. 
	 */
	public void removeSegment (Segment seg);
	
	/**
	 * Gets the target segment corresponding to a given source segment.
	 * This always returns a segment: if the segment does not exists it is created in target content.
	 * @param srcSeg the source segment of the corresponding segment to look up.
	 * @param trgLoc the target to look up.
	 * @return the corresponding target segment (may be empty).
	 */
	public Segment getCorrespondingTarget (Segment srcSeg,
		LocaleId trgLoc);
	
//TODO: what about the cases where we have s0-t1?
//Do we create an empty source segment 9and corresponding ones in all other targets?	
	/**
	 * Gets the source segment corresponding to a given target segment.
	 * @param trgSeg the target segment of the corresponding source segment to look for. 
	 * @return the corresponding source segment.
	 */
	public Segment getCorrespondingSource (Segment trgSeg);
	
	/**
	 * Joins all segments for all source and target contents.
	 */
	public void joinAll ();
	
	/**
	 * Collapses all the segments listed in the aligned pairs for given locale.
	 * @param alignedSegmentPairs the list of pairs to align
	 * @param trgLoc the locale of the target to work with.
	 */
	public void align (List<AlignedPair> alignedSegmentPairs,
		LocaleId trgLoc);

	
	/**
	 * Splits a given source segment into two.
	 * All target alignment statuses are updated, and an empty segment is created for each target to match with the new source segment.
	 * @param srcSeg the source segment to split.
	 * @param splitPos the position where to split.
	 */
	public void splitSource (Segment srcSeg,
		int splitPos);

	/**
	 * Splits a given target segment into two.
	 * All target alignment statuses are updated, and an empty segment is created for each other target as well as for the source
	 * to match with the new target segment.
	 * @param trgLoc the target locale to work on.
	 * @param trgSeg the targets segment.
	 * @param splitPos the position where to split.
	 */
	public void splitTarget (LocaleId trgLoc,
		Segment trgSeg,
		int splitPos);
	
	/**
	 * Merges two source segments (and anything in between).
	 * This also merges all the corresponding targets.
	 * @param srcSeg1 the first segment to merge. This segment keeps it's ID. 
	 * @param srcSeg2 the second segment to merge with the first.
	 */
	public void mergeSource (Segment srcSeg1,
		Segment srcSeg2);

	/**
	 * Collapses all segments in source and all targets.
	 * The remaining segment keep the information of the previous first one.
	 */
	public void collapseAll ();

	/** 
	 * Gets the status of the alignment for this text unit.
	 * @return the status of the alignment for this text unit.
	 */
	//TODO: should be an enum return
	public int getAlignemntStatus ();
	
	
	//==== Existing method
	
	/**
	 * Gets the source object for this TextUnit (a {@link TextContainer} object).
	 * @return the source object for this TextUnit.
	 */
	public TextContainer getSource ();

	/**
	 * Sets the source object for this TextUnit. Any existing source object is overwritten.
	 * @param textContainer the source object to set.
	 * @return the source object that has been set.
	 */
	public TextContainer setSource (TextContainer textContainer);

    /**
	 * Gets the target object for this TextUnit for a given locale.
	 * @param locId the locale to query.
	 * @return the target object for this text unit for the given locale,
	 * or null if it does not exist.
	 */
	public TextContainer getTarget (LocaleId locId);

    /**
	 * Sets the target object for this TextUnit for a given locale.
	 * Any existing target object for the given locale is overwritten.
	 * To set a target object based on the source, use the
	 * {@link #createTarget(LocaleId, boolean, int)} method.
	 * @param locId the target locale.
	 * @param text the target object to set.
	 * @return the target object that has been set.
	 */
	public TextContainer setTarget (LocaleId locId,
		TextContainer text);

    /**
	 * Removes a given target object from this TextUnit.
	 * @param locId the target locale to remove.
	 */
	public void removeTarget (LocaleId locId);

    /**
	 * Indicates if there is a target object for a given locale for this TextUnit.
	 * @param locId the locale to query.
	 * @return true if a target object exists for the given locale, false otherwise.
	 */
	public boolean hasTarget (LocaleId locId);

    /**
	 * Creates or get the target for this TextUnit.
	 * @param locId the target locale.
	 * @param overwriteExisting true to overwrite any existing target for the given locale.
	 * False to not create a new target object if one already exists for the given locale.
	 * @param creationOptions creation options:
	 * <ul><li>CREATE_EMPTY: Create an empty target object.</li>
	 * <li>COPY_CONTENT: Copy the text of the source (and any associated in-line code).</li>
	 * <li>COPY_PROPERTIES: Copy the source properties.</li>
	 * <li>COPY_ALL: Same as (COPY_CONTENT|COPY_PROPERTIES).</li></ul>
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
	 * @param locId the locale to set.
	 * @param content the new content to set.
	 * @return the new content for the given target locale for this text unit. 
	 */
	public TextFragment setTargetContent (LocaleId locId,
		TextFragment content);
	
	/**
	 * Segments the source content based on the rules provided by a given ISegmenter.
	 * <p>This methods also stores the boundaries for the segments so they can be re-applied later.
	 * for example when calling {@link #synchronizeSourceSegmentation(LocaleId)}.
	 * @param segmenter the segmenter to use to create the segments.
	 */
	public void createSourceSegments (ISegmenter segmenter);
	
	/**
	 * Segments the specified target content based on the rules provided by a given ISegmenter.
	 * <p>This method may cause the source and target segments to be desynchronized, that is:
	 * That each source segment may or may not be aligned with a corresponding target segment.
	 * You can associate a target-specific segmentation for the source using
	 * {@link #setSourceSegmentationForTarget(LocaleId, List)}.
	 * @param segmenter the segmenter to use to create the segments.
	 * @param targetLocale {@link LocaleId} of the target we want to segment.
	 */
	public void createTargetSegments (ISegmenter segmenter,
		LocaleId targetLocale);
	

}
