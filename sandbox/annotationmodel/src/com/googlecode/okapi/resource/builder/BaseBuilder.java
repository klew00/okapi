package com.googlecode.okapi.resource.builder;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.builder.ResourceEvent.ResourceEventType;

public abstract class BaseBuilder implements IResourceBuilder{

	private boolean isFinished = false;
	private Queue<ResourceEvent> eventQueue = new LinkedList<ResourceEvent>();
	private Deque<ResourceEventType> eventStack = new LinkedList<ResourceEventType>(); 

	private DocumentManager documentManager;
	
	public BaseBuilder(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}
	
	public boolean hasNext() {
		if(eventQueue.isEmpty() && isFinished ){
			return false;
		}
		return true;
	}

	public ResourceEvent next() {
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
	
	protected void addStartDocumentEvent(Document doc){
		eventQueue.add(
			new ResourceEvent(ResourceEventType.StartDocument, doc) 
		);
		eventStack.push(ResourceEventType.StartDocument);
	}
	
	protected void addEndDocumentEvent(){
		if(eventStack.pop() != ResourceEventType.StartDocument){
			throw new RuntimeException("Inconsistent model");
		}
		eventQueue.add(
			new ResourceEvent(ResourceEventType.EndDocument)
		);
	}
	
	protected void addEndEvent(){
		ResourceEventType event = eventStack.pop();
		eventQueue.add(
			new ResourceEvent(ResourceEventType.getEndEvent(event))
		);
		
	}
	
	public void close() {
		setEndOfDocument();
	}
	
}
