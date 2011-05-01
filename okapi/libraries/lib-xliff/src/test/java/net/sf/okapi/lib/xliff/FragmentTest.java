package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FragmentTest {

	@Test
	public void testSimpleFragment () {
		assertEquals("text",
			new Fragment("text").toString());
	}

	@Test
	public void testAppend () {
		Fragment frag = new Fragment();
		frag.append("text1");
		assertEquals("text1", frag.toString());
		frag.append("text2");
		assertEquals("text1text2", frag.toString());
		frag.append('3');
		assertEquals("text1text23", frag.toString());
	}

}
