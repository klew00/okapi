package net.sf.okapi.steps.gcaligner;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.ISegmenter;

/**
 * Helper methods for creating bilingual {@link TextUnit}s from {@link Segment}s. Methods to aid in segmentation.
 * 
 * @author HARGRAVEJE
 * 
 */
public final class Utils {

	/**
	 * Creates a new text unit based on an existing text unit, with its content set to the content of a source and a
	 * target segment. All other targets are removed.
	 * 
	 * @param srcTextUnit
	 *            text unit to use for origin.
	 * @param srcSegment
	 *            source segment.
	 * @param trgSegment
	 *            target segment.
	 * @param trgLocaleId
	 *            target locale.
	 * @return a new text unit with its content set to the given source and target segment.
	 */
	public static TextUnit makeBilingualTextUnit(TextUnit srcTextUnit, Segment srcSegment,
			Segment trgSegment, LocaleId trgLocaleId) {
		// Clone the original
		TextUnit tu = srcTextUnit.clone();
		// Empty the source content
		tu.getSource().clear();
		// Set the new source content: the given source segment
		srcSegment.getContent().trim();
		tu.setSourceContent(srcSegment.getContent());
		// Removes all targets
		for (LocaleId locId : tu.getTargetLocales()) {
			tu.removeTarget(locId);
		}
		// Create a new target with all the source data
		tu.createTarget(trgLocaleId, true, TextUnit.COPY_ALL);
		// Set the new target content: the given target segment
		trgSegment.getContent().trim();
		tu.setTargetContent(trgLocaleId, trgSegment.getContent());
		// Return the result
		return tu;
	}

	/**
	 * Create a bilingual {@link TextUnit} from a source sentence (1-0 match).
	 * 
	 * @param srcTextUnit
	 * @param srcSegment
	 * @return
	 */
	public static TextUnit makeBilingualTextUnit(TextUnit srcTextUnit, Segment srcSegment) {
		TextUnit tu = srcTextUnit.clone();
		// source
		tu.getSource().clear();

		// Set the new source content: the given source segment
		srcSegment.getContent().trim();
		tu.setSourceContent(srcSegment.getContent());

		// target
		for (LocaleId localeId : tu.getTargetLocales()) {
			tu.getTarget(localeId).clear();
		}

		return tu;
	}

	/**
	 * Create a bilingual {@link TextUnit} from a target sentence (0-1 match).
	 * 
	 * @param srcTextUnit
	 * @param trgSegment
	 * @param trgLocaleId
	 * @return
	 */
	public static TextUnit makeBilingualTextUnit(TextUnit srcTextUnit, Segment trgSegment,
			LocaleId trgLocaleId) {
		TextUnit tu = srcTextUnit.clone();

		// source
		tu.getSource().clear();

		// target
		for (LocaleId localeId : tu.getTargetLocales()) {
			tu.getTarget(localeId).clear();
		}
		tu.createTarget(trgLocaleId, true, TextUnit.COPY_ALL);
		tu.setTargetContent(trgLocaleId, trgSegment.getContent());
		trgSegment.getContent().trim();
		tu.getTarget(trgLocaleId).setContent(trgSegment.getContent());
		return tu;
	}

	/**
	 * Create a bilingual {@link TextUnit} from a list of source and target sentences (m-n match).
	 * 
	 * @param srcTextUnit
	 * @param srcSegments
	 * @param trgSegments
	 * @param trgLocaleId
	 * @return
	 */
	public static TextUnit makeBilingualTextUnit(TextUnit srcTextUnit, List<Segment> srcSegments,
			List<Segment> trgSegments, LocaleId trgLocaleId) {
		TextUnit tu = srcTextUnit.clone();

		// source
		tu.getSource().clear();
		for (Segment segment : srcSegments) {
			tu.getSourceContent().append(segment.getContent());
		}
		tu.getSourceContent().trim();
		tu.getSourceContent().renumberCodes();

		// target
		for (LocaleId localeId : tu.getTargetLocales()) {
			tu.getTarget(localeId).clear();
		}
		tu.createTarget(trgLocaleId, true, TextUnit.CREATE_EMPTY);

		for (Segment segment : trgSegments) {
			tu.getTargetContent(trgLocaleId).append(segment.getContent());
		}
		tu.getTarget(trgLocaleId).renumberCodes();
		tu.getTarget(trgLocaleId).trim();

		return tu;
	}

	/**
	 * Helper method that segments TextUnits source content.
	 * 
	 * @param textUnit
	 * @param segmenter
	 * @return
	 */
	public static TextUnit segmentSource(TextUnit textUnit, ISegmenter segmenter) {
		TextContainer tc = textUnit.getSource();
		segmenter.computeSegments(tc);
		tc.createSegments(segmenter.getRanges());
		return textUnit;
	}
}
