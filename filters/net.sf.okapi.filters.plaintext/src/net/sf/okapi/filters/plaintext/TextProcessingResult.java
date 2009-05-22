package net.sf.okapi.filters.plaintext;

public enum TextProcessingResult {
	
	/**
	 * Indefinite
	 */
	NONE,
	
	/**
	 * Text was not processed
	 */
	REJECTED,
	
	/**
	 * Test was processed, events were created and sent
	 */
	ACCEPTED, 	
	
	/**
	 * Text was not processed, to be processed it needs to be combined with a next portion 
	 */
	COMBINE_WITH_NEXT
}
