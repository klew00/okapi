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
	public void testCodesNoData () {
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<pc id=\"1\">text</pc><ph id=\"2\"/>", frag.toString());
	}

	@Test
	public void testCodesNoDataNotWellFormed1 () {
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "[o1]");
		frag.append("t1");
		frag.append(InlineType.OPENING, "2", "[o2]");
		frag.append("t2");
		frag.append(InlineType.CLOSING, "1", "[c1]");
		frag.append(InlineType.PLACEHOLDER, "3", "<br/>");
		frag.append("t3");
		frag.append(InlineType.CLOSING, "2", "[c2]");
		assertEquals("<sc id=\"1\"/>t1<sc id=\"2\"/>t2<ec rid=\"1\"/><ph id=\"3\"/>t3<ec rid=\"2\"/>", frag.toString());
	}

	//TODO: allow non-well-formed inside well-formed
//	@Test
//	public void testCodesNoDataNotWellFormed2 () {
//		Fragment frag = new Fragment(new Unit("id").getCodeStore());
//		frag.append(InlineType.OPENING, "1", "[o1]");
//		frag.append("t1");
//		frag.append(InlineType.OPENING, "2", "[o2]");
//		frag.append("t2");
//		frag.append(InlineType.OPENING, "3", "[o3]");
//		frag.append("t3");
//		frag.append(InlineType.CLOSING, "2", "[c2]");
//		frag.append("t4");
//		frag.append(InlineType.CLOSING, "3", "[c3]");
//		frag.append("t5");
//		frag.append(InlineType.CLOSING, "1", "[c1]");
//		assertEquals("<pc id=\"1\">t1<sc id=\"2\"/>t2<sc id=\"3\"/>t3<ec rid=\"2\"/>t4<ec rid=\"3\"/>t5</pc>", frag.toString());
//	}

	@Test
	public void testCodesDataInside () {
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<sc id=\"1\">&lt;elem atrr='&amp;amp;'></sc>text<ec rid=\"1\">&lt;/elem></ec><ph id=\"2\">&lt;br/></ph>",
			frag.getString(Fragment.STYLE_DATAINSIDE));
	}

	@Test
	public void testCodesDataOutside () {
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		
		frag.getCodeStore().calculateOriginalDataToIdsMap();
		assertEquals("<sc id=\"1\" nid=\"d1\"/>text<ec rid=\"1\" nid=\"d2\"/><ph id=\"2\" nid=\"d3\"/>",
			frag.getString(Fragment.STYLE_DATAOUTSIDE));
	}

	@Test
	public void testCodesDataOutsideWithReuse () {
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("t1");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		frag.append(InlineType.OPENING, "3", "<elem atrr='&amp;'>");
		frag.append("t2");
		frag.append(InlineType.CLOSING, "3", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "4", "<br/>");
		
		frag.getCodeStore().calculateOriginalDataToIdsMap();
		assertEquals("<sc id=\"1\" nid=\"d1\"/>t1<ec rid=\"1\" nid=\"d2\"/><ph id=\"2\" nid=\"d3\"/>"
			+ "<sc id=\"3\" nid=\"d1\"/>t2<ec rid=\"3\" nid=\"d2\"/><ph id=\"4\" nid=\"d3\"/>",
			frag.getString(Fragment.STYLE_DATAOUTSIDE));
	}

	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		Fragment frag = new Fragment(new Unit("id").getCodeStore());
		frag.append(InlineType.OPENING, "1", "[1]");
		frag.append("text with \u0305");
		frag.append(InlineType.CLOSING, "1", "[/1]");
		frag.append(InlineType.PLACEHOLDER, "2", "[2/]");
		
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
