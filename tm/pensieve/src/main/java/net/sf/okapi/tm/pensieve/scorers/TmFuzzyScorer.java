package net.sf.okapi.tm.pensieve.scorers;

import java.io.IOException;
import java.util.List;

import net.sf.okapi.tm.pensieve.common.TranslationUnitField;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;

public class TmFuzzyScorer extends Scorer {
	// note that the ROUGH_THRESHOLD is the lowest accepted threshold
	// TODO: externalize this
	private static float ROUGH_THRESHOLD = 0.65f;
	private List<Term> terms;
	private MultipleTermPositions multiTermPositions;
	private IndexReader reader;
	private float threshold;
	private float score;

	public TmFuzzyScorer(float threshold, Similarity similarity,
			List<Term> terms, MultipleTermPositions multiTermPositions,
			IndexReader reader) throws IOException {
		super(similarity);
		this.terms = terms;
		this.multiTermPositions = multiTermPositions;
		this.reader = reader;
		this.threshold = threshold;
	}

	@Override
	public int advance(int target) throws IOException {
		if (target == NO_MORE_DOCS) {
			return NO_MORE_DOCS;
		}
		
		int doc;
		while ((doc = nextDoc()) < target) {
		}
		return doc;
	}

	@Override
	public float score() throws IOException {
		return (float) score;
	}

	@Override
	public int nextDoc() throws IOException {
		if (!multiTermPositions.next()) {
			multiTermPositions.close();
			return NO_MORE_DOCS;
		}
		return findNextDoc(multiTermPositions.doc());
	}

	// TODO: simplify this logic
	private int findNextDoc(int currentDoc) throws IOException {
		while (true) {
			if (calculateRoughThreshold() >= ROUGH_THRESHOLD
					&& calculateThreshold(currentDoc) >= threshold) {
				break; // we found a match
			} else {
				if (multiTermPositions.next()) {
					currentDoc = multiTermPositions.doc();
					continue;
				} else {
					multiTermPositions.close();
					return NO_MORE_DOCS;
				}
			}
		}
		return multiTermPositions.doc();
	}

	// quick and dirty score to eliminate most of the very low scoring documents
	private float calculateRoughThreshold() {
		return (float) multiTermPositions.freq() / (float) terms.size();		
	}

	// calculate full dice coefficient
	private float calculateThreshold(int currentDoc) throws IOException {
		TermFreqVector tv = reader.getTermFreqVector(currentDoc,
				TranslationUnitField.SOURCE.name());
		score = (float) ((2.0f * (float) multiTermPositions.freq()) / 
					(float) (tv.size() + terms.size())) * 100.0f;
		return score;
	}
}
