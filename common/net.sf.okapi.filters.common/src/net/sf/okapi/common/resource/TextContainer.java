/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

public class TextContainer extends TextFragment {

	protected Hashtable<String, Property> properties;
	protected Annotations annotations;
	protected TextFragment masterSegment;
	protected ArrayList<TextFragment> segments;
	
	public TextContainer () {
		super();
		annotations = new Annotations();
	}

	public TextContainer (String text) {
		super(text);
		annotations = new Annotations();
	}
	
	@Override
	public String toString () {
		return text.toString();
	}
	
	@Override
	public TextContainer clone () {
		TextContainer tc = new TextContainer();
		// Clone the content
		tc.setContent(super.clone());
		// Clone the properties
		if ( properties != null ) {
			tc.properties = new Hashtable<String, Property>();
			for ( Property prop : properties.values() ) {
				tc.properties.put(prop.getName(), prop.clone()); 
			}
		}
		//TODO: Clone the annotations ???
		//TODO: Clone the segments
		return tc;
	}

	public TextFragment getContent () {
		return this;
	}
	
	public void setContent (TextFragment content) {
		text = new StringBuilder();
		insert(-1, content);
		// We don't change the current annotations
		// But we reset the segments
		if ( masterSegment != null ) {
			masterSegment = null;
			segments.clear();
		}
	}

	public boolean hasProperty (String name) {
		return (getProperty(name) != null);
	}
	
	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

	/**
	 * Indicates if this TextContainer is segmented.
	 * @return True if the this TextContainer is segmented.
	 */
	public boolean isSegmented () {
		return (masterSegment != null);
	}
	
	/**
	 * Gets the list of all current segments, or null if this object is not segmented.
	 * @return The list of all current segments, or null. 
	 */
	public List<TextFragment> getSegments () {
		return segments;
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
		if (( masterSegment.codes == null ) && (ranges.size() > 0 )) {
			codes = new ArrayList<Code>();
		}
		int oriLength = masterSegment.text.length();
		int diff = 0;
		for ( int i=0; i<ranges.size(); i++ ) {
			// Add the new segment in the list
			segments.add(subSequence(ranges.get(i).x+diff, ranges.get(i).y+diff));
			// Remove it from the main content
			int width = ranges.get(i).y-ranges.get(i).x;
			// For chunks < 2 there is no codes so we can just add the needed room for the segment marker
			if ( width == 1 ) masterSegment.insert(ranges.get(i).x+diff, new TextFragment("Z"));
			else if ( width == 0 ) masterSegment.insert(ranges.get(i).x+diff, new TextFragment("ZZ"));
			else { // Otherwise: we need to remove the chunk of coded text and its codes
				masterSegment.remove(ranges.get(i).x+diff, ranges.get(i).y+diff);
				// then re-insert room for the segment marker
				masterSegment.insert(ranges.get(i).x+diff, new TextFragment("ZZ"));
			}
			
			// Set the segment marker and its corresponding code
			masterSegment.codes.add(new Code(TagType.SEGMENTHOLDER, CODETYPE_SEGMENT, String.valueOf(i)));
			masterSegment.text.setCharAt(ranges.get(i).x+diff, (char)MARKER_SEGMENT);
			masterSegment.text.setCharAt(ranges.get(i).x+diff+1,
				toChar(masterSegment.codes.size()-1));
			// Compute the adjustment to take in account
			diff = (masterSegment.text.length()-oriLength);
		}
		// Return the number of segments
		return segments.size();
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
		for ( int i=0; i<masterSegment.text.length(); i++ ) {
			switch ( masterSegment.text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = masterSegment.getCode(masterSegment.text.charAt(++i));
				int index = Integer.parseInt(code.data);
				int add = segments.get(index).getCodedText().length();
				// Remove the segment marker
				masterSegment.remove(i-1, i+1);
				// Insert the segment
				masterSegment.insert(i-1, segments.get(index));
				// Adjust the value of i so it is at the end of the new segment
				i += (add-2); // -2 = size of code marker
				break;
			}
		}
		
		// Re-initialize the list of segments
		segments.clear();
		segments = null;
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
		for ( int i=0; i<masterSegment.text.length(); i++ ) {
			switch ( masterSegment.text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = masterSegment.getCode(text.charAt(++i));
				if ( segmentIndex == Integer.parseInt(code.data) ) {
					pos = i-1;
					// Remove the segment marker
					masterSegment.remove(pos, i+1);
					// Insert the segment
					masterSegment.insert(pos, segments.get(segmentIndex));
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
	 * Renumbers, from a given value, the segment indices associated to the 
	 * segment markers located after a given position. 
	 * @param start Start position (in coded text) where the renumbering should start.
	 * @param indexValue Value to use as first index value of the renumbering.
	 */
	private void renumberSegmentMarkers (int start,
		int indexValue)
	{
		Code code;
		for ( int i=start; i<masterSegment.text.length(); i++ ) {
			switch ( masterSegment.text.charAt(i) ) {
			case MARKER_OPENING:
			case MARKER_CLOSING:
			case MARKER_ISOLATED:
				i++; // Skip
				break;
			case MARKER_SEGMENT:
				code = masterSegment.getCode(text.charAt(++i));
				code.data = String.valueOf(indexValue++);
				break;
			}
		}
	}

}
