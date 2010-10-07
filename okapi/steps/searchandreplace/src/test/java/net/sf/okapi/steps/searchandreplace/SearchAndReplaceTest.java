package net.sf.okapi.steps.searchandreplace;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchAndReplaceTest {
	private SearchAndReplaceStep searchAndReplace;
	private net.sf.okapi.steps.searchandreplace.EventObserver eventObserver;
	private Pipeline pipeline;

	@Before
	public void setUp() throws Exception {
		searchAndReplace = new SearchAndReplaceStep();

		// create pipeline
		pipeline = new Pipeline();
		eventObserver = new EventObserver();
		pipeline.addObserver(eventObserver);
		pipeline.addStep(searchAndReplace);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void constructor() {
		assertNotNull("Step constructor creates a default Parameters",
				searchAndReplace.getParameters());
	}

	@Test
	public void getDescription() {
		assertNotNull("Step message is not null", searchAndReplace.getDescription());
		assertTrue("Step message is a string", searchAndReplace.getDescription() instanceof String);
		assertTrue("Step message is not zero length",
				searchAndReplace.getDescription().length() >= 1);
	}

	@Test
	public void replaceSourceCharacter() {		
		Parameters p = (Parameters)searchAndReplace.getParameters();
		p.reset();
		p.regEx = true;
		String pattern[] = new String[3];
		pattern[0] = Boolean.toString(true);
		pattern[1] = "\\{nb\\}|\\{tab\\}|\\{em\\}|\\{en\\}|\\{emsp\\}|\\{ensp\\}";
		pattern[2] = "";
		p.addRule(pattern);
		
		p.target = false;
		p.source = true;
		
		pipeline.startBatch();
		TextUnit tu = new TextUnit("1", "{nb}{tab}{em}{en}{emsp}{ensp}");
		tu.createTarget(LocaleId.SPANISH, true, IResource.COPY_ALL);
		Event e = new Event(EventType.TEXT_UNIT, tu);
		pipeline.process(e);
		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		tu = el.get(0).getTextUnit();
		assertTrue(tu.getSource().getFirstContent().isEmpty());
		assertEquals("{nb}{tab}{em}{en}{emsp}{ensp}", tu.getTarget(LocaleId.SPANISH).getFirstContent().toString());
		assertEquals(EventType.TEXT_UNIT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
		searchAndReplace.setTargetLocale(LocaleId.SPANISH);
	}

	@Test
	public void replaceSourceCharacterMultiplePatterns() {		
		Parameters p = (Parameters)searchAndReplace.getParameters();
		p.reset();
		p.regEx = true;
		String pattern[] = new String[3];
		pattern[0] = Boolean.toString(true);
		pattern[1] = "\\{nb\\}|\\{tab\\}|\\{em\\}";
		pattern[2] = "";
		p.addRule(pattern);
		
		String pattern2[] = new String[3];
		pattern2[0] = Boolean.toString(true);
		pattern2[1] = "\\{en\\}|\\{emsp\\}|\\{ensp\\}";
		pattern2[2] = "";
		p.addRule(pattern2);
		
		p.target = false;
		p.source = true;
		
		pipeline.startBatch();
		TextUnit tu = new TextUnit("1", "{nb}{tab}{em}{en}{emsp}{ensp}");
		tu.createTarget(LocaleId.SPANISH, true, IResource.COPY_ALL);
		Event e = new Event(EventType.TEXT_UNIT, tu);
		pipeline.process(e);
		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		tu = el.get(0).getTextUnit();
		assertTrue(tu.getSource().getFirstContent().isEmpty());
		assertEquals("{nb}{tab}{em}{en}{emsp}{ensp}", tu.getTarget(LocaleId.SPANISH).getFirstContent().toString());
		assertEquals(EventType.TEXT_UNIT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
		searchAndReplace.setTargetLocale(LocaleId.SPANISH);
	}

	@Test
	public void replaceSourceCharacterWithUnicodeChar() {		
		Parameters p = (Parameters)searchAndReplace.getParameters();
		p.reset();
		p.regEx = true;
		String pattern[] = new String[3];
		pattern[0] = Boolean.toString(true);
		pattern[1] = "\\{nb\\}|\\{tab\\}|\\{em\\}|\\{en\\}|\\{emsp\\}|\\{ensp\\}";
		pattern[2] = "\u0045";
		p.addRule(pattern);
		
		p.target = false;
		p.source = true;
		
		pipeline.startBatch();
		TextUnit tu = new TextUnit("1", "{nb}{tab}{em}{en}{emsp}{ensp}");		
		Event e = new Event(EventType.TEXT_UNIT, tu);
		pipeline.process(e);
		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		tu = el.get(0).getTextUnit();
		assertEquals("\u0045\u0045\u0045\u0045\u0045\u0045", tu.getSource().getFirstContent().toString());	
		assertEquals(EventType.TEXT_UNIT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
		searchAndReplace.setTargetLocale(LocaleId.SPANISH);
	}

	@Test
	public void replaceTargetCharacter() {
		searchAndReplace.setTargetLocale(LocaleId.SPANISH);
		Parameters p = (Parameters)searchAndReplace.getParameters();
		p.reset();
		p.regEx = true;
		String pattern[] = new String[3];
		pattern[0] = Boolean.toString(true);
		pattern[1] = "\\{nb\\}|\\{tab\\}|\\{em\\}|\\{en\\}|\\{emsp\\}|\\{ensp\\}";
		pattern[2] = "";
		p.addRule(pattern);
				
		pipeline.startBatch();
		TextUnit tu = new TextUnit("1", "{nb}{tab}{em}{en}{emsp}{ensp}");
		tu.createTarget(LocaleId.SPANISH, true, IResource.COPY_ALL);
		Event e = new Event(EventType.TEXT_UNIT, tu);
		pipeline.process(e);
		pipeline.endBatch();

		// test we observed the correct events
		List<Event> el = eventObserver.getResult();
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		tu = el.get(0).getTextUnit();
		assertEquals("{nb}{tab}{em}{en}{emsp}{ensp}", tu.getSource().getFirstContent().toString());
		assertTrue(tu.getTarget(LocaleId.SPANISH).getFirstContent().isEmpty());
		assertEquals(EventType.TEXT_UNIT, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}
}
