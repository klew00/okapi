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
import net.sf.okapi.common.resource.TextFragment;

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
        emWriter.getIndexWriter().commit();
    }

    @Test
    public void endIndexThrowsNoException() throws IOException {
        emWriter.endIndex();
        emWriter.endIndex();
    }

    public void endIndexCommits() throws IOException {
        emWriter.indexTranslationUnit(new TranslationUnit(new TextFragment("dax"), new TextFragment("is funny (sometimes)")));
        emWriter.endIndex();
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed after endIndex", 1, reader.maxDoc());
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNoSourceContent(){
        emWriter.getDocument(new TranslationUnit(null, new TextFragment("some target")));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentEmptySourceContent(){
        emWriter.getDocument(new TranslationUnit(new TextFragment(""), new TextFragment("some target")));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNullTU(){
        emWriter.getDocument(null);
    }

    @Test
    public void getDocumentValues(){
        String text = "blah blah blah";
        Document doc = emWriter.getDocument(new TranslationUnit(new TextFragment(text), new TextFragment("someone")));
        assertEquals("Document's content field", "blah blah blah", doc.getField(TranslationUnitFields.SOURCE.name()).stringValue());
        assertEquals("Document's content fuzzy field", "blah blah blah", doc.getField(TranslationUnitFields.SOURCE_FUZZY.name()).stringValue());
        assertEquals("Document's content exact field", "blah blah blah".hashCode()+"", doc.getField(TranslationUnitFields.SOURCE_EXACT.name()).stringValue());
        assertEquals("Document's target field", "someone", doc.getField(TranslationUnitFields.TARGET.name()).stringValue());
    }

    @Test
    public void getDocumentNoTarget(){
        Document doc = emWriter.getDocument(new TranslationUnit(new TextFragment("blah blah blah"), null));
        assertNull("Document's target field should be null", doc.getField(TranslationUnitFields.TARGET.name()));
    }

    @Test(expected = NullPointerException.class)
    public void indexTranslationUnitNull() throws IOException {
        emWriter.indexTranslationUnit(null);
    }

    @Test
    public void indexTranslationUnitNoIndexedDocsBeforeCall() throws IOException {
        assertEquals("num of docs indexed", 0, emWriter.getIndexWriter().numDocs());
    }

    @Test
    public void indexTranslationUnitBeforeCommit() throws IOException {
        emWriter.indexTranslationUnit(new TranslationUnit(new TextFragment("dax"), new TextFragment("is funny (sometimes)")));
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed before endIndex", 0, reader.maxDoc());
    }

    @Test
    public void indexTextUnit() throws IOException {
        emWriter.indexTranslationUnit(new TranslationUnit(new TextFragment("joe"), new TextFragment("schmoe")));
        assertEquals("num of docs indexed", 1, emWriter.getIndexWriter().numDocs());
    }

}
