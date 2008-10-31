package com.googlecode.okapi.pipeline;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.googlecode.okapi.events.Event;
import com.googlecode.okapi.events.EventFactory;
import com.googlecode.okapi.events.EventType;

public abstract class BaseDocumentParser implements IDocumentParser{

	private boolean isFinished = false;
	private Queue<Event> eventQueue = new LinkedList<Event>();
	private Stack<EventType> eventStack = new Stack<EventType>(); 
	private EventFactory eventFactory;
	
	public BaseDocumentParser(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}

	public EventFactory getEventFactory(){
		return eventFactory;
	}
	
	public boolean hasNext() {
		if(eventQueue.isEmpty() && isFinished ){
			return false;
		}
		return true;
	}

	public Event next() {
		if(eventQueue.isEmpty()){
			if(isFinished){
				throw new RuntimeException("Called next() after completion");
			}
			cacheNextEvent();
		}
		return eventQueue.poll();
	}
	
	protected abstract void cacheNextEvent();
	
	protected final void setEndOfDocument(){
		assert eventStack.isEmpty();
		isFinished = true;
	}
	
	protected void addEvent(Event event){
		// TODO validation
		eventQueue.add(event);
		if(event.getEventType().isStartEvent())
			eventStack.push(event.getEventType());
	}
	
	protected void addEndEvent(){
		// TODO validation
		EventType startType = eventStack.pop();
		if(!startType.isStartEvent()){
			throw new RuntimeException("No StartEvent in context");
		}
		Event endEvent = eventFactory.create(startType.getEndEvent());
		eventQueue.add(endEvent);
	}
	
	
	public void close() {
		setEndOfDocument();
	}

	// TODO: We could add convenience methods such as addEndDocumentEvent()...
	
}
