/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.query;

/**
 * Enumeration of the different match types possible for an alternate translation entry.
 * <p>
 * <b>Matches are in ranked order from highest to lowest. Please maintain ranked order when adding new entries.</b>
 */
public enum MatchType {

	/**
	 * EXACT and matches a unique id
	 */
	EXACT_UNIQUE_ID,

	/**
	 * EXACT and comes from the preceding version of the same document 
	 * (i.e., if v4 is leveraged this match must come from v3, not v2 or v1!!).
	 */
	EXACT_PREVIOUS_VERSION,

	/**
	 * EXACT and a small number of segments before and/or after.
	 */
	EXACT_LOCAL_CONTEXT,

	/**
	 * EXACT and comes from a repeated segment in the same document.
	 */
	EXACT_DOCUMENT_CONTEXT,

	/**
	 * EXACT and the structural type of the segment (title, paragraph, list element etc..)
	 */
	EXACT_STRUCTURAL,
	
	/**
	 * Matches text and codes exactly.
	 */
	EXACT,

	/**
	 * EXACT_TEXT_ONLY and matches with a unique id
	 */
	EXACT_TEXT_ONLY_UNIQUE_ID,
	
	/**
	 * EXACT_TEXT_ONLY and comes from a previous version of the same document
	 */
	EXACT_TEXT_ONLY_PREVIOUS_VERSION,
	
	/**
	 * Matches text exactly, but there is a difference in one or more codes and/or whitespace
	 */
	EXACT_TEXT_ONLY,
	
	/**
	 * Matches text and codes exactly, but only after the result 
	 * of some automated repair (i.e., number replacement, code repair, 
	 * capitalization, punctuation etc..)
	 */
	EXACT_REPAIRED,

	/**
	 * Matches FUZZY with a unique id
	 */
	FUZZY_UNIQUE_ID,
	
	/**
	 * FUZZY and comes from a previous version of the same document
	 */
	FUZZY_PREVIOUS_VERSION,
	
	/**
	 * Matches both text and/or codes partially.
	 */
	FUZZY,
	
	/**
	 * Matches both text and/or codes partially and some automated repair 
	 * (i.e., number replacement, code repair, capitalization, punctuation etc..) 
	 * was applied to the target
	 */
	FUZZY_REPAIRED,

	/**
	 * Matches assembled from phrases in the TM or other resource.
	 */
	PHRASE_ASSEMBLED,

	/**
	 * Indicates a translation coming from an MT engine.
	 */
	MT,

	/**
	 * TM concordance or phrase match (usually a word or term only)
	 */
	CONCORDANCE,

	/**
	 * Unknown match type. Used as default value only - should always be updated
	 * to a known match type. A UNKOWN type always sorts below all other
	 * matches. Make sure this type is the last in the list.
	 */
	UKNOWN
}
