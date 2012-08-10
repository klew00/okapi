package net.sf.okapi.steps.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.AlignmentStatus;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class ConvertSegmentsToTextUnitTest {
	private ConvertSegmentsToTextUnitsStep converter;
	private ITextUnit segmentedTu;
	private ITextUnit nonSegmentedTu;
	
	@Before
	public void setUp() {
		converter = new ConvertSegmentsToTextUnitsStep();

		// set up segmentedTU
		segmentedTu = new TextUnit("segmentedTU");
		segmentedTu.createTarget(LocaleId.SPANISH, true, IResource.CREATE_EMPTY);
		
		for (int j = 0; j < 3; j++) {			
			Segment srcSeg = new Segment(Integer.toString(j), new TextFragment("a segment. "));
			Segment trgSeg = new Segment(Integer.toString(j), new TextFragment("A SEGMENT. "));
			segmentedTu.getSource().append(srcSeg);
			segmentedTu.getTarget(LocaleId.SPANISH).append(trgSeg);
		}
		segmentedTu.getTarget(LocaleId.SPANISH).getSegments().setAlignmentStatus(AlignmentStatus.ALIGNED);
		
		// set up non segmented TU
		nonSegmentedTu = new TextUnit("NON-SEGMENTED");
		nonSegmentedTu.setSourceContent(new TextFragment("a segment. "));
		nonSegmentedTu.setTargetContent(LocaleId.SPANISH, new TextFragment("A SEGMENT. "));
	}
		
	@Test
	public void convertSegmentedTuToMultiple() {
		Event event = converter.handleTextUnit(new Event(EventType.TEXT_UNIT, segmentedTu));
		int count = 0;
		for (Event e : event.getMultiEvent()) {
			count++;
			ITextUnit tu = e.getTextUnit();
			assertEquals("a segment. ", tu.getSource().toString());
			assertEquals("A SEGMENT. ", tu.getTarget(LocaleId.SPANISH).toString());
		}
		
		assertEquals(3, count);
	}
	
	@Test
	public void convertNonSegmentedTuToMultiple() {
		Event event = converter.handleTextUnit(new Event(EventType.TEXT_UNIT, nonSegmentedTu));
		int count = 0;
		for (Event e : event.getMultiEvent()) {
			count++;
			ITextUnit tu = e.getTextUnit();
			assertEquals("a segment. ", tu.getSource().toString());
			assertEquals("A SEGMENT. ", tu.getTarget(LocaleId.SPANISH).toString());
		}
		
		assertEquals(1, count);
	}
	
	@Test
	public void convertNull() {
		Event event = converter.handleTextUnit(new Event(EventType.TEXT_UNIT, null));
		assertNull(event.getTextUnit());
	}
	
	@Test
	public void convertEmptyNonNull() {
		Event event = converter.handleTextUnit(new Event(EventType.TEXT_UNIT, new TextUnit("NON-NULL")));
		assertNotNull(event.getTextUnit());
	}
}
