package net.sf.okapi.steps.wordcount;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.wordcount.WordCounter;

import org.junit.Before;
import org.junit.Test;

public class WordCountTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testStatics() {
		
		// TODO debug
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
		
		assertEquals("abcdefghijklm", TextUnitUtil.getText(new TextFragment(st)));
		
		ArrayList<Integer> positions = new ArrayList<Integer> ();
		assertEquals("abcdefghijklm", TextUnitUtil.getText(new TextFragment(st), positions));
		
		assertEquals(4, positions.size());
		
		assertEquals(2, (int)positions.get(0));
		assertEquals(7, (int)positions.get(1));
		assertEquals(12, (int)positions.get(2));
		assertEquals(19, (int)positions.get(3));
		
		sb = new StringBuilder();
		
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
		sb.append("n");
		
		st = sb.toString(); 
		
		assertEquals("abcdefghijklmn", TextUnitUtil.getText(new TextFragment(st)));
		
		positions = new ArrayList<Integer> ();
		assertEquals("abcdefghijklmn", TextUnitUtil.getText(new TextFragment(st), positions));
		
		assertEquals(4, positions.size());
		
		assertEquals(2, (int)positions.get(0));
		assertEquals(7, (int)positions.get(1));
		assertEquals(12, (int)positions.get(2));
		assertEquals(19, (int)positions.get(3));
		
		st = "abcdefghijklmn";
		assertEquals(st, TextUnitUtil.getText(new TextFragment(st)));
	}
}
