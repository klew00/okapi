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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.analyzers.NgramAnalyzer;
import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TmMatchType;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitField;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.queries.TmFuzzyQuery;
import net.sf.okapi.tm.pensieve.scorers.TmFuzzySimilarity;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;

/**
 * Used to query the TM
 * 
 * @author Christian Hargraves
 * @author HARGRAVEJE
 */
public class PensieveSeeker implements ITmSeeker, Iterable<TranslationUnit> {
	private final static StringDistance defaultDistanceCalc = new LevensteinDistance();
	private final static NgramAnalyzer defaultFuzzyAnalyzer = new NgramAnalyzer(
			Locale.ENGLISH, 4);
	private final static Similarity SIMILARITY = new TmFuzzySimilarity();

	// TODO: externalize penalties in the future
	private static float SINGLE_CODE_DIFF_PENALTY = 1.0f;
	private static float WHITESPACE_OR_CASE_PENALTY = 5.0f;

	private Directory indexDir;
	private IndexReader indexReader;

	/**
	 * Creates an instance of TMSeeker
	 * 
	 * @param indexDir
	 *            The Directory implementation to use for the queries
	 * @throws IllegalArgumentException
	 *             If the indexDir is not set
	 */
	public PensieveSeeker(Directory indexDir) throws IllegalArgumentException {
		// TODO - Change indexDir to some other non-lucene class.
		if (indexDir == null) {
			throw new IllegalArgumentException("'indexDir' cannot be null!");
		}
		this.indexDir = indexDir;
	}

	/**
	 * gets an iterator to traverse all translation units in the indexdir
	 * 
	 * @return the iterator for translation units
	 */
	// TODO: Needs to accept query items and parameters
	public Iterator<TranslationUnit> iterator() {
		return new TranslationUnitIterator();
	}

	public Directory getIndexDir() {
		return indexDir;
	}

	private BooleanQuery createQuery(Metadata metadata) {
		return createQuery(metadata, null);
	}

	private BooleanQuery createQuery(Metadata metadata, Query q) {
		BooleanQuery bQuery = new BooleanQuery();
		if (q != null) {
			bQuery.add(q, BooleanClause.Occur.MUST);
		}

		if (metadata != null) {
			for (MetadataType type : metadata.keySet()) {
				bQuery.add(new TermQuery(new Term(type.fieldName(), metadata
						.get(type))), BooleanClause.Occur.MUST);
			}
		}
		return bQuery;
	}

	protected float calcEditDistance(String actual, String target) {
		return defaultDistanceCalc.getDistance(actual, target);
	}

	/**
	 * Translates a Document into a TranslationUnit
	 * 
	 * @param doc
	 *            The Document to translate
	 * @return a TranslationUnit that represents what was returned in the
	 *         document.
	 */
	TranslationUnit getTranslationUnit(Document doc) {
		// TODO Make sure metadata is supported here
		TranslationUnit tu = new TranslationUnit(new TranslationUnitVariant(
				getLocaleValue(doc, TranslationUnitField.SOURCE_LANG),
				new TextFragment(
						getFieldValue(doc, TranslationUnitField.SOURCE))),
				new TranslationUnitVariant(getLocaleValue(doc,
						TranslationUnitField.TARGET_LANG), new TextFragment(
						getFieldValue(doc, TranslationUnitField.TARGET))));

		for (MetadataType type : MetadataType.values()) {
			tu.setMetadataValue(type, getFieldValue(doc, type));
		}
		return tu;
	}

	/**
	 * Gets a Document's Field Value
	 * 
	 * @param doc
	 *            The document ot get the field value from
	 * @param field
	 *            The field to extract
	 * @return The value of the field
	 */
	String getFieldValue(Document doc, TranslationUnitField field) {
		return getFieldValue(doc, field.name());
	}

	/**
	 * Gets a Document's Field Value
	 * 
	 * @param doc
	 *            The document ot get the field value from
	 * @param type
	 *            The field to extract
	 * @return The value of the field
	 */
	String getFieldValue(Document doc, MetadataType type) {
		return getFieldValue(doc, type.fieldName());
	}

