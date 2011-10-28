package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.oasisopen.xliff.v2.IAnnotation;
import org.oasisopen.xliff.v2.InlineType;

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
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<pc id=\"1\">text</pc><ph id=\"2\"/>", frag.toXLIFF());
	}

	@Test
	public void testCodesNoDataNotWellFormed1 () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "[o1]");
		frag.append("t1");
		frag.append(InlineType.OPENING, "2", "[o2]");
		frag.append("t2");
		frag.append(InlineType.CLOSING, "1", "[c1]");
		frag.append(InlineType.PLACEHOLDER, "3", "<br/>");
		frag.append("t3");
		frag.append(InlineType.CLOSING, "2", "[c2]");
		assertEquals("<sc id=\"1\"/>t1<sc id=\"2\"/>t2<ec rid=\"1\"/><ph id=\"3\"/>t3<ec rid=\"2\"/>", frag.toXLIFF());
	}

	@Test
	public void testCodesNoDataNotWellFormed2 () {
		// Allow non-well-formed inside well-formed
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "[o1]");
		frag.append("t1");
		frag.append(InlineType.OPENING, "2", "[o2]");
		frag.append("t2");
		frag.append(InlineType.OPENING, "3", "[o3]");
		frag.append("t3");
		frag.append(InlineType.CLOSING, "2", "[c2]");
		frag.append("t4");
		frag.append(InlineType.CLOSING, "3", "[c3]");
		frag.append("t5");
		frag.append(InlineType.CLOSING, "1", "[c1]");
		assertEquals("<pc id=\"1\">t1<sc id=\"2\"/>t2<sc id=\"3\"/>t3<ec rid=\"2\"/>t4<ec rid=\"3\"/>t5</pc>", frag.toXLIFF());
	}

	@Test
	public void testSimpleAnnotations () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		IAnnotation ann = new Annotation("a1", true, "comment", "my comment");
		frag.append(ann);
		frag.append("t1 ");
		ann = new Annotation("a1", false, "comment");
		frag.append(ann);
		frag.append("t2.");
		assertEquals("<mrk id=\"a1\" type=\"comment\" value=\"my comment\">t1 </mrk>t2.", frag.toXLIFF());
	}
	
	@Test
	public void testMarkersNotWellFormed1 () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(new Annotation("1", true, "type1"));
		frag.append("t1");
		frag.append(InlineType.OPENING, "2", "[c2]");
		frag.append(new Annotation("1", false, "type1"));
		frag.append(InlineType.CLOSING, "2", "[/c2]");
		assertEquals("<sa id=\"1\" type=\"type1\"/>t1<sc id=\"2\"/><ea rid=\"1\"/><ec rid=\"2\"/>", frag.toXLIFF());
	}

	@Test
	public void testCodesDataInside () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		assertEquals("<sc id=\"1\">&lt;elem atrr='&amp;amp;'></sc>text<ec rid=\"1\">&lt;/elem></ec><ph id=\"2\">&lt;br/></ph>",
			frag.toXLIFF(Fragment.STYLE_DATAINSIDE));
	}

	@Test
	public void testCodesDataOutside () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("text");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		
		frag.getDataStore().calculateOriginalDataToIdsMap();
		assertEquals("<pc id=\"1\" nidEnd=\"d2\" nidStart=\"d1\">text</pc><ph id=\"2\" nid=\"d3\"/>",
			frag.toXLIFF(Fragment.STYLE_DATAOUTSIDE));
	}

	@Test
	public void testInvalidChars () {
		char[] chars = Character.toChars(0x10001);
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "[\u0002"+chars[0]+chars[1]+"\uFFFF]");
		frag.append("\u001a\u0002\t\n\u0020\uD7FF\u0019"+chars[0]+chars[1]+"\uFFFF");
		frag.append(InlineType.CLOSING, "1", "[/\u0002"+chars[0]+chars[1]+"\uFFFF]");
		
		frag.getDataStore().calculateOriginalDataToIdsMap();
		assertEquals("<sc id=\"1\">[<cp hex=\"0002\"/>"+chars[0]+chars[1]+"<cp hex=\"FFFF\"/>]</sc>"
			+ "<cp hex=\"001A\"/><cp hex=\"0002\"/>\t\n\u0020\uD7FF<cp hex=\"0019\"/>"+chars[0]+chars[1]+"<cp hex=\"FFFF\"/>"
			+ "<ec rid=\"1\">[/<cp hex=\"0002\"/>"+chars[0]+chars[1]+"<cp hex=\"FFFF\"/>]</ec>",
			frag.toXLIFF(Fragment.STYLE_DATAINSIDE));
	}

	@Test
	public void testCodesDataOutsideWithReuse () {
		Fragment frag = new Fragment(new Unit("id").getDataStore());
		frag.append(InlineType.OPENING, "1", "<elem atrr='&amp;'>");
		frag.append("t1");
		frag.append(InlineType.CLOSING, "1", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "2", "<br/>");
		frag.append(InlineType.OPENING, "3", "<elem atrr='&amp;'>");
		frag.append("t2");
		frag.append(InlineType.CLOSING, "3", "</elem>");
		frag.append(InlineType.PLACEHOLDER, "4", "<br/>");
		
		frag.getDataStore().calculateOriginalDataToIdsMap();
		assertEquals("<pc id=\"1\" nidEnd=\"d2\" nidStart=\"d1\">t1</pc><ph id=\"2\" nid=\"d3\"/>"
			+ "<pc id=\"3\" nidEnd=\"d2\" nidStart=\"d1\">t2</pc><ph id=\"4\" nid=\"d3\"/>",
			frag.toXLIFF(Fragment.STYLE_DATAOUTSIDE));
	}

	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		Fragment frag = new Fragment(new Unit("id").getDataStore());
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
		assertEquals(frag.toXLIFF(Fragment.STYLE_DATAINSIDE),
			frag2.toXLIFF(Fragment.STYLE_DATAINSIDE));
	}

}
