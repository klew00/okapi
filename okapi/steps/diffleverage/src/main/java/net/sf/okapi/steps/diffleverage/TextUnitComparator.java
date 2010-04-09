package net.sf.okapi.steps.diffleverage;

import java.util.Comparator;

import net.sf.okapi.common.resource.TextUnit;

/**
 * Compare two source {@link TextUnit}s. Text, isReferent() and (optionally) codes must all be same in order to be a
 * match.
 * 
 * @author HARGRAVEJE
 * 
 */
public class TextUnitComparator implements Comparator<TextUnit> {
	private boolean codeSensitive;

	public TextUnitComparator(final boolean codeSensitive) {
		this.codeSensitive = codeSensitive;
	}

	public void setCodeSensitive(boolean codeSensitive) {
		this.codeSensitive = codeSensitive;
	}

	public boolean isCodeSensitive() {
		return codeSensitive;
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
			return oldTextUnit.getSource().compareTo(newTextUnit.getSource(), codeSensitive);
		}
	}
}
