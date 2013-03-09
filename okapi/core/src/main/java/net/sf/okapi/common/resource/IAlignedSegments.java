/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiMisAlignmentException;

/**
 * Provides the methods to access all the source and target {@link Segment}s of a
 * {@link ITextUnit}.</p>
 * <p>
 * Several methods of this interface use flags to determine which sources and
 * targets to modify during segment operations. {@link VariantOptions} (in conjunction
 * with a {@link LocaleId}) and {@link CopyOptions} will determine which sources
 * and targets are modified and whether new Segments will contain content. These
 * flags are passed in an {@link EnumSet}. IAlignedSegments.MODIFY_AS_VARIANT
 * is recommended in order to maintain alignment between each target and its
 * associated source.</p>
 * <p>
 * To create an instance of this interface, use the method {@link ITextUnit#getAlignedSegments()}.</p>
 */
public interface IAlignedSegments extends Iterable<Segment> {

    /**
     * Flag used to determine the containers that will have segments modified
     * by an operation.
     * <p>
     * VariantOptions in conjunction with a {@link LocaleId}, represents directions
     * to a method on which of 5 different categories of {@link TextContainer}s
     * in the parent {@link ITextUnit} should be modified:
     * <dl>
     *   <dt>MODIFY_SOURCE</dt>
     *   <dd>modify the source used by the specified target locale</dd>
     *   <dt>MODIFY_TARGET</dt>
     *   <dd>modify the target for the specified locale</dd>
     *   <dt>MODIFY_TARGETS_WITH_SAME_SOURCE</dt>
     *   <dd>modify other targets that also use the source of the target locale</dd>
     *   <dt>MODIFY_VARIANT_SOURCES</dt>
     *   <dd>modify sources other than the one for the target locale</dd>
     *   <dt>MODIFY_TARGETS_OF_VARIANT_SOURCES</dt>
     *   <dd>modify targets that do not use the source for the target locale</dd>
     * </dl>
     * VariantOptions also defines 2 flags:
     * <dl>
     *   <dt>CANCEL_IF_MULTIPLE_TARGETS</dt>
     *   <dd>indicates that the method should not proceed if there are any targets
     *       in the TARGETS_WITH_SAME_SOURCE category</dd>
     *   <dt>CREATE_VARIANT_IF_MULTIPLE_TARGETS</dt>
     *   <dd>indicates that if there are any targets in the TARGETS_WITH_SAME_SOURCE
     *       category, a new variant source should be used rather than apply changes
     *       to the shared source</dd>
     * </dl>
     * </p>
     *
     * @see CopyOptions
     */
    public enum VariantOptions {
        CREATE_VARIANT_IF_MULTIPLE_TARGETS,
        CANCEL_IF_MULTIPLE_TARGETS,
        MODIFY_SOURCE,
        MODIFY_TARGET,
        MODIFY_TARGETS_WITH_SAME_SOURCE,
        MODIFY_VARIANT_SOURCES,
        MODIFY_TARGETS_OF_VARIANT_SOURCES
    }

    //TODO the following can be modified by anyone. Having get methods to clone
    // them does not seem a good solution.
    //Ideally these would be presented as immutable EnumSets, with a method to
    //return a mutable clone.

    /**
     * MODIFY_SOURCE + MODIFY_TARGET + CANCEL_IF_MULTIPLE_TARGETS
     *
     * @see VariantOptions
     */
    public final EnumSet<VariantOptions> MODIFY_ONLY_IF_ALONE = EnumSet.of(
            VariantOptions.MODIFY_SOURCE,
            VariantOptions.MODIFY_TARGET,
            VariantOptions.CANCEL_IF_MULTIPLE_TARGETS);

    /**
     * MODIFY_SOURCE + MODIFY_TARGET + CREATE_VARIANT_IF_MULTIPLE_TARGETS
     *
     * @see VariantOptions
     */
    public final EnumSet<VariantOptions> MODIFY_AS_VARIANT = EnumSet.of(
            VariantOptions.MODIFY_SOURCE,
            VariantOptions.MODIFY_TARGET,
            VariantOptions.CREATE_VARIANT_IF_MULTIPLE_TARGETS);
    
