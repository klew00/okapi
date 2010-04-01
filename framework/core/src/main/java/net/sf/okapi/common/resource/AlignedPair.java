package net.sf.okapi.common.resource;

import java.util.List;

import net.sf.okapi.common.LocaleId;

/**
 * An aligned pair consists of a source and target list of {@link TextPart}s, along with a {@link LocaleId} defining the
 * locale of the target {@link TextPart}s. An AlignedPair is read-only.
 * 
 * @author HARGRAVEJE
 * 
 */
public class AlignedPair {
	private final List<TextPart> sourceParts;
	private final List<TextPart> targetParts;
	private final LocaleId localeId;

	/**
	 * Create an AlignedPair from source and target {@link TextPart}s
	 * 
	 * @param sourceParts
	 *            List source inter-segment and segment parts
	 * @param targetParts
	 *            List target inter-segment and segment parts
	 * @param localeId
	 *            LocaleId of the target parts
	 */
	public AlignedPair(final List<TextPart> sourceParts, final List<TextPart> targetParts,
			final LocaleId localeId) {
		this.sourceParts = sourceParts;
		this.targetParts = targetParts;
		this.localeId = localeId;
	}

	/**
	 * Get the source {@link TextPart}s
	 * 
	 * @return list of {@link TextPart}s
	 */
	public List<TextPart> getSourceParts() {
		return sourceParts;
	}

	/**
	 * Get the target {@link TextPart}s
	 * 
	 * @return list of {@link TextPart}s
	 */
	public List<TextPart> getTargetParts() {
		return targetParts;
	}

	/**
	 * Get the {@link LocaleId} of the target parts
	 * 
	 * @return a {@link LocaleId}
	 */
	public LocaleId getLocaleId() {
		return localeId;
	}
}
