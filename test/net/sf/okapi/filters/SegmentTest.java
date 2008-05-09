package net.sf.okapi.filters;

import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

public class SegmentTest {
	static private final int INDEXBASE = 0xE200;
	static private int CtoI (char index) {
		return ((int)index)-INDEXBASE;
	}

	static private char ItoC (int index) {
		return (char)(index+INDEXBASE);
	}

	private ISegment segment;
	private String segmentWithCodesAsString;
	private String codesAsString;

	public static junit.framework.Test suite() {
	    return new JUnit4TestAdapter(SegmentTest.class);
	}

	@Before
	public void setUp() {
		segmentWithCodesAsString = String.format("text1 text2%1$c%2$ctext3 %3$c%4$cbolded text%5$c%6$c",
				ISegment.CODE_ISOLATED, ItoC(1-1),
				ISegment.CODE_OPENING, ItoC(2-1),
				ISegment.CODE_CLOSING, ItoC(3-1));

		ArrayList<Code> codes = new ArrayList<Code>();
		Code c1 = new Code(ISegment.CODE_ISOLATED, "br", "<br/>");
		c1.id = 1;
		codes.add(c1);

		Code c2 = new Code(ISegment.CODE_OPENING, "b", "<b>");
		c2.id = 2;
		codes.add(c2);

		Code c3 = new Code(ISegment.CODE_CLOSING, "b", "</b>");
		c3.id = 2;
		codes.add(c3);
		StringBuffer codeBuffer = new StringBuffer();
		for (Code code : codes) {
			codeBuffer.append(String.format("%1$d\u0086%2$d\u0086%3$s\u0086%4$s\u0087",
				code.id, code.type, code.data, code.label));
		}
		codesAsString = codeBuffer.toString();

		segment = new Segment();
		segment.append("text1");
		segment.append(' ');
		segment.append("text2");
		segment.append(ISegment.CODE_ISOLATED, "br", "<br/>");
		segment.append("text3 ");
		segment.append(ISegment.CODE_OPENING, "b", "<b>");
		segment.append("bolded text");
		segment.append(ISegment.CODE_CLOSING, "b", "</b>");
	}

	@Test
	public void reset() {
		segment.append("text1");
		segment.append(' ');
		segment.append("text2");
		segment.append(ISegment.CODE_ISOLATED, "br", "<br/>");
		segment.append("text3 ");
		segment.append(ISegment.CODE_OPENING, "b", "<b>");
		segment.append("bolded text");
		segment.append(ISegment.CODE_CLOSING, "b", "</b>");

		segment.reset();

		assertEquals("", segment.toString());
		assertEquals(0, segment.getCodeCount());
		assertEquals("", segment.getCodedText());
		assertEquals("", segment.getCodes());
		assertFalse(segment.hasCode());
	}

	@Test
	public void append() {
		assertEquals("text1 text2<br/>text3 <b>bolded text</b>", segment.toString(ISegment.TEXTTYPE_ORIGINAL));

		ISegment seg = new Segment();
		seg.append(segment);
		assertEquals("text1 text2<br/>text3 <b>bolded text</b>", seg.toString(ISegment.TEXTTYPE_ORIGINAL));
	}

	@Test
	public void copyFrom() {
		ISegment seg = new Segment();
		seg.copyFrom(segment);
		assertEquals("text1 text2<br/>text3 <b>bolded text</b>", segment.toString(ISegment.TEXTTYPE_ORIGINAL));
	}

	@Test
	public void codes() {
		assertEquals(3, segment.getCodeCount());
		assertEquals(segmentWithCodesAsString, segment.getCodedText());
		assertEquals(codesAsString, segment.getCodes());
		assertTrue(segment.hasCode());

		assertEquals(1, segment.getCodeID(0));
		assertEquals(2, segment.getCodeID(1));
		assertEquals(2, segment.getCodeID(2));

		assertEquals(0, segment.getCodeIndex(1, ISegment.CODE_ISOLATED));
		assertEquals(1, segment.getCodeIndex(2, ISegment.CODE_OPENING));
		assertEquals(2, segment.getCodeIndex(2, ISegment.CODE_CLOSING));

		assertEquals("br", segment.getCodeLabel(0));
		assertEquals("b", segment.getCodeLabel(1));
		assertEquals("b", segment.getCodeLabel(2));

		// setCodes
		ISegment seg = new Segment();
		seg.copyFrom(segment);
		seg.reset();
		seg.setCodes(segment.getCodes());
		assertEquals(codesAsString, segment.getCodes());
	}

	@Test
	public void segmentToString() {
		// These should always be the same
		assertEquals(segment.toString(ISegment.TEXTTYPE_ORIGINAL), segment.toString());
		assertEquals(segment.toString(ISegment.TEXTTYPE_CODED), segment.getCodedText());

		// Test each toString type
		assertEquals("text1 text2<br/>text3 <b>bolded text</b>", segment.toString());
		assertEquals(segmentWithCodesAsString, segment.toString(ISegment.TEXTTYPE_CODED));
		assertEquals("text1 text2<1/>text3 <2>bolded text</2>", segment.toString(ISegment.TEXTTYPE_GENERIC));
		assertEquals("text1 text2<br/>text3 <b>bolded text</b>", segment.toString(ISegment.TEXTTYPE_ORIGINAL));
		assertEquals("text1 text2text3 bolded text", segment.toString(ISegment.TEXTTYPE_PLAINTEXT));
		assertEquals("text1 text2<ph x=\"1\">&lt;br/></ph>text3 <bpt i=\"2\" x=\"2\">&lt;b></bpt>bolded text<ept i=\"2\">&lt;/b></ept>",
				segment.toString(ISegment.TEXTTYPE_TMX14));
		assertEquals("text1 text2<ph id=\"1\">&lt;br/></ph>text3 <bpt id=\"2\">&lt;b></bpt>bolded text<ept id=\"2\">&lt;/b></ept>",
				segment.toString(ISegment.TEXTTYPE_XLIFF12));
		assertEquals("text1 text2<x id=\"1\"/>text3 <g id=\"2\">bolded text</g>",
				segment.toString(ISegment.TEXTTYPE_XLIFF12XG));
	}

	@Test
	public void getLength() {
		assertEquals(34,  segment.getLength(ISegment.TEXTTYPE_CODED));
		assertEquals(39,  segment.getLength(ISegment.TEXTTYPE_GENERIC));
		assertEquals(40,  segment.getLength(ISegment.TEXTTYPE_ORIGINAL));
		assertEquals(28,  segment.getLength(ISegment.TEXTTYPE_PLAINTEXT));
		assertEquals(104, segment.getLength(ISegment.TEXTTYPE_TMX14));
		assertEquals(101, segment.getLength(ISegment.TEXTTYPE_XLIFF12));
		assertEquals(53,  segment.getLength(ISegment.TEXTTYPE_XLIFF12XG));
	}

	@Test
	public void setText() {
		ISegment seg = new Segment();
		seg.setText("Test settext method");
		assertEquals("Test settext method",  seg.toString());

		seg = new Segment();
		seg.setTextFromCoded(segmentWithCodesAsString);
		assertEquals(segmentWithCodesAsString, seg.toString(ISegment.TEXTTYPE_CODED));

		seg = new Segment();
		seg.setTextFromGeneric("text1 text2<1/>text3 <2>bolded text</2>");
		assertEquals("text1 text2<1/>text3 <2>bolded text</2>", seg.toString(ISegment.TEXTTYPE_GENERIC));
	}
}
