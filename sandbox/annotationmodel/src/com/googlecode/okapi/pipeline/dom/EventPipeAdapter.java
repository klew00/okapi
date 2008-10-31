package com.googlecode.okapi.pipeline.dom;

import java.util.Stack;

import com.googlecode.okapi.events.Event;
import com.googlecode.okapi.pipeline.IInputProcessor;
import com.googlecode.okapi.pipeline.IPullParser;
import com.googlecode.okapi.resource.ContentFragment;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.DocumentPartContainer;
import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.TextFlowProvider;

public class EventPipeAdapter implements IPullParser<DocumentEvent>{
	
	private static enum BuildState{Start,Properties,Annotations,Children}
	
	private Stack<Resource<?>> resources;
	private Stack<BuildState> buildStates;
	private IPullParser<Event> input;
	private Resource<?> current;
	
	public static enum EventOrder{
		LastDependentFirst,
		InOrder,
	}
	
	public Resource<?> getResource(){
		return current;
	}
	
	public EventPipeAdapter() {
		this.resources = new Stack<Resource<?>>();
		this.buildStates = new Stack<BuildState>();
	}
	
	public void close() {
		input.close();
		resources.clear();
		buildStates.clear();
		current = null;
	}

	public void setInput(IPullParser<Event> input) {
		this.input = input;
	}
	
	public boolean hasNext() {
		return input.hasNext();
	}

	public DocumentEvent next() {
		while(true){
			Event event = input.next();
			
			switch(event.getEventType()){
			case StartDocument:
				resources.push((Resource<?>) event);
				buildStates.push(BuildState.Start);
				break;
			case StartContainer:
			case StartReference:
			case StartDataPart:
			case StartTextFlow:
				switch(buildStates.peek()){
				case Start: // within a ContentFragment
					ContentFragment fragment = (ContentFragment) resources.peek();
					fragment.setPart( ((DocumentPart)event).getId() );
					break;
				case Children:
					DocumentPartContainer container = (DocumentPartContainer) resources.peek();
					container.getParts().add( ((DocumentPart)event).getId() );
					break;
				case Properties:
					DocumentPart part = (DocumentPart) resources.peek();
					part.getProperties().add( ((DocumentPart)event).getId() );
					break;
				}
				resources.push((Resource<?>) event);
				buildStates.push(BuildState.Start);
				break;
			case EndDocument:
				buildStates.pop();
				current = resources.pop();
				return DocumentEvent.Document;
			case EndContainer:
				buildStates.pop();
				current = resources.pop();
				return DocumentEvent.Container;
			case EndDataPart:
				buildStates.pop();
				current = resources.pop();
				return DocumentEvent.DataPart;
			case EndReference:
				buildStates.pop();
				current = resources.pop();
				return DocumentEvent.Reference;
			case EndTextFlow:
				buildStates.pop();
				current = resources.pop();
				return DocumentEvent.TextFlow;
			case AnnotationData:
				break;
			case StartAnnotation:
				buildStates.pop();
				buildStates.push(BuildState.Annotations);
				break;
			case EndAnnotation:
			case EndChildren:
			case EndProperties:
			case EndTextFlowContent:
				break;
			case EndContainerFragment:
			case EndResourceFragment:
			case EndTextFragment:
				buildStates.pop();
				resources.pop();
				break;
			case StartChildren:
				buildStates.pop();
				buildStates.push(BuildState.Children);
				break;
			case StartProperties:
				buildStates.pop();
				buildStates.push(BuildState.Properties);
				break;
			case StartResourceFragment:
			case StartTextFragment:
			case StartContainerFragment:
				TextFlowProvider flow = (TextFlowProvider) resources.peek();
				flow.getFlow().add((ContentFragment)event);
				buildStates.push(BuildState.Start);
				resources.push((ContentFragment)event);
				break;
			case StartTextFlowContent:
				buildStates.pop();
				buildStates.push(BuildState.Children);
				break;
			}
		}
	}
}
