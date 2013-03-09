package net.sf.okapi.common.skeleton;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.ITextUnit;

import org.junit.Before;
import org.junit.Test;

public class GenericSkeletonWriterTest {

	static final private LocaleId locEN = LocaleId.ENGLISH;
	static final private LocaleId locFR = LocaleId.FRENCH;
	static final private LocaleId locDE = LocaleId.GERMAN;
	
	private ISkeletonWriter gsw;
	private EncoderManager encMgt;

	@Before
	public void setUp () {
		gsw = new GenericSkeletonWriter();
		encMgt = new EncoderManager();
	}
	
	@Test
	public void testContentPlaceholder_NoTranslation () {
		// Start
		ArrayList<Event> events = createStartEvents(false);
		// TU
		GenericSkeleton gs = new GenericSkeleton();
		gs.add("before [");
		ITextUnit tu = createSimpleTU();
		gs.addContentPlaceholder(tu);
		gs.add("] after");
		tu.setSkeleton(gs);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// End
		addEndEvents(events);
		
		String expected = "before [text1] after";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);
	}
	
	@Test
	public void testContentPlaceholder_Translated () {
		// Start
		ArrayList<Event> events = createStartEvents(false);
		// TU
		GenericSkeleton gs = new GenericSkeleton();
		gs.add("before [");
		ITextUnit tu = createTranslatedTU();
		gs.addContentPlaceholder(tu);
		gs.add("] after");
		tu.setSkeleton(gs);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// End
		addEndEvents(events);
		
		String expected = "before [target1] after";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);
	}
	
	@Test
	public void testContentPlaceholder_Bilingual () {
		// Start
		ArrayList<Event> events = createStartEvents(true);
		// TU
		GenericSkeleton gs = new GenericSkeleton();
		gs.add("lang1=[");
		ITextUnit tu = createTranslatedTU();
		gs.addContentPlaceholder(tu);
		gs.add("] lang2=[");
		gs.addContentPlaceholder(tu, locFR);
		gs.add("]");
		tu.setSkeleton(gs);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// End
		addEndEvents(events);
		
		String expected = "lang1=[text1] lang2=[target1]";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);

		expected = "lang1=[text1] lang2=[TARGET1]";
		result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, true);
		assertEquals(expected, result);
	}
	
	@Test
	public void testContentPlaceholder_Trilingual () {
		// Start
		ArrayList<Event> events = createStartEvents(true);
		// TU
		GenericSkeleton gs = new GenericSkeleton();
		gs.add("lang1=[");
		ITextUnit tu = createTranslatedTU();
		tu.setTarget(locDE, new TextContainer("target2"));
		gs.addContentPlaceholder(tu);
		gs.add("] lang2=[");
		gs.addContentPlaceholder(tu, locFR);
		gs.add("] lang3=[");
		gs.addContentPlaceholder(tu, locDE);
		gs.add("]");
		tu.setSkeleton(gs);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// End
		addEndEvents(events);
		
		String expected = "lang1=[text1] lang2=[target1] lang3=[target2]";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);

		expected = "lang1=[text1] lang2=[TARGET1] lang3=[target2]";
		result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, true);
		assertEquals(expected, result);
		
		tu.setTarget(locFR, new TextContainer("target1")); // Reset content
		expected = "lang1=[text1] lang2=[target1] lang3=[TARGET2]";
		result = FilterTestDriver.generateOutput(events, locDE, gsw, encMgt, true);
		assertEquals(expected, result);
	}
	
	@Test
	public void testValuePlaceholders () {
		// Start
		ArrayList<Event> events = createStartEvents(false);
		// TU
		GenericSkeleton gs = new GenericSkeleton();
		gs.add("[");
		ITextUnit tu = createTranslatedTU();
		tu.setSourceProperty(new Property("srcProp", "val1", false));
		tu.setProperty(new Property("tuProp", "val2", false));
		tu.setTargetProperty(locFR, new Property("trgProp", "val3", false));
		gs.addContentPlaceholder(tu);
		gs.add("] srcProp={");
		gs.addValuePlaceholder(tu, "srcProp", null);
		gs.add("} tuProp={");
		gs.addValuePlaceholder(tu, "tuProp", LocaleId.EMPTY);
		gs.add("} trgProp={");
		gs.addValuePlaceholder(tu, "trgProp", locFR);
		gs.add("}");		
		tu.setSkeleton(gs);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// End
		addEndEvents(events);
		
		String expected = "[target1] srcProp={val1} tuProp={val2} trgProp={val3}";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);
	}
	
	@Test
	public void testReference () {
		// Start
		ArrayList<Event> events = createStartEvents(false);
		// TU
		GenericSkeleton gs1 = new GenericSkeleton();
		gs1.add("{sub-block [");
		ITextUnit tu = createSimpleTU();
		tu.setIsReferent(true);
		gs1.addContentPlaceholder(tu);
		gs1.add("]}");
		tu.setSkeleton(gs1);
		events.add(new Event(EventType.TEXT_UNIT, tu));
		// TU parent
		GenericSkeleton gs2 = new GenericSkeleton();
		gs2.add("Start ");
		gs2.addReference(tu);
		gs2.add(" end.");
		DocumentPart dp = new DocumentPart("dp1", false);
		dp.setSkeleton(gs2);
		events.add(new Event(EventType.DOCUMENT_PART, dp));
		// End
		addEndEvents(events);
		
		String expected = "Start {sub-block [text1]} end.";
		String result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, false);
		assertEquals(expected, result);

		expected = "Start {sub-block [TEXT1]} end.";
		result = FilterTestDriver.generateOutput(events, locFR, gsw, encMgt, true);
		assertEquals(expected, result);
	}
	
	@Test
	public void testSegmentRef() {
		ITextUnit tu1 = new TextUnit("tu1");
		
		TextFragment tf = new TextFragment();
		tf.append(new Code(TagType.OPENING, Code.TYPE_BOLD, "<b>"));
		tf.append("Source segment 11");
		tf.append(new Code(TagType.CLOSING, Code.TYPE_BOLD, "</b>"));
		
		tu1.getSource().getSegments().append(new Segment("sseg11", tf));
		tu1.getSource().getSegments().append(new Segment("sseg12", new TextFragment("Source segment 12")));
		
		tu1.setTarget(locDE, new TextContainer());
		tu1.getTarget(locDE).getSegments().append(new Segment("tseg11", new TextFragment("Target segment 11")));
		tu1.getTarget(locDE).getSegments().append(new Segment("tseg12", new TextFragment("Target segment 12")));
		
		GenericSkeleton skel = new GenericSkeleton();
		tu1.setSkeleton(skel);
		
		skel.add("{0>");
		createSrcSegRefPart(skel, tu1, "sseg11");
		skel.add("<}10{>");			
		createTrgSegRefPart(skel, tu1, "tseg11", locDE);
		skel.add("<0}");
		skel.add(" ");
		skel.add("{0>");
		createSrcSegRefPart(skel, tu1, "sseg12");
		skel.add("<}90{>");			
		createTrgSegRefPart(skel, tu1, "tseg12", locDE);
		skel.add("<0}");
		
		assertEquals("{0>[#$sseg11@%$segment$]<}10{>[#$tseg11@%$segment$]<0} {0>[#$sseg12@%$segment$]<}90{>[#$tseg12@%$segment$]<0}",
				skel.toString());
		List<GenericSkeletonPart> parts = skel.getParts();
		assertEquals(11, parts.size());
		
		GenericSkeletonPart part;
		part = parts.get(1);
		assertEquals(tu1, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(3);
		assertEquals(tu1, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		part = parts.get(7);
		assertEquals(tu1, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(9);
		assertEquals(tu1, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		ITextUnit tu2 = new TextUnit("tu2");
		
		tu2.getSource().getSegments().append(new Segment("sseg21", new TextFragment("Source segment 21")));
		tu2.getSource().getSegments().append(new Segment("sseg22", new TextFragment("Source segment 22")));
		
		tu2.setTarget(locDE, new TextContainer());
		tu2.getTarget(locDE).getSegments().append(new Segment("tseg21", new TextFragment("Target segment 21")));
		tu2.getTarget(locDE).getSegments().append(new Segment("tseg22", new TextFragment("Target segment 22")));
		
		skel = new GenericSkeleton();
		tu2.setSkeleton(skel);
		
		skel.add("{0>");
		createSrcSegRefPart(skel, tu2, "sseg21");
		skel.add("<}10{>");			
		createTrgSegRefPart(skel, tu2, "tseg21", locDE);
		skel.add("<0}");
		skel.add(" ");
		skel.add("{0>");
		createSrcSegRefPart(skel, tu2, "sseg22");
		skel.add("<}90{>");			
		createTrgSegRefPart(skel, tu2, "tseg22", locDE);
		skel.add("<0}");
		
		assertEquals("{0>[#$sseg21@%$segment$]<}10{>[#$tseg21@%$segment$]<0} {0>[#$sseg22@%$segment$]<}90{>[#$tseg22@%$segment$]<0}", 
				skel.toString());
		parts = skel.getParts();
		assertEquals(11, parts.size());
		
		part = parts.get(1);
		assertEquals(tu2, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(3);
		assertEquals(tu2, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		part = parts.get(7);
		assertEquals(tu2, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(9);
		assertEquals(tu2, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		//System.out.println(tu2.toString());
		
		ArrayList<Event> events = createStartEvents(true);
		events.add(new Event(EventType.TEXT_UNIT, tu1));
		events.add(new Event(EventType.TEXT_UNIT, tu2));
		addEndEvents(events);
		String expected = "{0><b>Source segment 11</b><}10{>Target segment 11<0} {0>Source segment 12<}90{>Target segment 12<0}" +
				"{0>Source segment 21<}10{>Target segment 21<0} {0>Source segment 22<}90{>Target segment 22<0}";
		String result = FilterTestDriver.generateOutput(events, locDE, gsw, encMgt, false);
		assertEquals(expected, result);
	}
	
	@Test
	public void testSegmentRef2() {
		ITextUnit tu1 = new TextUnit("tu1");
		
		TextFragment tf = new TextFragment();
		tf.append(new Code(TagType.OPENING, Code.TYPE_BOLD, "<b>"));
		tf.append("Source segment 1");
		tf.append(new Code(TagType.CLOSING, Code.TYPE_BOLD, "</b>"));
		
		tu1.getSource().getSegments().append(new Segment("sseg1", tf));
		tu1.getSource().getSegments().append(new Segment("sseg2", new TextFragment("Source segment 2")));
		
		tu1.setTarget(locDE, new TextContainer());
		tu1.getTarget(locDE).getSegments().append(new Segment("tseg1", new TextFragment("Target segment 1")));
		tu1.getTarget(locDE).getSegments().append(new Segment("tseg2", new TextFragment("Target segment 2")));
		
		GenericSkeleton skel = new GenericSkeleton();
		tu1.setSkeleton(skel);
		
		skel.add("{0>");
		createSrcSegRefPart(skel, tu1, "sseg1");
		skel.add("<}10{>");			
		createTrgSegRefPart(skel, tu1, "tseg1", locDE);
		skel.add("<0}");
		skel.add(" ");
		skel.add("{0>");
		createSrcSegRefPart(skel, tu1, "sseg2");
		skel.add("<}90{>");			
		createTrgSegRefPart(skel, tu1, "tseg2", locDE);
		skel.add("<0}");
		
		assertEquals("{0>[#$sseg1@%$segment$]<}10{>[#$tseg1@%$segment$]<0} {0>[#$sseg2@%$segment$]<}90{>[#$tseg2@%$segment$]<0}", 
				skel.toString());
		List<GenericSkeletonPart> parts = skel.getParts();
		assertEquals(11, parts.size());
		
		GenericSkeletonPart part;
		part = parts.get(1);
		assertEquals(tu1, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(3);
		assertEquals(tu1, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		part = parts.get(7);
		assertEquals(tu1, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(9);
		assertEquals(tu1, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		ITextUnit tu2 = new TextUnit("tu2");
		
		tu2.getSource().getSegments().append(new Segment("sseg21", new TextFragment("Source segment 21")));
		tu2.getSource().getSegments().append(new Segment("sseg22", new TextFragment("Source segment 22")));
		
		tu2.setTarget(locDE, new TextContainer());
		tu2.getTarget(locDE).getSegments().append(new Segment("tseg21", new TextFragment("Target segment 21")));
		
		TextFragment tf2 = new TextFragment();
		tf2.append("Target segment 22.1");
		Code code = new Code(TagType.PLACEHOLDER, null, TextFragment.makeRefMarker("tu1"));
		code.setReferenceFlag(true);
		tf2.append(code);
		tu1.setIsReferent(true);
		
		tf2.append("Target segment 22.2");
		tu2.getTarget(locDE).getSegments().append(new Segment("tseg22", tf2));
		
		skel = new GenericSkeleton();
		tu2.setSkeleton(skel);
		
		skel.add("{0>");
		createSrcSegRefPart(skel, tu1, "sseg1");
		skel.add("<}10{>");			
		createTrgSegRefPart(skel, tu1, "tseg1", locDE);
		skel.add("<0}");
		skel.add(" ");
		skel.add("{0>");
		createSrcSegRefPart(skel, tu2, "sseg22");
		skel.add("<}90{>");			
		createTrgSegRefPart(skel, tu2, "tseg22", locDE);
		skel.add("<0}");
		
		assertEquals("{0>[#$sseg1@%$segment$]<}10{>[#$tseg1@%$segment$]<0} {0>[#$sseg22@%$segment$]<}90{>[#$tseg22@%$segment$]<0}", 
				skel.toString());
		parts = skel.getParts();
		assertEquals(11, parts.size());
		
		part = parts.get(1);
		assertEquals(tu1, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(3);
		assertEquals(tu1, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		part = parts.get(7);
		assertEquals(tu2, part.getParent());
		assertEquals(null, part.getLocale());
		
		part = parts.get(9);
		assertEquals(tu2, part.getParent());
		assertEquals(locDE, part.getLocale());
		
		//System.out.println(tu2.toString());
		
		ArrayList<Event> events = createStartEvents(true);
		events.add(new Event(EventType.TEXT_UNIT, tu1));
		events.add(new Event(EventType.TEXT_UNIT, tu2));
		addEndEvents(events);
		String expected = "{0><b>Source segment 1</b><}10{>Target segment 1<0} " +
				"{0>Source segment 22<}90{>Target segment 22.1" +
				"{0><b>Source segment 1</b><}10{>Target segment 1<0} {0>Source segment 2<}90{>Target segment 2<0}" + // tu1 ref
				"Target segment 22.2<0}";
		String result = FilterTestDriver.generateOutput(events, locDE, gsw, encMgt, false);
		assertEquals(expected, result);
	}
	
	private void createSrcSegRefPart(ISkeleton skel, ITextUnit tu, String segId) {
		createSegmentRefPart(skel, tu, segId, null);
	}
	
	private void createTrgSegRefPart(ISkeleton skel, ITextUnit tu, String segId, LocaleId locId) {
		createSegmentRefPart(skel, tu, segId, locId);
	}
	
	private void createSegmentRefPart(ISkeleton skel, ITextUnit parent, String segId, LocaleId locId) {
		if (skel instanceof GenericSkeleton) {
			GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker(segId, Segment.REF_MARKER), 
					parent, locId);
			((GenericSkeleton)skel).getParts().add(part);
		}
	}
	
	private ITextUnit createSimpleTU () {
		ITextUnit tu = new TextUnit("id1");
		tu.setSourceContent(new TextFragment("text1"));
		return tu;
	}

	private ITextUnit createTranslatedTU () {
		ITextUnit tu = new TextUnit("id1");
		tu.setSourceContent(new TextFragment("text1"));
		tu.setTarget(locFR, new TextContainer("target1"));
		return tu;
	}

	private ArrayList<Event> createStartEvents (boolean multilangual) {
		ArrayList<Event> list = new ArrayList<Event>();
		StartDocument sd = new StartDocument("sd");
		sd.setEncoding("UTF-8", false);
		sd.setName("docName");
		sd.setLineBreak("\n");
		sd.setLocale(locEN);
		sd.setMultilingual(multilangual);
		GenericFilterWriter gfw = new GenericFilterWriter(gsw, encMgt);
		sd.setFilterWriter(gfw);
		list.add(new Event(EventType.START_DOCUMENT, sd));
		return list;
	}
	
	private void addEndEvents (ArrayList<Event> list) {
		Ending ending = new Ending("end");
		list.add(new Event(EventType.END_DOCUMENT, ending));
	}

}
