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

import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.*;
import net.sf.okapi.tm.pensieve.writer.PensieveWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.okapi.tm.pensieve.Helper;
import org.apache.lucene.index.IndexReader;

/**
 * User: Christian Hargraves
 * Date: Aug 17, 2009
 * Time: 1:04:24 PM
 */
public class PensieveSeekerTest {

    static final Directory DIR = new RAMDirectory();
    static final TranslationUnitVariant TARGET = new TranslationUnitVariant("EN", new TextFragment("target text"));
    static final String STR = "watch out for the killer rabbit";

    PensieveSeeker seeker;

    List<TmHit> tmhits;

    @Before
    public void setUp() throws FileNotFoundException {
        seeker = new PensieveSeeker(DIR);
    }

    @Test
    public void translationUnitIterator() throws Exception {
        PensieveWriter writer = getWriter();
        populateIndex(writer, 12, "patents are evil", "unittest");
        writer.endIndex();
        
        Iterator<TranslationUnit> tuIterator = seeker.iterator();
        List<TranslationUnit> tus = new ArrayList<TranslationUnit>();
        while(tuIterator.hasNext()) {
            tus.add(tuIterator.next());
        }
        assertEquals("number of tus", 13, tus.size());
        assertEquals("first document", "patents are evil0", tus.get(0).getSource().getContent().toString());
        assertEquals("second document", "patents are evil1", tus.get(1).getSource().getContent().toString());
    }

    @Test
    public void translationUnitIteratorNextCallOnEmpty() throws Exception {
        PensieveWriter writer = getWriter();
        populateIndex(writer, 1, "patents are evil", "unittest");
        writer.endIndex();

        Iterator<TranslationUnit> tuIterator = seeker.iterator();
        TranslationUnit tu;
        tu = tuIterator.next();
        tu = tuIterator.next();
        assertNotNull(tu);
        assertFalse(tuIterator.hasNext());
        assertNull(tuIterator.next());
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = OkapiIOException.class)
    public void iteratorInstantiationHandleIOException() throws IOException {
        PensieveSeeker spy = spy(seeker);
        doThrow(new IOException("some exception")).when(spy).openIndexReader();
        spy.iterator();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = OkapiIOException.class)
    public void iteratorInstantiationHandleCorruptedIndexException() throws IOException {
        PensieveSeeker spy = spy(seeker);
        doThrow(new CorruptIndexException("some exception")).when(spy).openIndexReader();
        spy.iterator();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = OkapiIOException.class)
    public void iteratorNextIOException() throws Exception {
        PensieveWriter writer = getWriter();
        populateIndex(writer, 1, "patents are evil", "unittest");
        writer.endIndex();

        Iterator<TranslationUnit> iterator = seeker.iterator();

        IndexReader mockIndexReader = mock(IndexReader.class);
        doThrow(new IOException("some exception")).when(mockIndexReader).document(anyInt());
        Helper.setPrivateMember(iterator, "ir", mockIndexReader);

        iterator.next();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = OkapiIOException.class)
    public void iteratorNextCorruptedIndexException() throws Exception {
        PensieveWriter writer = getWriter();
        populateIndex(writer, 1, "patents are evil", "unittest");
        writer.endIndex();

        Iterator<TranslationUnit> iterator = seeker.iterator();

        IndexReader mockIndexReader = mock(IndexReader.class);
        doThrow(new CorruptIndexException("some exception")).when(mockIndexReader).document(anyInt());
        Helper.setPrivateMember(iterator, "ir", mockIndexReader);

        iterator.next();
    }



    @Test(expected = UnsupportedOperationException.class)
    public void iteratorUnsupportedRemove() throws IOException {
        seeker.iterator().remove();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = OkapiIOException.class)
    public void searchHandleIOException() throws IOException {
        PensieveSeeker spy = spy(seeker);
        doThrow(new IOException("some exception")).when(spy).getIndexSearcher();
        spy.search(1, new PhraseQuery());
    }

    @Test
    public void getDirectory(){
        assertSame("directory", DIR, seeker.getIndexDir());
    }

    @Test
    public void getFieldValueNoField(){
        Document doc = new Document();
        assertNull("Null should be returned for an empty field", seeker.getFieldValue(doc, TranslationUnitField.SOURCE));
    }

