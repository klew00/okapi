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

	@Test
	public void testCodes1 () {
		Fragment frag = new Fragment();
		frag.append(Code.TYPE.OPENING, "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(Code.TYPE.CLOSING, "</elem>");
		frag.append(Code.TYPE.PLACEHOLDER, "<br/>");
		assertEquals("<pc id=\"0\">text</pc><ic id=\"2\"/>", frag.toString());
	}

	@Test
	public void testCodes2 () {
		Fragment frag = new Fragment();
		frag.append(Code.TYPE.OPENING, "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(Code.TYPE.CLOSING, "</elem>");
		frag.append(Code.TYPE.PLACEHOLDER, "<br/>");
		assertEquals("<sc id=\"0\">&lt;elem atrr='&amp;amp;'></sc>text<ec id=\"1\">&lt;/elem></ec><ic id=\"2\">&lt;br/></ic>",
			frag.getString(1));
	}
}
