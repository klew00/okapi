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

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.*;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Used to write, delete and update the index.
 */
public class PensieveWriter implements TMWriter {

    private IndexWriter writer;

    /**
     * Creates a PensieveWriter
     * @param indexDirectory - the Lucene Directory implementation of choice.
     * @throws IOException if the indexDirectory can not load
     */
    public PensieveWriter(Directory indexDirectory) throws IOException {
        writer = new IndexWriter(indexDirectory,
                new SimpleAnalyzer(), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    /**
     * Commits and closes (for now) the transaction.
     * @throws IOException if the commit cannot happen.
     */
    public void endIndex() throws IOException {
        //TODO: make a close method that is separate from this and rename this to commitIndex.
        try{
            writer.commit();
        }catch(AlreadyClosedException ignored){
        }finally{
            writer.close();
        }
    }

    /**
     * Gets a handle on the IndexWriter so that commits and rollbacks can happen outside. For now, this is a convience method.
     * In other words, don't depend on it working for you.
     * @return a handle on the IndexWriter used to Create, Update or Delete the index.
     */
    public IndexWriter getIndexWriter(){
        return writer;
    }

    /**
     * Adds a TranslationUnit to the index
     * @param tu The TranslationUnit to index
     * @throws IOException if the TU can not be indexed.
     */
    public void indexTranslationUnit(TranslationUnit tu) throws IOException {
        if (tu == null){
            throw new NullPointerException("TextUnit can not be null");
        }
        writer.addDocument(getDocument(tu));
    }

    /**
     * Deletes a TranslationUnit based on the id.
     * @param id The Unique ID of the TU to delete
     * @throws IOException if the delete can not happen
     */
    public void delete(String id) throws IOException {
        if (Util.isEmpty(id)){
            throw new IllegalArgumentException("id is a required field for delete to happen");
        }
        writer.deleteDocuments(new Term(MetadataType.ID.fieldName(), id));
    }

    /**
     * Updates a TranslationUnit.
     * @param tu The TranslationUnit to update
     * @throws IOException if the update can not happen
     */
    public void update(TranslationUnit tu) throws IOException {
        if (tu == null || tu.getMetadata().get(MetadataType.ID) == null){
            throw new IllegalArgumentException("tu must be set and at least have its ID set");
        }
        //TODO -- make this transactional
        delete(tu.getMetadata().get(MetadataType.ID));
        indexTranslationUnit(tu);
    }

    Document getDocument(TranslationUnit tu) {
        if (tu == null || tu.isSourceEmpty()){
            throw new NullPointerException("source content not set");
        }
        Document doc = new Document();
        doc.add(createField(TranslationUnitField.SOURCE, tu.getSource().getContent(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(createField(TranslationUnitField.SOURCE_LANG, tu.getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(createField(TranslationUnitField.SOURCE_EXACT, tu.getSource().getContent(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        if (!tu.isTargetEmpty()){
            doc.add(createField(TranslationUnitField.TARGET, tu.getTarget().getContent(), Field.Store.YES, Field.Index.NO));
            doc.add(createField(TranslationUnitField.TARGET_LANG, tu.getTarget(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        addMetadataToDocument(doc, tu.getMetadata());
        return doc;
    }

    Field createField(TranslationUnitField field,
                  TextFragment frag,
                  Field.Store store,
                  Field.Index index){
        return new Field(field.name(), frag.toString(), store, index);
    }

    Field createField(TranslationUnitField field,
                  TranslationUnitVariant tuv,
                  Field.Store store,
                  Field.Index index){
        return new Field(field.name(), tuv.getLang(), store, index);
    }

    void addMetadataToDocument(Document doc, Metadata metadata) {
        for(MetadataType type : metadata.keySet()) {
            doc.add(new Field(type.fieldName(), metadata.get(type), type.store(), type.indexType()));
        }
    }
}
