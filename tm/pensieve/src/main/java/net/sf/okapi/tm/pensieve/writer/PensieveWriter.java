package net.sf.okapi.tm.pensieve.writer;

import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.common.resource.TextFragment;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;
import net.sf.okapi.tm.pensieve.tmx.TMXHandler;

/**
 * User: Christian Hargraves
 * Date: Aug 5, 2009
 * Time: 8:40:02 AM
 */
public class PensieveWriter implements TMWriter {

    private IndexWriter writer;

    public PensieveWriter(Directory indexDirectory) throws IOException {
        writer = new IndexWriter(indexDirectory,
                new SimpleAnalyzer(), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void endIndex() throws IOException {
        try{
            writer.commit();
        }catch(AlreadyClosedException ignored){
        }finally{
            writer.close();
        }
    }

    public IndexWriter getIndexWriter(){
        return writer;
    }

    public void importTMX(String file, String sourceLang, String targetLang) throws IOException {
        List<TranslationUnit> tus = TMXHandler.getTranslationUnitsFromTMX(file, sourceLang, targetLang);
        for(TranslationUnit tu : tus) {
            indexTranslationUnit(tu);
        }
    }

    public void indexTranslationUnit(TranslationUnit tu) throws IOException {
        if (tu == null){
            throw new NullPointerException("TextUnit can not be null");
        }
        writer.addDocument(getDocument(tu));
    }

    Document getDocument(TranslationUnit tu) {
        if (tu == null || tu.isSourceEmpty()){
            throw new NullPointerException("source content not set");
        }
        Document doc = new Document();
        doc.add(createField(TranslationUnitFields.SOURCE, tu.getSource(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(createField(TranslationUnitFields.SOURCE_EXACT, tu.getSource(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        if (!tu.isTargetEmpty()){
            doc.add(createField(TranslationUnitFields.TARGET, tu.getTarget(), Field.Store.YES, Field.Index.NO));
        }
        return doc;
    }

    Field createField(TranslationUnitFields field,
                  TextFragment frag,
                  Field.Store store,
                  Field.Index index){
        return new Field(field.name(), frag.toString(), store, index);
    }

}
