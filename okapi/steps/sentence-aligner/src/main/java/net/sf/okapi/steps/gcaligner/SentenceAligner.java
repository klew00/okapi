/**
 *  Copyright 2009 Welocalize, Inc. 
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */
/*===========================================================================
 Additional changes Copyright (C) 2009-2010 by the Okapi Framework contributors
 ===========================================================================*/

package net.sf.okapi.steps.gcaligner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.resource.AlignedPair;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * SentenceAligner aligns source and target (paragraph) {@link TextUnit}s and returns a list of aligned sentence-based
 * {@link TextUnit} objects.
 */

public class SentenceAligner {
	private static final long MAX_CELL_SIZE = 80000L;

	public TextUnit align(TextUnit sourceParagraph, TextUnit targetParagraph, LocaleId srcLocale,
			LocaleId trgLocale) {
		return alignWithoutSkeletonAlignment(sourceParagraph, targetParagraph, srcLocale, trgLocale);
	}

	public TextUnit align(TextUnit bilingualParagraph, LocaleId srcLocale, LocaleId trgLocale) {
		return alignWithoutSkeletonAlignment(bilingualParagraph, srcLocale, trgLocale);
	}

	private TextUnit alignWithoutSkeletonAlignment(TextUnit sourceParagraph,
			TextUnit targetParagraph, LocaleId srcLocale, LocaleId trgLocale) {
		SegmentAlignmentFunction alignmentFunction = new SegmentAlignmentFunction(srcLocale,
				trgLocale);
		return alignSegments(sourceParagraph, targetParagraph, srcLocale, trgLocale,
				alignmentFunction);
	}

	private TextUnit alignWithoutSkeletonAlignment(TextUnit bilingualParagraph, LocaleId srcLocale,
			LocaleId trgLocale) {
		SegmentAlignmentFunction alignmentFunction = new SegmentAlignmentFunction(srcLocale,
				trgLocale);
		return alignSegments(bilingualParagraph, srcLocale, trgLocale, alignmentFunction);
	}

	private TextUnit alignSegments(TextUnit sourceParagraph, TextUnit targetParagraph,
			LocaleId srcLocale, LocaleId trgLocale, SegmentAlignmentFunction alignmentFunction) {

		// make sure the paragraphs have been segmented
		if (!(sourceParagraph.getSource().hasBeenSegmented() || targetParagraph.getSource()
				.hasBeenSegmented())) {
			throw new OkapiBadStepInputException("Source and target TextUnits must be segmented.");
		}

		// To prevent OutOfMemory exception, simply don't perform the
		// alignment for a block with a lot of segments. TEMPORARY FIX
		if (sourceParagraph.getSource().getSegments().count()
				* targetParagraph.getSource().getSegments().count() > MAX_CELL_SIZE) {
			throw new IllegalArgumentException("Too many segments. Can only align "
					+ Long.toString(MAX_CELL_SIZE)
					+ ". Where the number equals the source segments times the target segments.");
		}

		DpMatrix matrix = new DpMatrix(sourceParagraph.getSource().getSegments().asList(),
				targetParagraph.getSource().getSegments().asList(), alignmentFunction);

		List<DpMatrixCell> result = matrix.align();

		// record the result in a list of AlignedPairs
		List<AlignedPair> alignedPairs = new LinkedList<AlignedPair>();

		Iterator<DpMatrixCell> it = result.iterator();
		while (it.hasNext()) {
			DpMatrixCell cell = it.next();
			if (cell.getState() == DpMatrixCell.DELETED) {
				Segment sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
				alignedPairs.add(new AlignedPair(sourceSegment, null, trgLocale));
			} else if (cell.getState() == DpMatrixCell.INSERTED) {
				Segment targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				alignedPairs.add(new AlignedPair(null, targetSegment, trgLocale));
			} else if (cell.getState() == DpMatrixCell.MATCH) {
				Segment sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
				Segment targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				alignedPairs.add(new AlignedPair(sourceSegment, targetSegment, trgLocale));
			} else if (cell.getState() == DpMatrixCell.MULTI_MATCH) {
				List<Segment> sourceSegments = matrix.getAlignmentElementsX(
						cell.getMultiMatchXIndexBegin(), cell.getMultiMatchXIndexEnd());
				List<Segment> targetSegments = matrix.getAlignmentElementsY(
						cell.getMultiMatchYIndexBegin(), cell.getMultiMatchYIndexEnd());
				alignedPairs.add(new AlignedPair(new LinkedList<TextPart>(sourceSegments),
						new LinkedList<TextPart>(targetSegments), trgLocale));
			}
		}

		return TextUnitUtil.createMultilingualTextUnit(sourceParagraph, alignedPairs, trgLocale);
	}

	private TextUnit alignSegments(TextUnit bilingualParagraph, LocaleId srcLocale,
			LocaleId trgLocale, SegmentAlignmentFunction alignmentFunction) {

		// make sure the paragraphs have been segmented
		if (!(bilingualParagraph.getSource().hasBeenSegmented() || bilingualParagraph.getTarget(
				trgLocale).hasBeenSegmented())) {
			throw new OkapiBadStepInputException("Source and target TextUnits must be segmented.");
		}

		// To prevent OutOfMemory exception, simply don't perform the
		// alignment for a block with a lot of segments. TEMPORARY FIX
		if (bilingualParagraph.getSource().getSegments().count()
				* bilingualParagraph.getTarget(trgLocale).getSegments().count() > MAX_CELL_SIZE) {
			throw new IllegalArgumentException("Too many segments. Can only align "
					+ Long.toString(MAX_CELL_SIZE)
					+ ". Where the number equals the source segments times the target segments.");
		}

		DpMatrix matrix = new DpMatrix(bilingualParagraph.getSource().getSegments().asList(),
				bilingualParagraph.getTarget(trgLocale).getSegments().asList(), alignmentFunction);

		List<DpMatrixCell> result = matrix.align();

		// record the result in a list of AlignedPairs
		List<AlignedPair> alignedPairs = new LinkedList<AlignedPair>();

		Iterator<DpMatrixCell> it = result.iterator();
		while (it.hasNext()) {
			DpMatrixCell cell = it.next();
			if (cell.getState() == DpMatrixCell.DELETED) {
				Segment sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
				alignedPairs.add(new AlignedPair(sourceSegment, null, trgLocale));
			} else if (cell.getState() == DpMatrixCell.INSERTED) {
				Segment targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				alignedPairs.add(new AlignedPair(null, targetSegment, trgLocale));
			} else if (cell.getState() == DpMatrixCell.MATCH) {
				Segment sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
				Segment targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				alignedPairs.add(new AlignedPair(sourceSegment, targetSegment, trgLocale));
			} else if (cell.getState() == DpMatrixCell.MULTI_MATCH) {
				List<Segment> sourceSegments = matrix.getAlignmentElementsX(
						cell.getMultiMatchXIndexBegin(), cell.getMultiMatchXIndexEnd());
				List<Segment> targetSegments = matrix.getAlignmentElementsY(
						cell.getMultiMatchYIndexBegin(), cell.getMultiMatchYIndexEnd());
				alignedPairs.add(new AlignedPair(new LinkedList<TextPart>(sourceSegments),
						new LinkedList<TextPart>(targetSegments), trgLocale));
			}
		}

		return TextUnitUtil.createMultilingualTextUnit(bilingualParagraph, alignedPairs, trgLocale);
	}
}
