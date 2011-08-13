package net.sf.okapi.lib.xliff;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.oasisopen.xliff.v2.InlineType;

public class UnitTest {

	@Test
	public void testNewSegment () {
		Unit unit = new Unit("id");
		Segment seg = unit.appendNewSegment();
		assertNull(seg.getId());
		assertTrue(seg.getDataStore() == unit.getDataStore());
		assertEquals("", seg.getSource().toXLIFF());
		assertFalse(seg.hasTarget());
	}
	
	@Test
	public void testSegment () {
		Unit unit = createUnitWithSegment();
		assertEquals("Text<ph id=\"1\"/>in <pc id=\"2\">bold</pc>",
			unit.iterator().next().getSource().toXLIFF());
		assertEquals("TEXT<ph id=\"1\"/>IN <pc id=\"2\">BOLD</pc>",
			unit.getPart(0).getTarget(false).toXLIFF());
	}

	@Test
	public void testCandidates () {
		Unit unit = createUnitWithSegment();
		assertEquals("unit-text<ph id=\"1\"/>",
			unit.getCandidates().get(0).getSource().toXLIFF());
		assertEquals("UNIT-TEXT<ph id=\"1\"/>",
			unit.getCandidates().get(0).getTarget().toXLIFF());
		
		Segment seg = (Segment)unit.getPart(0);
		assertEquals("seg-text<ph id=\"1\"/>",
			seg.getCandidates().get(0).getSource().toXLIFF());
		assertEquals("SEG-TEXT<ph id=\"1\"/>",
			seg.getCandidates().get(0).getTarget().toXLIFF());
	}
	
	@Test
	public void testNotes () {
		Unit unit = createUnitWithSegment();
		assertEquals("unit-SrcNote", unit.getNotes().get(0).getText());
		assertEquals(Note.AppliesTo.SOURCE, unit.getNotes().get(0).getAppliesTo());
		assertEquals("unit-TrgNote", unit.getNotes().get(1).getText());
		assertEquals(Note.AppliesTo.TARGET, unit.getNotes().get(1).getAppliesTo());
		assertEquals("unit-SrcAndTrgNote", unit.getNotes().get(2).getText());
		assertEquals(Note.AppliesTo.DEFAULT, unit.getNotes().get(2).getAppliesTo());

		Segment seg = (Segment)unit.getPart(0);
		assertEquals("seg-SrcNote", seg.getNotes().get(0).getText());
		assertEquals(Note.AppliesTo.SOURCE, seg.getNotes().get(0).getAppliesTo());
		assertEquals("seg-TrgNote", seg.getNotes().get(1).getText());
		assertEquals(Note.AppliesTo.TARGET, seg.getNotes().get(1).getAppliesTo());
		assertEquals("seg-SrcAndTrgNote", seg.getNotes().get(2).getText());
		assertEquals(Note.AppliesTo.DEFAULT, seg.getNotes().get(2).getAppliesTo());
	}
	
	@Test
	public void testEventSerialization ()
		throws IOException, ClassNotFoundException
	{
		EventData obj1 = new EventData();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj1);
		oos.close();
		
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		EventData obj2 = (EventData)ois.readObject();
		
