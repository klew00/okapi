// Modifications made by the Okapi FrameWork Team under the LGPL licenese
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

package net.sf.okapi.lib.search.lucene.highlight;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

/**
 * Highlighting class used to extract the best sections of text from a document
 * and markup the terms used in the query.
 * 
 * Latest Modifications 01/10/03 + Support for MultiTermQuery, RangeQuery and
 * PrefixQuery highlighting is now reliant on caller using query.rewrite BEFORE
 * calling the highlighter and the search operation. This is to ensure that the
 * expensive rewrite operation that expands search terms is called only once and
 * the rewritten query is used by both the searcher and the highlighter.
 * 
 * Modifications 22/09/03 (Many thanks to Manfred Hardt for his help) + Added
 * support for MultiTermQuery, RangeQuery and PrefixQuery highlighting +
 * Improved readability - contiguous fragments in results are merged into one,
 * in orginal order + Added JUnit tests + Added getBestFragment(String text, int
 * fragmentSize) method
 * 
 * Originally adapted from Maik Schreiber's LuceneTools.java,v 1.5 2001/10/16
 * 07:25:55.
 * 
 * @author Maik Schreiber (mailto: bZ@iq-computing.de) /Mark Harwood
 *         (mark@searcharea.co.uk)
 */
public final class QueryHighlightExtractor {

    private static final int MAX_TEXT_SIZE = 256 * 1024 * 1024; //256 meg
                                                                // should do it.

    private Query query;

    private HashSet terms = new HashSet();

    private HashSet fields = new HashSet();

    private StringBuffer newText;

    private TextHighlighter textHighlighter;

    private boolean extractPhrases = false;

    /**
     * Constructor using a custom term highlighter.
     * 
     * @param highlighter
     *            TermHighlighter to use to highlight terms in the text
     * @param query
     *            Query which contains the terms to be highlighted in the text
     *            (this must be rewritten with query.rewrite(reader) BEFORE
     *            calling this method in order to highlight multiterm queries
     * @param analyzer
     *            Analyzer used to construct the Query
     */
    public QueryHighlightExtractor(TermHighlighter highlighter, Query query,
            Analyzer analyzer) throws IOException {
        this.query = query;
        // get terms in query
        getTerms(query, terms, false);
        this.textHighlighter = new TextHighlighter(highlighter, terms, fields,
                analyzer);
    }

    public QueryHighlightExtractor() throws IOException {
    }

    /**
     * Highlights all of the text in accordance to the given query
     * 
     * @param text
     *            text to highlight terms in
     * @return highlighted text
     */
    public final String highlightText(String text) throws IOException {
        return getBestFragment(text, MAX_TEXT_SIZE);
    }

    /**
     * Returns the most relevant fragment of text up to a user-defined maxSize.
     * Highlights a text in accordance to the given query, extracting the most
     * relevant section. The document text is analysed in fragmentSize chunks to
     * record hit statistics across the document. After accumulating stats, the
     * fragment with the highest score is returned
     * 
     * @param text
     *            text to highlight terms in
     * @param fragmentSize
     *            the size in bytes of the fragment to be returned
     * 
     * @return highlighted text fragment or null
     */
    public final String getBestFragment(String text, int fragmentSize)
            throws IOException {
        return textHighlighter.getBestFragment(text, fragmentSize);
    }

