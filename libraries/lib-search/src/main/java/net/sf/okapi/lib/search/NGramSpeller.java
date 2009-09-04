package net.sf.okapi.lib.search;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

/**
 * Do spelling correction based on ngram frequency of terms in an index.
 * 
 * Developed based on <a
 * href="http://marc.theaimsgroup.com/?l=lucene-user&m=109474652805339&w=2">this
 * message</a> in the lucene-user list.
 * 
 * <p>
 * There are two parts to this algorithm. First a ngram lookup table is formed
 * for all terms in an index. Then suggested spelling corrections can be done
 * based on this table.
 * <p>
 * The "lookup table" is actually another Lucene index. It is built by going
 * through all terms in your original index and storing the term in a Document
 * with all ngrams that make it up. Ngrams of length 3 and 4 are suggested.
 * <p>
 * 
 * In addition the prefix and suffix ngrams are stored in case you want to use a
 * heuristic that people usually know the first few characters of a word.
 * 
 * <p>
 * The entry's boost is set by default to log(word_freq)/log(num_docs).
 * 
 * <p>
 * 
 * For a word like "kings" a {@link Document} with the following fields is made
 * in the ngram index:
 * 
 * <pre>
 * word:kings
 * gram3:kin
 * gram3:ing
 * gram3:ngs
 * gram4:king
 * gram4:ings
 * start3:kin
 * start4:king
 * end3:ngs
 * end4:ings
 * 
 * boost: log(freq('kings'))/log(num_docs).
 * </pre>
 * 
 * 
 * When a lookup is done a query is formed with all ngrams in the misspelled
 * word.
 * 
 * <p>
 * For a word like <code>"kingz"</code> a query is formed like this.
 * 
 * Query: <br>
 * <code>
 * gram3:kin gram3:ing gram3:ngz start3:kin^B1 end3:ngz^B2 start4:king^B1 end4:ingz^B2
 * </code> <br>
 * 
 * Above B1 and B2 are the prefix and suffix boosts. The prefix boost should
 * probably be &gt;= 2.0 and the suffix boost should probably be just a little
 * above 1.
 * 
 * <p>
 * <b>To build</b> the ngram index based on the "contents" field in an existing
 * index 'orig_index' you run the main() driver like this:<br>
 * <code>
 * java org.apache.lucene.spell.NGramSpeller -f contents -i orig_index -o ngram_index
 * </code>
 * 
 * <p>
 * Once you build an index you can <b>perform spelling corrections using</b>
 * {@link #suggestUsingNGrams suggestUsingNGrams(...)}.
 * 
 * 
 * <p>
 * 
 * To play around with the code against an index approx 100k javadoc-generated
 * web pages circa Sept/2004 go here: <a href='http://www.searchmorph.com/kat/spell.jsp'>http://www.searchmorph.com/kat/spell.jsp</a>
 * .
 * 
 * <p>
 * Of interest might be the <a
 * href="http://secondstring.sourceforge.net/">secondstring</a> string matching
 * package and <a href=
 * "http://specialist.nlm.nih.gov/nls/gspell/doc/apiDoc/overview-summary.html"
 * >gspell</a>.
 * 
 * 
 * 
 * @author <a href="mailto:dave&#64;tropo.com?subject=NGramSpeller">David
 *         Spencer</a>
 * 
 */
public final class NGramSpeller {
	/**
	 *
	 */
	private NGramSpeller() {
	}

