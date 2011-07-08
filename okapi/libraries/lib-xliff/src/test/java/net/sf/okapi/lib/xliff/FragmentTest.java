package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class FragmentTest {

	@Test
	public void testSimpleFragment () {
		assertEquals("text", new Fragment(null, false, "text").toString());
	}

	@Test
	public void testAppend () {
		Fragment frag = new Fragment(null, false, "text1");
		assertEquals("text1", frag.toString());
		frag.append("text2");
		assertEquals("text1text2", frag.toString());
		frag.append('3');
		assertEquals("text1text23", frag.toString());
	}

	@Test
	public void testCodes1 () {
		Fragment frag = new Fragment(new Unit("id").getCodesStore());
		frag.append(CodeType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(CodeType.CLOSING, "1", "</elem>");
		frag.append(CodeType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<pc id=\"1\">text</pc><ph id=\"2\"/>", frag.toString());
	}

	@Test
	public void testCodes2 () {
		Fragment frag = new Fragment(new Unit("id").getCodesStore());
		frag.append(CodeType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(CodeType.CLOSING, "1", "</elem>");
		frag.append(CodeType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<sc id=\"1\">&lt;elem atrr='&amp;amp;'></sc>text<ec rid=\"1\">&lt;/elem></ec><ph id=\"2\">&lt;br/></ph>",
			frag.getString(1));
	}

	@Test
	public void testCodes3 () {
		Fragment frag = new Fragment(new Unit("id").getCodesStore());
		frag.append(CodeType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(CodeType.CLOSING, "1", "</elem>");
		frag.append(CodeType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<sc id=\"1\" nid=\"so1\"/>text<ec rid=\"1\" nid=\"sc1\"/><ph id=\"2\" nid=\"sp2\"/>",
			frag.getString(2));
	}

	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		Fragment frag = new Fragment(new Unit("id").getCodesStore());
		frag.append(CodeType.OPENING, "1", "[1]");
		frag.append("text with \u0305");
		frag.append(CodeType.CLOSING, "1", "[/1]");
		frag.append(CodeType.PLACEHOLDER, "2", "[2/]");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(frag);
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Fragment frag2 = (Fragment)ois.readObject();
		assertEquals(frag.getString(Fragment.STYLE_DATAINSIDE),
			frag2.getString(Fragment.STYLE_DATAINSIDE));
	}

}
