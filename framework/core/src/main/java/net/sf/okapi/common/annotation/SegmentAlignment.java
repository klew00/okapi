package net.sf.okapi.common.annotation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.resource.Segment;

public class SegmentAlignment {
	private List<Segment> sourceSegments;
	private List<Segment> targetSegments;
	
	public SegmentAlignment() {
		sourceSegments = new LinkedList<Segment>();
		targetSegments = new LinkedList<Segment>();
	}
	
	SegmentAlignment(Segment[] source, Segment[] target) {
		sourceSegments = Arrays.asList(source);
		targetSegments = Arrays.asList(target);
	}
	
	public void addSourceSegment(Segment segment) {
		sourceSegments.add(segment);
	}

	public void addTargetSegment(Segment segment) {
		targetSegments.add(segment);
	}
	
	int getSourceSegmentSize() {
		return sourceSegments.size();
	}
	
	int getTargetSegmentSize() {
		return targetSegments.size();
	}
	
	public List<Segment> getSourceSegments() {
		return sourceSegments;
	}
	
	public List<Segment> getTargetSegments() {
		return targetSegments;
	}
	
	Segment[] getSourceSegmentsAsArray() {
		Segment[] s = null;
		if (sourceSegments.size() > 0) {
			s = new Segment[sourceSegments.size()];
			sourceSegments.toArray(s);
		}
		return s;
	}
	
	Segment[] getTargetSegmentsAsArray() {
		Segment[] t = null;
		if (targetSegments.size() > 0) {
			t = new Segment[targetSegments.size()];
			sourceSegments.toArray(t);
		}
		return t;
	}
}
