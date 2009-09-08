package net.sf.okapi.tm.pensieve.common;

import org.apache.lucene.document.Field;

/**
 * This is pretty yucky, but we want to shield from Lucene. This may seem like it should be a boolean, but for now
 * we are just following Lucene and YES and NO are the only non-deprecated options.
 */
public enum StoreType {

    YES(Field.Store.YES),
    NO(Field.Store.NO);

    private Field.Store store;
    private StoreType(Field.Store store){
        this.store = store;
    }

    public Field.Store store(){
        return store;
    }
}
