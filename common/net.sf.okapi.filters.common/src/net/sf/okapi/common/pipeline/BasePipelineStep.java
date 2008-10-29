/**
 * 
 */
package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.filters.FilterEvent;

public abstract class BasePipelineStep implements IPipelineStep {

	public FilterEvent handleEvent(FilterEvent event) {
		switch (event.getEventType()) {
		case START_DOCUMENT:
			handleStartDocument(event);
			break;

		case END_DOCUMENT:
			handleEndDocument(event);
			break;

		case START_SUBDOCUMENT:
			handleStartSubDocument(event);
			break;

		case END_SUBDOCUMENT:
			handleEndSubDocument(event);
			break;

		case START_GROUP:
			handleStartGroup(event);
			break;

		case END_GROUP:
			handleEndGroup(event);
			break;

		case TEXT_UNIT:
			handleTextUnit(event);
			break;

		case TEXT_GROUP:
			handleTextGroup(event);
			break;

		case SKELETON_UNIT:
			handleSkeletonUnit(event);
			break;

		default:
			break;				
		}
		
		return event;
	}

	// By default we eat all events - override these methods if need to process
	// the event
	protected void handleStartDocument(FilterEvent event) {
	}

	protected void handleEndDocument(FilterEvent event) {
	}

	protected void handleStartSubDocument(FilterEvent event) {
	}

	protected void handleEndSubDocument(FilterEvent event) {
	}

	protected void handleStartGroup(FilterEvent event) {
	}

	protected void handleEndGroup(FilterEvent event) {
	}

	protected void handleTextUnit(FilterEvent event) {
	}

	protected void handleTextGroup(FilterEvent event) {
	}

	protected void handleSkeletonUnit(FilterEvent event) {
	}
}
