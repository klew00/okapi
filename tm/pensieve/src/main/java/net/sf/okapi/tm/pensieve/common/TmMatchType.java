package net.sf.okapi.tm.pensieve.common;

/**
 * TM match types in rank order (best match first)
 * 
 * @author HARGRAVEJE
 */
public enum TmMatchType {
	/**
	 * Matches both codes and text exactly and a unique id
	 */
	EXACT_ID,

	/**
	 * Matches both codes and text exactly and the segment before and/or after.
	 */
	EXACT_LOCAL_CONEXT,

	/**
	 * Matches both codes and text exactly and the structural type of the
	 * segment (title, paragraph, list element etc..)
	 */
	EXACT_STRUCTURAL,

	/**
	 * Matches text and codes exactly.
	 */
	EXACT,

	/**
	 * Matches text exactly, but there is a difference in one or more codes
	 */
	FUZZY_FULL_TEXT_MATCH,

	/**
	 * Matches both text and codes partially.
	 */
	FUZZY,

	/**
	 * Default NULL match type
	 */
	NONE
}
