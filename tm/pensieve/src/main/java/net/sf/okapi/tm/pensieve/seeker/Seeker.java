/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitFields;

import java.util.List;
import java.io.IOException;

/**
 *
 * @author HaslamJD
 */
public interface Seeker {

    List<TranslationUnit> searchForWords(TranslationUnitFields field, String query, int max) throws IOException;

    List<TranslationUnit> searchExact(TranslationUnitFields field, String query, int max) throws IOException;

    List<TranslationUnit> searchFuzzyWuzzy(TranslationUnitFields field, String query, int max) throws IOException;
}