    /**
     * MODIFY_SOURCE + MODIFY_TARGET + MODIFY_TARGETS_WITH_SAME_SOURCE
     *
     * @see VariantOptions
     */
    public final EnumSet<VariantOptions> MODIFY_SOURCE_AND_ASSOCIATED_TARGETS = EnumSet.of(
            VariantOptions.MODIFY_SOURCE,
            VariantOptions.MODIFY_TARGET,
            VariantOptions.MODIFY_TARGETS_WITH_SAME_SOURCE);

    /**
     * MODIFY_SOURCE + MODIFY_TARGET
     *
     * @see VariantOptions
     */
    public final EnumSet<VariantOptions> MODIFY_SOURCE_AND_ASSOCIATED_TARGET = EnumSet.of(
            VariantOptions.MODIFY_SOURCE,
            VariantOptions.MODIFY_TARGET);

    /**
     * Modify all {@link TextContainer}s in the parent {@link ITextUnit}.
     * MODIFY_SOURCE + MODIFY_TARGET + MODIFY_TARGETS_WITH_SAME_SOURCE +
     * MODIFY_VARIANT_SOURCES + MODIFY_TARGETS_OF_VARIANT_SOURCES
     *
     * @see VariantOptions
     */
    public final EnumSet<VariantOptions> MODIFY_ALL = EnumSet.of(
            VariantOptions.MODIFY_SOURCE,
            VariantOptions.MODIFY_TARGET,
            VariantOptions.MODIFY_TARGETS_WITH_SAME_SOURCE,
            VariantOptions.MODIFY_VARIANT_SOURCES,
            VariantOptions.MODIFY_TARGETS_OF_VARIANT_SOURCES);


    /**
     * Flag to indicate which segments should contain content when new or copied
     * segments are created. Empty segments are created if a segment is flagged
     * to be modified in {@link VariantOptions} but is not flagged in CopyOptions.
     * If no segment is created or modified, this flag does nothing.
     * <dl>
     *   <dt>COPY_TO_SOURCE</dt>
     *   <dd>the source for the given target locale should contain content</dd>
     *   <dt>COPY_TO_TARGET</dt>
     *   <dd>the target for the given target locale should contain content</dd>
     *   <dt>COPY_TO_TARGETS_WITH_SAME_SOURCE</dt>
     *   <dd>other targets that also use the source of the target locale should
     *       contain content</dd>
     *   <dt>COPY_TO_VARIANT_SOURCES</dt>
     *   <dd>sources other than the one for the target locale should contain
     *       content</dd>
     *   <dt>COPY_TO_TARGETS_OF_VARIANT_SOURCES</dt>
     *   <dd>targets that do not use the source of the target locale should
     *       contain content</dd>
     * </dl>
     *
     * @see VariantOptions
     */
    public enum CopyOptions {
        COPY_TO_SOURCE,
        COPY_TO_TARGET,
        COPY_TO_TARGETS_WITH_SAME_SOURCE,
        COPY_TO_VARIANT_SOURCES,
        COPY_TO_TARGETS_OF_VARIANT_SOURCES
    }

    /**
     * No new segments should contain any content
     *
     * @see CopyOptions
     */
    public final EnumSet<CopyOptions> COPY_TO_NONE = EnumSet.noneOf(CopyOptions.class);

    /**
     * Source and target for the given locale should contain content.
     * <p>
     * COPY_TO_SOURCE + COPY_TO_TARGET</p>
     *
     * @see CopyOptions
     */
    public final EnumSet<CopyOptions> COPY_TO_SOURCE_AND_TARGET = EnumSet.of(
            CopyOptions.COPY_TO_SOURCE,
            CopyOptions.COPY_TO_TARGET);

    /**
     * Copy to all {@link TextContainer}s in this {@link ITextUnit}.
     *
     * @see CopyOptions
     */
    public final EnumSet<CopyOptions> COPY_TO_ALL = EnumSet.allOf(CopyOptions.class);


    //TODO this doesn't make as much sense with variant sources, calling iterator
    //directly on the default source may be more appropriate than providing an iterator here
    /**
     * Gets an iterator for the default source segments of this text unit.
     * This iterator does not iterate through non-segment parts of the content.
     *
     * @return an iterator for the source segments of this text unit.
     */
    @Override
    public Iterator<Segment> iterator();

    /**
     * Gets an iterator for the source of the specified target locale.
     * This iterator does not iterate through non-segment parts of the content.
     *
     * @param trgLoc the target locale for the source to iterate over.
     * @return an iterator for the source segments used for trgLoc.
     */
    public Iterator<Segment> iterator(LocaleId trgLoc);

