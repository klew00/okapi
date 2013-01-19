/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Common methods to provide segmentation facility to extracted content. 
 */
public interface ISegmenter {

	/**
	 * Calculate the segmentation of a given plain text string.
	 * @param text plain text to segment.
	 * @return the number of segments calculated.
	 */
	public int computeSegments (String text);
	
	/**
	 * Calculates the segmentation of a given TextContainer object.
	 * If the content is already segmented, it is un-segmented automatically before
	 * being processed.
	 * @param container the object to segment.
	 * @return the number of segments calculated.
	 */
	public int computeSegments (TextContainer container);

	/**
	 * Compute the range of the next segment for a given TextContainer object.
	 * The next segment is searched from the first character after the last
	 * segment marker found in the container.
	 * @param container the text container where to look for the next segment. 
	 * @return a range corresponding to the start and end position of the found
	 * segment, or null if no more segments are found.
	 */
	public Range getNextSegmentRange (TextContainer container);

	/**
	 * Gets the list of all the split positions in the text
	 * that was last segmented. You must call {@link #computeSegments(TextContainer)}
	 * or {@link #computeSegments(String)} before calling this method.
	 * A split position is the first character position of a new segment.
	 * <p><b>IMPORTANT: The position returned here are the position WITHOUT taking
	 * in account any options for trimming or not leading and trailing white-spaces.</b>
	 * @return An array of integers where each value is a split position
	 * in the coded text that was segmented.
	 */
	public List<Integer> getSplitPositions ();

	/**
	 * Gets the list off all segments ranges calculated when
	 * calling {@link #computeSegments(String)}, or
	 * {@link #computeSegments(TextContainer)}.
	 * @return the list of all segments ranges. each range is stored in
	 * a {@link Range} object where start is the start and end the end of the range.
	 * Returns null if no ranges have been defined yet.
	 */
	public List<Range> getRanges ();

	/**
	 * Gets the language used to apply the rules.
	 * @return the language code used to apply the rules, or null, if none has
	 * been specified.
	 */
	public LocaleId getLanguage ();

}