	/**
	 * Gets a Document's Field Value
	 * 
	 * @param doc
	 *            The document ot get the field value from
	 * @param fieldName
	 *            The name of the field to extract
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

	LocaleId getLocaleValue(Document doc, TranslationUnitField field) {
		return new LocaleId(getFieldValue(doc, field.name()), false);
	}

	protected IndexSearcher getIndexSearcher() throws IOException {
		return new IndexSearcher(indexDir, true);
	}

	protected IndexReader openIndexReader() throws IOException {
		if (indexReader == null) {
			indexReader = IndexReader.open(indexDir, true);
		}
		return indexReader;
	}

	/**
	 * Search for exact matches.
	 * 
	 * @param queryFrag
	 *            the fragment to query.
	 * @param max
	 *            the maximum number of hits to return.
	 * @param metadata
	 *            any associated attributes to use for filter.
	 * @return the list of hits of the given argument.
	 */
	public List<TmHit> searchExact(TextFragment queryFrag, Metadata metadata) {
		TermQuery query = new TermQuery(new Term(
				TranslationUnitField.SOURCE_EXACT.name(), queryFrag
						.getCodedText()));
		IndexSearcher is = null;
		List<TmHit> tmhits = new LinkedList<TmHit>();
		List<TmHit> noDups;
		BooleanQuery bQuery = createQuery(metadata, query);

		try {
			is = getIndexSearcher();
			// TODO: Fix this before Lucene 3.0
			Hits hits = is.search(bQuery);
			List<Code> tmCodes;
			String tmCodedText;
			Document doc;
			TmHit tmHit;

			for (int j = 0; j < hits.length(); j++) {
				doc = hits.doc(j);
				tmCodes = Code.stringToCodes(getFieldValue(doc,
						TranslationUnitField.SOURCE_CODES));
				tmCodedText = getFieldValue(doc,
						TranslationUnitField.SOURCE_EXACT);
				tmHit = new TmHit();
				tmHit.setScore(100.0f);
				tmHit.setMatchType(TmMatchType.EXACT);
				tmHit.setTu(createTranslationUnit(doc, tmCodedText, tmCodes));
				tmhits.add(tmHit);

				/*
				 * System.out.println(queryFrag.toString());
				 * System.out.println(tmHit.getScore());
				 * System.out.println(tmHit.getTu().toString());
				 * System.out.println();
				 */
			}

			// sort TmHits on TmMatchType, Score and Source String
			Collections.sort(tmhits);

			// remove duplicate hits
			noDups = new LinkedList<TmHit>(new LinkedHashSet<TmHit>(tmhits));

		} catch (IOException e) {
			throw new OkapiIOException("Could not complete query.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
		return noDups;
	}

	/**
	 * Search for exact and fuzzy matches
	 * 
	 * @param queryFrag
	 *            the fragment to query.
	 * @param threshold
	 *            the minimal score value to return.
	 * @param max
	 *            the maximum number of hits to return.
	 * @param metadata
	 *            any associated attributes to use for filter.
	 * @return the list of hits of the given argument.
	 * @throws IllegalArgumentException
	 *             If threshold is greater than 100 or less than 0
	 */
	public List<TmHit> searchFuzzy(TextFragment queryFrag, int threshold,
			int max, Metadata metadata) {
		if (threshold < 0 || threshold > 100) {
			throw new IllegalArgumentException("");
		}

		float searchThreshold = (float) threshold;
		if (threshold < 0)
			searchThreshold = 0.0f;
		if (threshold > 100)
			searchThreshold = 100.0f;

		String queryText = queryFrag.getText();

		// create basic ngram analyzer to tokenize query
		TokenStream queryTokenStream = defaultFuzzyAnalyzer
				.tokenStream(TranslationUnitField.SOURCE.name(),
						new StringReader(queryText));
		// get the TermAttribute from the TokenStream
		TermAttribute termAtt = (TermAttribute) queryTokenStream
				.addAttribute(TermAttribute.class);
		TmFuzzyQuery fQuery = new TmFuzzyQuery(searchThreshold);
		try {
			queryTokenStream.reset();
			while (queryTokenStream.incrementToken()) {
				Term t = new Term(TranslationUnitField.SOURCE.name(), termAtt
						.term());
				fQuery.add(t);
			}
			queryTokenStream.end();
			queryTokenStream.close();
		} catch (IOException e) {
			throw new OkapiIOException(e.getMessage(), e);
		}

		return getFuzzyHits(max, searchThreshold, fQuery, queryFrag, metadata);
	}

	/**
	 * /** Search for fuzzy matches and adjust hit type and score based on
	 * differences with whitespace, codes and casing.
	 * 
	 * 
	 * @param threshold
	 *            the minumum score to return (between 0.0 and 1.0)
	 * @param max
	 *            the maximum number of hits to return.
	 * @param query
	 *            the query
	 * @param queryFrag
	 *            the text fragment for the query.
	 * @param metadata
	 *            any associated attributes to use for filter.
	 * @return the list of hits found for the given arguments (never null).
	 */
	List<TmHit> getFuzzyHits(int max, float threshold, Query query,
			TextFragment queryFrag, Metadata metadata) {
		IndexSearcher is = null;
		List<TmHit> tmhits = new ArrayList<TmHit>();
		List<Code> queryCodes = queryFrag.getCodes();
		List<TmHit> noDups;
		Filter filter = null;

		try {
			is = getIndexSearcher();
			is.setSimilarity(new TmFuzzySimilarity());
			if (metadata != null && !metadata.isEmpty()) {
				filter = new QueryWrapperFilter(createQuery(metadata));
			}
			TopDocs hits = is.search(query, filter, max);
			List<Code> tmCodes;
			String tmCodedText;
			ScoreDoc scoreDoc;
			TmHit tmHit;
			String sourceTextOnly;
			float score;

			for (int j = 0; j < hits.scoreDocs.length; j++) {
				scoreDoc = hits.scoreDocs[j];

				tmHit = new TmHit();
				tmCodes = Code
						.stringToCodes(getFieldValue(is.doc(scoreDoc.doc),
								TranslationUnitField.SOURCE_CODES));
				tmCodedText = getFieldValue(is.doc(scoreDoc.doc),
						TranslationUnitField.SOURCE_EXACT);

				// remove codes so we can compare text only
				sourceTextOnly = TextFragment.getText(tmCodedText);

				// default hit values
				TmMatchType matchType = TmMatchType.FUZZY;
				score = scoreDoc.score;
				tmHit.setCodeMismatch(false);

				// These are 100%, adjust match type and penalize for whitespace
				// and case difference
				if (score >= 100.0f
						&& tmCodedText.equals(queryFrag.getCodedText())) {
					matchType = TmMatchType.EXACT;
				} else if (score >= 100.0f
						&& sourceTextOnly.equals(queryFrag.getText())) {
					matchType = TmMatchType.FUZZY_FULL_TEXT_MATCH;
				} else if (score >= 100.0f) {
					// must be a whitespace or case difference
					score -= WHITESPACE_OR_CASE_PENALTY;
				}

				// code penalty
				if (queryCodes.size() != tmCodes.size()) {
					score -= (SINGLE_CODE_DIFF_PENALTY * (float) Math
							.abs(queryCodes.size() - (float) tmCodes.size()));
					tmHit.setCodeMismatch(true);
				}

				tmHit.setScore(score);
				tmHit.setTu(createTranslationUnit(is.doc(scoreDoc.doc),
						tmCodedText, tmCodes));
				tmHit.setMatchType(matchType);

				// check if the penalties have pushed the match below threshold
				if (tmHit.getScore() < threshold) {
					continue;
				}

				tmhits.add(tmHit);

				/*System.out.println(queryFrag.toString());
				System.out.println(tmHit.getScore());
				System.out.println(tmHit.getMatchType());
				System.out.println(tmHit.getTu().toString());
				System.out.println();*/
			}

			// sort TmHits on TmMatchType, Score and Source String
			Collections.sort(tmhits);
			// remove duplicate hits
			noDups = new LinkedList<TmHit>(new LinkedHashSet<TmHit>(tmhits));
		} catch (IOException e) {
			throw new OkapiIOException("Could not complete query.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}

		return noDups;
	}

	/**
	 * Creates a {@link TranslationUnit} for a given document.
	 * 
	 * @param doc
	 *            the document from which to create the new translation unit.
	 * @param srcCodedText
	 *            the source coded text to re-use.
	 * @param srcCodes
	 *            the source codes to re-use.
	 * @return a new translation unit for the given document.
	 */
	private TranslationUnit createTranslationUnit(Document doc,
			String srcCodedText, List<Code> srcCodes) {
		TextFragment frag = new TextFragment();
		frag.setCodedText(srcCodedText, srcCodes, false);
		TranslationUnitVariant srcTuv = new TranslationUnitVariant(
				getLocaleValue(doc, TranslationUnitField.SOURCE_LANG), frag);

		frag = new TextFragment();
		List<Code> codes = Code.stringToCodes(getFieldValue(doc,
				TranslationUnitField.TARGET_CODES));
		String codedText = getFieldValue(doc, TranslationUnitField.TARGET);
		frag.setCodedText(codedText == null ? "" : codedText, codes, false);
		TranslationUnitVariant trgTuv = new TranslationUnitVariant(
				getLocaleValue(doc, TranslationUnitField.TARGET_LANG), frag);

		TranslationUnit tu = new TranslationUnit(srcTuv, trgTuv);
		for (MetadataType type : MetadataType.values()) {
			tu.setMetadataValue(type, getFieldValue(doc, type));
		}
		return tu;
	}

	private TranslationUnit createTranslationUnit(Document doc) {
		TextFragment frag = new TextFragment();
		List<Code> codes = Code.stringToCodes(getFieldValue(doc,
				TranslationUnitField.SOURCE_CODES));
		frag.setCodedText(
				getFieldValue(doc, TranslationUnitField.SOURCE_EXACT), codes,
				false);
		TranslationUnitVariant srcTuv = new TranslationUnitVariant(
				getLocaleValue(doc, TranslationUnitField.SOURCE_LANG), frag);

		frag = new TextFragment();
		codes = Code.stringToCodes(getFieldValue(doc,
				TranslationUnitField.TARGET_CODES));
		String codedText = getFieldValue(doc, TranslationUnitField.TARGET);
		frag.setCodedText(codedText == null ? "" : codedText, codes, false);
		TranslationUnitVariant trgTuv = new TranslationUnitVariant(
				getLocaleValue(doc, TranslationUnitField.TARGET_LANG), frag);

		TranslationUnit tu = new TranslationUnit(srcTuv, trgTuv);
		for (MetadataType type : MetadataType.values()) {
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
					// Using createTranslationUnit(), not
					// createTranslationUnit()
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
			throw new UnsupportedOperationException(
					"Will not support remove method - Please remove items via ITmSeeker interface");
		}
	}
}