    /**
     * Adds given source and target segments to the end of the content.
     * <p>
     * If srcSeg is non-null, the content of srcSeg will be used for any
     * new segments that are not empty, otherwise the content of trgSeg
     * will be used. srcSeg and trgSeg cannot both be null.</p>
     *
     * @param srcSeg the source segment to add.
     * @param trgSeg the target segment to add. Null to use a clone of srcSeg instead.
     * @param trgLoc the target locale for which to append segments
     * @param variantOptions determines which sources and targets will have
     *        segments added. See {@link VariantOptions}.
     * @param copyOptions determines whether to use the content of srcSeg or
     *        an empty segment for any segments that are added. See {@link CopyOptions}.
     *
     * @throws IllegalArgumentException if srcSeg and trgSeg are both null
     */
    public void append(Segment srcSeg,
                       Segment trgSeg,
                       LocaleId trgLoc,
                       EnumSet<VariantOptions> variantOptions,
                       EnumSet<CopyOptions> copyOptions);
	
    /**
     * Inserts given source and target segments at the specified position in
     * the list of segments.
     * <p>
     * If the same segment content is to be used in multiple sources or
     * targets, the content of the given source segment will be used.</p>
     * <p>
     * The validated id (after insertion) of srcSeg will be applied to all
     * other inserted segments, including trgSeg.</p>
     *
     * @param index the segment index position.
     * @param srcSeg the source segment to insert.
     * @param trgSeg the target segment to insert. Null to use srcSeg instead.
     * @param trgLoc the target locale for which to insert the segment
     * @param variantOptions determines which sources and targets will have
     *        a segment inserted. See {@link VariantOptions}.
     * @param copyOptions determines whether to use the content of srcSeg or
     *        an empty segment for any segments that are inserted. See {@link CopyOptions}.
     *
     * @throws IllegalArgumentException if srcSeg is null
     */
    public void insert(int index,
                       Segment srcSeg,
                       Segment trgSeg,
                       LocaleId trgLoc,
                       EnumSet<VariantOptions> variantOptions,
                       EnumSet<CopyOptions> copyOptions);


    //NOTE: this replaces setSource(...) and setTarget(...), since variantOptions
    //      can specify source or target easily
    /**
     * Replaces a segment at a given position with a clone of the given segment.
     * <p>
     * The segment id is determined by the segment at the position of index
     * in the source for trgLoc, the segment id is then used to locate the
     * segments in other sources and targets to replace.</p>
     * <p>
     * If the id of the new segment is different from the current one, the
     * new id will be propagated to all other sources and targets indicated
     * by idUpdateOptions.</p>
     *
     * @param index the segment index position
     * @param seg the new segment to place at the position
     * @param trgLoc the locale used to specify the target and source to use
     * @param variantOptions determines which targets and sources will have
     *                       a segment replaced. See {@link VariantOptions}.
     * @param idUpdateOptions determines which targets and sources will have
     *                        their id updated. See {@link VariantOptions}.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     * @throws IllegalArgumentException if seg is null
     */
    public void setSegment(int index,
                           Segment seg,
                           LocaleId trgLoc,
                           EnumSet<VariantOptions> variantOptions,
                           EnumSet<VariantOptions> idUpdateOptions);


    /**
     * Removes the given segment and any segments with the same id from the
     * specified sources and targets.
     *
     * @param seg the segment to remove.
     * @param trgLoc the locale used in specifying which sources and targets
     *               to use.
     * @param variantOptions determines which sources and targets will have
     *                       a segment removed. See {@link VariantOptions}.
     * @return true if remove success
     */
    public boolean remove(Segment seg,
                          LocaleId trgLoc,
                          EnumSet<VariantOptions> variantOptions);
	
