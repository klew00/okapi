package net.sf.okapi.steps.wordcount;

import static org.junit.Assert.assertEquals;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Test;

public class TestWordCount {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private LocaleId locES005 = LocaleId.fromString("es-005");
	
	@Test
	public void testStatics () {
		assertEquals(5, WordCounter.count("Test word count is correct.", locEN));
		assertEquals(9, WordCounter.count("The quick (\"brown\") fox can't jump 32.3 feet, right?", locEN));
		assertEquals(9, WordCounter.count("The quick (\u201Cbrown\u201D) fox can\u2019t jump 32.3 feet, right?", locEN));

		assertEquals(4, WordCounter.count("Words in a sentence", locEN));
		assertEquals(4, WordCounter.count("Words in a sentence", locES005));
	}	

	@Test
	public void testCountApostrophe () {
		//Should be 4 per http://www.lisa.org/fileadmin/standards/GMX-V.html#Words "L'objectif" is 2 words in FR
		assertEquals(4, WordCounter.count("L'objectif est defini.", locFR));
		assertEquals(4, WordCounter.count("L\u2019objectif est defini.", locFR));
		
		assertEquals(10, WordCounter.count("Elle a été la première Française d'une famille d'émigrés.", locFR));
		assertEquals(10, WordCounter.count("Elle a été la première Française d\u2019une famille d\u2019émigrés.", locFR));

		assertEquals(5, WordCounter.count("He can't eat that fast.", locEN));
		assertEquals(5, WordCounter.count("He can\u2019t eat that fast.", locEN));
	}
	
	@Test
	public void testCountHyphen () {
		assertEquals(5, WordCounter.count("  Al Capone was an Italian-American.  ", locEN));
	}
	
	@Test
	public void testCountGMXExamples () {
		assertEquals(9, WordCounter.count("This sentence has a word count of 9 words.", locEN));
		assertEquals(11, WordCounter.count("This sentence/text unit has a word count of 11 words.", locEN));
	}
	
	@Test
	public void testCountTokens () {
		//TODO: GMX "words" are really "tokens" this is a problem 
		assertEquals(3, WordCounter.count("123 123.4 123,5", locEN));
		//TODO: Not quite "tokens"
		assertEquals(0, WordCounter.count("( ) \" \' { } [ ] / % $ @ # ? ! * _ -", locEN));
	}
	
	@Test
	public void testCountEmpty () {
		assertEquals(0, WordCounter.count("", locEN));
		assertEquals(0, WordCounter.count(" \t\n\f\r ", locEN));

		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "b", "[b]");
		assertEquals(0, WordCounter.count(tf, locEN));
	}
	
	@Test
	public void testCountFragments () {
		TextFragment tf = new TextFragment("abc");
		tf.append(TagType.PLACEHOLDER, "b", "[b]");
		tf.append("def");
		assertEquals(1, WordCounter.count(tf, locEN));
	}
	
}
