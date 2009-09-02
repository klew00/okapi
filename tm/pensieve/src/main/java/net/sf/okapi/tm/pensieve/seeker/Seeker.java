/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.tm.pensieve.common.TranslationUnit;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author HaslamJD
 */
public interface Seeker {

    List<TranslationUnit> searchForWords(String query, int max) throws IOException;

    List<TranslationUnit> searchExact(String query, int max) throws IOException;

    List<TranslationUnit> searchFuzzyWuzzy(String query, int max) throws IOException;
}
