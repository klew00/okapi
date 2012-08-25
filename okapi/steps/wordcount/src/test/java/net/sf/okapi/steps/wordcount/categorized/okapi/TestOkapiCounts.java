package net.sf.okapi.steps.wordcount.categorized.okapi;

import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.extra.steps.TextUnitLogger;
import net.sf.okapi.steps.wordcount.common.BaseCountStep;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import org.junit.Before;
import org.junit.Test;

public class TestOkapiCounts {

	private BaseCountStep bcs;
	private StartDocument sd;
	private Event sdEvent;
	private ITextUnit tu;
	private Event tuEvent;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Before
	public void startup() {
		sd = new StartDocument("sd");
		sd.setLocale(LocaleId.ENGLISH);
		sdEvent = new Event(EventType.START_DOCUMENT, sd);

		tu = new TextUnit("tu");
		tu.setSource(new TextContainer("Elephants cannot fly."));
		tuEvent = new Event(EventType.TEXT_UNIT, tu);
		tu.setTarget(LocaleId.FRENCH, new TextContainer(
				"Les éléphants ne peuvent pas voler."));
		TextContainer target = tu.getTarget(LocaleId.FRENCH);
		target.setAnnotation(new AltTranslationsAnnotation());
	}

	@Test
	public void testConcordanceWordCountStep() {
		// Not yet implemented
	}

	@Test
	public void testExactDocumentContextMatchWordCountStep() {
		testCount(ExactDocumentContextMatchWordCountStep.class,
				MatchType.EXACT_DOCUMENT_CONTEXT);
	}

	private void testCount(Class<? extends BaseCountStep> cls,
			MatchType matchType) {
		AltTranslationsAnnotation ata = tu.getTarget(LocaleId.FRENCH)
				.getAnnotation(AltTranslationsAnnotation.class);
		ata.add(new AltTranslation(LocaleId.ENGLISH, LocaleId.FRENCH, tu
				.getSource().getFirstContent(), null, null, matchType, 100,
				null));
		try {
			bcs = ClassUtil.instantiateClass(cls);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		bcs.setSourceLocale(LocaleId.ENGLISH);
		bcs.setTargetLocale(LocaleId.FRENCH);
		bcs.handleEvent(sdEvent);
		bcs.handleEvent(tuEvent);
		logger.debug(TextUnitLogger.getTuInfo(tu, LocaleId.ENGLISH));

		assertEquals(3, BaseCounter.getCount(tu, matchType.name())); //
	}

	@Test
	public void testExactLocalContextMatchWordCountStep() {
		testCount(ExactLocalContextMatchWordCountStep.class,
				MatchType.EXACT_LOCAL_CONTEXT);
	}

	@Test
	public void testExactMatchWordCountStep() {
		testCount(ExactMatchWordCountStep.class, MatchType.EXACT);
	}

	@Test
	public void testExactPreviousVersionMatchWordCountStep() {
		testCount(ExactPreviousVersionMatchWordCountStep.class,
				MatchType.EXACT_PREVIOUS_VERSION);
	}

	@Test
	public void testExactRepairedWordCountStep() {
		testCount(ExactRepairedWordCountStep.class, MatchType.EXACT_REPAIRED);
	}

	@Test
	public void testExactStructuralMatchWordCountStep() {
		testCount(ExactStructuralMatchWordCountStep.class,
				MatchType.EXACT_STRUCTURAL);
	}

	@Test
	public void testExactTextOnlyPreviousVersionMatchWordCountStep() {
		testCount(ExactTextOnlyPreviousVersionMatchWordCountStep.class,
				MatchType.EXACT_TEXT_ONLY_PREVIOUS_VERSION);
	}

	@Test
	public void testExactTextOnlyUniqueIdMatchWordCountStep() {
		testCount(ExactTextOnlyUniqueIdMatchWordCountStep.class,
				MatchType.EXACT_TEXT_ONLY_UNIQUE_ID);
	}

	@Test
	public void testExactTextOnlyWordCountStep() {
		testCount(ExactTextOnlyWordCountStep.class, MatchType.EXACT_TEXT_ONLY);
	}

	@Test
	public void testExactUniqueIdMatchWordCountStep() {
		testCount(ExactUniqueIdMatchWordCountStep.class,
				MatchType.EXACT_UNIQUE_ID);
	}

	@Test
	public void testFuzzyMatchWordCountStep() {
		testCount(FuzzyMatchWordCountStep.class, MatchType.FUZZY);
	}

	@Test
	public void testFuzzyPreviousVersionMatchWordCountStep() {
		testCount(FuzzyPreviousVersionMatchWordCountStep.class,
				MatchType.FUZZY_PREVIOUS_VERSION);
	}

	@Test
	public void testFuzzyRepairedWordCountStep() {
		testCount(FuzzyRepairedWordCountStep.class, MatchType.FUZZY_REPAIRED);
	}

	@Test
	public void testFuzzyUniqueIdMatchWordCountStep() {
		testCount(FuzzyUniqueIdMatchWordCountStep.class,
				MatchType.FUZZY_UNIQUE_ID);
	}

	@Test
	public void testMTWordCountStep() {
		testCount(MTWordCountStep.class, MatchType.MT);
	}

	@Test
	public void testPhraseAssembledWordCountStep() {
		// Not yet implemented
	}
}
