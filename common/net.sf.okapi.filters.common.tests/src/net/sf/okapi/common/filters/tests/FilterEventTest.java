package net.sf.okapi.common.filters.tests;


import net.sf.okapi.common.filters.FilterEvent;
import org.junit.Test;
import static org.junit.Assert.*;

public class FilterEventTest {

	@Test
	public void testGenericEventTypes() {	
		FilterEvent event = new FilterEvent(FilterEvent.FilterEventType.END_DOCUMENT, null);
		assertEquals(event.getFilterEventType(), FilterEvent.FilterEventType.END_DOCUMENT);
		assertNull(event.getFilterResource());
	}
}
