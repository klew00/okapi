package net.sf.okapi.lib.segmentation.opennlp;

import java.util.List;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.TextContainer;

public class MaxEntSegmenter implements ISegmenter {

	@Override
	public int computeSegments(String text) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeSegments(TextContainer container) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Range getNextSegmentRange(TextContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getSplitPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Range> getRanges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocaleId getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

}
