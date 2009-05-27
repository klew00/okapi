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
	 * Text was not processed, processing will be done later
	 */
	DELAYED_DECISION
}
