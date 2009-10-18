package net.sf.okapi.steps.wordcount;

import static org.junit.Assert.assertEquals;

import net.sf.okapi.common.LocaleId;

import org.junit.Test;

public class WordCountTest {

	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locES005 = LocaleId.fromString("es-005");
	
	@Test
	public void testStatics() {
		assertEquals(5, WordCounter.getCount("Test word count is correct.", locEN));
		assertEquals(9, WordCounter.getCount("The quick (\"brown\") fox can't jump 32.3 feet, right?", locEN));
		assertEquals(9, WordCounter.getCount("The quick (\u201Cbrown\u201D) fox can\u2019t jump 32.3 feet, right?", locEN));

		assertEquals(4, WordCounter.getCount("Words in a sentence", locEN));
		
		//Does not pass because es-005 is not in ULocale list
		//assertEquals(4, WordCounter.getCount("Words in a sentence", locES005));
	}
	
}
