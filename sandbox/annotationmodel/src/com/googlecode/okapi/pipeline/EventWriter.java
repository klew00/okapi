package com.googlecode.okapi.pipeline;

import java.io.PrintStream;

import com.googlecode.okapi.events.Event;

public class EventWriter extends BaseEventHandler{

	private static final int INDENT = 2;
	private PrintStream out;
	int stack = 0;
	
	public EventWriter(IDocumentParser input, PrintStream writer) {
		super(input);
		this.out = writer;
	}
	
	public EventWriter(PrintStream writer) {
		super();
		this.out = writer;
	}

	@Override
	public void handleEvent(Event event) {
		out.println(event.getEventType().name());
		super.handleEvent(event);
	}
}
