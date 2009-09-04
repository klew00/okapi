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
import net.sf.okapi.tm.pensieve.common.MetaDataTypes;
import net.sf.okapi.tm.pensieve.common.TranslationUnitValue;

/**
 * User: Christian Hargraves
 * Date: Aug 11, 2009
 * Time: 6:35:45 AM
 */
public class PensieveWriterTest {

    PensieveWriter emWriter;
    IndexWriter writer;
    static final File GOOD_DIR = new File("../data/");
    static final File GOOD_FILE = new File(GOOD_DIR, "apache1.0.txt");
    RAMDirectory dir;

    @Before
    public void init() throws IOException {
        dir = new RAMDirectory();
        emWriter = new PensieveWriter(dir);
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
        emWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitValue("EN", new TextFragment("dax")), new TranslationUnitValue("ES", new TextFragment("is funny (sometimes)"))));
        emWriter.endIndex();
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed after endIndex", 1, reader.maxDoc());
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNoSourceContent(){
        emWriter.getDocument(new TranslationUnit(null, new TranslationUnitValue("EN", new TextFragment("some target"))));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentEmptySourceContent(){
        emWriter.getDocument(new TranslationUnit(new TranslationUnitValue("EN", new TextFragment("")), new TranslationUnitValue("EN", new TextFragment("some target"))));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNullTU(){
        emWriter.getDocument(null);
    }

    @Test
    public void getDocumentValues(){
        String text = "blah blah blah";
        TranslationUnit tu = new TranslationUnit(new TranslationUnitValue("EN", new TextFragment(text)), new TranslationUnitValue("EN", new TextFragment("someone")));
        tu.getMetadata().put(MetaDataTypes.SOURCE_LANG, "EN");
        tu.getMetadata().put(MetaDataTypes.TARGET_LANG, "FR");
        Document doc = emWriter.getDocument(tu);
        assertEquals("Document's content field", "blah blah blah", doc.getField(TranslationUnitFields.SOURCE.name()).stringValue());
        assertEquals("Document's content exact field", "blah blah blah", doc.getField(TranslationUnitFields.SOURCE_EXACT.name()).stringValue());
        assertEquals("Document's target field", "someone", doc.getField(TranslationUnitFields.TARGET.name()).stringValue());
//        assertEquals("Document's source lang field", "EN", doc.getField(TranslationUnitFields.SOURCE_LANG.name()).stringValue());
//        assertEquals("Document's target lang field", "FR", doc.getField(TranslationUnitFields.TARGET_LANG.name()).stringValue());
    }

    @Test
    public void getDocumentNoTarget(){
        Document doc = emWriter.getDocument(new TranslationUnit(new TranslationUnitValue("EN", new TextFragment("blah blah blah")), null));
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
        emWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitValue("EN", new TextFragment("dax")), new TranslationUnitValue("EN", new TextFragment("is funny (sometimes)"))));
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed before endIndex", 0, reader.maxDoc());
    }

    @Test
    public void indexTextUnit() throws IOException {
        emWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitValue("EN", new TextFragment("joe")), new TranslationUnitValue("EN", new TextFragment("schmoe"))));
        assertEquals("num of docs indexed", 1, emWriter.getIndexWriter().numDocs());
    }

    @Test
    public void importTMXDocCount() throws IOException {
        emWriter.importTMX("/sample_tmx.xml", "EN", "IT");
        assertEquals("entries in TM", 2, emWriter.getIndexWriter().numDocs());
    }    
}
