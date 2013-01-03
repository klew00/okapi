package net.sf.okapi.lib.segmentation.opennlp;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.TextContainer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class OkapiMaxEntSegmenter implements ISegmenter {

	private static final String DEFAULT_MODEL_PATH = "net/sf/okapi/lib/segmentation/opennlp/sent";
	private SentenceDetectorME sentenceDetector;
	private List<Range> ranges;
	private SentenceModel model;
	private LocaleId locale;
	private Span[] sentencePositions;

	public OkapiMaxEntSegmenter(URI modelPath, LocaleId locale) {		
		this.locale = locale;
		ranges = new LinkedList<Range>();
		
		String mp;
		if (modelPath == null) {
			// find a locale based default model
			mp = FileUtil.getLocaleBasedFile(DEFAULT_MODEL_PATH, "bin", locale);
			if (mp == null) {
				throw new OkapiIOException("Cannot find default OpenNLP sentence breaking model for: " + locale.toString());
			}
		} else {
			mp = modelPath.getPath();
		}

		try {
			model = new SentenceModel(new FileInputStream(mp));
			this.sentenceDetector = new SentenceDetectorME(model, new OkapiSentenceDetectorFactory(locale));
		} catch (Exception e) {
			throw new OkapiIOException("Error loading OpenNLP sentence breaking model: " + mp, e);
		}
	}

	@Override
	public int computeSegments(String text) {
		TextContainer tmp = new TextContainer(text);
		return computeSegments(tmp);
	}

	@Override
	public int computeSegments(TextContainer container) {
		// Do we have codes?
		// Avoid to create an un-segmented copy if we can
		boolean hasCode;
		if (container.contentIsOneSegment()) {
			hasCode = container.getSegments().getFirstContent().hasCode();
		} else {
			hasCode = container.getUnSegmentedContentCopy().hasCode();
		}
		String codedText = container.getCodedText();
		
		sentencePositions = sentenceDetector.sentPosDetect(codedText);
		for (Span s : sentencePositions) {
			Range r = new Range(s.getStart(), s.getEnd());
			ranges.add(r);
		}
		return sentencePositions.length;
	}

	@Override
	public Range getNextSegmentRange(TextContainer container) {
		return null;
	}

	@Override
	public List<Integer> getSplitPositions() {
		List<Integer> positions = new LinkedList<Integer>();
		for (Span s : sentencePositions) {
			positions.add(s.getEnd());
		}
		return Collections.unmodifiableList(positions);
	}

	@Override
	public List<Range> getRanges() {
		return Collections.unmodifiableList(ranges);
	}

	@Override
	public LocaleId getLanguage() {
		return locale;
	}
}
