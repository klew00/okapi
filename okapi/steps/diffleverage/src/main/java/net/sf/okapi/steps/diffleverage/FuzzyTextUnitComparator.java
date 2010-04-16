package net.sf.okapi.steps.diffleverage;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.search.lucene.analysis.AlphabeticNgramTokenizer;
import net.sf.okapi.lib.search.lucene.scorer.Util;

/**
 * Fuzzily Compare two source {@link TextUnit}s. Text, isReferent() and (optionally) codes must all be same in order to
 * be a match. The fuzzy compare uses n-grams and does a in memory comparison to get a score. Only scores greater than {
 * {@link #threshold} are considered matches.
 * 
 * @author HARGRAVEJE
 * 
 */
public class FuzzyTextUnitComparator implements Comparator<TextUnit> {
	private static final int NGRAM_SIZE = 3;

	private boolean codeSensitive;
	private int threshold;
	private final AlphabeticNgramTokenizer tokenizer;
	private Set<String> oldSourceTokens;
	private Set<String> newSourceTokens;

	public FuzzyTextUnitComparator(final boolean codeSensitive, final int threshold,
			final LocaleId localeid) {
		this.codeSensitive = codeSensitive;
		setThreshold(threshold);
		tokenizer = new AlphabeticNgramTokenizer(new StringReader(""), NGRAM_SIZE, localeid
				.toJavaLocale());
		oldSourceTokens = new HashSet<String>();
		newSourceTokens = new HashSet<String>();
	}

	public void setCodeSensitive(final boolean codeSensitive) {
		this.codeSensitive = codeSensitive;
	}

	public boolean isCodeSensitive() {
		return codeSensitive;
	}

	public void setThreshold(final int threshold) {
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
			final int result = oldTextUnit.getSource().compareTo(newTextUnit.getSource(),
					codeSensitive);
			if (result == 0) {
				return result;
			} else {
				// do fuzzy compare
				return fuzzyCompare(oldTextUnit, newTextUnit, result);
			}
		}
	}

	private int fuzzyCompare(final TextUnit oldSource, final TextUnit newSource,
			int exactCompareResult) {
		oldSourceTokens.clear();
		newSourceTokens.clear();

		try {
			// get old source string tokens
			tokenizer.reset(new StringReader(oldSource.getSource().getUnSegmentedContentCopy()
					.getText()));
			while (tokenizer.incrementToken()) {
				oldSourceTokens.add(tokenizer.getTermAttribute().term());
			}
			// get the new source tokens
			tokenizer.reset(new StringReader(newSource.getSource().getUnSegmentedContentCopy()
					.getText()));
			while (tokenizer.incrementToken()) {
				newSourceTokens.add(tokenizer.getTermAttribute().term());
			}
		} catch (IOException e) {
			throw new OkapiBadStepInputException("Error tokenizing source TextUnits", e);
		}

		// now calculate dice coefficient to get fuzzy score
		int oldSize = oldSourceTokens.size();
		int newSize = newSourceTokens.size();
		oldSourceTokens.retainAll(newSourceTokens);
		int intersectionSize = oldSourceTokens.size();
		float score = Util.calculateDiceCoefficient(intersectionSize, oldSize, newSize);
		if (score >= threshold) {
			return 0;
		}

		return exactCompareResult;
	}
}
