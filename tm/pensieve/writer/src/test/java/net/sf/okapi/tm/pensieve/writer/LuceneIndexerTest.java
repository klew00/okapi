package net.sf.okapi.tm.pensieve.writer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.RAMDirectory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * User: Christian Hargraves
 * Date: Aug 11, 2009
 * Time: 6:35:45 AM
 */
public class LuceneIndexerTest {

    LuceneIndexer indexer;
    IndexWriter writer;
    static final File GOOD_DIR = new File("../data/");
    static final File GOOD_FILE = new File(GOOD_DIR, "apache1.0.txt");
    RAMDirectory dir;

    @Before
    public void init() throws IOException {
        dir = new RAMDirectory();
        indexer = new LuceneIndexer(dir);
        writer = indexer.getIndexWriter();
    }

    @Test
    public void constructorCreatesWriter(){
        assertNotNull("the indexer writer was not created as expected", writer);
    }

    @Test
    public void constructorUsesExpectedDirectory(){
        assertTrue("The index directory should end with 'target/test-classes'", writer.getDirectory() instanceof RAMDirectory);
    }

    @Test(expected = AlreadyClosedException.class)
    public void endIndexClosesWriter() throws IOException {
        indexer.endIndex();
        writer.commit();
    }

    @Test
    public void endIndexThrowsNoException() throws IOException {
        indexer.endIndex();
        indexer.endIndex();
    }

    @Test
    public void endIndexCommits() throws IOException {
        indexer.indexTextUnit(new TextUnit("dax", "is funny (sometimes)"));
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed before endIndex", 0, reader.maxDoc());
        indexer.endIndex();
        reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed after endIndex", 1, reader.maxDoc());
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNoContent(){
        indexer.getDocument(new TextUnit("some author", null));
    }

    @Test
    public void getDocumentValues(){
        Document doc = indexer.getDocument(new TextUnit("someone", "blah blah blah"));
        assertEquals("Document's content field", "blah blah blah", doc.getField(TextUnitFields.CONTENT.name()).stringValue());
        assertEquals("Document's content exact field", "blah blah blah", doc.getField(TextUnitFields.CONTENT_EXACT.name()).stringValue());
        assertEquals("Document's author field", "someone", doc.getField(TextUnitFields.AUTHOR.name()).stringValue());
    }

    @Test
    public void getDocumentNoAuthor(){
        Document doc = indexer.getDocument(new TextUnit(null, "blah blah blah"));
        assertNull("Document's author field should be null", doc.getField(TextUnitFields.AUTHOR.name()));
    }

    @Test(expected = NullPointerException.class)
    public void indexTextUnitNull() throws IOException {
        indexer.indexTextUnit(null);
    }

    @Test
    public void indexTextUnitnoIndexedDocsBeforeCall() throws IOException {
        assertEquals("num of docs indexed", 0, indexer.getIndexWriter().numDocs());
    }

    @Test
    public void indexTextUnit() throws IOException {
        indexer.indexTextUnit(new TextUnit("joe", "schmoe"));
        assertEquals("num of docs indexed", 1, indexer.getIndexWriter().numDocs());
    }

}
