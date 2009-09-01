/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.writer;

import java.io.IOException;

/**
 *
 * @author HaslamJD
 */
public interface TMWriter {
    void endIndex() throws IOException;

    void indexTextUnit(TranslationUnit tu) throws IOException;
}
