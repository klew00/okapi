/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.resource;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TextContainer extends TextFragment {

	static public final String CODETYPE_SEGMENT  = "$segment";
	
	ArrayList<TextFragment>  segments;
	
	/**
	 * Creates an empty TextContainer object.
	 */
	public TextContainer () {
	}
	
	/**
	 * Creates a TextContainer object with a given parent.
	 * @param parent The parent TextUnit to use.
	 */
	public TextContainer (TextUnit parent) {
		setParent(parent);
	}
	
	/**
	 * Indicates if this TextContainer is segmented.
	 * @return True if the content of this object is segmented.
	 */
	public boolean isSegmented () {
		return (segments != null);
	}
	
	/**
	 * Gets the list of all current segments, or null if the object is not segmented.
	 * @return The list of all current segments, or null. 
	 */
	public List<TextFragment> getSegments () {
		return segments;
	}

	public int createSegments (List<Point> ranges) {
		// Un-segment all if needed
		//TODO: Find a way to offer re-segmentation on top of existing one
		mergeAllSegments();
		if( ranges == null ) return 0;

		// Extract the segments using the ranges
		segments = new ArrayList<TextFragment>();
		if (( codes == null ) && (ranges.size() > 0 )) codes = new ArrayList<Code>();
		int oriLength = text.length();
//		StringBuilder tmp = new StringBuilder(text);
		int diff = 0;
		for ( int i=0; i<ranges.size(); i++ ) {
			// Add the new segment in the list
			segments.add(subSequence(ranges.get(i).x+diff, ranges.get(i).y+diff));
			// Remove it from the main content
			int width = ranges.get(i).y-ranges.get(i).x;
			if ( width > 2 ) remove(ranges.get(i).x+diff, ranges.get(i).y+diff-2);
			else if ( width == 1 ) insert(ranges.get(i).x+diff, new TextFragment("Z"));
			else if ( width == 0 ) insert(ranges.get(i).x+diff, new TextFragment("ZZ"));
			// Else width == 2 : Do nothing
			
			// Add the segment marker and its corresponding code
			codes.add(new Code(TagType.PLACEHOLDER, CODETYPE_SEGMENT, String.valueOf(i)));
			text.setCharAt(ranges.get(i).x+diff, (char)MARKER_ISOLATED);
			text.setCharAt(ranges.get(i).x+diff+1, toChar(codes.size()-1));
			// Compute the adjustment to take in account
			diff = (text.length()-oriLength);
		}
		// Set the new coded text
		// Return the number of segments
		return segments.size();
	}
	
	/**
	 * Joins a given segment with the closest to it on the right side.
	 * @param segmentIndex Index of the first segment to join.
	 */
	public void joinSegmentWithNext (int segmentIndex) {
		Code code;
		int pos1 = -1;
		int pos2 = -1;
		int seg2Index = -1;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
				i++; // Skip
				break;
			case MARKER_ISOLATED:
				code = getCode(text.charAt(++i));
				if ( !code.type.equals(CODETYPE_SEGMENT) ) break;
				// Else: it's a segment marker
				if ( pos1 > -1 ) {
					if ( segmentIndex == Integer.parseInt(code.data) ) {
						pos1 = i-1;
					}
				}
				else {
					seg2Index = Integer.parseInt(code.data);
					pos2 = i-1;
					i = text.length(); // Stop the loop
					break;
				}
			}
		}
		
		// Assumes pos1 and pos2 are > -1 now
		// Get the inter-segment part and add it to first segment
		segments.get(segmentIndex).append(subSequence(pos1+2, pos2));
		// Remove inter-segment part and marker for second segment
		remove(pos1+2, pos2+1);
		// Update the indices of the remaining segments
		//TODO: Update the indices of the remaining segments
		// Add second segment to first one
		segments.get(segmentIndex).append(segments.get(seg2Index));
		// Remove second segment
		segments.remove(seg2Index);
	}
	
	/**
	 * Merges back together all segments of this TextContainer object, and clear the 
	 * list of segments.
	 * Note that the merging is driven by the coded text of the object, so any 
	 * segments without a corresponding marker in the coded text will not be merge 
	 * and will be lost.
	 */
	public void mergeAllSegments () {
		if ( !isSegmented() ) return;
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
				i++; // Skip
				break;
			case MARKER_ISOLATED:
				code = getCode(text.charAt(++i));
				if ( !code.type.equals(CODETYPE_SEGMENT) ) break;
				// Else: it's a segment marker
				int index = Integer.parseInt(code.data);
				int add = segments.get(index).getCodedText().length();
				// Remove the segment marker
				remove(i-1, i+1);
				// Insert the segment
				insert(i-1, segments.get(index));
				// Adjust the value of i so it is at the end of the new segment
				i += (add-1);
				break;
			}
		}
		
		// Re-initialize the list of segments
		segments.clear();
		segments = null;
	}
}
