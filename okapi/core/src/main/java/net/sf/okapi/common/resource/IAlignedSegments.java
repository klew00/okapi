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

import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;

/**
 * EXPERIMENTAL interface. Do not use yet.
 * Provides the methods to access all the source and target segments of a {@link ITextUnit}.
 * The methods of this interface try to automatically preserve the one-to-one match between
 * source segments and all the target segments.
 * To create an instance of this interface, use the method {@link ITextUnit#getSegments()}.
 */
public interface IAlignedSegments extends Iterable<Segment> {

    //TODO consider whether it would be more appropriate to retrieve an
    // aligned segment object for a specific target locale, as this would
    // allow easy selection of the appropriate source.
    // An alternative would be to have an AlignedSegments object aware of the
    // difference between default and variant source, and perform operations
    // as appropriate, or just to always request the source from the parent
    // ITextUnit

	/**
	 * Gets an iterator for the source segments of this text unit.
	 * This iterator does not iterate through non-segment parts of the content.
	 * @return an iterator for the source segments of this text unit.
	 */
	@Override
	public Iterator<Segment> iterator();

	/**
	 * Adds a segment at the end of the source content.
	 * Empty corresponding segments are created in all targets in this text unit.
	 * @param srcSeg the source segment to add.
	 */
	public void append (Segment srcSeg);
	
	/**
	 * Adds a segment at the end of the source content, along with a corresponding target segment.
	 * Empty corresponding segments are created in all other targets in this text unit.
	 * @param srcSeg the source segment to add.
	 * @param trgSeg the corresponding segment for the given target. 
	 * @param trgLoc the locale id of the target.
	 */
	public void append (Segment srcSeg,
		Segment trgSeg,
		LocaleId trgLoc);
	
	/**
	 * Inserts a given source segment at the specified position in the list of segments.
	 * A corresponding empty segment is created at the same position in each target.
	 * @param index the segment index position.
	 * @param srcSeg the segment to insert.
	 */
	public void insert (int index,
		Segment srcSeg);
	
	/**
	 * Inserts a given source segment and its corresponding target at a given position.
	 * A corresponding empty segment is created at the same position in each of the remaining targets.
	 * @param index  the segment index position.
	 * @param srcSeg the source segment to insert.
	 * @param trgSeg the corresponding target segment.
	 * @param trgLoc the locale of the target segment.
	 */
	public void insert (int index,
		Segment srcSeg,
		Segment trgSeg,
		LocaleId trgLoc);
	
	/**
	 * Replaces a source segment at a given position by a clone of a given segment.
	 * If the id of the new segment is different from the current one, the target segments corresponding
	 * to the replaced segment have their id updated to match the one of the new segment.
	 * @param index the segment index position.
	 * @param srcSeg the new source segment to place at the position.
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public void setSource (int index,
		Segment srcSeg);

	/**
	 * Replaces a target segment at a given position.
	 * <p>If the given target does not exists yet, one segment and empty is created automatically.
	 * <p>If the id of the new target segment is different from the current one, the source and other
	 * target segments corresponding to the replaced segment have their id updated to match the one of the new segment.
	 * @param index the index position.
	 * @param trgSeg the new target segment to place at the position.
	 * @param trgLoc the locale of the target segment.
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public void setTarget (int index,
		Segment trgSeg,
		LocaleId trgLoc);

	/**
	 * Removes the given segment and its corresponding matches from the source and all the targets.
	 * @param seg the segment to remove.
	 * @return true if remove success 
	 */
	public boolean remove (Segment seg);
	
	/**
	 * Gets the source segment at a given position.
	 * The first segment has the index 0, the second has the index 1, etc.
	 * @param index the segment index of the segment to retrieve.
	 * @return the segment at the given position.
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public Segment getSource (int index);
	
	/**
	 * Gets the target segment corresponding to a given source segment.
	 * This always returns a segment: If the target does not exists one is created.
	 * If the segment does not exists one is created at the end of the target content.
	 * @param seg the source (or other target) segment for which a corresponding target segment is requested.
	 * @param trgLoc the target to look up.
	 * @return the corresponding target segment (may be empty).
	 */
	public Segment getCorrespondingTarget (Segment seg,
		LocaleId trgLoc);
	
	/**
	 * Gets the source segment corresponding to a given target segment.
	 * <p>If no corresponding source segment is found an empty one is added at the end of the 
	 * container and returned as the new corresponding segment.
	 * @param trgSeg the target segment for which the corresponding source segment is requested. 
	 * @return the corresponding source segment.
	 */
	public Segment getCorrespondingSource (Segment trgSeg);
	
	/**
	 * Aligns all the segments listed in the aligned pairs for given locale.
	 * @param alignedSegmentPairs the list of pairs to align
	 * @param trgLoc the locale of the target to work with.
	 */
	public void align (List<AlignedPair> alignedSegmentPairs,
		LocaleId trgLoc);

	/**
	 * Aligns all the segments for the given locale. Assumes the same number of 
	 * source and target segments otherwise an exception is thrown.
	 * @param trgLoc the locale of the target to work with.
	 */
	public void align (LocaleId trgLoc);

	/**
	 * Aligns all the segments for given locale by collapsing all 
	 * segments into one. 
	 * @param trgLoc the locale of the target to work with.
	 */
	public void alignCollapseAll (LocaleId trgLoc);

	
	/**
	 * Splits a given source segment into two.
	 * All target alignment statuses are updated, and an empty segment is created for each target
	 * to match with the new source segment.
	 * @param srcSeg the source segment to split.
	 * @param splitPos the position where to split.
	 * @return the new source segment created, or null if none was created.
	 */
	public Segment splitSource (Segment srcSeg,
		int splitPos);

	/**
	 * Splits a given target segment into two.
	 * All target alignment statuses are updated, and an empty segment is created for each
	 * other target as well as for the source to match with the new target segment.
	 * @param trgLoc the target locale to work on.
	 * @param trgSeg the targets segment.
	 * @param splitPos the position where to split.
	 * @return the new target segment created, or null if none was created.
	 */
	public Segment splitTarget (LocaleId trgLoc,
		Segment trgSeg,
		int splitPos);
	
	/**
	 * Joins (in source and all targets) the segment for a given segment's id to all the parts between
	 * that segment and the next, as well as the next segment. 
	 * @param seg a segment holding the id to use for the join. 
	 */
	public void joinWithNext (Segment seg);
	
	/**
	 * Joins all segments for all source and target contents.
	 */
	public void joinAll ();
	
	/** 
	 * Gets the status of the alignment for this text unit.
	 * @return the status of the alignment for this text unit.
	 */
	public AlignmentStatus getAlignmentStatus ();
	
	
	/**
	 * Segments the source content based on the rules provided by a given ISegmenter.
	 * <p>The targets are not modified.
	 * @param segmenter the segmenter to use to create the segments.
	 */
	public void segmentSource (ISegmenter segmenter);
	
	/**
	 * Segments the specified target content based on the rules provided by a given ISegmenter.
	 * <p>If the given target does not exist one is created.
	 * @param segmenter the segmenter to use to create the segments.
	 * @param targetLocale {@link LocaleId} of the target we want to segment.
	 */
	public void segmentTarget (ISegmenter segmenter,
		LocaleId targetLocale);
	
}
