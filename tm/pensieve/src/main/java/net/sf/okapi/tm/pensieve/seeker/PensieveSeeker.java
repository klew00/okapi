/*===========================================================================
Copyright (C) 2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitField;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.sf.okapi.tm.pensieve.analyzers.NgramAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;

/**
 * Used to query the TM
 *
 * @author Christian Hargraves
 */
public class PensieveSeeker implements ITmSeeker, Iterable<TranslationUnit> {

    
    private Directory indexDir;    
    private final static StringDistance distanceCalc = new NGramDistance(4);

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
     * @param metadata The metadata attributes to also match against, null for no metadata
     * @return A list of matches for a given set of words. In this case OR is assumed.
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchForWords(String query, int max, Metadata metadata) {
        QueryParser parser = new QueryParser(TranslationUnitField.SOURCE.name(), new NgramAnalyzer(Locale.ENGLISH, 4));
        Query q;
        try {
            q = parser.parse(query);
        } catch (ParseException pe) {
            throw new OkapiIOException("Query String didn't parse: " + query, pe);
        }
        return search(max, q, metadata, query);
    }

    /**
     * Gets a list of fuzzy matches for a given phrase.
     *
     * @param query The query string WITHOUT ~ and threshold value
     * @param max The max number of results
     * @param threshold The desired threshold - null for default threshold of 0.5f
     * @param metadata The metadata attributes to also match against, null for no metadata
     * @return A list of fuzzy matches
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchFuzzy(final String query, Float similarityThreshold, int max, Metadata metadata) {
        Query q;
        if (similarityThreshold == null) {
            q = new FuzzyQuery(new Term(TranslationUnitField.SOURCE_EXACT.name(), query));
        } else {
            q = new FuzzyQuery(new Term(TranslationUnitField.SOURCE_EXACT.name(), query), similarityThreshold);
        }
        return search(max, q, metadata, query);
    }

    /**
     * Gets a list of exact matches for a given phrase.
     *
     * @param max The max number of results
     * @param metadata The metadata attributes to also match against, null for no metadata
     * @return A list of exact matches
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchExact(String query, int max, Metadata metadata) {
        //If using QueryParser.parse("\"phrase to match\""), the indexed field must be set to Field.Index.ANALYZED
        //At which point subphrases will also match. This is not the desired behavior of an exact match.
        //Query q = new QueryParser(field.name(), new SimpleAnalyzer()).parse("\""+query+"\"");
        //The combination of Field.Index.NOT_ANALYZED and using the PhraseQuery does the exact match as expected.
        //This means that if we follow this way, then it will require the same tu to be indexed twice; one time as
        //Field.Index.ANALYZED (for word searching) and another time as Field.Index.NOT_ANALYZED (for exact matches)
        PhraseQuery q = new PhraseQuery();
        q.add(new Term(TranslationUnitField.SOURCE_EXACT.name(), query));
        return search(max, q, metadata, query);
    }

    /**
     * Gets a list of TmHits which have segments that contain the provided subphrase
     *
     * @param subPhrase The subphrase to match again
     * @param maxHits   The maximum number of hits to return
     * @param metadata The metadata attributes to also match against, null for no metadata
     * @return A list of TmHits which have segments that contain the provided subphrase
     * @throws OkapiIOException if the search cannot be completed do to I/O problems
     */
    public List<TmHit> searchSubphrase(String subPhrase, int maxHits, Metadata metadata) {
        return searchForWords("\"" + subPhrase + "\"", maxHits, metadata);
    }

    public Directory getIndexDir() {
        return indexDir;
    }

    private BooleanQuery createQuery(Query q, Metadata metadata) {
        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(q, BooleanClause.Occur.MUST);
        if (metadata != null) {
            for (MetadataType type : metadata.keySet()) {
                bQuery.add(new TermQuery(new Term(type.fieldName(), metadata.get(type))), BooleanClause.Occur.MUST);
            }
        }
        return bQuery;
    }

