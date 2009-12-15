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
import java.util.Vector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.SpanWeight;

/**
 *
 * @author  HargraveJE
 */
public class FuzzyQuery
extends PhraseQuery
{
    private String field;
    private Vector terms = new Vector();
    private int slop = 0;
    
    /** Creates a new instance of FuzzyQuery */
    public FuzzyQuery()
    {
    }
    
    public void setSlop(int s)
    { slop = s; }
    
    /** Returns the slop.  See setSlop(). */
    public int getSlop()
    { return slop; }
    
    /** Adds a term to the end of the query phrase. */
    public void add(Term term)
    {
        if (terms.size() == 0)
            field = term.field();
        else if (term.field() != field)
            throw new IllegalArgumentException
            ("All phrase terms must be in the same field: " + term);
        
        terms.addElement(term);
    }
    
    /** Returns the set of terms in this phrase. */
    public Term[] getTerms()
    {
        return (Term[])terms.toArray(new Term[0]);
    }
    
    private class FuzzyWeight extends Weight // DWH 9-8-09 no longer an interface */
    {
        private Searcher searcher;
        private float value;
        private float idf;
        private float queryNorm;
        private float queryWeight;
        
        public FuzzyWeight(Searcher searcher)
        {
            this.searcher = searcher;
        }
        
        public Query getQuery()
        { return FuzzyQuery.this; }
        
        public float getValue()
        { return value; }
        
        public float sumOfSquaredWeights() throws IOException
        {
//          idf = searcher.getSimilarity().idf(terms, searcher); DWH 9-12-09 deprecated
        	  idf = searcher.getSimilarity().idfExplain(terms,searcher).getIdf(); // DWH 9-12-09
            queryWeight = idf * getBoost();             // compute query weight
            return queryWeight * queryWeight;           // square it
        }
        
        public void normalize(float queryNorm)
        {
            this.queryNorm = queryNorm;
            queryWeight *= queryNorm;                   // normalize query weight
            value = queryWeight * idf;                  // idf for document
        }
        
				@Override
				public Scorer scorer(IndexReader arg0, boolean arg1, boolean arg2)
						throws IOException {
					return fscorer(arg0); // DWH 9-11-09 Lucene 2.9 works as did before DWH 9-14-09 was scorer
					 // but this would be better to depend on the boolean parameters
				}

				public FuzzyScorer fscorer(IndexReader arg0, boolean arg1, boolean arg2)
				  // DWH 9-14-09 copy of scorer method, but this doesn't override a Weight method 
					throws IOException {
						return fscorer(arg0); // DWH 9-14-09 Lucene 2.9, was scorer
				}

				public FuzzyScorer fscorer(IndexReader reader) throws IOException
				  // DWH 9-14-09 was Scorer scorer
        {
            if (terms.size() == 0)			  // optimize zero-term case
                return null;
            
            Term[] ta = getTerms();
            TermPositions[] tps = new TermPositions[terms.size()];
            for (int i = 0; i < ta.length; i++)
            {
                TermPositions p = reader.termPositions(ta[i]);
                if (p == null)
                    return null;
                tps[i] = p;
            }
            MultipleTermPositions mtp = new MultipleTermPositions(reader, ta);
            return new FuzzyScorer(this, ta, searcher.getSimilarity(), slop, tps, mtp, reader, reader.norms(field));
        }
        
        @SuppressWarnings("deprecation")
				public Explanation explain(IndexReader reader, int doc)
        throws IOException
        {
            
            Explanation result = new Explanation();
            result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");
            
            StringBuffer docFreqs = new StringBuffer();
            StringBuffer query = new StringBuffer();
            query.append('\"');
            for (int i = 0; i < terms.size(); i++)
            {
                if (i != 0)
                {
                    docFreqs.append(" ");
                    query.append(" ");
                }
                
                Term term = (Term)terms.elementAt(i);
                
                docFreqs.append(term.text());
                docFreqs.append("=");
                docFreqs.append(searcher.docFreq(term));
                
                query.append(term.text());
            }
            query.append('\"');
            
            Explanation idfExpl =
            new Explanation(idf, "idf(" + field + ": " + docFreqs + ")");
            
            // explain query weight
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");
            
            Explanation boostExpl = new Explanation(getBoost(), "boost");
            if (getBoost() != 1.0f)
                queryExpl.addDetail(boostExpl);
            queryExpl.addDetail(idfExpl);
            
            Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
            queryExpl.addDetail(queryNormExpl);
            
            queryExpl.setValue(boostExpl.getValue() *
            idfExpl.getValue() *
            queryNormExpl.getValue());
            
            result.addDetail(queryExpl);
            
            // explain field weight
            Explanation fieldExpl = new Explanation();
            fieldExpl.setDescription("fieldWeight("+field+":"+query+" in "+doc+
            "), product of:");
            
            Explanation tfExpl = fscorer(reader).fuzzyExplain(doc);
              // DWH 9-14-09 was scorer(reader).explain(doc)
            fieldExpl.addDetail(tfExpl);
            fieldExpl.addDetail(idfExpl);
            
            Explanation fieldNormExpl = new Explanation();
            byte[] fieldNorms = reader.norms(field);
            float fieldNorm =
            fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
            fieldExpl.addDetail(fieldNormExpl);
            
            fieldExpl.setValue(tfExpl.getValue() *
            idfExpl.getValue() *
            fieldNormExpl.getValue());
            
            result.addDetail(fieldExpl);
            
            // combine them
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());
            
            if (queryExpl.getValue() == 1.0f)
                return fieldExpl;
            
            return result;
        }
    }
    
    public Weight createWeight(Searcher searcher)
    {
        return new FuzzyWeight(searcher);
    }
    
    /** Prints a user-readable version of this query. */
    public String toString(String f)
    {
        StringBuffer buffer = new StringBuffer();
        if (!field.equals(f))
        {
            buffer.append(field);
            buffer.append(":");
        }
        
        buffer.append("\"");
        for (int i = 0; i < terms.size(); i++)
        {
            buffer.append(((Term)terms.elementAt(i)).text());
            if (i != terms.size()-1)
                buffer.append(" ");
        }
        buffer.append("\"");
        
        if (slop != 0)
        {
            buffer.append("~");
            buffer.append(slop);
        }
        
        if (getBoost() != 1.0f)
        {
            buffer.append("^");
            buffer.append(Float.toString(getBoost()));
        }
        
        return buffer.toString();
    }
    
    /** Returns true iff <code>o</code> is equal to this. */
    public boolean equals(Object o)
    {
        if (!(o instanceof FuzzyQuery))
            return false;
        FuzzyQuery other = (FuzzyQuery)o;
        return (this.getBoost() == other.getBoost())
        && (this.slop == other.slop)
        &&  this.terms.equals(other.terms);
    }
}
