package net.sf.okapi.lib.xliff;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class UnitTest {

	@Test
	public void testNewSegment () {
		Unit unit = new Unit("id");
		Segment seg = unit.appendNewSegment();
		assertNull(seg.id);
		assertTrue(seg.getCodesStore() == unit.getCodesStore());
		assertEquals("", seg.getSource().toString());
	}
	
	@Test
	public void testSimpleUnit () {
		Unit unit = createUnitWithSegment();
		assertEquals("Text<ph id=\"1\"/>in <pc id=\"1\">bold</pc>",
			unit.iterator().next().getSource().toString());
		assertEquals("TEXT<ph id=\"1\"/>IN <pc id=\"1\">BOLD</pc>",
			unit.getPart(0).getTarget(false).toString());
	}

	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		Unit unit = createUnitWithSegment();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(unit);
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Unit unit2 = (Unit)ois.readObject();
		assertEquals(unit.iterator().next().getSource().getString(Fragment.STYLE_DATAINSIDE),
			unit2.getPart(0).getSource().getString(Fragment.STYLE_DATAINSIDE));
	}

	private Unit createUnitWithSegment () {
		Unit unit = new Unit("u1");
		Fragment srcFrag = unit.appendNewSegment().getSource();
		srcFrag.append("Text");
		srcFrag.append(CodeType.PLACEHOLDER, "1", "<br/>");
		srcFrag.append("in ");
		srcFrag.append(CodeType.OPENING, "1", "<b>");
		srcFrag.append("bold");
		srcFrag.append(CodeType.CLOSING, "2", "</b>");
		
		Fragment trgFrag = ((Segment)unit.getPart(0)).getTarget(true);
		trgFrag.append("TEXT");
		trgFrag.append(CodeType.PLACEHOLDER, "1", "<BR/>");
		trgFrag.append("IN ");
		trgFrag.append(CodeType.OPENING, "1", "<B>");
		trgFrag.append("BOLD");
		trgFrag.append(CodeType.CLOSING, "2", "</B>");

		return unit;
	}
}