    @Test
    public void getFieldValue(){
        Document doc = new Document();
        doc.add(new Field(TranslationUnitField.SOURCE.name(), "lk", Field.Store.NO,  Field.Index.NOT_ANALYZED));
        assertEquals("source field", "lk",  seeker.getFieldValue(doc, TranslationUnitField.SOURCE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullIndexDir() {
        new PensieveSeeker(null);
    }

    @Test
    public void searchForWordsNothingFound() throws Exception {
        PensieveWriter writer = getWriter();
        writer.endIndex();
        tmhits = seeker.searchForWords("anonexistentwordthatshouldnowayeverexist", 10);
        assertNotNull("docs returned should not be null", tmhits);
        assertEquals("number of docs found", 0, tmhits.size());
    }

    @Test
    public void searchForWordsOverMaxDocs() throws Exception {
        PensieveWriter writer = getWriter();

        populateIndex(writer, 12, "patents are evil", "unittest");
        final int desiredReturns = 2;
        writer.endIndex();
        tmhits = seeker.searchForWords("patents", desiredReturns);
        assertEquals("number of docs found", desiredReturns, tmhits.size());
    }

    @Test
    public void searchForWordsUnderMaxDocs() throws Exception {
        PensieveWriter writer = getWriter();

        final int desiredReturns = 8;

        populateIndex(writer, desiredReturns, "patents are evil", "unittest");
        writer.endIndex();

        tmhits = seeker.searchForWords("patents", 10);
        assertEquals("number of docs found", desiredReturns, tmhits.size());
    }

    @Test(expected = RuntimeException.class)
    public void searchWordsInvalidQuery() throws Exception {
        seeker.searchForWords("patents evil are]", 10);
    }

    @Test
    public void searchSubphrase() throws Exception {
        PensieveWriter writer = getWriter();

        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("patents are evil")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("patents evil are")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("are patents evil")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("completely unrelated phrase")),TARGET));
        writer.endIndex();

        tmhits = seeker.searchSubphrase("patents evil", 10);
        assertEquals("number of docs found", 2, tmhits.size());
        assertEquals("1st target", "patents evil are", tmhits.get(0).getTu().getSource().getContent().toString());
        assertEquals("1st target", "are patents evil", tmhits.get(1).getTu().getSource().getContent().toString());
    }

