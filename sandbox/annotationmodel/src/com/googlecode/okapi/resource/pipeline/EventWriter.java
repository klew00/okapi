package com.googlecode.okapi.resource.pipeline;

import java.io.PrintStream;

import com.googlecode.okapi.pipeline.IPullParser;
import com.googlecode.okapi.resource.Event;

public class EventWriter extends BaseEventHandler{

	private static final int INDENT = 2;
	private PrintStream out;
	int stack = 0;
	
	public EventWriter(IPullParser<Event> input, PrintStream writer) {
		super(input);
		this.out = writer;
	}
	
	public EventWriter(PrintStream writer) {
		super();
		this.out = writer;
	}

	@Override
	public void handleEvent(Event event) {
		if(event.getEventType().isStartEvent()){
			doIndent();
			stack++;
		}
		else if(event.getEventType().isEndEvent()){
			stack--;
			doIndent();
		}
		out.println(event.getEventType().name());
		super.handleEvent(event);
	}
	
	private void doIndent(){
		for(int i=0;i<stack*INDENT;i++){
			out.print(' ');
		}
	}
}
