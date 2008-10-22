package com.googlecode.okapi.resource.builder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.ResourceFactory;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.builder.ResourceEvent.ResourceEventType;
import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public abstract class BaseDocumentParser implements IDocumentParser{

	private boolean isFinished = false;
	private Queue<ResourceEvent> eventQueue = new LinkedList<ResourceEvent>();
	private Stack<ResourceEvent> eventStack = new Stack<ResourceEvent>(); 

	protected DocumentManager documentManager;
	
	public BaseDocumentParser(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ResourceFactory getResourceFactory(){
		return documentManager.getFactory();
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
	
	protected void addStartDocumentEvent(){
		ResourceEventType type = ResourceEventType.StartDocument;
		ResourceEvent event = new ResourceEvent(type, documentManager.getDocument()); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndDocumentEvent(){
		if(eventStack.pop().type != ResourceEventType.StartDocument){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndDocument, documentManager.getDocument()); 
		eventQueue.add(event);
	}

	protected void addTextFlowEvent(TextFlow textFlow){
		addStartTextFlowEvent(textFlow);
		addEndTextFlowEvent();
	}
	
	protected void addStartTextFlowEvent(TextFlow textFlow){
		ResourceEventType type = ResourceEventType.StartTextFlow;
		ResourceEvent event = new ResourceEvent(type, textFlow); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndTextFlowEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartTextFlow){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndTextFlow,startEvent.value); 
		eventQueue.add(event);
	}

	protected void addContainerEvent(Container container){
		addStartContainerEvent(container);
		addEndContainerEvent();
	}
	
	protected void addStartContainerEvent(Container container){
		ResourceEventType type = ResourceEventType.StartContainer;
		ResourceEvent event = new ResourceEvent(type, container); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndContainerEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartContainer){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndContainer,startEvent.value); 
		eventQueue.add(event);
	}
	
	protected void addDataPartEvent(DataPart dataPart){
		addStartDataPartEvent(dataPart);
		addEndDataPartEvent();
	}
	
	protected void addStartDataPartEvent(DataPart dataPart){
		ResourceEventType type = ResourceEventType.StartDataPart;
		ResourceEvent event = new ResourceEvent(type, dataPart); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndDataPartEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartDataPart){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndDataPart,startEvent.value); 
		eventQueue.add(event);
	}

	protected void addReferenceEvent(Reference reference){
		addStartReferenceEvent(reference);
		addEndReferenceEvent();

	}
	
	protected void addStartReferenceEvent(Reference reference){
		ResourceEventType type = ResourceEventType.StartReference;
		ResourceEvent event = new ResourceEvent(type, reference); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndReferenceEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartReference){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndReference,startEvent.value); 
		eventQueue.add(event);
	}
	
	protected void addEndEvent(){
		ResourceEvent startEvent = eventStack.pop();
		ResourceEvent event = new ResourceEvent(ResourceEventType.getEndEvent(startEvent.type),startEvent.value); 
		eventQueue.add(event);
	}
	
	protected void addPropertiesEvent(){
		addStartPropertiesEvent();
		addEndPropertiesEvent();
	}
	
	protected void addStartPropertiesEvent(){
		ResourceEventType type = ResourceEventType.StartProperties;
		ResourceEvent event = new ResourceEvent(type); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndPropertiesEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartProperties){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndProperties); 
		eventQueue.add(event);
	}
	
	public void addTextFlowContentEvent(){
		addStartTextFlowContentEvent();
		addEndTextFlowContentEvent();
	}

	public void addStartTextFlowContentEvent(){
		ResourceEventType type = ResourceEventType.StartTextFlowContent;
		ResourceEvent event = new ResourceEvent(type); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	public void addEndTextFlowContentEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartTextFlowContent){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndTextFlowContent); 
		eventQueue.add(event);
	}
	
	public void addContainerFragmentEvent(ContainerFragment fragment){
		addStartContainerFragmentEvent(fragment);
		addEndContainerFragmentEvent();
	}
	
	public void addStartContainerFragmentEvent(ContainerFragment fragment){
		ResourceEventType type = ResourceEventType.StartContainerFragment;
		ResourceEvent event = new ResourceEvent(type, fragment); 
		eventQueue.add(event);
		eventStack.push(event);
	}
	
	protected void addEndContainerFragmentEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartContainerFragment){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndContainerFragment,startEvent.value); 
		eventQueue.add(event);
	}

	protected void addTextFragmentEvent(TextFragment fragment){
		addStartTextFragmentEvent(fragment);
		addEndTextFragmentEvent();
	}
	protected void addStartTextFragmentEvent(TextFragment fragment){
		ResourceEventType type = ResourceEventType.StartTextFragment;
		ResourceEvent event = new ResourceEvent(type, fragment); 
		eventQueue.add(event);
		eventStack.push(event);
	}

	protected void addEndTextFragmentEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartTextFragment){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndTextFragment,startEvent.value); 
		eventQueue.add(event);
	}

	protected void addResourceFragmentEvent(ResourceFragment fragment){
		addStartResourceFragmentEvent(fragment);
		addEndResourceFragmentEvent();
	}
	protected void addStartResourceFragmentEvent(ResourceFragment fragment){
		ResourceEventType type = ResourceEventType.StartResourceFragment;
		ResourceEvent event = new ResourceEvent(type, fragment); 
		eventQueue.add(event);
		eventStack.push(event);
	}

	protected void addEndResourceFragmentEvent(){
		ResourceEvent startEvent = eventStack.pop();
		if(startEvent.type != ResourceEventType.StartResourceFragment){
			throw new RuntimeException("Inconsistent model");
		}
		ResourceEvent event = new ResourceEvent(ResourceEventType.EndResourceFragment,startEvent.value); 
		eventQueue.add(event);
	}
	
	public void close() {
		setEndOfDocument();
	}
	
}