    @Test
    public void searchFuzzyMiddleMatch() throws Exception {
        PensieveWriter writer = getWriter();


        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(STR)),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch for the killer rabbit")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch out the killer rabbit")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch rabbit")),TARGET));

        writer.endIndex();
        tmhits = seeker.searchFuzzy(STR+"~", 10);
        assertEquals("number of docs found", 3, tmhits.size());
    }

    @Test
    public void searchFuzzyWordOrder80Percent() throws Exception {
        PensieveWriter writer = getWriter();

        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch rabbit")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(STR)),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("rabbit killer the for out watch")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch for the killer rabbit")),TARGET));

        writer.endIndex();
        tmhits = seeker.searchFuzzy(STR+"~0.8", 10);
        assertEquals("number of docs found", 2, tmhits.size());
        assertEquals("1st match", "watch out for the killer rabbit", tmhits.get(0).getTu().getSource().getContent().toString());
        assertEquals("2nd match", "watch for the killer rabbit", tmhits.get(1).getTu().getSource().getContent().toString());
    }

    @Test
    public void searchFuzzyMiddleMatch80Percent() throws Exception {
        PensieveWriter writer = getWriter();

        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch rabbit")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(STR)),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch out the killer rabbit and some extra stuff")),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch for the killer rabbit")),TARGET));

        writer.endIndex();
        tmhits = seeker.searchFuzzy(STR+"~0.8", 10);
        assertEquals("number of docs found", 2, tmhits.size());
        assertEquals("1st match", "watch out for the killer rabbit", tmhits.get(0).getTu().getSource().getContent().toString());
        assertEquals("2nd match", "watch for the killer rabbit", tmhits.get(1).getTu().getSource().getContent().toString());
    }

    @Test
    public void searchFuzzyScoreSortNoFuzzyThreshold() throws Exception {
        PensieveWriter writer = getWriter();
        String[] testStrings = {STR,
            STR + " 1",
            STR + " 2 words",
            STR + " 3 words now"
        };

        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(testStrings[0])),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(testStrings[1])),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(testStrings[2])),TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(testStrings[3])),TARGET));
        writer.endIndex();
        //If you add a threshold it changes the sort order
        tmhits = seeker.searchFuzzy(STR+"~", 10);
        
        assertEquals("number of docs found", 4, tmhits.size());
        assertEquals("first match", testStrings[0], tmhits.get(0).getTu().getSource().getContent().toString());

        //Verify sort order
        Float previous = tmhits.get(0).getScore();
        for(int i = 1; i < tmhits.size(); i++){
            Float currentScore = tmhits.get(i).getScore();
            assertEquals(i + " match", testStrings[i], tmhits.get(i).getTu().getSource().getContent().toString());
            assertTrue("results should be sorted descending by score", currentScore < previous);
            previous = currentScore;
        }        
    }

    @Test
    public void searchFuzzyEndMatch() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch out for the killer rabbit";

        final int numOfIndices = 9;

        populateIndex(writer, numOfIndices, str, "two");

        writer.endIndex();
        tmhits = seeker.searchFuzzy(str+"~", 10);
        assertEquals("number of docs found", 9, tmhits.size());
    }

    @Test
    public void searchExactSingleMatch() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch out for the killer rabbit";

        final int numOfIndices = 18;

        populateIndex(writer, numOfIndices, str, "two");

        writer.endIndex();
        //Fuzzy or phrase matching would return "watch out for the killer rabbit1" & "watch out for the killer rabbit11"
        tmhits = seeker.searchExact(str+1, 10);
        assertEquals("number of docs found", 1, tmhits.size());
    }

    @Test
    public void searchExactMultipleMatches() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        for(int i = 0; i < 5; i++){
            writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(str)), TARGET));
        }

        writer.endIndex();
        tmhits = seeker.searchExact(str, 10);
        assertEquals("number of docs found", 5, tmhits.size());
    }

    @Test
    public void searchExactDifferentStopWords() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(str)), TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch out for the the killer rabbit")), TARGET));

        writer.endIndex();
        tmhits = seeker.searchExact(str, 10);
        assertEquals("number of docs found", 1, tmhits.size());
    }

    @Test
    public void searchExactDifferentCases() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch Out for The killEr rabbit";
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(str)), TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch out for the the killer rabbit")), TARGET));

        writer.endIndex();
        tmhits = seeker.searchExact(str, 10);
        assertEquals("number of docs found", 1, tmhits.size());
    }

    @Test
    public void searchExactDifferentOrder() throws Exception {
        PensieveWriter writer = getWriter();
        String str = "watch out for the killer rabbit";
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(str)), TARGET));
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("watch out for the the killer rabbit")), TARGET));

        writer.endIndex();
        tmhits = seeker.searchExact("killer rabbit the for out watch", 10);
        assertEquals("number of docs found", 0, tmhits.size());
    }

    //TODO support metadata
    @Test
    public void getTranslationUnitFields() throws Exception {
        final String source = "watch out for the killer rabbit";
        final String target = "j";
        final String targetLang = "KR";
        final String sourceLang = "EN";
        Document doc = new Document();
        doc.add(new Field(TranslationUnitField.SOURCE_EXACT.name(), source,
                Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitField.SOURCE.name(), source,
                Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitField.SOURCE_LANG.name(), sourceLang,
                Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitField.TARGET.name(), target,
                Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(TranslationUnitField.TARGET_LANG.name(), targetLang,
                Field.Store.YES, Field.Index.ANALYZED));
        TranslationUnit tu = seeker.getTranslationUnit(doc);
        assertEquals("source field", source, tu.getSource().getContent().toString());
        assertEquals("source lang", sourceLang, tu.getSource().getLang());
        assertEquals("target field", target, tu.getTarget().getContent().toString());
        assertEquals("target lang", targetLang, tu.getTarget().getLang());
    }

    @Test
    public void getTranslationUnitMeta() throws Exception {
        final String source = "watch out for the killer rabbit";
        final String target = "j";
        final String id = "1";
        final String filename = "fname";
        final String groupname = "gname";
        final String type = "typeA";
        final String targetLang = "KR";
        final String sourceLang = "EN";
        Document doc = new Document();
        doc.add(new Field(TranslationUnitField.SOURCE.name(), source,
                Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitField.SOURCE_LANG.name(), sourceLang,
                Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(TranslationUnitField.TARGET.name(), target,
                Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(TranslationUnitField.TARGET_LANG.name(), targetLang,
                Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field(MetadataType.ID.fieldName(), id,
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MetadataType.FILE_NAME.fieldName(), filename,
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MetadataType.GROUP_NAME.fieldName(), groupname,
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MetadataType.TYPE.fieldName(), type,
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        TranslationUnit tu = seeker.getTranslationUnit(doc);
        assertEquals("id field", id, tu.getMetadata().get(MetadataType.ID));
        assertEquals("filename field", filename, tu.getMetadata().get(MetadataType.FILE_NAME));
        assertEquals("groupname field", groupname, tu.getMetadata().get(MetadataType.GROUP_NAME));
        assertEquals("type field", type, tu.getMetadata().get(MetadataType.TYPE));
    }

    PensieveWriter getWriter() throws Exception {
        return new PensieveWriter(DIR);
    }

    void populateIndex(PensieveWriter writer, int numOfEntries, String source, String target) throws Exception {

        for (int i=0; i < numOfEntries; i++) {
            writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(source + i)), new TranslationUnitVariant("EN", new TextFragment(target))));
        }
        writer.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("something that in no way should ever match")), new TranslationUnitVariant("EN", new TextFragment("unittesttarget"))));
    }
}
