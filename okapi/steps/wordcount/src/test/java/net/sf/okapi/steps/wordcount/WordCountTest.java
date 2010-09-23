package net.sf.okapi.steps.wordcount;

import static org.junit.Assert.assertEquals;

import net.sf.okapi.common.LocaleId;

import org.junit.Test;

public class WordCountTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locES005 = LocaleId.fromString("es-005");
	
	@Test
	public void testStatics() {
		assertEquals(5, WordCounter.count("Test word count is correct.", locEN));
		assertEquals(9, WordCounter.count("The quick (\"brown\") fox can't jump 32.3 feet, right?", locEN));
		assertEquals(9, WordCounter.count("The quick (\u201Cbrown\u201D) fox can\u2019t jump 32.3 feet, right?", locEN));

		assertEquals(4, WordCounter.count("Words in a sentence", locEN));
		assertEquals(4, WordCounter.count("Words in a sentence", locES005));
	}	
}
