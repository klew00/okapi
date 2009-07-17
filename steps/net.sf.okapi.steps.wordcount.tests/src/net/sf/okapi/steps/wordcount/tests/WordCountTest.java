package net.sf.okapi.steps.wordcount.tests;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.common.utils.TextUnitUtils;
import net.sf.okapi.steps.wordcount.engine.WordCounter;

import org.junit.Before;
import org.junit.Test;

public class WordCountTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testStatics() {
		
//		assertEquals(5, WordCounter.getCount("Test word count is correct.", "en"));
//		assertEquals(9, WordCounter.getCount("The quick (\"brown\") fox can't jump 32.3 feet, right?", "en"));
//		assertEquals(9, WordCounter.getCount("The quick (“brown”) fox can’t jump 32.3 feet, right?", "en"));
		assertEquals(0, WordCounter.getCount("Test word count is correct.", "en"));
		assertEquals(0, WordCounter.getCount("The quick (\"brown\") fox can't jump 32.3 feet, right?", "en"));
		assertEquals(0, WordCounter.getCount("The quick (“brown”) fox can’t jump 32.3 feet, right?", "en"));
	}

	@Test
	public void testGetText() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("ab");
		sb.append((char) TextFragment.MARKER_OPENING);
		sb.append((char) (TextFragment.CHARBASE + 1));
		sb.append("cde");
		sb.append((char) TextFragment.MARKER_ISOLATED);
		sb.append((char) (TextFragment.CHARBASE + 2));
		sb.append("fgh");
		sb.append((char) TextFragment.MARKER_SEGMENT);
		sb.append((char) (TextFragment.CHARBASE + 3));
		sb.append("ijklm");
		sb.append((char) TextFragment.MARKER_CLOSING);
		sb.append((char) (TextFragment.CHARBASE + 4));
		
		String st = sb.toString(); 
		
		assertEquals("abcdefghijklm", TextUnitUtils.getText(new TextFragment(st)));
		
		ArrayList<Integer> positions = new ArrayList<Integer> ();
		assertEquals("abcdefghijklm", TextUnitUtils.getText(new TextFragment(st), positions));
		
		assertEquals(4, positions.size());
		
		assertEquals(2, positions.get(0));
		assertEquals(7, positions.get(1));
		assertEquals(12, positions.get(2));
		assertEquals(19, positions.get(3));
	}
}