    /**
     * Gets the source segment for the given target locale at a given position.
     * <p>
     * The first segment has the index 0, the second has the index 1, etc.</p>
     *
     * @param index the segment index of the segment to retrieve.
     * @param trgLoc the target locale for the source from which to retrieve
     *               the indicated segment.
     * @return the segment at the given position.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public Segment getSource(int index, LocaleId trgLoc);
	
    /**
     * Gets the target segment corresponding to a given source segment.
     * <p>
     * This always returns a segment: If the target does not exists one is created.
     * If the segment does not exists one is created at the end of the target content.</p>
     *
     * @param seg the source (or other target) segment for which a
     *            corresponding target segment is requested.
     * @param trgLoc the target to look up.
     * @param variantOptions determines which sources and targets will have
     *                       a segment added if the segment does not exist.
     *                       The option for the target of trgLoc (i.e. MODIFY_TARGET)
     *                       is ignored so that a segment can always be returned.
     *                       See {@link VariantOptions}.
     * @param copyOptions determines whether newly created segments will
     *                    contain the content of seg. See {@link CopyOptions}.
     * @return the corresponding target {@link Segment} (may be empty).
     */
    public Segment getCorrespondingTarget(Segment seg,
                                          LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions,
                                          EnumSet<CopyOptions> copyOptions);
    
    /**
     * Gets the target segment corresponding to a given source segment given 
     * the most common use case of one source and one target. Same as calling
     * with {@link VariantOptions} MODIFY_SOURCE_AND_ASSOCIATED_TARGET 
     * and {@link CopyOptions} COPY_TO_NONE
     * <p>
     * This always returns a segment: If the target does not exists one is created.
     * If the segment does not exists one is created at the end of the target content.</p>
     *
     * @param seg the source (or other target) segment for which a
     *            corresponding target segment is requested.
     * @param trgLoc the target to look up.
     * @return the corresponding target {@link Segment} (may be empty).
     */
    public Segment getCorrespondingTarget(Segment seg, LocaleId trgLoc);
    	
    /**
     * Gets the source segment corresponding to a given target segment.
     * <p>
     * If no corresponding source segment is found a new one is added at the
     * end of the container and returned as the new corresponding segment.</p>
     *
     * @param trgSeg the target segment for which the corresponding source
     *               segment is requested.
     * @param trgLoc the target locale of the source to look up.
     * @param variantOptions determines which sources and targets will have
     *                       a segment added if the segment does not exist.
     *                       The options for the source of trgLoc are ignored
     *                       so that a segment can always be returned. See
     *                       {@link VariantOptions}.
     * @param copyOptions determines whether newly created segments will
     *                    contain the content of trgSeg. See {@link CopyOptions}.
     * @return the corresponding source segment.
     */
    public Segment getCorrespondingSource(Segment trgSeg,
                                          LocaleId trgLoc,
                                          EnumSet<VariantOptions> variantOptions,
                                          EnumSet<CopyOptions> copyOptions);

    /**
     * Gets the source segment corresponding to a given target segment given 
     * the most common use case of one source and one target. Same as calling
     * with {@link VariantOptions} MODIFY_SOURCE_AND_ASSOCIATED_TARGET 
     * and {@link CopyOptions} COPY_TO_NONE
     * <p>
     * If no corresponding source segment is found a new one is added at the
     * end of the container and returned as the new corresponding segment.</p>
     *
     * @param trgSeg the target segment for which the corresponding source
     *               segment is requested.
     * @param trgLoc the target locale of the source to look up.
     */
    public Segment getCorrespondingSource(Segment trgSeg, LocaleId trgLoc);

    /**
     * Aligns all the segments listed in the aligned pairs for given locale.
     * <p>
     * This will replace any content in the target and variant source for the
     * given target locale (if either are not present they will be created).</p>
     * <p>
     * The target for the given locale will be considered aligned with its
     * source when this operation is complete.</p>
     *
     * @param alignedSegmentPairs the list of pairs to align
     * @param trgLoc the target locale to work with.
     */
    public void align(List<AlignedPair> alignedSegmentPairs,
                      LocaleId trgLoc);

    /**
     * Aligns all the target segments with the source segments for the given locale.
     * <p>
     * Assumes the same number of source and target segments otherwise an
     * exception is thrown.</p>
     *
     * @param trgLoc the locale of the target to work with.
     * @throws OkapiMisAlignmentException if there are a different number of
     *                                    source and target segments.
     */
    public void align(LocaleId trgLoc);


    /**
     * Aligns all the segments for the specified sources and targets by collapsing all
     * segments into one.
     * <p>
     * variantOptions determines which targets and sources will be collapsed,
     * and targets will be considered aligned with their sources only if both
     * the source and target for that locale are collapsed.</p>
     *
     * @param trgLoc the target locale of the target (and its corresponding
     *               source) to collapse.
     * @param variantOptions determines which targets and sources will be
     *                       collapsed to a single segment. See {@link VariantOptions}.
     */
    public void alignCollapseAll(LocaleId trgLoc,
                                     EnumSet<VariantOptions> variantOptions);

