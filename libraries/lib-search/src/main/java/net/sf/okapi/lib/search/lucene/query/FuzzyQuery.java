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
import java.util.Vector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

/**
 * 
 * @author HargraveJE
 */
public class FuzzyQuery extends PhraseQuery {
	private String field;
	private Vector terms = new Vector();
	private int slop = 0;

	/** Creates a new instance of FuzzyQuery */
	public FuzzyQuery() {
	}

	public void setSlop(int s) {
		slop = s;
	}

	/** Returns the slop. See setSlop(). */
	public int getSlop() {
		return slop;
	}

	/** Adds a term to the end of the query phrase. */
	public void add(Term term) {
		if (terms.size() == 0)
			field = term.field();
		else if (term.field() != field)
			throw new IllegalArgumentException(
					"All phrase terms must be in the same field: " + term);

		terms.addElement(term);
	}

	/** Returns the set of terms in this phrase. */
	public Term[] getTerms() {
		return (Term[]) terms.toArray(new Term[0]);
	}

	private class FuzzyWeight extends Weight {
		private Searcher searcher;
		private float value;
		private float idf;
		private float queryNorm;
		private float queryWeight;

		public FuzzyWeight(Searcher searcher) {
			this.searcher = searcher;
		}

		public Query getQuery() {
			return FuzzyQuery.this;
		}

		public float getValue() {
			return value;
		}

		public float sumOfSquaredWeights() throws IOException {
			idf = searcher.getSimilarity().idf(terms, searcher);
			queryWeight = idf * getBoost(); // compute query weight
			return queryWeight * queryWeight; // square it
		}

		public void normalize(float queryNorm) {
			this.queryNorm = queryNorm;
			queryWeight *= queryNorm; // normalize query weight
			value = queryWeight * idf; // idf for document
		}

		public Scorer scorer(IndexReader reader) throws IOException {
			if (terms.size() == 0) // optimize zero-term case
				return null;

			Term[] ta = getTerms();
			TermPositions[] tps = new TermPositions[terms.size()];
			for (int i = 0; i < ta.length; i++) {
				TermPositions p = reader.termPositions(ta[i]);
				if (p == null)
					return null;
				tps[i] = p;
			}
			MultipleTermPositions mtp = new MultipleTermPositions(reader, ta);
			return new FuzzyScorer(this, ta, searcher.getSimilarity(), slop,
					tps, mtp, reader, reader.norms(field));
		}

		public Explanation explain(IndexReader reader, int doc)
				throws IOException {

			Explanation result = new Explanation();
			result.setDescription("weight(" + getQuery() + " in " + doc
					+ "), product of:");

			StringBuffer docFreqs = new StringBuffer();
			StringBuffer query = new StringBuffer();
			query.append('\"');
			for (int i = 0; i < terms.size(); i++) {
				if (i != 0) {
					docFreqs.append(" ");
					query.append(" ");
				}

				Term term = (Term) terms.elementAt(i);

				docFreqs.append(term.text());
				docFreqs.append("=");
				docFreqs.append(searcher.docFreq(term));

				query.append(term.text());
			}
			query.append('\"');

			Explanation idfExpl = new Explanation(idf, "idf(" + field + ": "
					+ docFreqs + ")");

			// explain query weight
			Explanation queryExpl = new Explanation();
			queryExpl.setDescription("queryWeight(" + getQuery()
					+ "), product of:");

			Explanation boostExpl = new Explanation(getBoost(), "boost");
			if (getBoost() != 1.0f)
				queryExpl.addDetail(boostExpl);
			queryExpl.addDetail(idfExpl);

			Explanation queryNormExpl = new Explanation(queryNorm, "queryNorm");
			queryExpl.addDetail(queryNormExpl);

			queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue()
					* queryNormExpl.getValue());

			result.addDetail(queryExpl);

			// explain field weight
			Explanation fieldExpl = new Explanation();
			fieldExpl.setDescription("fieldWeight(" + field + ":" + query
					+ " in " + doc + "), product of:");

			Explanation tfExpl = scorer(reader).explain(doc);
			fieldExpl.addDetail(tfExpl);
			fieldExpl.addDetail(idfExpl);

			Explanation fieldNormExpl = new Explanation();
			byte[] fieldNorms = reader.norms(field);
			float fieldNorm = fieldNorms != null ? Similarity
					.decodeNorm(fieldNorms[doc]) : 0.0f;
			fieldNormExpl.setValue(fieldNorm);
			fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc="
					+ doc + ")");
			fieldExpl.addDetail(fieldNormExpl);

			fieldExpl.setValue(tfExpl.getValue() * idfExpl.getValue()
					* fieldNormExpl.getValue());

			result.addDetail(fieldExpl);

			// combine them
			result.setValue(queryExpl.getValue() * fieldExpl.getValue());

			if (queryExpl.getValue() == 1.0f)
				return fieldExpl;

			return result;
		}

		@Override
		public Scorer scorer(IndexReader arg0, boolean arg1, boolean arg2)
				throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public Weight createWeight(Searcher searcher) {
		return new FuzzyWeight(searcher);
	}

	/** Prints a user-readable version of this query. */
	public String toString(String f) {
		StringBuffer buffer = new StringBuffer();
		if (!field.equals(f)) {
			buffer.append(field);
			buffer.append(":");
		}

		buffer.append("\"");
		for (int i = 0; i < terms.size(); i++) {
			buffer.append(((Term) terms.elementAt(i)).text());
			if (i != terms.size() - 1)
				buffer.append(" ");
		}
		buffer.append("\"");

		if (slop != 0) {
			buffer.append("~");
			buffer.append(slop);
		}

		if (getBoost() != 1.0f) {
			buffer.append("^");
			buffer.append(Float.toString(getBoost()));
		}

		return buffer.toString();
	}

	/** Returns true iff <code>o</code> is equal to this. */
	public boolean equals(Object o) {
		if (!(o instanceof FuzzyQuery))
			return false;
		FuzzyQuery other = (FuzzyQuery) o;
		return (this.getBoost() == other.getBoost())
				&& (this.slop == other.slop) && this.terms.equals(other.terms);
	}
}
