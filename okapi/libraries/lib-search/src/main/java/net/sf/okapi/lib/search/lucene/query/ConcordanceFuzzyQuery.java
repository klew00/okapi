/*===========================================================================
 Copyright (C) 2008-2009 by the Okapi Framework contributors
 -----------------------------------------------------------------------------
 This library is free software; you can redistribute it and/or modify it 
 under the terms of the GNU Lesser General Public License as published by 
 the Free Software Foundation; either version 2.1 of the License, or (at 
 your option) any later version.

 This library is distributed in the hope that it will be useful, but 
 WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
 General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License 
 along with this library; if not, write to the Free Software Foundation, 
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 ===========================================================================*/

package net.sf.okapi.lib.search.lucene.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.search.lucene.scorer.ConcordanceFuzzyScorer;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

/**
 * 
 * @author HargraveJE
 */
@SuppressWarnings("serial")
public class ConcordanceFuzzyQuery extends Query {
	private String field;
	float threshold;
	private List<Term> terms;
	private int slop = 0;

	/**
	 * Creates a new instance of ConcordanceFuzzyQuery
	 * 
	 * @param threshold
	 */
	public ConcordanceFuzzyQuery(float threshold) {
		terms = new ArrayList<Term>();
		this.threshold = threshold;
	}

	public void setSlop(int slop) {
		this.slop = slop;
	}

	/** Returns the slop. See setSlop(). */
	public int getSlop() {
		return slop;
	}

	/** Adds a term to the end of the query phrase. */
	public void add(Term term) {
		if (terms.size() == 0) {
			field = term.field();
		} else if (term.field() != field) {
			throw new IllegalArgumentException("All phrase terms must be in the same field: "
					+ term);
		}
		terms.add(term);
	}

	/** Returns the set of terms in this phrase. */
	public Term[] getTerms() {
		return (Term[]) terms.toArray(new Term[0]);
	}

	protected class ConcordanceFuzzyWeight extends Weight {
		Similarity similarity;

		public ConcordanceFuzzyWeight(Searcher searcher) throws IOException {
			super();
			this.similarity = searcher.getSimilarity();
		}

		@Override
		public Explanation explain(IndexReader reader, int doc) throws IOException {
			return new Explanation(getValue(), toString());
		}

		@Override
		public Query getQuery() {
			return ConcordanceFuzzyQuery.this;
		}

		@Override
		public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer)
				throws IOException {

			// optimize zero-term or no match case
			if (terms.size() == 0)
				return null;

			Term[] termArray = new Term[terms.size()];
			termArray = terms.toArray(termArray);
			TermPositions[] termPositions = new TermPositions[terms.size()];
			for (int i = 0; i < termArray.length; i++) {
				TermPositions p = reader.termPositions(termArray[i]);
				if (p == null)
					return null;
				termPositions[i] = p;
			}
			MultipleTermPositions multipleTermPositions = new MultipleTermPositions(reader,
					termArray);

			return new ConcordanceFuzzyScorer(threshold, similarity, terms, termPositions,
					multipleTermPositions, reader);
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

	@Override
	public Weight createWeight(Searcher searcher) throws IOException {
		return new ConcordanceFuzzyWeight(searcher);
	}

	@Override
	public String toString(String field) {
		return terms.toString();
	}
}
