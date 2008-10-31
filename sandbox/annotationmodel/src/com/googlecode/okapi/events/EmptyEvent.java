package com.googlecode.okapi.events;

public interface EmptyEvent extends Event{

	public static final EmptyEvent EndDocument = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndDocument;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndContainer = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndContainer;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndTextFlow = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndTextFlow;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndReference = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndReference;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndDataPart = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndDataPart;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent StartProperties = new EmptyEvent(){
		public EventType getEventType() { return EventType.StartProperties;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndProperties = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndProperties;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent StartChildren = new EmptyEvent(){
		public EventType getEventType() { return EventType.StartChildren;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndChildren = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndChildren;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent StartTextFlowContent = new EmptyEvent(){
		public EventType getEventType() { return EventType.StartTextFlowContent;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndTextFlowContent = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndTextFlowContent;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndContainerFragment = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndContainerFragment;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndTextFragment = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndTextFragment;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndResourceFragment = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndResourceFragment;}
		public boolean isEmptyEvent() { return true;}
	};

	public static final EmptyEvent EndAnnotation = new EmptyEvent(){
		public EventType getEventType() { return EventType.EndAnnotation;}
		public boolean isEmptyEvent() { return true;}
	};

}
