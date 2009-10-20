package net.sf.okapi.tm.pensieve.queries;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.tm.pensieve.scorers.TmFuzzyScorer;
import net.sf.okapi.tm.pensieve.scorers.TmFuzzySimilarity;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

@SuppressWarnings("serial")
public class TmFuzzyQuery extends Query {
	List<Term> terms;
	float threshold;
	
	public TmFuzzyQuery(float threshold) {
		terms = new LinkedList<Term>();
		this.threshold = threshold;
	}

	public void add(Term term) {
		terms.add(term);
	}
	
	@Override
	public Weight createWeight(Searcher searcher) throws IOException {
		return new TmFuzzyWeight(searcher);
	}
	
	@Override
	public Query rewrite(IndexReader reader) throws IOException {
		return this;
	}

	@Override
	public String toString(String field) {
		return terms.toString();
	}

	protected class TmFuzzyWeight extends Weight {
		Similarity similarity;

		public TmFuzzyWeight(Searcher searcher) throws IOException {
			super();
			this.similarity = new TmFuzzySimilarity();
		}

		@Override
		public Explanation explain(IndexReader reader, int doc)
				throws IOException {
			return new Explanation(getValue(), toString());
		}

		@Override
		public Query getQuery() {
			return TmFuzzyQuery.this;
		}

		@Override
		public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder,
				boolean topScorer) throws IOException {

			if (terms.size() == 0) // optimize zero-term case
				return null;

			Term[] termArray = new Term[terms.size()];
			termArray = terms.toArray(termArray);
			MultipleTermPositions multipleTermPositions = new MultipleTermPositions(
					reader, termArray);
			return new TmFuzzyScorer(threshold, similarity, terms, multipleTermPositions, reader);
		}

		@Override
		public float getValue() {
			return 1.0f;
		}

		@Override
		public void normalize(float norm) {
		}

		@Override
		public float sumOfSquaredWeights() throws IOException {
			return 1.0f;
		}
	}
}
