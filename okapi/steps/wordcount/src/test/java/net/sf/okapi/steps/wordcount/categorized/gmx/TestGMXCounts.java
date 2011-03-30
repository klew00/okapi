package net.sf.okapi.steps.wordcount.categorized.gmx;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.steps.TextUnitLogger;
import net.sf.okapi.steps.leveraging.LeveragingStep;
import net.sf.okapi.steps.wordcount.common.BaseCountStep;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import net.sf.okapi.steps.wordcount.common.GMX;

import org.junit.Before;
import org.junit.Test;

public class TestGMXCounts {
	
	private BaseCountStep bcs;
	private StartDocument sd;
	private Event sdEvent;
	private TextUnit tu;
	private Event tuEvent;
	
	@Before
	public void startup() {
		sd = new StartDocument("sd");
		sd.setLocale(LocaleId.ENGLISH);
		sdEvent = new Event(EventType.START_DOCUMENT, sd);
		
		tu = new TextUnit("tu");
		tu.setSource(new TextContainer("12:00 is 15 minutes after 11:45. You can check at freetime@example.com 8-) for $300"));
		tuEvent = new Event(EventType.TEXT_UNIT, tu);
	}

	@Test
	public void testGMXAlphanumericOnlyTextUnitWordCountStep () {
		bcs = new GMXAlphanumericOnlyTextUnitWordCountStep();
		bcs.handleEvent(sdEvent);
		bcs.handleEvent(tuEvent);
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
		net.sf.okapi.connectors.pensieve.Parameters rparams = 
			new net.sf.okapi.connectors.pensieve.Parameters();
		rparams.setDbDirectory(pathBase + "testtm");
		tu.setSource(new TextContainer("Elephants cannot fly."));
		
		LeveragingStep ls = new LeveragingStep();
		ls.setSourceLocale(LocaleId.ENGLISH);
		ls.setTargetLocale(LocaleId.FRENCH);
		net.sf.okapi.steps.leveraging.Parameters params = (net.sf.okapi.steps.leveraging.Parameters) ls.getParameters();
		params.setResourceParameters(rparams.toString());
		params.setResourceClassName(net.sf.okapi.connectors.pensieve.PensieveTMConnector.class.getName());
		params.setThreshold(60);
		params.setFillTarget(true);
		ls.handleEvent(sdEvent);
		ls.handleEvent(tuEvent);
		System.out.println(TextUnitLogger.getTuInfo(tu, LocaleId.ENGLISH));
		
		bcs = new GMXLeveragedMatchedWordCountStep();
		bcs.setSourceLocale(LocaleId.ENGLISH);
		bcs.setTargetLocale(LocaleId.FRENCH);
		bcs.handleEvent(sdEvent);
		bcs.handleEvent(tuEvent);
		System.out.println(TextUnitLogger.getTuInfo(tu, LocaleId.ENGLISH));
		
		assertEquals(3, BaseCounter.getCount(tu, GMX.LeveragedMatchedWordCount)); // 
	}
	
	@Test
	public void testGMXMeasurementOnlyTextUnitWordCountStep () {
		bcs = new GMXMeasurementOnlyTextUnitWordCountStep();
		bcs.handleEvent(sdEvent);
		bcs.handleEvent(tuEvent);
		assertEquals(3, BaseCounter.getCount(tu, GMX.MeasurementOnlyTextUnitWordCount)); // 11:45, 12:00, $300
	}
	
	@Test
	public void testGMXNumericOnlyTextUnitWordCountStep () {
		bcs = new GMXNumericOnlyTextUnitWordCountStep();
		bcs.handleEvent(sdEvent);
		bcs.handleEvent(tuEvent);
		assertEquals(7, BaseCounter.getCount(tu, GMX.NumericOnlyTextUnitWordCount)); // 12, 00, 15, 11, 45, 8, 300
	}
	
	@Test
	public void testGMXProtectedWordCountStep () {
		bcs = new GMXProtectedWordCountStep();
		bcs.handleEvent(sdEvent);		
		
		tu.setIsTranslatable(false);
		bcs.handleEvent(tuEvent);
		assertEquals(15, BaseCounter.getCount(tu, GMX.ProtectedWordCount));		
		
		tu.setIsTranslatable(true);
		bcs.handleEvent(tuEvent);
		assertEquals(0, BaseCounter.getCount(tu, GMX.ProtectedWordCount)); // 0 - not counted in a translatable TU
	}
	
	@Test
	public void testGMXRepetitionMatchedWordCountStep () {
		// Not yet implemented
	}	
}