    /**
     * Splits a given source segment into two.
     * <p>
     * New segments are created to match the given source in any sources and
     * targets indicated by variantOptions, with content either empty or
     * copied from the new source segment as indicated by copyOptions.</p>
     * <p>
     * Alignment statuses are updated for all locales that have been modified.</p>
     *
     * @param trgLoc the target locale that uses the source in which the
     *               segment is to be split
     * @param srcSeg the source segment to split.
     * @param splitPos the position where to split.
     * @param variantOptions determines which sources and targets will have
     *                       new segments added. See {@link VariantOptions}
     * @param copyOptions determines whether the content of new segments
     *                    will be empty or the same as the new segment.
     *                    See {@link CopyOptions}.
     * @return the new source segment created, or null if none was created.
     */
    public Segment splitSource(LocaleId trgLoc,
                               Segment srcSeg,
                               int splitPos,
                               EnumSet<VariantOptions> variantOptions,
                               EnumSet<CopyOptions> copyOptions);

    /**
     * Splits a given target segment into two.
     * <p>
     * New segments are created to match the given target in any sources and
     * targets indicated by variantOptions, with content either empty or
     * copied from the new target segment as indicated by copyOptions.</p>
     * <p>
     * Alignment statuses are updated for all locales that have been modified.</p>
     *
     * @param trgLoc the target locale to work on.
     * @param trgSeg the targets segment.
     * @param splitPos the position where to split.
     * @param variantOptions determines which sources and targets will have
     *                       new segments added. See {@link VariantOptions}.
     * @param copyOptions determines whether the content of new segments
     *                    will be empty or the same as the new segment. See
     *                    {@link CopyOptions}.
     * @return the new target segment created, or null if none was created.
     */
    public Segment splitTarget(LocaleId trgLoc,
                               Segment trgSeg,
                               int splitPos,
                               EnumSet<VariantOptions> variantOptions,
                               EnumSet<CopyOptions> copyOptions);
	
    /**
     * Joins the segment for a given segment's id to the next segment,
     * including all the parts between the two segments.
     *
     * @param seg a segment holding the id to use for the join.
     * @param trgLoc the target locale used with variantOptions to determine
     *               which sources and targets will have segments joined.
     * @param variantOptions determines which sources and targets will have
     *                       segments joined. See {@link VariantOptions}.
     */
    public void joinWithNext(Segment seg,
                             LocaleId trgLoc,
                             EnumSet<VariantOptions> variantOptions);

    /**
     * Joins all segments for the specified sources and targets. The content
     * becomes a single segment.
     *
     * @param trgLoc the target locale used with variantOptions to determine
     *               which sources and targets will have all segments joined.
     * @param variantOptions determines which sources and targets will have
     *                       all segments joined. See {@link VariantOptions}.
     */
    public void joinAll(LocaleId trgLoc,
                        EnumSet<VariantOptions> variantOptions);
	
    /**
     * Gets the status of the alignment for this entire text unit. The status
     * will be NOT_ALIGNED if any of the targets in the parent TextUnit have
     * a status of NOT_ALIGNED
     *
     * @return the status of the alignment for this text unit.
     */
    public AlignmentStatus getAlignmentStatus();
	
    /**
     * Gets the status of the alignment the given target locale in this text
     * unit.
     *
     * @param trgLoc the target locale for which to get the alignment status.
     * @return the status of the alignment for this text unit.
     */
    public AlignmentStatus getAlignmentStatus(LocaleId trgLoc);

    /**
     * Segments the source content used for the given target locale based on
     * the rules provided by a given {@link ISegmenter}.
     * <p>No associated targets are modified.</p>
     *
     * @param segmenter the segmenter to use to create the segments.
     * @param targetLocale the target locale that uses the source to segment.
     */
    public void segmentSource (ISegmenter segmenter, LocaleId targetLocale);
	
    /**
     * Segments the specified target content based on the rules provided by
     * a given {@link ISegmenter}.
     * <p>If the given target does not exist one is created.</p>
     *
     * @param segmenter the segmenter to use to create the segments.
     * @param targetLocale {@link LocaleId} of the target we want to segment.
     */
    public void segmentTarget (ISegmenter segmenter, LocaleId targetLocale);
	
}
