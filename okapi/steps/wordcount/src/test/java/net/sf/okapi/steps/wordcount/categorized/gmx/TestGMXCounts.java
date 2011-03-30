package net.sf.okapi.steps.wordcount.categorized.gmx;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.wordcount.common.BaseCountStep;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import net.sf.okapi.steps.wordcount.common.GMX;

import org.junit.Before;
import org.junit.Test;

public class TestGMXCounts {
	
	private BaseCountStep bcs;
	private StartDocument sd;
	private TextUnit tu;
	
	@Before
	public void startup() {
		sd = new StartDocument("sd");
		sd.setLocale(LocaleId.ENGLISH);
		
		tu = new TextUnit("tu");
		tu.setSource(new TextContainer("12:00 is 15 minutes after 11:45. You can check at freetime@example.com 8-) for $300"));		
	}

	@Test
	public void testGMXAlphanumericOnlyTextUnitWordCountStep () {
		bcs = new GMXAlphanumericOnlyTextUnitWordCountStep();
		bcs.handleEvent(new Event(EventType.START_DOCUMENT, sd));
		bcs.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		assertEquals(2, BaseCounter.getCount(tu, GMX.AlphanumericOnlyTextUnitWordCount)); // freetime@example.com, 8-)
	}
	
	@Test
	public void testGMXExactMatchedWordCountStep () {
		
	}
	
	@Test
	public void testGMXFuzzyMatchWordCountStep () {
		
	}
	
	@Test
	public void testGMXLeveragedMatchedWordCountStep () {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("").getPath()) + "/";
		net.sf.okapi.connectors.pensieve.Parameters params = 
			new net.sf.okapi.connectors.pensieve.Parameters();
		params.setDbDirectory(pathBase + "testtm");
	}
	
	@Test
	public void testGMXMeasurementOnlyTextUnitWordCountStep () {
		bcs = new GMXMeasurementOnlyTextUnitWordCountStep();
		bcs.handleEvent(new Event(EventType.START_DOCUMENT, sd));
		bcs.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		assertEquals(3, BaseCounter.getCount(tu, GMX.MeasurementOnlyTextUnitWordCount)); // 11:45, 12:00, $300
	}
	
	@Test
	public void testGMXNumericOnlyTextUnitWordCountStep () {
		bcs = new GMXNumericOnlyTextUnitWordCountStep();
		bcs.handleEvent(new Event(EventType.START_DOCUMENT, sd));
		bcs.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		assertEquals(7, BaseCounter.getCount(tu, GMX.NumericOnlyTextUnitWordCount)); // 12, 00, 15, 11, 45, 8, 300
	}
	
	@Test
	public void testGMXProtectedWordCountStep () {
		bcs = new GMXProtectedWordCountStep();
		bcs.handleEvent(new Event(EventType.START_DOCUMENT, sd));
		
		tu.setIsTranslatable(false);
		bcs.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		assertEquals(15, BaseCounter.getCount(tu, GMX.ProtectedWordCount));		
		
		tu.setIsTranslatable(true);
		bcs.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		assertEquals(0, BaseCounter.getCount(tu, GMX.ProtectedWordCount)); // 0 - not counted in a translatable TU
	}
	
	@Test
	public void testGMXRepetitionMatchedWordCountStep () {
		// Not yet implemented
	}	
}

