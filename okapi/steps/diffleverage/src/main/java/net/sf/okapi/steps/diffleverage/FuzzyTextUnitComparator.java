package net.sf.okapi.steps.diffleverage;

import java.util.Comparator;

import net.sf.okapi.common.resource.TextUnit;

/**
 * Fuzzily Compare two source {@link TextUnit}s. Text, isReferent() and (optionally) codes must all be same in order to
 * be a match. The fuzzy compare uses n-grams and does a in memory comparison to get a score. Only scores greater than {
 * {@link #threshold} are considered matches.
 * 
 * @author HARGRAVEJE
 * 
 */
public class FuzzyTextUnitComparator implements Comparator<TextUnit> {
	private boolean codeSensitive;
	private float threshold;

	public FuzzyTextUnitComparator(boolean codeSensitive, float threshold) {
		this.codeSensitive = codeSensitive;
		this.setThreshold(threshold);
	}

	public void setCodeSensitive(boolean codeSensitive) {
		this.codeSensitive = codeSensitive;
	}

	public boolean isCodeSensitive() {
		return codeSensitive;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return threshold;
	}

	@Override
	public int compare(final TextUnit oldTextUnit, final TextUnit newTextUnit) {
		if (oldTextUnit.isReferent() && !newTextUnit.isReferent()) {
			return -1; // old is greater than new
			// (not sure what greater means in this case but we have to return something)
		} else if (!oldTextUnit.isReferent() && newTextUnit.isReferent()) {
			return 1; // new is greater than old
			// (not sure what greater means in this case but we have to return something)
		} else {
			// both are either referents or not
			int result = oldTextUnit.getSource().compareTo(newTextUnit.getSource(), codeSensitive);
			if (result == 0) {
				return result;
			} else {
				// do fuzzy compare
				return 0;
			}
		}
	}
}
