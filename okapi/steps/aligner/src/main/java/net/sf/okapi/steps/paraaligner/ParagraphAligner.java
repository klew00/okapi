/*  Copyright 2009 Welocalize, Inc. 
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
 Additional changes Copyright (C) 2009-2011 by the Okapi Framework contributors
 ===========================================================================*/

package net.sf.okapi.steps.paraaligner;

import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.gcaligner.AlignmentFunction;
import net.sf.okapi.steps.gcaligner.AlignmentScorer;
import net.sf.okapi.steps.gcaligner.DpMatrix;
import net.sf.okapi.steps.gcaligner.DpMatrixCell;
import net.sf.okapi.steps.gcaligner.Penalties;

/**
 * SentenceAligner aligns source and target (paragraph) {@link TextUnit}s and returns a list of aligned sentence-based
 * {@link TextUnit} objects.
 */

public class ParagraphAligner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParagraphAligner.class.getName());
	
	private static final long MAX_CELL_SIZE = 80000L;
	private List<AlignmentScorer<ITextUnit>> scorerList;
	
	public ParagraphAligner(List<AlignmentScorer<ITextUnit>> scorerList) {
		this.scorerList = scorerList;
	}

	public List<ITextUnit> align(List<ITextUnit> sourceParagraphs, List<ITextUnit> targetParagraphs, LocaleId srcLocale,
			LocaleId trgLocale, boolean outputOneTOneMatchesOnly) {
		return alignWithoutSkeletonAlignment(sourceParagraphs, targetParagraphs, srcLocale, trgLocale, outputOneTOneMatchesOnly);
	}

	private List<ITextUnit> alignWithoutSkeletonAlignment(List<ITextUnit> sourceParagraphs,
			List<ITextUnit> targetParagraphs, LocaleId srcLocale, LocaleId trgLocale, boolean outputOneTOneMatchesOnly) {
		AlignmentFunction<ITextUnit> alignmentFunction = new AlignmentFunction<ITextUnit>(srcLocale,
				trgLocale, scorerList, new Penalties());
		return alignSegments(sourceParagraphs, targetParagraphs, srcLocale, trgLocale,
				alignmentFunction, outputOneTOneMatchesOnly);
	}

	private List<ITextUnit> alignSegments(List<ITextUnit> sourceParagraphs, List<ITextUnit> targetParagraphs,
			LocaleId srcLocale, LocaleId trgLocale, AlignmentFunction<ITextUnit> alignmentFunction, boolean outputOneTOneMatchesOnly) {

		// To prevent OutOfMemory exception, simply don't perform the
		// alignment for a block with a lot of segments. TEMPORARY FIX
		if (sourceParagraphs.size()
				* targetParagraphs.size() > MAX_CELL_SIZE) {
			throw new IllegalArgumentException("Too many segments. Can only align "
					+ Long.toString(MAX_CELL_SIZE)
					+ ". Where the number equals the source segments times the target segments.");
		}

		DpMatrix<ITextUnit> matrix = new DpMatrix<ITextUnit>(sourceParagraphs, targetParagraphs, alignmentFunction);

		List<DpMatrixCell> result = matrix.align();
				
		Iterator<DpMatrixCell> it = result.iterator();
		while (it.hasNext()) {
			DpMatrixCell cell = it.next();
			
			if (outputOneTOneMatchesOnly) {
				if (cell.getState() == DpMatrixCell.MATCH) {
					ITextUnit sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
					ITextUnit targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				}
				continue;
			}			
			
			if (cell.getState() == DpMatrixCell.DELETED) {
				ITextUnit sourceSegment = matrix.getAlignmentElementX(cell.getXindex());				
				LOGGER.warn(sourceSegment.toString() +
						"\nTarget segment deleted (TU ID: " + sourceSegment.getName() + "): Non 1-1 match. Please confirm alignment.");
			} else if (cell.getState() == DpMatrixCell.INSERTED) {
				ITextUnit targetSegment = matrix.getAlignmentElementY(cell.getYindex());
				LOGGER.warn(targetSegment.toString() +
						"\nSource segment deleted (TU ID: " + targetSegment.getName() + "): Non 1-1 match. Please confirm alignment.");
			} else if (cell.getState() == DpMatrixCell.MATCH) {
				ITextUnit sourceSegment = matrix.getAlignmentElementX(cell.getXindex());
				ITextUnit targetSegment = matrix.getAlignmentElementY(cell.getYindex());
			} else if (cell.getState() == DpMatrixCell.MULTI_MATCH) {
				List<ITextUnit> sourceSegments = matrix.getAlignmentElementsX(
						cell.getMultiMatchXIndexBegin(), cell.getMultiMatchXIndexEnd());
				List<ITextUnit> targetSegments = matrix.getAlignmentElementsY(
						cell.getMultiMatchYIndexBegin(), cell.getMultiMatchYIndexEnd());
				LOGGER.warn(sourceSegments.get(0).getSource().toString()
						+ "\nMulti-ITextUnit Match (TU ID: " + sourceSegments.get(0).getName() + "): Non 1-1 match. Please confirm alignment.");
			}
		}
		
		return sourceParagraphs;
	}
}
