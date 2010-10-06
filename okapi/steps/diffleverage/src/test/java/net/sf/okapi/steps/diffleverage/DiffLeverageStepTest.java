package net.sf.okapi.steps.diffleverage;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import junit.framework.Assert;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiffLeverageStepTest {
	private Pipeline pipeline;
	private DiffLeverageStep diffLeverage;
	private EventObserver eventObserver;

	@Before
	public void setUp() throws Exception {
		// create pipeline
		pipeline = new Pipeline();
		eventObserver = new EventObserver();
		pipeline.addObserver(eventObserver);

		// add filter step
		IFilter filter = new POFilter();
		pipeline.addStep(new RawDocumentToFilterEventsStep(filter));

		// add DiffLeverage step
		diffLeverage = new DiffLeverageStep();

		diffLeverage.setSourceLocale(LocaleId.ENGLISH);
		Parameters p = (Parameters)diffLeverage.getParameters();
		p.setCopyToTarget(true);
		
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.po.POFilter");
		diffLeverage.setFilterConfigurationMapper(fcMapper);
		pipeline.addStep(diffLeverage);

	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}

	@Test
	public void diffLeverageSimplePOFiles() throws URISyntaxException {
		URL url = DiffLeverageStepTest.class.getResource("/Test_en_fr_old.po");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH);
		t.setFilterConfigId("okf_po");
		diffLeverage.setSecondInput(t);
		diffLeverage.setTargetLocale(LocaleId.FRENCH);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/Test_en_fr_new.po"),
				"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.DOCUMENT_PART, el.remove(0).getEventType());

		Event tue1 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue1.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue1.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name100 (old)", tue1.getTextUnit()
				.getTarget(LocaleId.FRENCH).toString());

		Event tue2 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue2.getEventType());
		// TU target was *not* copied from the old TU
		Assert.assertNull(tue2.getTextUnit().getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name200", tue2.getTextUnit().getTarget(
				LocaleId.FRENCH).toString());

		Event tue3 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue3.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name300 (old)", tue3.getTextUnit()
				.getTarget(LocaleId.FRENCH).toString());

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}

	@Test
	public void diffLeverageSimplePOFilesWithAltTranslationAnnotation() throws URISyntaxException {
		URL url = DiffLeverageStepTest.class.getResource("/Test_en_fr_old.po");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH);
		t.setFilterConfigId("okf_po");
		Parameters p = (Parameters)diffLeverage.getParameters();
		p.setCopyToTarget(false);
		diffLeverage.setSecondInput(t);
		diffLeverage.setTargetLocale(LocaleId.FRENCH);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/Test_en_fr_new.po"),
				"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.DOCUMENT_PART, el.remove(0).getEventType());

		Event tue1 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue1.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue1.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name100 (old)", getAltTransTarget(tue1.getTextUnit(), LocaleId.FRENCH));

		Event tue2 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue2.getEventType());
		// TU target was *not* copied from the old TU
		Assert.assertNull(tue2.getTextUnit().getAnnotation(DiffMatchAnnotation.class));
		Assert.assertNull(tue2.getTextUnit().getAnnotation(AltTranslationsAnnotation.class));

		Event tue3 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue3.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name300 (old)", getAltTransTarget(tue3.getTextUnit(), LocaleId.FRENCH));

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}

	@Test
	public void diffLeverageMediumPOFiles() throws URISyntaxException {
		URL url = DiffLeverageStepTest.class.getResource("/Test_en_en_old.po");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.ENGLISH);
		t.setFilterConfigId("okf_po");
		diffLeverage.setSecondInput(t);
		diffLeverage.setTargetLocale(LocaleId.ENGLISH);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/Test_en_en_new.po"),
				"UTF-8", LocaleId.ENGLISH, LocaleId.ENGLISH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());

		Event tue1 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue1.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue1.getTextUnit().getTarget(LocaleId.ENGLISH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("text 2 to translate", tue1.getTextUnit()
				.getTarget(LocaleId.ENGLISH).toString());

		Event tue2 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue2.getEventType());
		// TU target was *not* copied from the old TU
		Assert.assertNotNull(tue2.getTextUnit().getTarget(LocaleId.ENGLISH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("text 4 to translate", tue2.getTextUnit().getTarget(
				LocaleId.ENGLISH).toString());

		Event tue3 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue3.getTextUnit().getTarget(LocaleId.ENGLISH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("text 6 to translate", tue3.getTextUnit()
				.getTarget(LocaleId.ENGLISH).toString());
		
		Event tue4 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue4.getTextUnit().getTarget(LocaleId.ENGLISH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("text 8 to translate", tue4.getTextUnit()
				.getTarget(LocaleId.ENGLISH).toString());

		Event tue5 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue4.getTextUnit().getTarget(LocaleId.ENGLISH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("text 10 to translate", tue5.getTextUnit()
				.getTarget(LocaleId.ENGLISH).toString());

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}
	
	@Test
	public void diffLeverageFuzzySimplePOFiles() throws URISyntaxException {
		URL url = DiffLeverageStepTest.class.getResource("/Test_en_fr_old.po");
		RawDocument t = new RawDocument(url.toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH);
		t.setFilterConfigId("okf_po");
		diffLeverage.setSecondInput(t);
		diffLeverage.setTargetLocale(LocaleId.FRENCH);
		((Parameters)diffLeverage.getParameters()).setFuzzyThreshold(70);

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("/Test_en_fr_new.po"),
				"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));

		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.START_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.DOCUMENT_PART, el.remove(0).getEventType());

		Event tue1 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue1.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue1.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name100 (old)", tue1.getTextUnit()
				.getTarget(LocaleId.FRENCH).toString());

		Event tue2 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue2.getEventType());
		// TU target was copied from the old TU
		Assert.assertNotNull(tue2.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name200 (old)", tue2.getTextUnit().getTarget(
				LocaleId.FRENCH).toString());

		Event tue3 = el.remove(0);
		assertEquals(EventType.TEXT_UNIT, tue3.getEventType());
		// TU target copied from old TU
		Assert.assertNotNull(tue3.getTextUnit().getTarget(LocaleId.FRENCH).getAnnotation(DiffMatchAnnotation.class));
		Assert.assertEquals("Message pour l'identificateur name300 (old)", tue3.getTextUnit()
				.getTarget(LocaleId.FRENCH).toString());

		assertEquals(EventType.END_DOCUMENT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}
	
	private String getAltTransTarget(TextUnit tu, LocaleId targetLocale) {
		AltTranslationsAnnotation ata = tu.getTarget(targetLocale).getAnnotation(AltTranslationsAnnotation.class); 
		return ata.getFirst().getTarget().toString();
	}
}
