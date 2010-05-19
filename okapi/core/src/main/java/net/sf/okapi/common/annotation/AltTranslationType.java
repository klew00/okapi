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

package net.sf.okapi.common.annotation;

/**
 * Enumeration of the different match types possible for an alternate translation entry.
 * <p>
 * <h2>Matches are in ranked order from highest to lowest. Please maintain ranked order when adding new entries.</h2>
 */
public enum AltTranslationType {

	/**
	 * Matches EXACT and matches a unique id
	 */
	EXACT_UNIQUE_ID,

	/**
	 * A match that is EXACT and comes from a previous version of the same document
	 */
	EXACT_PREVIOUS_VERSION,

	/**
	 * Matches EXACT and a small number of segments before and/or after.
	 */
	EXACT_LOCAL_CONEXT,

	/**
	 * Matches EXACT and comes from a repeated segment in the same document
	 */
	EXACT_REPEATED_SEGMENT_SAME_DOCUMENT,

	/**
	 * Matches EXACT and the structural type of the segment (title, paragraph, list element etc..)
	 */
	EXACT_STRUCTURAL,
	
	/**
	 * Matches text and codes exactly.
	 */
	EXACT,

	/**
	 * Matches FUZZY with a unique id
	 */
	FUZZY_UNIQUE_ID,

	/**
	 * Matches FUZZY and comes from a previous version of the same document
	 */
	FUZZY_PREVIOUS_VERSION,

	/**
	 * Matches text exactly, but there is a difference in one or more codes
	 */
	FUZZY_EXACT_TEXT,

	/**
	 * Matches both text and/or codes partially.
	 */
	FUZZY,

	/**
	 * Matches assembled from phrases in the TM.
	 */
	PHRASE_ASSEMBLED,

	/**
	 * Indicates a translation coming from an MT engine.
	 */
	MT,

	/**
	 * Unknown match type. A NONE type always sorts below all other matches. Make sure this type is the last in the list.
	 */
	UKNOWN
}
