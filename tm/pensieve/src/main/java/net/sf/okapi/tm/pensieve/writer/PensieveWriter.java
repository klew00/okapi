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
import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
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
        doc.add(createField(TranslationUnitFields.SOURCE, tu.getSource().getContent(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(createField(TranslationUnitFields.SOURCE_LANG, tu.getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(createField(TranslationUnitFields.SOURCE_EXACT, tu.getSource().getContent(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        if (!tu.isTargetEmpty()){
            doc.add(createField(TranslationUnitFields.TARGET, tu.getTarget().getContent(), Field.Store.YES, Field.Index.NO));
            doc.add(createField(TranslationUnitFields.TARGET_LANG, tu.getTarget(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        return doc;
    }

    Field createField(TranslationUnitFields field,
                  TextFragment frag,
                  Field.Store store,
                  Field.Index index){
        return new Field(field.name(), frag.toString(), store, index);
    }

    Field createField(TranslationUnitFields field,
                  TranslationUnitVariant tuv,
                  Field.Store store,
                  Field.Index index){
        return new Field(field.name(), tuv.getLang(), store, index);
    }

}
