package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class XLIFFWriterTest {

	@Test
	public void testTwoSegments () {
		XLIFFWriter writer = new XLIFFWriter();
		StringWriter strWriter = new StringWriter();
		writer.create(strWriter);
		writer.setLineBreak("\n");
		
		Unit unit = new Unit("id");
		unit.add(new Segment("Source 1."))
			.add(new Segment("Source 2."));
		writer.writeUnit(unit);
		
		writer.close();
		assertEquals("<unit id=\"id\">\n<segment>\n<source>Source 1.</source>\n"
			+ "</segment>\n<segment>\n<source>Source 2.</source>\n</segment>\n</unit>\n",
			strWriter.toString());
		
	}

}
