/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TextUnitFields;

import java.util.List;
import java.io.IOException;

/**
 *
 * @author HaslamJD
 */
public interface Seeker {

    List<TranslationUnit> searchForWords(TextUnitFields field, String query, int max) throws IOException;

    List<TranslationUnit> searchExact(TextUnitFields field, String query, int max) throws IOException;
}
