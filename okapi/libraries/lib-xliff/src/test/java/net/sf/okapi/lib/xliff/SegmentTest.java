package net.sf.okapi.lib.xliff;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.oasisopen.xliff.v2.IFragment;
import org.oasisopen.xliff.v2.ISegment;
import org.oasisopen.xliff.v2.InlineType;

public class SegmentTest {

	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		ISegment seg = new Segment(new Unit("id").getDataStore());
		IFragment frag = seg.getSource();
		frag.append(InlineType.OPENING, "1", "[1]");
		frag.append("text with \u0305 and \u0001");
		frag.append(InlineType.CLOSING, "1", "[/1]");
		frag.append(InlineType.PLACEHOLDER, "2", "[2and\u0001/]");
		seg.setPreserveWS(true);
		seg.setTranslatable(false);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(seg);
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		ISegment seg2 = (Segment)ois.readObject();
		IFragment frag2 = seg2.getSource();
		assertEquals(frag.toXLIFF(Fragment.STYLE_DATAINSIDE),
			frag2.toXLIFF(Fragment.STYLE_DATAINSIDE));
		assertEquals(seg.getId(), seg2.getId());
		assertEquals(seg.getPreserveWS(), seg2.getPreserveWS());
		assertEquals(seg.isTranslatable(), seg2.isTranslatable());
	}

}