	/**
	 * Main driver, used to build an index. You probably want invoke like this: <br>
	 * <code>
	 * java org.apache.lucene.spell.NGramSpeller -f contents -i orig_index -o ngram_index
	 * </code>
	 */
	public static void main(String[] args) throws Throwable {
		int minThreshold = 5;
		int ng1 = 3;
		int ng2 = 4;
		int maxr = 10;
		int maxd = 5;
		int mf; // DWH 3-28-07
		int mbd; // DWH 3-28-07
		String out = "gram_index";
		String gi = "gram_index";

		String name = null;
		String field = "contents";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i"))
				name = args[++i];
			else if (args[i].equals("-gi"))
				gi = args[++i];
			else if (args[i].equals("-o"))
				out = args[++i];
			else if (args[i].equals("-t")) // test transpositions
			{
				String s = args[++i];
				o.println("TRANS: " + s);
				String[] ar = formTranspositions(s);
				for (int j = 0; j < ar.length; j++)
					o.println("\t" + ar[j]);
				System.exit(0);
			} else if (args[i].equals("-ng1"))
				ng1 = Integer.parseInt(args[++i]);
			else if (args[i].equals("-ng2"))
				ng2 = Integer.parseInt(args[++i]);
			else if (args[i].equals("-help") || args[i].equals("--help")
					|| args[i].equals("-h")) {
				o.println("To form an ngram index:");
				o
						.println("NGramSpeller -i ORIG_INDEX -o NGRAM_INDEX [-ng1 MIN] [-ng2 MAX] [-f FIELD]");
				o.println("Defaults are ng1=3, ng2=4, field='contents'");
				System.exit(100);
			} else if (args[i].equals("-q")) {
				String goal = args[++i];
				o.println("[NGrams] for " + goal + " from " + gi);

				float bStart = 2.0f;
				float bEnd = 1.0f;
				float bTransposition = 0f;

				o.println("bStart: " + bStart);
				o.println("bEnd: " + bEnd);
				o.println("bTrans: " + bTransposition);
				o.println("ng1: " + ng1);
				o.println("ng2: " + ng2);

				IndexReader ir = IndexReader.open(gi);
				IndexSearcher searcher = new IndexSearcher(gi);
				List lis = new ArrayList(maxr);
				String[] res = suggestUsingNGrams(searcher, goal, ng1, ng2,
						maxr, bStart, bEnd, bTransposition, maxd, lis, true); // more
																				// popular
				o.println("Returned " + res.length + " from " + gi
						+ " which has " + ir.numDocs() + " words in it");
				Iterator it = lis.iterator();
				while (it.hasNext()) {
					Object[] ar = (Object[]) it.next();
					String word = (String) ar[0];
					o.println("word=" + ar[0] + " score=" + ar[1] + " dist="
							+ ar[2] + " freq=" + ar[3]);
					int[] mm = (int[]) ar[4];
					for (int j = ng1; j < mm.length; j++)
						o.println("\tmm[ " + j + " ] = " + mm[j]);
				}
				o.println();
				o.println("query: " + lastQuery.toString("contents"));

				Hits ghits = searcher.search(new TermQuery(new Term(F_WORD,
						"recursive")));

				if (ghits.length() >= 1) // umm, should only be 0 or 1
				{
					Document doc = ghits.doc(0);
					o.println("TEST DOC: " + doc);

				}
				searcher.close();
				ir.close();
				return;
			} else if (args[i].equals("-f"))
				field = args[++i];
			else {
				o.println("hmm? " + args[i]);
				System.exit(1);
			}
		}
		if (name == null) {
			o.println("opps, you need to specify the input index w/ -i");
			System.exit(1);
		}
		o.println("Opening " + name);
		IndexReader.unlock(FSDirectory.getDirectory(name, false));
		final IndexReader r = IndexReader.open(name);

		o.println("Docs: " + nf.format(r.numDocs()));
		o.println("Using field: " + field);

		IndexWriter writer = new IndexWriter(out, new WhitespaceAnalyzer(),
				true);
		// writer.mergeFactor *= 50;
		mf = writer.getMergeFactor(); // DWH 3-28-07 old way was deprecated
		writer.setMergeFactor(mf * 50); // DWH 3-28-07 old way was deprecated
		// writer.minMergeDocs *= 50;
		mbd = writer.getMaxBufferedDocs(); // DWH 3-28-07 old way was deprecated
		writer.setMaxBufferedDocs(mbd * 50); // DWH 3-28-07 old way was
												// deprecated

		o.println("Forming index from " + name + " to " + out);
		int res = formNGramIndex(r, writer, ng1, ng2, field, minThreshold);

