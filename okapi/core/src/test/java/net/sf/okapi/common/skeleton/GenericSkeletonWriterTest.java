package net.sf.okapi.common.skeleton;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

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
		TextUnit tu = createSimpleTU();
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
		TextUnit tu = createTranslatedTU();
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
		TextUnit tu = createTranslatedTU();
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
		TextUnit tu = createTranslatedTU();
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
		TextUnit tu = createTranslatedTU();
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
		TextUnit tu = createSimpleTU();
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
	
	private TextUnit createSimpleTU () {
		TextUnit tu = new TextUnit("id1");
		tu.setSourceContent(new TextFragment("text1"));
		return tu;
	}

	private TextUnit createTranslatedTU () {
		TextUnit tu = new TextUnit("id1");
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
