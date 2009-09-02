/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.seeker;


import java.io.IOException;
import net.sf.okapi.tm.pensieve.common.TMHit;
import java.util.List;

/**
 *
 * @author HaslamJD
 */
public interface Seeker {

    List<TMHit> searchForWords(String query, int max) throws IOException;

    List<TMHit> searchExact(String query, int max) throws IOException;

    List<TMHit> searchFuzzyWuzzy(String query, int max) throws IOException;
}