		o.println("done, did " + res + " ngrams");
		writer.optimize();
		writer.close();
		r.close();
	}

	/**
	 * Using an NGram algorithm try to find alternate spellings for a "goal"
	 * word based on the ngrams in it.
	 * 
	 * @param searcher
	 *            the searcher for the "ngram" index
	 * 
	 * @param goal
	 *            the word you want a spell check done on
	 * 
	 * @param ng1
	 *            the min ngram length to use, probably 3 and it defaults to 3
	 *            if you pass in a value &lt;= 0
	 * 
	 * @param ng2
	 *            the max ngram length to use, probably 3 or 4
	 * 
	 * @param maxr
	 *            max results to return, probably a small number like 5 for
	 *            normal use or 10-100 for testing
	 * 
	 * @param bStart
	 *            how to boost matches that start the same way as the goal word,
	 *            probably greater than 2
	 * 
	 * @param bEnd
	 *            how to boost matches that end the same way as the goal word,
	 *            probably greater than or equal to 1
	 * 
	 * @param bTransposition
	 *            how to boost matches that are also simple transpositions, or 0
	 *            to disable
	 * 
	 * @param maxd
	 *            filter for the max Levenshtein string distance for matches,
	 *            probably a number like 3, 4, or 5, or use 0 for it to be
	 *            ignored. This prevents words radically longer but similar to
	 *            the goal word from being returned.
	 * 
	 * @param details
	 *            if non null is a list with one entry per match. Each entry is
	 *            an array of ([String] word, [Double] score, [Integer]
	 *            Levenshtein string distance, [Integer] word freq).
	 * 
	 * @param morePopular
	 *            if true says to only return suggestions more popular than the
	 *            misspelled word. This prevents rare words from being
	 *            suggested. Note that for words that don't appear in the index
	 *            at all this has no effect as those words will have a frequency
	 *            of 0 anyway.
	 * 
	 * @return the strings suggested with the best one first
	 */
	public static String[] suggestUsingNGrams(Searcher searcher, String goal,
			int ng1, int ng2, int maxr, float bStart, float bEnd,
			float bTransposition, int maxd, List details, boolean morePopular)
			throws Throwable {
		List res = new ArrayList(maxr);
		BooleanQuery query = new BooleanQuery();
		if (ng1 <= 0)
			ng1 = 3; // guess
		if (ng2 < ng1)
			ng2 = ng1;
		if (bStart < 0)
			bStart = 0;
		if (bEnd < 0)
			bEnd = 0;
		if (bTransposition < 0)
			bTransposition = 0;

		// calculate table of all ngrams for goal word
		String[][] gramt = new String[ng2 + 1][];
		for (int ng = ng1; ng <= ng2; ng++)
			gramt[ng] = formGrams(goal, ng);

		int goalFreq = 0;
		if (morePopular) {
			Hits ghits = searcher.search(new TermQuery(new Term(F_WORD, goal)));

			if (ghits.length() >= 1) // umm, should only be 0 or 1
			{
				Document doc = ghits.doc(0);
				goalFreq = Integer.parseInt(doc.get(F_FREQ));
			}
		}

		if (bTransposition > 0)
			add(query, F_TRANSPOSITION, goal, bTransposition);

		TRStringDistance sd = new TRStringDistance(goal);
		for (int ng = ng1; ng <= ng2; ng++) // for every ngram in range
		{
			String[] grams = gramt[ng]; // form word into ngrams (allow dups
										// too)

			if (grams.length == 0)
				continue; // hmm

			String key = "gram" + ng; // form key

			if (bStart > 0) // should we boost prefixes?
				add(query, "start" + ng, grams[0], bStart); // matches start of
															// word

			if (bEnd > 0) // should we boost suffixes
				add(query, "end" + ng, grams[grams.length - 1], bEnd); // matches
																		// end
																		// of
																		// word

			// match ngrams anywhere, w/o a boost
			for (int i = 0; i < grams.length; i++) {
				add(query, key, grams[i]);
			}
		}

		Hits hits = searcher.search(query);
		int len = hits.length();
		int remain = maxr;
		int stop = Math.min(len, 100 * maxr); // go thru more than 'maxr'
												// matches in case the distance
												// filter triggers
		for (int i = 0; i < stop && remain > 0; i++) {
			Document d = hits.doc(i);
			String word = d.get(F_WORD); // get orig word
			if (word.equals(goal))
				continue; // don't suggest a word for itself, that would be
							// silly
			int dist = sd.getDistance(word); // use distance filter
			if (maxd > 0 && dist > maxd) {
				continue;
			}
			int suggestionFreq = Integer.parseInt(d.get(F_FREQ));
			if (morePopular && goalFreq > suggestionFreq) {
				continue; // don't suggest a rarer word
			}
			remain--;
			res.add(word);

			if (details != null) // only non-null for testing probably
			{
				int[] matches = new int[ng2 + 1];
				for (int ng = ng1; ng <= ng2; ng++) {
					String[] have = formGrams(word, ng);
					int match = 0;
					String[] cur = gramt[ng];
					for (int k = 0; k < have.length; k++) {
						boolean looking = true;
						for (int j = 0; j < cur.length && looking; j++) {
							if (have[k].equals(cur[j])) {
								// o.println( "\t\tmatch: " + have[ k] + " on "
								// + word);
								match++;
								looking = false;
							}
						}
						/*
						 * if ( looking) o.println( "\t\tNO MATCH: " + have[ k]
						 * + " on " + word);
						 */
					}
					matches[ng] = match;
				}
				details
						.add(new Object[] { word, new Double(hits.score(i)),
								new Integer(dist), new Integer(suggestionFreq),
								matches });
			}
		}
		lastQuery = query; // hack for now
		return (String[]) res.toArray(new String[0]);
	}

	/**
	 * Go thru all terms and form an index of the "ngrams" of length 'ng1' to
	 * 'ng2' in each term. The ngrams have field names like "gram3" for a 3 char
	 * ngram, and "gram4" for a 4 char one. The starting and ending (or prefix
	 * and suffix) "n" characters are also stored for each word with field names
	 * "start3" and "end3".
	 * 
	 * 
	 * @param r
	 *            the index to read terms from
	 * 
	 * @param w
	 *            the writer to write the ngrams to, or if null an index named
	 *            "gram_index" will be created. If you pass in non-null then you
	 *            should optimize and close the index.
	 * 
	 * @param ng1
	 *            the min number of chars to form ngrams with (3 is suggested)
	 * 
	 * @param ng2
	 *            the max number of chars to form ngrams with, can be equal to
	 *            ng1
	 * 
	 * @param fields
	 *            the field name to process ngrams from.
	 * 
	 * @param minThreshold
	 *            terms must appear in at least this many docs else they're
	 *            ignored as the assumption is that they're so rare (...)
	 * 
	 * @return the number of ngrams added
	 * 
	 */
	private static int formNGramIndex(IndexReader r, IndexWriter _w, int ng1,
			int ng2, String field, int minThreshold) throws IOException {
		int mins = 0;
		float nudge = 0.01f; // don't allow boosts to be too small
		IndexWriter w;
		if (_w == null)
			w = new IndexWriter("gram_index", new WhitespaceAnalyzer(), // should
																		// have
																		// no
																		// effect
					true);
		else
			w = _w;

		int mod = 10000; // for status
		int nd = r.numDocs();
		final float base = (float) Math.log(1.0d / ((double) nd));

		if (field == null)
			field = "contents"; // def field
		field = field.intern(); // is it doced that you can use == on fields?

		int grams = 0; // # of ngrams added
		final TermEnum te = r.terms(new Term(field, ""));
		int n = 0;
		int skips = 0;
		while (te.next()) {
			boolean show = false; // for debugging
			Term t = te.term();
			String have = t.field();
			if (have != field && !have.equals(field)) // wrong field
			{
				break;
			}
			int df = te.docFreq();
			if ((++n % mod) == 0) {
				show = true;
				o.println("term: " + t + " n=" + nf.format(n) + " grams="
						+ nf.format(grams) + " mins=" + nf.format(mins)
						+ " skip=" + nf.format(skips) + " docFreq=" + df);
			}
			if (df < minThreshold) // not freq enough, too rare to consider
			{
				mins++;
				continue;
			}

			String text = t.text();
			int len = text.length();
			if (len < ng1)
				continue; // too short we bail but "too long" is fine...
			// but note that long tokens that are rare prob won't get here
			// anyway as they won't
			// pass the 'minThreshold' check above

			Document doc = new Document();
			// doc.add( Field.Keyword( F_WORD, text)); // orig term
			doc.add(new Field(F_WORD, text, Field.Store.YES,
					Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old way was
												// deprecated
			// doc.add( Field.Keyword( F_FREQ, ""+df)); // for popularity cutoff
			// optionx
			doc.add(new Field(F_FREQ, "" + df, Field.Store.YES,
					Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old way was
												// deprecated

			String[] trans = formTranspositions(text);
			for (int i = 0; i < trans.length; i++)
				// doc.add( Field.Keyword( F_TRANSPOSITION, trans[ i]));
				doc.add(new Field(F_TRANSPOSITION, trans[i], Field.Store.YES,
						Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old way was
													// deprecated

			// now loop thru all ngrams of lengths 'ng1' to 'ng2'
			for (int ng = ng1; ng <= ng2; ng++) {
				String key = "gram" + ng;
				String end = null;
				for (int i = 0; i < len - ng + 1; i++) {
					String gram = text.substring(i, i + ng);
					// doc.add( Field.Keyword( key, gram));
					doc.add(new Field(key, gram, Field.Store.YES,
							Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old way
														// was deprecated
					if (i == 0)
						// doc.add( Field.Keyword( "start"+ng, gram));
						doc.add(new Field("start" + ng, gram, Field.Store.YES,
								Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old
															// way was
															// deprecated
					end = gram;
					grams++;
				}
				if (end != null) // may not be present if len==ng1
				// doc.add( Field.Keyword( "end"+ng, end));
					doc.add(new Field("end" + ng, end, Field.Store.YES,
							Field.Index.UN_TOKENIZED)); // DWH 3-28-07 old way
														// was deprecated
			}

			float f1 = te.docFreq();
			float f2 = nd;

			float bo = (float) ((Math.log(f1) / Math.log(f2)) + nudge);
			doc.setBoost(bo);
			if (show)
				o.println("f1=" + f1 + " nd=" + nd + " boost=" + bo + " base="
						+ base + " word=" + text);
			w.addDocument(doc);
		}
		if (_w == null) // else you have to optimize/close
		{
			w.optimize();
			w.close();
		}
		return grams;
	}

	/**
	 * Add a clause to a boolean query.
	 */
	private static void add(BooleanQuery q, String k, String v, float boost) {
		Query tq = new TermQuery(new Term(k, v));
		tq.setBoost(boost);
		// q.add( new BooleanClause( tq, false, false));
		q.add(new BooleanClause(tq, BooleanClause.Occur.SHOULD)); // DWH 3-28-07
																	// old way
																	// was
																	// deprecated
	}

	/**
	 *
	 */
	public static String[] formTranspositions(String s) {
		int len = s.length();
		List res = new ArrayList(len - 1);
		for (int i = 0; i < (len - 1); i++) {
			char c1 = s.charAt(i);
			char c2 = s.charAt(i + 1);
			if (c1 == c2)
				continue;
			res.add(s.substring(0, i) + c2 + c1 + s.substring(i + 2));
		}
		return (String[]) res.toArray(new String[0]);
	}

	/**
	 * Form all ngrams for a given word.
	 * 
	 * @param text
	 *            the word to parse
	 * @param ng
	 *            the ngram length e.g. 3
	 * @return an array of all ngrams in the word and note that duplicates are
	 *         not removed
	 */
	public static String[] formGrams(String text, int ng) {
		List res = new ArrayList(text.length() - ng + 1);
		int len = text.length();
		StringBuffer sb = new StringBuffer(ng);

		for (int i = 0; i < len - ng + 1; i++) {
			res.add(text.substring(i, i + ng));
		}
		return (String[]) res.toArray(new String[0]);
	}

	/**
	 * Add a clause to a boolean query.
	 */
	private static void add(BooleanQuery q, String k, String v) {
		// q.add( new BooleanClause( new TermQuery( new Term( k, v)), false,
		// false));
		q.add(new BooleanClause(new TermQuery(new Term(k, v)),
				BooleanClause.Occur.SHOULD)); // DWH 3-28-07 old way was
												// deprecated
	}

	/**
	 * Field name for each word in the ngram index.
	 */
	public static final String F_WORD = "word";

	/**
	 * Frequency, for the popularity cutoff option which says to only return
	 * suggestions that occur more frequently than the misspelled word.
	 */
	public static final String F_FREQ = "freq";

	/**
	 * Store transpositions too.
	 */
	public static final String F_TRANSPOSITION = "transposition";

	/**
	 *
	 */
	private static final PrintStream o = System.out;

	/**
	 *
	 */
	private static final NumberFormat nf = NumberFormat.getInstance();

	public static Query lastQuery;

	/**
	 * Presumably this is implemented somewhere in the apache/jakarta/commons
	 * area but I couldn't find it.
	 * 
	 * @link http://www.merriampark.com/ld.htm
	 * 
	 */
	private static class TRStringDistance {

		final char[] sa;
		final int n;
		final int[][][] cache = new int[30][][];

		/**
		 * Optimized to run a bit faster than the static getDistance(). In one
		 * benchmark times were 5.3sec using ctr vs 8.5sec w/ static method,
		 * thus 37% faster.
		 */
		private TRStringDistance(String target) {
			sa = target.toCharArray();
			n = sa.length;
		}

		// *****************************
		// Compute Levenshtein distance
		// *****************************
		public int getDistance(String other) {
			int d[][]; // matrix
			int cost; // cost

			// Step 1
			final char[] ta = other.toCharArray();
			final int m = ta.length;
			if (n == 0) {
				return m;
			}
			if (m == 0) {
				return n;
			}

			if (m >= cache.length)
				d = form(n, m);
			else if (cache[m] != null)
				d = cache[m];
			else
				d = cache[m] = form(n, m);

			// Step 3

			for (int i = 1; i <= n; i++) {
				final char s_i = sa[i - 1];

				// Step 4

				for (int j = 1; j <= m; j++) {
					final char t_j = ta[j - 1];

					// Step 5

					if (s_i == t_j) // same
						cost = 0;
					else
						// not a match
						cost = 1;

					// Step 6

					d[i][j] = min3(d[i - 1][j] + 1, d[i][j - 1] + 1,
							d[i - 1][j - 1] + cost);

				}

			}

			// Step 7

			return d[n][m];

		}

		/**
		 *
		 */
		private static int[][] form(int n, int m) {
			int[][] d = new int[n + 1][m + 1];
			// Step 2

			for (int i = 0; i <= n; i++)
				d[i][0] = i;

			for (int j = 0; j <= m; j++)
				d[0][j] = j;
			return d;
		}

		// ****************************
		// Get minimum of three values
		// ****************************
		private static int min3(int a, int b, int c) {
			int mi;

			mi = a;
			if (b < mi) {
				mi = b;
			}
			if (c < mi) {
				mi = c;
			}
			return mi;

		}

		// *****************************
		// Compute Levenshtein distance
		// *****************************
		public static int getDistance(String s, String t) {
			return getDistance(s.toCharArray(), t.toCharArray());
		}

		// *****************************
		// Compute Levenshtein distance
		// *****************************
		public static int getDistance(final char[] sa, final char[] ta) {
			int d[][]; // matrix
			int i; // iterates through s
			int j; // iterates through t
			char s_i; // ith character of s
			char t_j; // jth character of t
			int cost; // cost

			// Step 1

			final int n = sa.length;
			final int m = ta.length;
			if (n == 0) {
				return m;
			}
			if (m == 0) {
				return n;
			}
			d = new int[n + 1][m + 1];

			// Step 2

			for (i = 0; i <= n; i++) {
				d[i][0] = i;
			}

			for (j = 0; j <= m; j++) {
				d[0][j] = j;
			}

			// Step 3

			for (i = 1; i <= n; i++) {

				s_i = sa[i - 1];

				// Step 4

				for (j = 1; j <= m; j++) {

					t_j = ta[j - 1];

					// Step 5

					if (s_i == t_j) {
						cost = 0;
					} else {
						cost = 1;
					}

					// Step 6

					d[i][j] = min3(d[i - 1][j] + 1, d[i][j - 1] + 1,
							d[i - 1][j - 1] + cost);

				}

			}

			// Step 7

			return d[n][m];

		}
	}

}
