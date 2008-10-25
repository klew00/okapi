package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.filters.FilterEvent;

public interface IFilterDrivenUtility2 extends IUtility2 {

	/**
	 * Handles the events provided by the filter.
	 * @param event the event to process.
	 */
	public void handleEvent(FilterEvent event);

}
