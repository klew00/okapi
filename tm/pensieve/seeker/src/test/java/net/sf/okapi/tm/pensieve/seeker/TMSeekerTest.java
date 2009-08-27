package net.sf.okapi.tm.pensieve.seeker;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import net.sf.okapi.tm.pensieve.writer.LuceneIndexer;
import net.sf.okapi.tm.pensieve.writer.TextUnit;
import net.sf.okapi.tm.pensieve.writer.TextUnitFields;

/**
 * User: Christian Hargraves
 * Date: Aug 17, 2009
 * Time: 1:04:24 PM
 */
public class TMSeekerTest {

    static final Directory DIR = new RAMDirectory();
    TMSeeker seeker;

    @Before
    public void setUp() throws FileNotFoundException {
        seeker = new TMSeeker(DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullIndexDir() {
        new TMSeeker(null);
    }

    @Test
    public void searchForWordsNothingFound() throws Exception {
        LuceneIndexer indexer = getWriter();
        indexer.endIndex();
        List<TextUnit> docs = seeker.searchForWords(TextUnitFields.CONTENT, "anonexistentwordthatshouldnowayeverexist", 10);
        assertNotNull("docs returned should not be null", docs);
        assertEquals("number of docs found", 0, docs.size());
    }

    @Test
    public void searchForWordsOverMaxDocs() throws Exception {
        LuceneIndexer indexer = getWriter();

        populateIndex(indexer, 12, "patents are evil", "unittest");
        final int desiredReturns = 2;
        indexer.endIndex();
        List<TextUnit> docs = seeker.searchForWords(TextUnitFields.CONTENT, "patents", desiredReturns);
        assertEquals("number of docs found", desiredReturns, docs.size());
    }

    @Test
    public void searchForWordsUnderMaxDocs() throws Exception {
        LuceneIndexer indexer = getWriter();

        final int desiredReturns = 8;

        populateIndex(indexer, desiredReturns, "patents are evil", "unittest");
        indexer.endIndex();

        List<TextUnit> docs = seeker.searchForWords(TextUnitFields.CONTENT, "patents", 10);
        assertEquals("number of docs found", desiredReturns, docs.size());
    }

    @Test(expected = RuntimeException.class)
    public void searchWordsInvalidQuery() throws Exception {
        seeker.searchForWords(TextUnitFields.CONTENT, "patents evil are]", 10);
    }

    @Test
    public void searchWordsMultipleSubPhrases() throws Exception {
        LuceneIndexer indexer = getWriter();

        indexer.indexTextUnit(new TextUnit("joe", "patents are evil"));
        indexer.indexTextUnit(new TextUnit("joe", "patents evil are"));
        indexer.indexTextUnit(new TextUnit("joe", "are patents evil"));
        indexer.indexTextUnit(new TextUnit("joe", "completely unrelated phrase"));
        indexer.endIndex();

        List<TextUnit> docs = seeker.searchForWords(TextUnitFields.CONTENT, "\"patents evil\"", 10);
        assertEquals("number of docs found", 2, docs.size());
    }

    @Test
    public void searchExactSingleMatch() throws Exception {
        LuceneIndexer indexer = getWriter();
        String str = "watch out for the killer rabbit";

        final int numOfIndices = 18;

        populateIndex(indexer, numOfIndices, str, "two");

        indexer.endIndex();
        List<TextUnit> docs = seeker.searchExact(TextUnitFields.CONTENT_EXACT, str+1, 10);
        assertEquals("number of docs found", 1, docs.size());
    }

    @Test
    public void searchExactMultipleMatches() throws Exception {
        LuceneIndexer indexer = getWriter();
        String str = "watch out for the killer rabbit";
        for(int i = 0; i < 5; i++){
            indexer.indexTextUnit(new TextUnit("joe", str));
        }

        indexer.endIndex();
        List<TextUnit> docs = seeker.searchExact(TextUnitFields.CONTENT_EXACT, str, 10);
        assertEquals("number of docs found", 5, docs.size());
    }

    @Test
    public void searchExactDifferentStopWords() throws Exception {
        LuceneIndexer indexer = getWriter();
        String str = "watch out for the killer rabbit";
        indexer.indexTextUnit(new TextUnit("joe", str));
        indexer.indexTextUnit(new TextUnit("joe", "watch out for the the killer rabbit"));

        indexer.endIndex();
        List<TextUnit> docs = seeker.searchExact(TextUnitFields.CONTENT_EXACT, str, 10);
        assertEquals("number of docs found", 1, docs.size());
    }

    @Test
    public void searchExactDifferentOrder() throws Exception {
        LuceneIndexer indexer = getWriter();
        String str = "watch out for the killer rabbit";
        indexer.indexTextUnit(new TextUnit("joe", str));
        indexer.indexTextUnit(new TextUnit("joe", "watch out for the the killer rabbit"));

        indexer.endIndex();
        List<TextUnit> docs = seeker.searchExact(TextUnitFields.CONTENT_EXACT, "killer rabbit the for out watch", 10);
        assertEquals("number of docs found", 0, docs.size());
    }

    @Test
    public void getTextUnit() throws Exception {
        String str = "watch out for the killer rabbit";
        Document doc = new Document();
        doc.add(new Field(TextUnitFields.CONTENT_EXACT.name(), str,
                Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(TextUnitFields.AUTHOR.name(), "j",
                Field.Store.NO, Field.Index.ANALYZED));
        TextUnit tu = seeker.getTextUnit(doc);
        assertEquals("content field", str, tu.getContent());
        assertEquals("author field", "j", tu.getAuthor());
    }

    LuceneIndexer getWriter() throws Exception {
        return new LuceneIndexer(DIR);
    }

    void populateIndex(LuceneIndexer indexer, int numOfEntries, String text, String author) throws Exception {

        for (int i=0; i<numOfEntries; i++) {
            indexer.indexTextUnit(new TextUnit(author, text + i));
        }
        indexer.indexTextUnit(new TextUnit("unittest", "something that in no way should ever match"));
    }
}
