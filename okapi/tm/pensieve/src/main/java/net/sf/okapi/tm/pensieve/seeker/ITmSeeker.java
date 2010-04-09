/*
 * =========================================================================== Copyright (C) 2008-2009 by the Okapi
 * Framework contributors ----------------------------------------------------------------------------- This library is
 * free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA See also the full
 * LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.tm.pensieve.seeker;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;

import java.util.List;
import net.sf.okapi.tm.pensieve.common.Metadata;

/**
 * Used to query the TM.
 * 
 * @author HaslamJD
 * @author HARGRAVEJE
 */
public interface ITmSeeker {

	/**
	 * Get a list of exact matches for a given text fragment, taking inline codes in account.
	 * 
	 * @param query
	 *            the fragment to search for
	 * @param metadata
	 *            the metadata attributes to also match against, null for no metadata.
	 * @return a list of exact matches
	 * @throws net.sf.okapi.common.exceptions.OkapiIOException
	 *             if the search cannot be completed due to I/O problems
	 */
	List<TmHit> searchExact(TextFragment query, Metadata metadata);

	/**
	 * 
	 * Get a list of fuzzy matches for a given text fragment, taking inline codes in account.
	 * 
	 * @param query
	 *            the fragment to search for.
	 * @param threshold
	 *            the minimal score value to return.
	 * @param maxHits
	 *            the max number of hits returned.
	 * @param metadata
	 *            the metadata attributes to also match against, null for no metadata.
	 * @return a list of exact or fuzzy matches.
	 * @throws net.sf.okapi.common.exceptions.OkapiIOException
	 *             if the search cannot be completed do to I/O problems
	 */
	List<TmHit> searchFuzzy(TextFragment query, int threshold, int maxHits, Metadata metadata);

	/**
	 * 
	 * Get a list of concordance matches (without position offsets) for a given text string. Simple condordance does not
	 * allow codes.
	 * 
	 * @param query
	 *            the string to search for.
	 * @param threshold
	 *            the minimal score value to return.
	 * @param maxHits
	 *            the max number of hits returned
	 * @param metadata
	 *            the metadata attributes to also match against, null for no metadata.
	 * @return a list of exact or fuzzy concordance marches.
	 * @throws net.sf.okapi.common.exceptions.OkapiIOException
	 *             if the search cannot be completed do to I/O problems
	 */
	List<TmHit> searchSimpleConcordance(String query, int threshold, int maxHits, Metadata metadata);

	/**
	 * Close the searcher
	 */
	void close();
}
