package net.sf.okapi.common.filters;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;

import org.junit.Test;
import static org.junit.Assert.*;

public class FilterEventTest {

	@Test
	public void testGenericEventTypes() {	
		Event event = new Event(EventType.END_DOCUMENT, null);
		assertEquals(event.getEventType(), EventType.END_DOCUMENT);		
	}
}
