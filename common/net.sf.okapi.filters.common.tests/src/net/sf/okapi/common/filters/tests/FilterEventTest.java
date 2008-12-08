package net.sf.okapi.common.filters.tests;


import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;

import org.junit.Test;
import static org.junit.Assert.*;

public class FilterEventTest {

	@Test
	public void testGenericEventTypes() {	
		FilterEvent event = new FilterEvent(FilterEventType.END_DOCUMENT, null);
		assertEquals(event.getEventType(), FilterEventType.END_DOCUMENT);		
	}
}
