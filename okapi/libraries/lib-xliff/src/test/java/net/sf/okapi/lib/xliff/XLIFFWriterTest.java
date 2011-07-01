package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class XLIFFWriterTest {

	@Test
	public void testDummy () {
		assertEquals("text", "text");
	}
	
	@Test
	public void testTwoSegments () {
		XLIFFWriter writer = new XLIFFWriter();
		StringWriter strWriter = new StringWriter();
		writer.create(strWriter);
		writer.setLineBreak("\n");
		
		Unit unit = new Unit("id");
		unit.appendNewSegment().setSource("Source 1.");
		unit.appendNewSegment().setSource("Source 2.");
		writer.writeUnit(unit);
		
		writer.close();
		assertEquals("<file>\n<unit id=\"id\">\n<segment>\n<source>Source 1.</source>\n"
			+ "</segment>\n<segment>\n<source>Source 2.</source>\n</segment>\n</unit>\n",
			strWriter.toString());
	}

	@Test
	public void testSegmentWithMatch () {
		XLIFFWriter writer = new XLIFFWriter();
		StringWriter strWriter = new StringWriter();
		writer.create(strWriter);
		writer.setLineBreak("\n");
		
		Unit unit = new Unit("id");
		Segment seg = unit.appendNewSegment();
		seg.setSource("Source 1.");
		seg.addCandidate(new Alternate("Source candidate 1.", "Target candidate 1."));
		writer.writeUnit(unit);
		
		writer.close();
		assertEquals("<file>\n<unit id=\"id\">\n<segment>\n<source>Source 1.</source>\n"
			+ "<matches>\n<match>\n<source>Source candidate 1.</source>\n"
			+ "<target>Target candidate 1.</target>\n</match>\n</matches>\n</segment>\n</unit>\n",
			strWriter.toString());
	}

}
