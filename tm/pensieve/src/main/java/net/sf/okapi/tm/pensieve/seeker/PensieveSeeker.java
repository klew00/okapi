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
package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.tm.pensieve.common.*;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.CorruptIndexException;

/**
 * Used to query the TM
 *
 * @author Christian Hargraves
 */
public class PensieveSeeker implements ITmSeeker, Iterable<TranslationUnit> {

    private Directory indexDir;

    /**
     * Creates an instance of TMSeeker
     *
     * @param indexDir The Directory implementation to use for the queries
     * @throws IllegalArgumentException If the indexDir is not set
     */
    public PensieveSeeker(Directory indexDir) throws IllegalArgumentException {
        //TODO - Change indexDir to some other non-lucene class.
        if (indexDir == null) {
            throw new IllegalArgumentException("'indexDir' cannot be null!");
        }
        this.indexDir = indexDir;
    }

    /**
     * gets an iterator to traverse all translation units in the indexdir
     * @return the iterator for translation units
     */
    //TODO:  Needs to accept query items and parameters
    public Iterator<TranslationUnit> iterator() {
        return new TranslationUnitIterator();
    }

    /**
     * Gets a list of matches for a given set of words. In this case OR is assumed.
     *
     * @param query The words to query for
     * @param max   The max number of results
     * @return A list of matches for a given set of words. In this case OR is assumed.
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchForWords(String query, int max) {
        QueryParser parser = new QueryParser(TranslationUnitField.SOURCE.name(), new SimpleAnalyzer());
        Query q;
        try {
            q = parser.parse(query);
        } catch (ParseException pe) {
            throw new OkapiIOException("Query String didn't parse: " + query, pe);
        }
        return search(max, q);
    }

    /**
     * Gets a list of fuzzy matches for a given phrase.
     *
     * @param query The query string WITHOUT ~ and threshold value
     * @param max The max number of results
     * @param threshold The desired threshold - null for default threshold of 0.5f
     * @return A list of fuzzy matches
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchFuzzy(String query, Float similarityThreshold, int max) {

        Query q;
        if (similarityThreshold == null) {
            q = new FuzzyQuery(new Term(TranslationUnitField.SOURCE_EXACT.name(), query));
        } else {
            q = new FuzzyQuery(new Term(TranslationUnitField.SOURCE_EXACT.name(), query), similarityThreshold);
        }
        return search(max, q);
    }

    /**
     * Gets a list of exact matches for a given phrase.
     *
     * @param max The max number of results
     * @return A list of exact matches
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchExact(String query, int max) {
        //If using QueryParser.parse("\"phrase to match\""), the indexed field must be set to Field.Index.ANALYZED
        //At which point subphrases will also match. This is not the desired behavior of an exact match.
        //Query q = new QueryParser(field.name(), new SimpleAnalyzer()).parse("\""+query+"\"");
        //The combination of Field.Index.NOT_ANALYZED and using the PhraseQuery does the exact match as expected.
        //This means that if we follow this way, then it will require the same tu to be indexed twice; one time as
        //Field.Index.ANALYZED (for word searching) and another time as Field.Index.NOT_ANALYZED (for exact matches)
        PhraseQuery q = new PhraseQuery();
        q.add(new Term(TranslationUnitField.SOURCE_EXACT.name(), query));
        return search(max, q);
    }

    /**
     * Gets a list of TmHits which have segments that contain the provided subphrase
     *
     * @param subPhrase The subphrase to match again
     * @param maxHits   The maximum number of hits to return
     * @return A list of TmHits which have segments that contain the provided subphrase
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchSubphrase(String subPhrase, int maxHits) {
        return searchForWords("\"" + subPhrase + "\"", maxHits);
    }

//    public List<TranslationUnit> getAllTranslationUnits() {
//        List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
//        IndexReader ir;
//        try {
//            ir = openIndexReader();
//            for (int i = 0; i < ir.maxDoc(); i++) {
//                tus.add(getTranslationUnit(ir.document(i)));
//            }
//        } catch (CorruptIndexException cie) {
//            throw new OkapiIOException("The index is corrupt: " + cie.getMessage(), cie);
//        } catch (IOException ioe) {
//            throw new OkapiIOException("Could not complete query: " + ioe.getMessage(), ioe);
//        }
//        return tus;
//
//    }
    public Directory getIndexDir() {
        return indexDir;
    }

    List<TmHit> search(int max, Query q) {
        IndexSearcher is = null;
        List<TmHit> tmhits = new ArrayList<TmHit>();
        try {
            is = getIndexSearcher();
            TopDocs hits = is.search(q, max);
            for (int j = 0; j < hits.scoreDocs.length; j++) {
                ScoreDoc scoreDoc = hits.scoreDocs[j];
                TmHit tmhit = new TmHit();
                tmhit.setScore(scoreDoc.score);
                tmhit.setTu(getTranslationUnit(is.doc(scoreDoc.doc)));
                tmhits.add(tmhit);
            }
        } catch (IOException ioe) {
            throw new OkapiIOException("Could not complete query: " + ioe.getMessage(), ioe);
        } finally {
            //TODO we need to test this
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return tmhits;
    }

    /**
     * Translates a Document into a TranslationUnit
     *
     * @param doc The Document to translate
     * @return a TranslationUnit that represents what was returned in the document.
     */
    TranslationUnit getTranslationUnit(Document doc) {
        //TODO Make sure metadata is supported here
        TranslationUnit tu = new TranslationUnit(new TranslationUnitVariant(getFieldValue(doc, TranslationUnitField.SOURCE_LANG),
                new TextFragment(getFieldValue(doc, TranslationUnitField.SOURCE))),
                new TranslationUnitVariant(getFieldValue(doc, TranslationUnitField.TARGET_LANG),
                new TextFragment(getFieldValue(doc, TranslationUnitField.TARGET))));

        for (MetadataType type : MetadataType.values()) {
            tu.setMetadataValue(type, getFieldValue(doc, type));
        }
        return tu;
    }

