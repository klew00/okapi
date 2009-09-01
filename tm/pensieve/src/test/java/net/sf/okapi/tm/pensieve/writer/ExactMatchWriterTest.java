package net.sf.okapi.tm.pensieve.writer;

import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
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
public class ExactMatchWriterTest {

    ExactMatchWriter emWriter;
    IndexWriter writer;
    static final File GOOD_DIR = new File("../data/");
    static final File GOOD_FILE = new File(GOOD_DIR, "apache1.0.txt");
    RAMDirectory dir;

    @Before
    public void init() throws IOException {
        dir = new RAMDirectory();
        emWriter = new ExactMatchWriter(dir);
        writer = emWriter.getIndexWriter();
    }

    @Test
    public void constructorCreatesWriter(){
        assertNotNull("the emWriter emWriter was not created as expected", emWriter);
    }

    @Test
    public void constructorUsesExpectedDirectory(){
        assertTrue("The index directory should end with 'target/test-classes'", writer.getDirectory() instanceof RAMDirectory);
    }

    @Test(expected = AlreadyClosedException.class)
    public void endIndexClosesWriter() throws IOException {
        emWriter.endIndex();
        writer.commit();
    }

    @Test
    public void endIndexThrowsNoException() throws IOException {
        emWriter.endIndex();
        emWriter.endIndex();
    }

    @Test
    public void endIndexCommits() throws IOException {
        emWriter.indexTextUnit(new TranslationUnit("dax", "is funny (sometimes)"));
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed before endIndex", 0, reader.maxDoc());
        emWriter.endIndex();
        reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed after endIndex", 1, reader.maxDoc());
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNoContent(){
        emWriter.getDocument(new TranslationUnit("some author", null));
    }

    @Test
    public void getDocumentValues(){
        Document doc = emWriter.getDocument(new TranslationUnit("someone", "blah blah blah"));
        assertEquals("Document's content field", "blah blah blah", doc.getField(TranslationUnitFields.CONTENT.name()).stringValue());
        assertEquals("Document's content exact field", "blah blah blah", doc.getField(TranslationUnitFields.CONTENT_EXACT.name()).stringValue());
        assertEquals("Document's author field", "someone", doc.getField(TranslationUnitFields.AUTHOR.name()).stringValue());
    }

    @Test
    public void getDocumentNoAuthor(){
        Document doc = emWriter.getDocument(new TranslationUnit(null, "blah blah blah"));
        assertNull("Document's author field should be null", doc.getField(TranslationUnitFields.AUTHOR.name()));
    }

    @Test(expected = NullPointerException.class)
    public void indexTextUnitNull() throws IOException {
        emWriter.indexTextUnit(null);
    }

    @Test
    public void indexTextUnitnoIndexedDocsBeforeCall() throws IOException {
        assertEquals("num of docs indexed", 0, emWriter.getIndexWriter().numDocs());
    }

    @Test
    public void indexTextUnit() throws IOException {
        emWriter.indexTextUnit(new TranslationUnit("joe", "schmoe"));
        assertEquals("num of docs indexed", 1, emWriter.getIndexWriter().numDocs());
    }

}
