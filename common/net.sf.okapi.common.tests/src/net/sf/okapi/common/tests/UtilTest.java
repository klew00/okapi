package net.sf.okapi.common.tests;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {
	
	private CharsetEncoder chsEnc;
	
	@Before
	public void setUp() throws Exception {
		chsEnc = Charset.forName("iso-8859-1").newEncoder();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testTrimStart () {
		String in = " \t ztext  ";
		assertEquals("text  ", Util.trimStart(in, " \tz"));
	}

	@Test
	public void testGetDirectoryName_BSlash () {
		String in = "C:\\test\\file";
		assertEquals("C:\\test", Util.getDirectoryName(in));
	}

	@Test
	public void testGetDirectoryName_Slash () {
		String in = "/home/test/file";
		assertEquals("/home/test", Util.getDirectoryName(in));
	}

	@Test
	public void testGetDirectoryName_DirBSlash () {
		String in = "C:\\test\\";
		assertEquals("C:\\test", Util.getDirectoryName(in));
	}

	@Test
	public void testGetDirectoryName_DirSlash () {
		String in = "/home/test/";
		assertEquals("/home/test", Util.getDirectoryName(in));
	}

	@Test
	public void testGetDirectoryName_Filename () {
		String in = "myFile.ext";
		assertEquals("", Util.getDirectoryName(in));
	}

	@Test
	public void testGetDirectoryName_URL () {
		String in = "/C:/test/file.ext";
		assertEquals("/C:/test", Util.getDirectoryName(in));
	}

	@Test
	public void testEscapeToXML_Quote0 () {
		String in = "&<>'\"";
		assertEquals("&amp;&lt;>'\"", Util.escapeToXML(in, 0, false, null));
	}

	@Test
	public void testEscapeToXML_Quote1 () {
		String in = "&<>'\"";
		assertEquals("&amp;&lt;>&apos;&quot;", Util.escapeToXML(in, 1, false, null));
	}

	@Test
	public void testEscapeToXML_Quote2 () {
		String in = "&<>'\"";
		assertEquals("&amp;&lt;>&#39;&quot;", Util.escapeToXML(in, 2, false, null));
	}

	@Test
	public void testEscapeToXML_Quote3 () {
		String in = "&<>'\"";
		assertEquals("&amp;&lt;>'&quot;", Util.escapeToXML(in, 3, false, null));
	}

	@Test
	public void testEscapeToXML_GT () {
		String in = "&<>'\"";
		assertEquals("&amp;&lt;&gt;'&quot;", Util.escapeToXML(in, 3, true, null));
	}

	@Test
	public void testEscapeToXML_ExtCharsWithNull () {
		String in = "\u00d0\u0440Z\uD840\uDC00";
		assertEquals("\u00d0\u0440Z\uD840\uDC00", Util.escapeToXML(in, 0, false, null));
	}

	@Test
	public void testEscapeToXML_ExtCharsWithLatin1 () {
		String in = "\u00d0\u0440Z\uD840\uDC00";
		assertEquals("\u00d0&#x0440;Z&#x20000;", Util.escapeToXML(in, 0, false, chsEnc));
	}

	@Test
	public void testGetExtension () {
		String in = "myFile.abc.ext";
		assertEquals(".ext", Util.getExtension(in));
	}

	@Test
	public void testGetExtension_Alone () {
		String in = ".ext";
		assertEquals(".ext", Util.getExtension(in));
	}

	@Test
	public void testGetExtension_None () {
		String in = "myFile";
		assertNull(Util.getExtension(in));
	}

	@Test
	public void testGetExtension_Dot () {
		String in = "myFile.";
		assertEquals(".", Util.getExtension(in));
	}

	@Test
	public void testSplitLanguageCode () {
		String in = "en";
		String[] res = Util.splitLanguageCode(in);
		assertEquals(res[0], "en");
		assertEquals(res[1], "");
	}

	@Test
	public void testSplitLanguageCode_4Letters () {
		String in = "en-BZ";
		String[] res = Util.splitLanguageCode(in);
		assertEquals(res[0], "en");
		assertEquals(res[1], "BZ");
	}

	@Test
	public void testSplitLanguageCode_Underline () {
		String in = "en_BZ";
		String[] res = Util.splitLanguageCode(in);
		assertEquals(res[0], "en");
		assertEquals(res[1], "BZ");
	}

	@Test
	public void testGetPercentage () {
		assertEquals(45, Util.getPercentage(450, 1000));
	}

	@Test
	public void testGetPercentage_WithZero () {
		assertEquals(1, Util.getPercentage(10, 0));
	}

}
