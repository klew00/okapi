/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.steps.segmentation;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class RenumberingUtil {
	
	/**
	 * Renumber the codes in a TextContainer for segmentation.  As much
	 * as possible, this will renumber the code IDs in each TextFragment to 
	 * begin at 1.  In cases where a tag pair is split across one or more
	 * TextFragments, this is not possible.  In this case, these "runs" of
	 * connected TextFragments will be treated as a single logical ID-space
	 * and collectively renumbered to start at 1.
	 * <br>
	 * Note that this can cause problems when code IDs are not consecutive
	 * or non-numeric.
	 * 
	 * @param tc TextContainer to renumber
	 */
	public static void renumberCodesForSegmentation(TextContainer tc) {
		if ( tc == null ) {
			return;
		}
		
		// tc should already be segmented by this point.
		boolean inUnmatched = false;
		int unmatchedMinId = 0;
		for ( Segment seg : tc.getSegments() ) {
			TextFragment tf = seg.getContent();
			if (RenumberingUtil.containsOnlyMatchingCodes(tf)) {
				inUnmatched = false;
				reduceCodeIdsByOffset(tf, calculateCodeOffset(tf));
			}
			else if (!inUnmatched) {
				// Start of an unmatched run
				inUnmatched = true;
				unmatchedMinId = calculateCodeOffset(tf);
				reduceCodeIdsByOffset(tf, unmatchedMinId);
			}
			else {
				// Already in an unmatched run
				reduceCodeIdsByOffset(tf, unmatchedMinId);
			}
		}
	}

	/**
	 * Find the minimum ID of any code in the TextFragment, then
	 * use that to calculate the offset/delta by which all code
	 * IDs in the TextFragment need to be adjusted.
	 * 
	 * @param tf TextFragment to examine
	 * @return code ID offset for the TextFragment, which is one 
	 * 		   less than the minimum code ID
	 */
	public static int calculateCodeOffset(TextFragment tf) {
		if (tf.getCodes().size() == 0) {
			return 0;
		}
		int minId = Integer.MAX_VALUE;
		for (Code code : tf.getCodes()) {
			if (minId > code.getId()) {
				minId = code.getId();
			}
		}
		return minId - 1;
	}

	/**
	 * Reduce the IDs for all codes in a TextFragment by a fixed amount.
	 * @param tf
	 * @param offset
	 */
	private static void reduceCodeIdsByOffset(TextFragment tf, int offset) {
		for (Code code : tf.getCodes()) {
			code.setId(code.getId() - offset);
		}
	}
	
	
	/**
	 * Check to see if this text fragment contains either open or closed
	 * tags that do not have a corresponding paired tag within the same
	 * TextFragment. 
	 * @param tc
	 * @return true if unmatched/unpaired codes are present
	 */
	public static boolean containsOnlyMatchingCodes(TextFragment tf) {
		List<Code> codes = tf.getCodes();
		
		// Find min, max ID values
		int minId = Integer.MAX_VALUE, maxId = Integer.MIN_VALUE;
		for (Code code : codes) {
			int id = code.getId();
			if (id < minId) minId = id;
			if (id > maxId) maxId = id;
		}
		int size = (maxId - minId + 1);
		
		int[] values = new int[size];
		for (Code c : codes) {
			int id = c.getId();
			int index = id - minId;
			values[index] += codeVal(c);
		}
		for (int i = 0; i < size; i++) {
			if (values[i] != 0) return false;
		}
		return true;
	}
	
	private static int codeVal(Code c) {
		switch (c.getTagType()) {
		case OPENING:
			return 1;
		case CLOSING:
			return -1;
		case PLACEHOLDER:
			return 0;
		}
		return 0;
	}
	
	/**
	 * Reverse the renumbering process that was performed during segmetnation.
	 * @param tc
	 */
	public static void renumberCodesForDesegmentation(TextContainer tc) {
		if ( tc == null ) {
			return;
		}

		int nextId = 1;
		boolean inUnmatched = false;
		int unmatchedDelta = 0;
		for ( Segment seg : tc.getSegments() ) {
			TextFragment tf = seg.getContent();
			int count = tf.getCodes().size();
			if ( count == 0 ) continue;
			
			if (RenumberingUtil.containsOnlyMatchingCodes(tf)) {
				inUnmatched = false;
				nextId += incrementCodeIdsByOffset(tf, nextId - 1);
			}
			else if (!inUnmatched) {
				// Start of an unmatched run
				inUnmatched = true;
				unmatchedDelta = nextId - 1;
				nextId += incrementCodeIdsByOffset(tf, unmatchedDelta);
			}
			else {
				// Already in an unmatched run
				nextId += incrementCodeIdsByOffset(tf, unmatchedDelta);
			}
		}
	}
	
	// Return the number of codes whos IDs were updated
	private static int incrementCodeIdsByOffset(TextFragment tf, int delta) {
		int count = 0;
		for ( Code code : tf.getCodes() ) {
			code.setId(code.getId() + delta);
			if (code.getTagType() != TagType.CLOSING)
				count++;
		}
		return count;
	}
}
