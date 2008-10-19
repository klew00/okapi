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

package net.sf.okapi.apptest.resource;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TextContainer extends TextFragment {

	static public final String CODETYPE_SEGMENT  = "$seg$";
	
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
	 * Clones this object.
	 */
	@Override
	public TextContainer clone () {
		TextContainer newCont = new TextContainer(parent);
		newCont.setCodedText(getCodedText(), getCodes(), false);
		newCont.id = id;
		newCont.lastCodeID = lastCodeID;
		if ( isSegmented() ) {
			newCont.segments = new ArrayList<TextFragment>(segments);
		}
		return newCont;
	}
	
	/**
	 * Indicates if this TextContainer is segmented.
	 * @return True if the content of this object is segmented.
	 */
	public boolean isSegmented () {
		return ((segments != null) && (segments.size() > 0));
	}
	
	/**
	 * Gets the list of all current segments, or null if the object is not segmented.
	 * @return The list of all current segments, or null. 
	 */
	public List<TextFragment> getSegments () {
		return segments;
	}
	
	/**
	 * Sets the list of segments for this object. No change is made to the
	 * content itself, so it must already match the given list at the end, or it
	 * must be build separately.
	 * @param segments The new list of segments.
	 */
	public void setSegments (ArrayList<TextFragment> segments) {
		this.segments = segments;
	}

	/**
	 * Gets the fragment before a given segment marker.
	 * @param segmentIndex The index of the segment to look for.
	 * @return The fragment between the given segment and the previous one, or the start
	 * of the text if the given fragment is the first one, or null if the fragment
	 * is not found.
	 */
	public TextFragment getFragmentBeforeSegment(int segmentIndex) {
		int start = 0;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++;
				break;
			case MARKER_SEGMENT:
				Code code = codes.get(toIndex(text.charAt(++i)));
				if ( Integer.parseInt(code.data) == segmentIndex ) {
					return subSequence(start, i-1);
				}
				else start = i+1; // Reset the start of the fragment
			}
		}
		return null;
	}
	
	/**
	 * Gets the fragment after a given segment marker.
	 * @param segmentIndex The index of the segment to look for.
	 * @return The fragment between the given segment and the next one, or the end
	 * of the text if the given fragment is the last one, or null if the fragment
	 * is not found.
	 */
	public TextFragment getFragmentAfterSegment (int segmentIndex) {
		int start = -1;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++;
				break;
			case MARKER_SEGMENT:
				Code code = codes.get(toIndex(text.charAt(++i)));
				if ( Integer.parseInt(code.data) == segmentIndex ) {
					start = i+1;
				}
				else if ( Integer.parseInt(code.data) == segmentIndex+1 ) {
					return subSequence(start, i-1);
				}
			}
		}
		if ( start == -1 ) return null; // Not found such segment
		// Else: was the last segment marker
		return subSequence(start, text.length());
	}

	/**
	 * Segments this object into one or more segments corresponding to given ranges in 
	 * the coded text. If the object is already segmented, it will be automatically
	 * un-segmented first.
	 * <p>The segments are accessible with {@link #getSegments()}. The coded text
	 * of the main object, becomes a place-holder string for the inter-segment content. 
	 * @param ranges List of the ranges corresponding to the segments. They must be
	 * expressed in coded text units, never overlap, and be ordered from the left-most
	 * to the right-most.
	 * @return The number of segments.
	 */
	public int createSegments (List<Point> ranges) {
		// Un-segment all if needed
		//TODO: Find a way to offer re-segmentation on top of existing one
		mergeAllSegments();
		if( ranges == null ) return 0;

		// Extract the segments using the ranges
		segments = new ArrayList<TextFragment>();
		if (( codes == null ) && (ranges.size() > 0 )) codes = new ArrayList<Code>();
		int oriLength = text.length();
		int diff = 0;
		for ( int i=0; i<ranges.size(); i++ ) {
			// Add the new segment in the list
			segments.add(subSequence(ranges.get(i).x+diff, ranges.get(i).y+diff));
			// Remove it from the main content
			int width = ranges.get(i).y-ranges.get(i).x;
			// For chunks < 2 there is no codes so we can just add the needed room for the segment marker
			if ( width == 1 ) insert(ranges.get(i).x+diff, new TextFragment("Z"));
			else if ( width == 0 ) insert(ranges.get(i).x+diff, new TextFragment("ZZ"));
			else { // Otherwise: we need to remove the chunk of coded text and its codes
				remove(ranges.get(i).x+diff, ranges.get(i).y+diff);
				// then re-insert room for the segment marker
				insert(ranges.get(i).x+diff, new TextFragment("ZZ"));
			}
			
			// Set the segment marker and its corresponding code
			codes.add(new Code(TagType.SEGMENTHOLDER, CODETYPE_SEGMENT, String.valueOf(i)));
			text.setCharAt(ranges.get(i).x+diff, (char)MARKER_SEGMENT);
			text.setCharAt(ranges.get(i).x+diff+1, toChar(codes.size()-1));
			// Compute the adjustment to take in account
			diff = (text.length()-oriLength);
		}
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
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = getCode(text.charAt(++i));
				if ( pos1 == -1 ) { // Search for left segment
					if ( segmentIndex == Integer.parseInt(code.data) ) {
						pos1 = i-1;
					}
				}
				else { // Search for right segment
					seg2Index = Integer.parseInt(code.data);
					pos2 = i-1;
					i = text.length(); // Stop the loop
					break;
				}
			}
		}
		
		if ( seg2Index == -1 ) return;
		
		// Assumes pos1 and pos2 are > -1 now
		// Get the inter-segment part and add it to first segment
		segments.get(segmentIndex).append(subSequence(pos1+2, pos2));
		// Remove inter-segment part and marker for second segment
		remove(pos1+2, pos2+2);
		// Update the indices of the remaining segments
		renumberSegmentMarkers(pos1+2, segmentIndex+1);
		// Add second segment to first one
		segments.get(segmentIndex).append(segments.get(seg2Index));
		// Remove second segment
		segments.remove(seg2Index);
	}
	
	/**
	 * Merges a given segment back into the main coded text.
	 * @param segmentIndex Index of the segment to merge.
	 * @return The position (in the coded text) of the start of the merged
	 * segment, or -1 if the segment index was not found.
	 */
	public int mergeSegment (int segmentIndex) {
		Code code;
		int pos = -1;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = getCode(text.charAt(++i));
				if ( segmentIndex == Integer.parseInt(code.data) ) {
					pos = i-1;
					// Remove the segment marker
					remove(pos, i+1);
					// Insert the segment
					insert(pos, segments.get(segmentIndex));
					// Remove the segment from the segment list
					segments.remove(segmentIndex);
					// Renumber the remaining segment
					renumberSegmentMarkers(pos, segmentIndex);
					return pos;
				}
			}
		}
		return -1; // Segment index not found
	}
	
	/**
	 * Adds a new segment to the container. If the container is
	 * not segmented, the current content remain part of the
	 * main coded text, and the new fragment becomes the first
	 * segment and is appended at the end of the existing content.
	 * @param fragment The fragment to add.
	 */
	public void addSegment (TextFragment fragment) {
		if ( segments == null ) {
			segments = new ArrayList<TextFragment>();
			// Any existing content stays in the main coded text
		}
		if ( codes == null ) {
			codes = new ArrayList<Code>();
		}
		
		// Add the segment to the list
		segments.add(fragment);
		// Create the segment marker in the main coded text
		codes.add(new Code(TagType.SEGMENTHOLDER, CODETYPE_SEGMENT,
			String.valueOf(segments.size()-1)));
		text.append(""+(char)MARKER_SEGMENT+toChar(codes.size()-1));
	}

	/**
	 * Creates a new segment from a section of the container text.
	 * Any existing segmentation remains in place, but the section 
	 * must not contain existing segment markers.
	 * @param start The position of the first character or marker of the section
	 * (in the coded text representation).
	 * @param end The position just after the last character or marker of the section
	 * (in the coded text representation). You can use -1 to indicate the end of the
	 * text.
	 * @return The segment just created, or null if no segment was created (for
	 * example when the width provided was 0).
	 */
	public TextFragment createSegment (int start,
		int end)
	{
		// Compute end for -1
		if ( end == -1 ) end = text.length();
		// Check if the segment is empty
		if ( start == end ) return null;

		// Create lists if needed
		if ( segments == null ) {
			segments = new ArrayList<TextFragment>();
		}
		if ( codes == null ) {
			codes = new ArrayList<Code>();
		}

		// Get the segment index value for the first segment to create
		// (where it goes in the list of existing segments)
		int segIndex = 0;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				// Are we after the start of the segment to insert?
				if ( i >= start ) i = text.length(); // Then stop here
				else segIndex++; // Else wait for next marker or end of text
				break;
			}
		}

		// Add the new segment in the list
		boolean inserted = true;
		if ( segIndex > segments.size()-1 ) {
			segments.add(subSequence(start, end));
			segIndex = segments.size()-1;
			inserted = false; // new segment was added
		}
		else {
			segments.add(segIndex, subSequence(start, end));
		}
		
		// Remove it from the main content
		int width = end-start;
		if ( width > 2 ) remove(start, end-2);
		else if ( width == 1 ) insert(start, new TextFragment("Z"));
		else if ( width == 0 ) insert(start, new TextFragment("ZZ"));
		// Else width == 2 : Do nothing
			
		// Set the segment marker and its corresponding code
		text.setCharAt(start, (char)MARKER_SEGMENT);
		// Add the segment marker
		codes.add(new Code(TagType.SEGMENTHOLDER, CODETYPE_SEGMENT,
			String.valueOf(segIndex)));
		// Index of the marker is independent of its location
		text.setCharAt(start+1, toChar(codes.size()-1));

		// If required: update the indices of the segment markers after the new one
		if ( inserted ) renumberSegmentMarkers(start+2, segIndex+1);
		// Return the created segment
		return segments.get(segIndex);
	}
	
	/**
	 * Clears the container of all content. The parent is not modified.
	 */
	public void clear () {
		super.clear();
		if ( segments != null ) segments.clear();
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
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = getCode(text.charAt(++i));
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
	
	/**
	 * Renumbers, from a given value, the segment indices associated to the 
	 * segment markers located after a given position. 
	 * @param start Start position (in coded text) where the renumbering should start.
	 * @param indexValue Value to use as first index value of the renumbering.
	 */
	private void renumberSegmentMarkers (int start,
		int indexValue)
	{
		Code code;
		for ( int i=start; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = getCode(text.charAt(++i));
				code.data = String.valueOf(indexValue++);
				break;
			}
		}
		
	}
}
