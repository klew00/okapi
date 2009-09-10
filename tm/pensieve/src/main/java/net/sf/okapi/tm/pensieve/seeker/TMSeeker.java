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

package net.sf.okapi.tm.pensieve.seeker;


import java.io.IOException;
import net.sf.okapi.tm.pensieve.common.TMHit;
import java.util.List;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;

/**
 * Used to query the TM.
 * @author HaslamJD
 */
public interface TMSeeker {

    /**
     * Gets a list of matches for a given set of words. In this case OR is assumed.
     * @param query The words to query for
     * @param max The max number of results
     * @return A list of matches for a given set of words. In this case OR is assumed.
     * @throws IOException if the search cannot be completed do to I/O problems
     */
    List<TMHit> searchForWords(String query, int max) throws IOException;

    /**
     * Gets a list of exact matches for a given phrase.
     * @param query The exact text to search for
     * @param max The max number of results
     * @return A list of exact matches
     * @throws IOException if the search cannot be completed do to I/O problems
     */
    List<TMHit> searchExact(String query, int max) throws IOException;

    /**
     * Gets a list of fuzzy matches for a given phrase.
     * @param query The fuzzy query to match
     * @param max The max number of results
     * @return A list of fuzzy matches
     * @throws IOException if the search cannot be completed do to I/O problems
     */
    List<TMHit> searchFuzzyWuzzy(String query, int max) throws IOException;

    /**
     * Gets all entries in the TM as a list TranslationUnit
     * @return A list of all Translation Units
     * @throws IOException if the search cannot be completed do to I/O problems
     */
    List<TranslationUnit> getAllTranslationUnits() throws IOException;
    //TODO accept fields and values for filtering
}
