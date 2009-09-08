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

package net.sf.okapi.tm.pensieve.writer;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import static net.sf.okapi.tm.pensieve.common.TranslationUnitField.*;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.MetaData;
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
public class PensieveWriterTest {

    PensieveWriter tmWriter;
    IndexWriter writer;
    static final File GOOD_DIR = new File("../data/");
    static final File GOOD_FILE = new File(GOOD_DIR, "apache1.0.txt");
    RAMDirectory dir;

    @Before
    public void init() throws IOException {
        dir = new RAMDirectory();
        tmWriter = new PensieveWriter(dir);
        writer = tmWriter.getIndexWriter();
    }

       @Test
    public void addMetadataToDocument(){
        MetaData md = new MetaData();
        md.put(MetadataType.FILE_NAME, "some/file");
        md.put(MetadataType.GROUP_NAME, "some group");
        md.put(MetadataType.ID, "someId");
        md.put(MetadataType.TYPE, "someType");
        Document doc = new Document();
        tmWriter.addMetadataToDocument(doc, md);
        assertEquals("Document's file name field", "some/file", getFieldValue(doc, MetadataType.FILE_NAME.fieldName()));
        assertEquals("Document's group name field", "some group", getFieldValue(doc, MetadataType.GROUP_NAME.fieldName()));
        assertEquals("Document's id field", "someId", getFieldValue(doc, MetadataType.ID.fieldName()));
        assertEquals("Document's type field", "someType", getFieldValue(doc, MetadataType.TYPE.fieldName()));
    }

    @Test
    public void constructorCreatesWriter(){
        assertNotNull("the tmWriter tmWriter was not created as expected", tmWriter);
    }

    @Test
    public void constructorUsesExpectedDirectory(){
        assertTrue("The index directory should end with 'target/test-classes'", writer.getDirectory() instanceof RAMDirectory);
    }

    @Test(expected = AlreadyClosedException.class)
    public void endIndexClosesWriter() throws IOException {
        tmWriter.endIndex();
        tmWriter.getIndexWriter().commit();
    }

    @Test
    public void endIndexThrowsNoException() throws IOException {
        tmWriter.endIndex();
        tmWriter.endIndex();
    }

    public void endIndexCommits() throws IOException {
        tmWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN",
                new TextFragment("dax")), new TranslationUnitVariant("ES", new TextFragment("is funny (sometimes)"))));
        tmWriter.endIndex();
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed after endIndex", 1, reader.maxDoc());
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNoSourceContent(){
        tmWriter.getDocument(new TranslationUnit(null, new TranslationUnitVariant("EN",
                new TextFragment("some target"))));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentEmptySourceContent(){
        tmWriter.getDocument(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("")),
                new TranslationUnitVariant("EN", new TextFragment("some target"))));
    }

    @Test(expected = NullPointerException.class)
    public void getDocumentNullTU(){
        tmWriter.getDocument(null);
    }

    @Test
    public void getDocumentValues(){
        String text = "blah blah blah";
        TranslationUnit tu = new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment(text)),
                new TranslationUnitVariant("FR", new TextFragment("someone")));
        Document doc = tmWriter.getDocument(tu);
        assertEquals("Document's content field", "blah blah blah", getFieldValue(doc, SOURCE.name()));
        assertEquals("Document's content exact field", "blah blah blah", getFieldValue(doc, SOURCE_EXACT.name()));
        assertEquals("Document's target field", "someone", getFieldValue(doc, TARGET.name()));
        assertEquals("Document's source lang field", "EN", getFieldValue(doc, SOURCE_LANG.name()));
        assertEquals("Document's target lang field", "FR", getFieldValue(doc, TARGET_LANG.name()));
    }

    @Test
    public void getDocumentNoTarget(){
        Document doc = tmWriter.getDocument(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("blah blah blah")), null));
        assertNull("Document's target field should be null", doc.getField(TARGET.name()));
    }

    @Test(expected = NullPointerException.class)
    public void indexTranslationUnitNull() throws IOException {
        tmWriter.indexTranslationUnit(null);
    }

    @Test
    public void indexTranslationUnitNoIndexedDocsBeforeCall() throws IOException {
        assertEquals("num of docs indexed", 0, tmWriter.getIndexWriter().numDocs());
    }

    @Test
    public void indexTranslationUnitBeforeCommit() throws IOException {
        tmWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("dax")), new TranslationUnitVariant("EN", new TextFragment("is funny (sometimes)"))));
        IndexReader reader = IndexReader.open(dir, true);
        assertEquals("num of docs indexed before endIndex", 0, reader.maxDoc());
    }

    @Test
    public void indexTextUnit() throws IOException {
        tmWriter.indexTranslationUnit(new TranslationUnit(new TranslationUnitVariant("EN", new TextFragment("joe")), new TranslationUnitVariant("EN", new TextFragment("schmoe"))));
        assertEquals("num of docs indexed", 1, tmWriter.getIndexWriter().numDocs());
    }

    @Test
    public void validateTUNoSourceLang(){

    }

    private String getFieldValue(Document doc, String fieldName) {
        return doc.getField(fieldName).stringValue();
    }
}