    protected float calcEditDistance(String actual, String target) {
        return distanceCalc.getDistance(actual, target);
    }

    List<TmHit> search(int max, Query q, Metadata metadata, String origQuery) {
        IndexSearcher is = null;
        List<TmHit> tmhits = new ArrayList<TmHit>();
        BooleanQuery bQuery = createQuery(q, metadata);
        try {
            is = getIndexSearcher();
            TopDocs hits = is.search(bQuery, max);
            for (int j = 0; j < hits.scoreDocs.length; j++) {
                ScoreDoc scoreDoc = hits.scoreDocs[j];
                TmHit tmhit = new TmHit();
                tmhit.setTu(getTranslationUnit(is.doc(scoreDoc.doc)));
                float score = calcEditDistance(tmhit.getTu().getSource().getContent().toString(), origQuery);
                tmhit.setScore(score);
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

    //=== Added for try out of inline codes support
    /**
     * Search for exact matches.
     * @param queryFrag the fragment to query.
     * @param max the maximum number of hits to return.
     * @param metadata any associated attributes to use for filter.
     * @return the list of hits of the given argument.
     */
	public List<TmHit> searchExact2 (TextFragment queryFrag,
		int max,
		Metadata metadata)
	{
		PhraseQuery query = new PhraseQuery();
		query.add(new Term(TranslationUnitField.SOURCE_EXACT.name(), queryFrag.getCodedText()));
		return search2(max, 1.0f, query, queryFrag, metadata);
	}

    /**
     * Search for exact and fuzzy matches
     * @param queryFrag the fragment to query.
     * @param threshold the minimal score value to return.
     * @param max the maximum number of hits to return.
     * @param metadata any associated attributes to use for filter.
     * @return the list of hits of the given argument.
     */
	public List<TmHit> searchFuzzy2 (TextFragment queryFrag,
		int threshold,
		int max,
		Metadata metadata)
	{
		Query query;
		float searchThreshold = threshold / 100.0f;
		if ( threshold < 0 ) searchThreshold = 0.0f;
		if ( threshold > 100 ) searchThreshold = 1.0f;
		query = new FuzzyQuery(new Term(TranslationUnitField.SOURCE_EXACT.name(),
			queryFrag.getCodedText()));
		return search2(max, searchThreshold, query, queryFrag, metadata);
	}

    /**
     * Search the best list of hits for a given query, taking inline codes into account.
     * @param threshold the minumum score to return (between 0.0 and 1.0)
     * @param max the maximum number of hits to return.
     * @param query the query
     * @param queryFrag the text fragment for the query.
     * @param metadata any associated attributes to use for filter.
     * @return the list of hits found for the given arguments (never null).
     */
	private List<TmHit> search2 (int max,
		float threshold,
		Query query,
		TextFragment queryFrag,
		Metadata metadata)
	{
		IndexSearcher is = null;
		List<TmHit> tmhits = new ArrayList<TmHit>();
		BooleanQuery bQuery = createQuery(query, metadata);
		List<Code> queryCodes = queryFrag.getCodes();
		String queryCodedText = queryFrag.getCodedText();

		try {
			is = getIndexSearcher();
			TopDocs hits = is.search(bQuery, max);
			boolean sort = false;
			List<Code> tmCodes;
			String tmCodedText;
			ScoreDoc scoreDoc;
			TmHit tmHit;
			float score;
			
			for ( int j=0; j<hits.scoreDocs.length; j++ ) {
				scoreDoc = hits.scoreDocs[j];
				tmCodes = Code.stringToCodes(getFieldValue(is.doc(scoreDoc.doc),
					TranslationUnitField.SOURCE_CODES));
				tmCodedText = getFieldValue(is.doc(scoreDoc.doc), TranslationUnitField.SOURCE);
                score = calcEditDistance(tmCodedText, queryCodedText);
                if ( score < threshold ) break; // Done
				if (( queryCodes.size() > 0 ) && ( tmCodes.size() > 0 )) {
					// If tmScrCodes is null, equals will return false
					if ( !Code.sameCodes(queryCodes, tmCodes) ) {
						// Substract code penalty
						// TODO: we may want to have different penalty per type of code differences
						score -= 0.01f;
		                if ( score < threshold ) continue; // Don't include this hit
		                sort = true;
					}
				}
				// Else: Either or none has code(s)
				// In this case any potential differences is already set by
				// the markers in the text, so there is nothing to do
				// Set the translation unit
				tmHit = new TmHit();
				tmHit.setScore(score);
				tmHit.setTu(createTranslationUnit(is.doc(scoreDoc.doc), tmCodedText, tmCodes));
				tmhits.add(tmHit);
			}

			// Re-sort if needed
			if ( sort ) {
				// Reverse the result so the highest score first
				// (TmHit implements comparator using natural ascending order)
				Collections.sort(tmhits, Collections.reverseOrder());
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Could not complete query.", e);
		}
		finally {
			if ( is != null ) {
				try {
					is.close();
				}
				catch ( IOException ignored ) {}
			}
		}
		return tmhits;
	}

    /**
     * Creates a {@link TranslationUnit} for a given document.
     * @param doc the document from which to create the new translation unit.
     * @param srcCodedText the source coded text to re-use.
     * @param srcCodes the source codes to re-use.
     * @return a new translation unit for the given document.
     */
    private TranslationUnit createTranslationUnit(Document doc,
    	String srcCodedText,
    	List<Code> srcCodes)
    {
    	TextFragment frag = new TextFragment();
    	frag.setCodedText(srcCodedText, srcCodes, false);
    	TranslationUnitVariant srcTuv = new TranslationUnitVariant(
    		getFieldValue(doc, TranslationUnitField.SOURCE_LANG), frag);

    	frag = new TextFragment();
    	List<Code> codes = Code.stringToCodes(getFieldValue(doc, TranslationUnitField.TARGET_CODES));
    	frag.setCodedText(getFieldValue(doc, TranslationUnitField.TARGET), codes, false);
    	TranslationUnitVariant trgTuv = new TranslationUnitVariant(
   			getFieldValue(doc, TranslationUnitField.TARGET_LANG), frag);

    	TranslationUnit tu = new TranslationUnit(srcTuv, trgTuv);
    	for ( MetadataType type : MetadataType.values() ) {
    		tu.setMetadataValue(type, getFieldValue(doc, type));
    	}
    	return tu;
    }

    private TranslationUnit createTranslationUnit(Document doc) {
		TextFragment frag = new TextFragment();
		List<Code> codes = Code.stringToCodes(getFieldValue(doc, TranslationUnitField.SOURCE_CODES));
		frag.setCodedText(getFieldValue(doc, TranslationUnitField.SOURCE), codes, false);
		TranslationUnitVariant srcTuv = new TranslationUnitVariant(
			getFieldValue(doc, TranslationUnitField.SOURCE_LANG), frag);

		frag = new TextFragment();
		codes = Code.stringToCodes(getFieldValue(doc, TranslationUnitField.TARGET_CODES));
		frag.setCodedText(getFieldValue(doc, TranslationUnitField.TARGET), codes, false);
		TranslationUnitVariant trgTuv = new TranslationUnitVariant(
			getFieldValue(doc, TranslationUnitField.TARGET_LANG), frag);

		TranslationUnit tu = new TranslationUnit(srcTuv, trgTuv);
		for ( MetadataType type : MetadataType.values() ) {
			tu.setMetadataValue(type, getFieldValue(doc, type));
		}
		return tu;
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
                    // Using createTranslationUnit(), not createTranslationUnit()
                    // ensure that we get the inline codes
                    tu = createTranslationUnit(ir.document(currentIndex++));
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
