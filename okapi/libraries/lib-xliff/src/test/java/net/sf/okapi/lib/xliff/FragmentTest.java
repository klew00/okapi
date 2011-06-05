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
		frag.append(CodeType.OPENING, "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(CodeType.CLOSING, "</elem>");
		frag.append(CodeType.PLACEHOLDER, "<br/>");
		assertEquals("<pc id=\"1\">text</pc><ph id=\"3\"/>", frag.toString());
	}

	@Test
	public void testCodes2 () {
		Fragment frag = new Fragment();
		frag.append(CodeType.OPENING, "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(CodeType.CLOSING, "</elem>");
		frag.append(CodeType.PLACEHOLDER, "<br/>");
		assertEquals("<sc id=\"1\">&lt;elem atrr='&amp;amp;'></sc>text<ec id=\"2\">&lt;/elem></ec><ph id=\"3\">&lt;br/></ic>",
			frag.getString(1));
	}

	@Test
	public void testCodesWithDuplicatedID () {
		Fragment frag = new Fragment();
		frag.append(CodeType.OPENING, "i1", "[1]");
		frag.append("text");
		frag.append(CodeType.CLOSING, "i1", "[/1]"); // Same ID hould be corrected to "1" (next available)
		//todo: rid missing
		assertEquals("<sc id=\"i1\">[1]</sc>text<ec id=\"1\">[/1]</ec>", frag.getString(Fragment.STYLE_DATAINSIDE));
	}

}
