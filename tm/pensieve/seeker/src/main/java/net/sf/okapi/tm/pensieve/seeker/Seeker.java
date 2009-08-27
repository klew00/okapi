/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.tm.pensieve.writer.TextUnit;
import net.sf.okapi.tm.pensieve.writer.TextUnitFields;

import java.util.List;
import java.io.IOException;

/**
 *
 * @author HaslamJD
 */
public interface Seeker {

    List<TextUnit> searchForWords(TextUnitFields field, String query, int max) throws IOException;

    List<TextUnit> searchExact(TextUnitFields field, String query, int max) throws IOException;
}