    /**
     * Gets a Document's Field Value
     *
     * @param doc   The document ot get the field value from
     * @param field The field to extract
     * @return The value of the field
     */
    String getFieldValue(Document doc, TranslationUnitField field) {
        return getFieldValue(doc, field.name());
    }

    /**
     * Gets a Document's Field Value
     *
     * @param doc  The document ot get the field value from
     * @param type The field to extract
     * @return The value of the field
     */
    String getFieldValue(Document doc, MetadataType type) {
        return getFieldValue(doc, type.fieldName());
    }

    /**
     * Gets a Document's Field Value
     *
     * @param doc       The document ot get the field value from
     * @param fieldName The name of the field to extract
     * @return The value of the field
     */
    String getFieldValue(Document doc, String fieldName) {
        String fieldValue = null;
        Field tempField = doc.getField(fieldName);
        if (tempField != null) {
            fieldValue = tempField.stringValue();
        }
        return fieldValue;
    }

    protected IndexSearcher getIndexSearcher() throws IOException {
        return new IndexSearcher(indexDir, true);
    }

    protected IndexReader openIndexReader() throws IOException {
        return IndexReader.open(indexDir, true);
    }

    private class TranslationUnitIterator implements Iterator<TranslationUnit> {

        private int currentIndex;
        private int maxIndex;
        private IndexReader ir;

        TranslationUnitIterator() {
            try {
                ir = openIndexReader();
            } catch (CorruptIndexException cie) {
                throw new OkapiIOException(cie.getMessage(), cie);
            } catch (IOException ioe) {
                throw new OkapiIOException(ioe.getMessage(), ioe);
            }
            currentIndex = 0;
            maxIndex = ir.maxDoc();
        }

        public boolean hasNext() {
            return currentIndex < maxIndex;
        }

        public TranslationUnit next() {
            TranslationUnit tu = null;
            if (hasNext()) {
                try {
                    tu = getTranslationUnit(ir.document(currentIndex++));
                } catch (CorruptIndexException cie) {
                    throw new OkapiIOException(cie.getMessage(), cie);
                } catch (IOException ioe) {
                    throw new OkapiIOException(ioe.getMessage(), ioe);
                }
            }
            return tu;
        }

        public void remove() {
            throw new UnsupportedOperationException("Will not support remove method - Please remove items via ITmSeeker interface");
        }
    }
}
