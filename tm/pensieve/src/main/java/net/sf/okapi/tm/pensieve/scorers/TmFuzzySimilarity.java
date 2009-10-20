package net.sf.okapi.tm.pensieve.scorers;

import org.apache.lucene.search.Similarity;

public class TmFuzzySimilarity extends Similarity {
	private static final long serialVersionUID = -6299053650170316232L;
	
	@Override
	public float coord(int overlap, int maxOverlap) {
		return 1.0f;
	}

	@Override
	public float idf(int docFreq, int numDocs) {
		return 1.0f;
	}

	@Override
	public float lengthNorm(String fieldName, int numTokens) {
		return 1.0f;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		return 1.0f;
	}

	@Override
	public float sloppyFreq(int distance) {
		return 1.0f;
	}

	@Override
	public float tf(float freq) {
		return 1.0f;
	}
}