    /**
     * Extracts all term texts of a given Query into a HashSet -potentially of
     * use in areas outside of highlighting. Term texts will be returned in
     * lower-case.
     * 
     * @param query
     *            Query to extract term texts from
     * @param terms
     *            HashSet where extracted term texts should be put into
     *            (Elements: Term or Term[])
     * @param prohibited
     *            <code>true</code> to extract "prohibited" terms, too
     * @param extractPhrases
     *            <code>true</code> if true does NOT decompose phrases into
     *            single terms. HasHSet will then contain Term and Term[]
     *            objects
     */
    public final void getTerms(Query query, HashSet terms, boolean prohibited,
            boolean extractPhrases) throws IOException {
        this.extractPhrases = extractPhrases;

        if (query instanceof BooleanQuery)
            getTermsFromBooleanQuery((BooleanQuery) query, terms, prohibited);
        else if (query instanceof PhraseQuery)
            getTermsFromPhraseQuery((PhraseQuery) query, terms);
        else if (query instanceof TermQuery)
            getTermsFromTermQuery((TermQuery) query, terms);
        else if ((query instanceof PrefixQuery)
                || (query instanceof RangeQuery)
                || (query instanceof MultiTermQuery)) {
            //client should call rewrite BEFORE calling highlighter
//            Query expandedQuery = rewrite(reader, query);
//            getTerms(reader, expandedQuery, terms, prohibited);
        }
    }

    /**
     * Extracts all term texts of a given Query into a HashSet - potentially of
     * use in areas outside of highlighting. Term texts will be returned in
     * lower-case.
     * 
     * @param query
     *            Query to extract term texts from
     * @param terms
     *            HashSet where extracted term texts should be put into
     *            (Elements: String)
     * @param prohibited
     *            <code>true</code> to extract "prohibited" terms, too HasHSet
     *            will then contain Term and Term[] objects
     */
    public final void getTerms(Query query, HashSet terms, boolean prohibited)
            throws IOException {
        getTerms(query, terms, prohibited, false);
    }

    /**
     * Extracts all term texts of a given BooleanQuery. Term texts will be
     * returned in lower-case.
     * 
     * @param query
     *            BooleanQuery to extract term texts from
     * @param terms
     *            HashSet where extracted term texts should be put into
     *            (Elements: String)
     * @param prohibited
     *            <code>true</code> to extract "prohibited" terms, too
     */
    private final void getTermsFromBooleanQuery(BooleanQuery query,
            HashSet terms, boolean prohibited) throws IOException {
        BooleanClause[] queryClauses = query.getClauses();
        int i;

        for (i = 0; i < queryClauses.length; i++) {
//          if (prohibited || !queryClauses[i].prohibited)
//            getTerms(queryClauses[i].query, terms, prohibited,extractPhrases);
            if (prohibited || !(queryClauses[i].getOccur()==BooleanClause.Occur.MUST_NOT))
                // DWH 3-29-07 old way was deprecated
              getTerms(queryClauses[i].getQuery(), terms, prohibited, extractPhrases);
                // DWH 3-29-07 old way was deprecated
        }
    }

    /**
     * Extracts all term texts of a given PhraseQuery. Term texts will be
     * returned in lower-case.
     * 
     * @param query
     *            PhraseQuery to extract term texts from
     * @param terms
     *            HashSet where extracted term texts should be put into
     *            (Elements: String)
     */
    private final void getTermsFromPhraseQuery(PhraseQuery query, HashSet terms) {
        Term[] queryTerms = query.getTerms();
        int i;

        String test = queryTerms.toString();

        if (extractPhrases) {
            terms.add(queryTerms);
            return;
        }

        for (i = 0; i < queryTerms.length; i++) {
            terms.add(getTermsFromTerm(queryTerms[i]));
        }
    }

    /**
     * Extracts all term texts of a given TermQuery. Term texts will be returned
     * in lower-case.
     * 
     * @param query
     *            TermQuery to extract term texts from
     * @param terms
     *            HashSet where extracted term texts should be put into
     *            (Elements: String)
     */
    private final void getTermsFromTermQuery(TermQuery query, HashSet terms) {
        terms.add(getTermsFromTerm(query.getTerm()));
    }

    /**
     * Extracts the term of a given Term. The term will be returned in
     * lower-case.
     * 
     * @param term
     *            Term to extract term from
     * 
     * @return the Term's term text
     */
    private final Term getTermsFromTerm(Term term) {
        if (term.text().equals(" ")) {
            return null;
        }

        return term;
    }
}