package net.sf.okapi.tm.pensieve.common;

import org.apache.lucene.document.Field;

/**
 * User: Christian Hargraves
 * Date: Sep 8, 2009
 * Time: 1:30:00 PM
 */
public enum IndexType {

    NO(Field.Index.NO),
    ANALYZED(Field.Index.ANALYZED),
    NOT_ANALYZED(Field.Index.NOT_ANALYZED),
    NOT_ANALYZED_NO_NORMS(Field.Index.NOT_ANALYZED_NO_NORMS),
    ANALYZED_NO_NORMS(Field.Index.ANALYZED_NO_NORMS);

    private Field.Index indexType;

    private IndexType(Field.Index indexType){
        this.indexType = indexType;
    }

    public Field.Index indexType(){
        return indexType;
    }
}
