package net.sf.okapi.common.filters;

public enum FilterEventType {
	START,
	START_DOCUMENT,
	END_DOCUMENT,
	START_SUBDOCUMENT,
	END_SUBDOCUMENT,
	START_GROUP,
	END_GROUP,
	TEXT_UNIT,
	DOCUMENT_PART,
	FINISHED
}
