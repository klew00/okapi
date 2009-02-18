package net.sf.okapi.common.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.sf.okapi.common.MemMappedCharSequence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MemMappedCharSequenceTest {
	private MemMappedCharSequence charSequence;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testString() {
		String testString = "This is a test of the in memory string handling";
		charSequence = new MemMappedCharSequence(testString);
		assertEquals(47, charSequence.length());
		assertEquals("test", charSequence.subSequence(10, 14));
		assertEquals("test", charSequence.subSequence(10, 14));
		assertEquals('T', charSequence.charAt(0));
		assertTrue(charSequence.containsAt(testString, 0));
		assertEquals(testString, charSequence.toString());
		assertEquals(25, charSequence.lastIndexOf("memory", testString.length()));
	}

	@Test
	public void testLarge() throws FileNotFoundException {
		InputStream in = new FileInputStream("D:/OKAPI/net.sf.okapi.common.tests/src/testfiles/FCH.tmx");
		charSequence = new MemMappedCharSequence(in, "UTF-16LE");
		assertEquals(7257720, charSequence.length());
		assertEquals('<', charSequence.charAt(1));
		assertEquals('U', charSequence.charAt(31));
		assertEquals(7257718, charSequence.lastIndexOf(">", charSequence.length()-1));				
	}
	
	@Test
	public void testLowercase() throws FileNotFoundException {
		InputStream in = new FileInputStream("D:/OKAPI/net.sf.okapi.common.tests/src/testfiles/FCH.tmx");
		charSequence = new MemMappedCharSequence(in, "UTF-16LE", true);		
		assertEquals('u', charSequence.charAt(31));
	}
}
