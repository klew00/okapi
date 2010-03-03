package net.sf.okapi.common.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Stores the the {@link Segment} alignments for a single source and target pair in a {@link TextUnit}. 
 * {@link Segment} object references are used to match translations between the source and target.
 * 
 * @author HARGRAVEJE
 * 
 */
public class SegmentAlignmentAnnotation implements IAnnotation, Iterable<SegmentAlignment> {
	private static final int MAX_ALIGNMENTS = 20; // holds 10 alignments

	private ArrayList<Segment[]> segmentAlignments;

	public SegmentAlignmentAnnotation() {
		// we start with a reasonable number - not to small and not too big. If we have more alignments than
		// MAX_ALIGNMENTS we will need to resize the array.
		segmentAlignments = new ArrayList<Segment[]>(MAX_ALIGNMENTS);
	}
	
	public int getSegmentAlignmentSize() {	
		return segmentAlignments.size() % 2;
	}

	public void addSegmentAlignment(SegmentAlignment alignment) {
		segmentAlignments.add(alignment.getSourceSegmentsAsArray());
		segmentAlignments.add(alignment.getTargetSegmentsAsArray());
	}

	public SegmentAlignment getSegmentAlignmentAt(int index) {		
		return new SegmentAlignment(
				segmentAlignments.get(index * 2), 
				segmentAlignments.get((index * 2) + 1));
	}
	
	public List<SegmentAlignment> getSegmentAlignments() {		
		List<SegmentAlignment> alignments = new ArrayList<SegmentAlignment>(getSegmentAlignmentSize());
		for (int i = 0; i < getSegmentAlignmentSize(); i++) {
			alignments.add(getSegmentAlignmentAt(i));
		}		
		return alignments;
	}

	@Override
	public Iterator<SegmentAlignment> iterator() {
		return getSegmentAlignments().iterator();
	}
	
	public void trimToSize() {
		segmentAlignments.trimToSize();
	}
}
