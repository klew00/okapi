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
import net.sf.okapi.tm.pensieve.common.TMHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Hargraves
 */
public class TMSeeker implements Seeker {
    private Directory indexDir;

    public TMSeeker(Directory indexDir) throws IllegalArgumentException {
        if (indexDir == null) {
            throw new IllegalArgumentException("indexDir cannot be null!");
        }
        this.indexDir = indexDir;
    }

    public List<TMHit> searchForWords(String query, int max) throws IOException {
        QueryParser parser = new QueryParser(TranslationUnitFields.SOURCE.name(), new SimpleAnalyzer());
        Query q;
        try {
            q = parser.parse(query);
        } catch (ParseException pe) {
            throw new RuntimeException("Query String didn't parse: " + query, pe);
        }
        return search(max, q);
    }

    public List<TMHit> searchFuzzyWuzzy(String query, int max) throws IOException {
        Query q = new FuzzyQuery(new Term(TranslationUnitFields.SOURCE_EXACT.name(), query));
        return search(max, q);
    }

    public List<TMHit> searchExact(String query, int max) throws IOException {
        //If using QueryParser.parse("\"phrase to match\""), the indexed field must be set to Field.Index.ANALYZED
        //At which point subphrases will also match. This is not the desired behavior of an exact match.
        //Query q = new QueryParser(field.name(), new SimpleAnalyzer()).parse("\""+query+"\"");
        //The combination of Field.Index.NOT_ANALYZED and using the PhraseQuery does the exact match as expected.
        //This means that if we follow this way, then it will require the same tu to be indexed twice; one time as
        //Field.Index.ANALYZED (for word searching) and another time as Field.Index.NOT_ANALYZED (for exact matches)
        PhraseQuery q = new PhraseQuery();
        q.add(new Term(TranslationUnitFields.SOURCE_EXACT.name(), query));
        return search(max, q);
    }

    private List<TMHit> search(int max, Query q) throws IOException {
        IndexSearcher is = null;
        List<TMHit> tmhits = new ArrayList<TMHit>();
        try{
            is = new IndexSearcher(indexDir, true);
            TopDocs hits = is.search(q, max);
            for (int j = 0; j < hits.scoreDocs.length; j++) {
                ScoreDoc scoreDoc = hits.scoreDocs[j];
                TMHit tmhit = new TMHit();
                tmhit.setScore(scoreDoc.score);
                tmhit.setTu(getTranslationUnit(is.doc(scoreDoc.doc)));
                tmhits.add(tmhit);
            }
        }finally{
            //TODO we need to test this
            if (is != null){
                is.close();
            }
        }
        return tmhits;
    }

    TranslationUnit getTranslationUnit(Document doc) {
        return new TranslationUnit(new TranslationUnitVariant(getFieldValue(doc, TranslationUnitFields.SOURCE_LANG),
                new TextFragment(getFieldValue(doc, TranslationUnitFields.SOURCE))),
                new TranslationUnitVariant(getFieldValue(doc, TranslationUnitFields.TARGET_LANG),
                        new TextFragment(getFieldValue(doc, TranslationUnitFields.TARGET))));
    }

    String getFieldValue(Document doc, TranslationUnitFields field){
        String fieldValue = null;
        Field tempField = doc.getField(field.name());
        if (tempField != null) {
            fieldValue = tempField.stringValue();
        }
        return fieldValue;
    }
}
