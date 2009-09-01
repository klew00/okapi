package net.sf.okapi.tm.pensieve.writer;

import net.sf.okapi.tm.pensieve.common.TextUnitFields;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * User: Christian Hargraves
 * Date: Aug 5, 2009
 * Time: 8:40:02 AM
 */
public class ExactMatchWriter implements TMWriter {

    private IndexWriter writer;

    public ExactMatchWriter(Directory indexDirectory) throws IOException {
        writer = new IndexWriter(indexDirectory,
                new SimpleAnalyzer(), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void endIndex() throws IOException {
        try{
            writer.commit();
        }catch(AlreadyClosedException ignored){
        }finally{
            try{
                writer.close();
            }catch(AlreadyClosedException ignored){
            }
        }
    }

    public IndexWriter getIndexWriter(){
        return writer;
    }

    public void indexTextUnit(TranslationUnit tu) throws IOException {
        if (tu == null){
            throw new NullPointerException("TextUnit can not be null");
        }
        writer.addDocument(getDocument(tu));
    }

    Document getDocument(TranslationUnit tu) {
        if (tu == null || tu.getContent() == null){
            throw new NullPointerException("content not set");
        }
        Document doc = new Document();
        doc.add(new Field(TextUnitFields.CONTENT.name(), tu.getContent(),
                Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(TextUnitFields.CONTENT_EXACT.name(), tu.getContent(),
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        if (tu.getAuthor() != null){
            doc.add(new Field(TextUnitFields.AUTHOR.name(), tu.getAuthor(),
                    Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        return doc;
    }
}