		assertEquals(obj1.getId(), obj2.getId());
	}
	
	@Test
	public void testSerialization ()
		throws IOException, ClassNotFoundException
	{
		Unit unit1 = createUnitWithSegment();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(unit1);
		oos.close();
		
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Unit unit2 = (Unit)ois.readObject();

		// Check unit-level candidates (source)
		assertEquals(unit1.getCandidates().get(0).getSource().toXLIFF(Fragment.STYLE_DATAINSIDE),
			unit2.getCandidates().get(0).getSource().toXLIFF(Fragment.STYLE_DATAINSIDE));
		// Check unit-level candidates (target)
		assertEquals(unit1.getCandidates().get(0).getTarget().toXLIFF(Fragment.STYLE_DATAINSIDE),
			unit2.getCandidates().get(0).getTarget().toXLIFF(Fragment.STYLE_DATAINSIDE));
		assertEquals(unit1.getId(), unit2.getId());
		// Check unit-level notes
		assertEquals(unit1.getNotes().get(0).getText(), unit2.getNotes().get(0).getText());
		assertEquals(unit1.getNotes().get(0).getAppliesTo(), unit2.getNotes().get(0).getAppliesTo());
		assertEquals(unit1.getNotes().get(1).getText(), unit2.getNotes().get(1).getText());
		assertEquals(unit1.getNotes().get(1).getAppliesTo(), unit2.getNotes().get(1).getAppliesTo());
		assertEquals(unit1.getNotes().get(2).getText(), unit2.getNotes().get(2).getText());
		assertEquals(unit1.getNotes().get(2).getAppliesTo(), unit2.getNotes().get(2).getAppliesTo());
		
		Segment seg1 = (Segment)unit1.getPart(0);
		Segment seg2 = (Segment)unit2.getPart(0);
		// Check attributes
		assertEquals(seg1.isTranslatable(), seg2.isTranslatable());
		assertEquals(seg1.getId(), seg2.getId());
		// Check source
		assertEquals(seg1.getSource().toXLIFF(Fragment.STYLE_DATAINSIDE),
			seg2.getSource().toXLIFF(Fragment.STYLE_DATAINSIDE));
		// Check target
		assertEquals(seg1.getTarget(false).toXLIFF(Fragment.STYLE_DATAINSIDE),
			seg2.getTarget(false).toXLIFF(Fragment.STYLE_DATAINSIDE));
		// Check segment-level candidates (source)
		assertEquals(seg1.getCandidates().get(0).getSource().toXLIFF(Fragment.STYLE_DATAINSIDE),
			seg2.getCandidates().get(0).getSource().toXLIFF(Fragment.STYLE_DATAINSIDE));
		// Check segment-level candidates (target)
		assertEquals(seg1.getCandidates().get(0).getTarget().toXLIFF(Fragment.STYLE_DATAINSIDE),
			seg2.getCandidates().get(0).getTarget().toXLIFF(Fragment.STYLE_DATAINSIDE));
		// Check segment-level notes
		assertEquals(seg1.getNotes().get(0).getText(), seg2.getNotes().get(0).getText());
		assertEquals(seg1.getNotes().get(0).getAppliesTo(), seg2.getNotes().get(0).getAppliesTo());
		assertEquals(seg1.getNotes().get(1).getText(), seg2.getNotes().get(1).getText());
		assertEquals(seg1.getNotes().get(1).getAppliesTo(), seg2.getNotes().get(1).getAppliesTo());
		assertEquals(seg1.getNotes().get(2).getText(), seg2.getNotes().get(2).getText());
		assertEquals(seg1.getNotes().get(2).getAppliesTo(), seg2.getNotes().get(2).getAppliesTo());
	}

	private Unit createUnitWithSegment () {
		Unit unit = new Unit("u1");
		Segment seg = unit.appendNewSegment();
		Fragment srcFrag = seg.getSource();
		srcFrag.append("Text");
		srcFrag.append(InlineType.PLACEHOLDER, "1", "<br/>");
		srcFrag.append("in ");
		srcFrag.append(InlineType.OPENING, "2", "<b>");
		srcFrag.append("bold");
		srcFrag.append(InlineType.CLOSING, "2", "</b>");
		
		Fragment trgFrag = seg.getTarget(true);
		trgFrag.append("TEXT");
		trgFrag.append(InlineType.PLACEHOLDER, "1", "<BR/>");
		trgFrag.append("IN ");
		trgFrag.append(InlineType.OPENING, "2", "<B>");
		trgFrag.append("BOLD");
		trgFrag.append(InlineType.CLOSING, "2", "</B>");

		// Segment-level note
		seg.addNote(createNote("seg", Note.AppliesTo.SOURCE));
		seg.addNote(createNote("seg", Note.AppliesTo.TARGET));
		seg.addNote(createNote("seg", Note.AppliesTo.DEFAULT));
		// Unit-level note
		unit.addNote(createNote("unit", Note.AppliesTo.SOURCE));
		unit.addNote(createNote("unit", Note.AppliesTo.TARGET));
		unit.addNote(createNote("unit", Note.AppliesTo.DEFAULT));

		// Segment-level candidate
		seg.addCandidate(createAlternate("seg"));
		// Unit-level candidate
		unit.addCandidate(createAlternate("unit"));
		
		return unit;
	}
	
	private Candidate createAlternate (String prefix) {
		Candidate alt = new Candidate();
		Fragment frag = new Fragment(alt.getDataStore());
		// Source
		frag.append(prefix+"-text");
		frag.append(InlineType.PLACEHOLDER, "1", "<br/>");
		alt.setSource(frag);
		// Target
		frag = new Fragment(alt.getDataStore());
		frag.append(prefix.toUpperCase()+"-TEXT");
		frag.append(InlineType.PLACEHOLDER, "1", "<br/>");
		alt.setTarget(frag);
		// Done
		return alt;
	}
	
	private Note createNote (String prefix,
		Note.AppliesTo appliesTo)
	{
		switch ( appliesTo ) {
		case SOURCE:
			return new Note(prefix+"-SrcNote", appliesTo);
		case TARGET:
			return new Note(prefix+"-TrgNote", appliesTo);
		case DEFAULT:
		default:
			return new Note(prefix+"-SrcAndTrgNote", appliesTo);
		}
	}
}
