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
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;

final class ConcordanceFuzzyScorer extends Scorer {
	private static int max(int x1, int x2) {
		return (x1 > x2 ? x1 : x2);
	}

	private static int max(int x1, int x2, int x3, int x4) {
		return max(max(x1, x2), max(x3, x4));
	}

	private List<Term> terms;
	private MultipleTermPositions mtp;
	private TermPositions[] tps;
	private IndexReader reader;
	private float score;
	private int[] query;
	private int matches = 0;
	private float threshold;

	ConcordanceFuzzyScorer(float threshold, Similarity similarity, List<Term> terms,
			TermPositions[] tps, MultipleTermPositions mtp, IndexReader reader) throws IOException {
		super(similarity);
		this.threshold = threshold;
		this.terms = terms;
		this.mtp = mtp;
		this.tps = tps;
		this.reader = reader;

		// initialize term symbols for locale alignment
		query = new int[terms.size()];
		for (int i = 1; i < terms.size(); i++) {
			query[i] = i;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.search.Scorer#next()
	 */
	public boolean next() throws IOException {
		if (!mtp.next()) {
			mtp.close();
			return false;
		}

		return findNext();
	}

	private boolean findNext() throws IOException {
		// quick and dirty measure for bad matches
		while (true) {
			if (calculateSimpleFilter() > threshold && (calculateScore(mtp.doc())) > threshold) {
				break; // we found a match
			} else {
				if (mtp.next())
					continue;
				else
					return false;
			}
		}
		return true;
	}

	public int doc() {
		return mtp.doc();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.lucene.search.Scorer#score()
	 */
	public float score() throws IOException {
		return score;
	}

	/*
	 * DWH 9-14-09 deprecated in Lucene 2.9 but not used anyway public boolean skipTo(int doc) throws IOException {
	 * return mtp.skipTo(doc); }
	 */
	private float calculateSimpleFilter() {
		return (float) mtp.freq() / (float) terms.size();
	}

	private float calculateScore(int d) throws IOException {
		int[] hit = null;
		int[] query = new int[terms.size()];
		int maxpos = 0;
		int minpos = 0;

		// find maximum position in the hit
		minpos = mtp.nextPosition();
		for (int i = 1; i < mtp.freq(); i++) {
			maxpos = mtp.nextPosition();
		}

		int size = (maxpos - minpos) + 1;
		if (maxpos <= 0) {
			size = 1;
		}
		hit = new int[size];

		for (int i = 0; i < terms.size(); i++) {
			query[i] = i + 1;
			TermPositions tp = tps[i];
			if (tp == null)
				continue; // already reached the end
			if (!tp.skipTo(d)) // end of the line, no more docs for this term
			{
				tps[i].close();
				tps[i] = null;
				continue;
			}

			if (tp.doc() != d) {
				tps[i] = reader.termPositions(terms.get(i));
				// start over. TODO how to restart without creating a new
				// stream?
				continue; // this term must not be in our document
			}

			for (int j = 0; j < tp.freq(); j++) {
				int p = tp.nextPosition();
				hit[p - minpos] = i + 1;
			}

		}
		float score = editDistance(hit, query);
		return score;
	}

	private class TraceBack {
		public int i;

		public int j;

		public TraceBack(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	private float editDistance(int[] seq1, int[] seq2) {
		int d = 1;
		int n = seq1.length, m = seq2.length;
		int[][] F = new int[n + 1][m + 1]; // acummulate scores
		TraceBack[][] T = new TraceBack[n + 1][m + 1]; // path traceback
		int s = 0;
		int maxi = n, maxj = m;
		int maxval = Integer.MIN_VALUE;
		TraceBack start;

		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				s = 0;
				if (seq1[i - 1] == seq2[j - 1])
					s = 2;

				int val = max(0, F[i - 1][j - 1] + s, F[i - 1][j] - d, F[i][j - 1] - d);
				F[i][j] = val;
				if (val == 0)
					T[i][j] = null;
				else if (val == F[i - 1][j - 1] + s)
					T[i][j] = new TraceBack(i - 1, j - 1);
				else if (val == F[i - 1][j] - d)
					T[i][j] = new TraceBack(i - 1, j);
				else if (val == F[i][j - 1] - d)
					T[i][j] = new TraceBack(i, j - 1);
				if (val > maxval) {
					maxval = val;
					maxi = i;
					maxj = j;
				}
			}
		}
		start = new TraceBack(maxi, maxj);

		// retrace the optimal path and calculate score
		matches = 0;
		TraceBack tb = start;
		int i = tb.i;
		int j = tb.j;
		while ((tb = next(tb, T)) != null) {
			i = tb.i;
			j = tb.j;
			if (seq1[i] == seq2[j])
				matches++;
		}
		return (float) matches / (float) terms.size();
		// return (float) mtp.freq() / (float) matches; // for queries whose text is large or equal to the hits
	}

	private TraceBack next(TraceBack tb, TraceBack[][] tba) {
		TraceBack tb2 = tb;
		return tba[tb2.i][tb2.j];
	}

	@Override
	public int advance(int target) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int docID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int nextDoc() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
}