package net.sf.okapi.apptest.annotation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.resource.Code;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;

public class SegmentationAnnotation extends TextFragment implements IAnnotation {

	ArrayList<TextFragment> segments;
	
	public SegmentationAnnotation () {
		super(null);
	}
	
	public SegmentationAnnotation (TextUnit parent) {
		super(parent);
	}

	@Override
	public SegmentationAnnotation clone () {
		SegmentationAnnotation newCont = new SegmentationAnnotation(parent);
		newCont.setCodedText(getCodedText(), getCodes(), false);
		//newCont.id = id;
		newCont.lastCodeID = lastCodeID;
		if ( isSegmented() ) {
			newCont.segments = new ArrayList<TextFragment>(segments);
		}
		return newCont;
	}
	
	public boolean isSegmented () {
		return ((segments != null) && (segments.size() > 0));
	}
	
	public List<TextFragment> getSegments () {
		return segments;
	}
	
	public void setSegments (ArrayList<TextFragment> segments) {
		this.segments = segments;
	}

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
				if ( Integer.parseInt(code.getData()) == segmentIndex ) {
					return subSequence(start, i-1);
				}
				else start = i+1; // Reset the start of the fragment
			}
		}
		return null;
	}
	
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
				if ( Integer.parseInt(code.getData()) == segmentIndex ) {
					start = i+1;
				}
				else if ( Integer.parseInt(code.getData()) == segmentIndex+1 ) {
					return subSequence(start, i-1);
				}
			}
		}
		if ( start == -1 ) return null; // Not found such segment
		// Else: was the last segment marker
		return subSequence(start, text.length());
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
		int diff = 0;
		for ( int i=0; i<ranges.size(); i++ ) {
			// Add the new segment in the list
			segments.add(subSequence(ranges.get(i).x+diff, ranges.get(i).y+diff));
			// Remove it from the main content
			int width = ranges.get(i).y-ranges.get(i).x;
			// For chunks < 2 there is no codes so we can just add the needed room for the segment marker
			if ( width == 1 ) insert(ranges.get(i).x+diff, new TextFragment(null, "Z"));
			else if ( width == 0 ) insert(ranges.get(i).x+diff, new TextFragment(null, "ZZ"));
			else { // Otherwise: we need to remove the chunk of coded text and its codes
				remove(ranges.get(i).x+diff, ranges.get(i).y+diff);
				// then re-insert room for the segment marker
				insert(ranges.get(i).x+diff, new TextFragment(null, "ZZ"));
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
					if ( segmentIndex == Integer.parseInt(code.getData()) ) {
						pos1 = i-1;
					}
				}
				else { // Search for right segment
					seg2Index = Integer.parseInt(code.getData());
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
				if ( segmentIndex == Integer.parseInt(code.getData()) ) {
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
		else if ( width == 1 ) insert(start, new TextFragment(null, "Z"));
		else if ( width == 0 ) insert(start, new TextFragment(null, "ZZ"));
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
	
	public void clear () {
		super.clear();
		if ( segments != null ) segments.clear();
	}
	
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
				int index = Integer.parseInt(code.getData());
				int add = segments.get(index).getCodedText().length();
				// Remove the segment marker
				remove(i-1, i+1);
				// Insert the segment
				insert(i-1, segments.get(index));
				// Adjust the value of i so it is at the end of the new segment
				i += (add-2); // -2 = size of code marker
				break;
			}
		}
		
		// Re-initialize the list of segments
		segments.clear();
		segments = null;
	}
	
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
				code.setData(String.valueOf(indexValue++));
				break;
			}
		}
		
	}

}
