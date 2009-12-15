/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2001 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Apache" and "Apache Software Foundation" and "Apache Lucene"
 * must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact
 * apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", "Apache
 * Lucene", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE
 * SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Apache Software Foundation. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */

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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
//import org.apache.lucene.search.Collector; // DWH 9-8-09 was deprecated HitCollector
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

final class FuzzyScorer extends Scorer {
    /**
     * Finds and returns the smallest of three integers
     */
    private static final int min(int a, int b, int c) {
        int t = (a < b) ? a : b;
        return (t < c) ? t : c;
    }

    private static int max(int x1, int x2) {
        return (x1 > x2 ? x1 : x2);
    }

    private static int max(int x1, int x2, int x3) {
        return max(x1, max(x2, x3));
    }

    private static int max(int x1, int x2, int x3, int x4) {
        return max(max(x1, x2), max(x3, x4));
    }

    /**
     * This static array saves us from the time required to create a new array
     * everytime editDistance is called.
     */
    private int e[][] = new int[1][1];

    private int slop;

    //    private int doc;
    private Weight weight;

    private Term[] terms;

    private MultipleTermPositions mtp;

    private TermPositions[] tps;

    private Similarity similarity;

    private IndexReader reader;

    private float value;

    private float score;

    private int[] query;

    private byte[] norms;

    private int matches = 0;

    private boolean isNext = true;

    FuzzyScorer(Weight weight, Term[] terms, Similarity similarity, int slop,
            TermPositions[] tps, MultipleTermPositions mtp, IndexReader reader,
            byte[] norms) throws IOException {
        super(similarity);
        this.slop = slop;
        this.terms = terms;
        this.weight = weight;
        this.mtp = mtp;
        this.tps = tps;
        this.reader = reader;
        this.value = weight.getValue();
        this.norms = norms;

        // intit term symbols for locale alignment
        query = new int[terms.length];
        for (int i = 1; i < terms.length; i++) {
            query[i] = i;
        }
    }

    public Explanation fuzzyExplain(int doc) throws IOException { // DWH 9-14-09 for Lucene 2.9, was explain
        Explanation tfExplanation = new Explanation();

        float phraseFreq = (mtp.doc() == doc) ? 1.0f : 0.0f;
        tfExplanation.setValue(getSimilarity().tf(phraseFreq));
        tfExplanation.setDescription("tf(FuzzyMatch=" + phraseFreq + ")");

        return tfExplanation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Scorer#next()
     */
/* DWH 9-14-09 deprecated in Lucene 2.9 but not used anyway
    public boolean next() throws IOException {
        if (!mtp.next()) {
            mtp.close();
            return false;
        }

        return findNext();
    }

    private boolean findNext() throws IOException {
        float threshold = (float) slop * 0.01f;
        float s = 0.0f;

        Similarity similarity = getSimilarity();

        // quick and dirty measure for bad matches
        while (true) {
            if (calculateSimpleFilter() > threshold &&
                 (s = calculateScore(mtp.doc())) > threshold) {
                break; // we found a match
            } else {
                if (mtp.next())
                    continue;
                else
                    return false;
            }
        }

        score = s * value;

        return true;
    }

    public int doc() {
        return mtp.doc();
    }
*/
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Scorer#score()
     */
    public float score() throws IOException {
        return score;
    }
/* DWH 9-14-09 deprecated in Lucene 2.9 but not used anyway
    public boolean skipTo(int doc) throws IOException {
        return mtp.skipTo(doc);
    }
*/
    private float calculateSimpleFilter() {
        return (float) mtp.freq() / (float) terms.length;
    }

    private float calculateScore(int d) throws IOException {
        int[] hit = null;
        int[] query = new int[terms.length];
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

        for (int i = 0; i < terms.length; i++) {
            query[i] = i + 1;
            TermPositions tp = tps[i];
            if (tp == null)
                continue; // already reached the end
            if (!tp.skipTo(d)) //end of the line, no more docs for this term
            {
                tps[i].close();
                tps[i] = null;
                continue;
            }
            //int doc = tp.doc(); //debug
            if (tp.doc() != d) {
                tps[i] = reader.termPositions(terms[i]);
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

                int val = max(0, F[i - 1][j - 1] + s, F[i - 1][j] - d,
                        F[i][j - 1] - d);
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
        return (float) matches / (float) terms.length;
        //return (float) mtp.freq() / (float) matches;  // for queries whose text is large or equal to the hits
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