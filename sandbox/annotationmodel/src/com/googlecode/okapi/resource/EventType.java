package com.googlecode.okapi.resource;

public enum EventType {
		
		// Document
		StartDocument, EndDocument,
		
		// DocumentParts
		StartContainer, EndContainer,
		StartTextFlow, EndTextFlow,
		StartDataPart, EndDataPart,
		StartReference, EndReference,
		
		// DocumentPart body events
		StartProperties, EndProperties,
		
		// Document and Container body events
		StartChildren, EndChildren,
		
		// TextFlow body events
		StartTextFlowContent, EndTextFlowContent,

		// TextFlow content events
		StartContainerFragment, EndContainerFragment,
		StartTextFragment, EndTextFragment,
		StartResourceFragment, EndResourceFragment,
		
		// Resource Annotations
		StartAnnotation,
		AnnotationData,
		EndAnnotation;
		
		
		public boolean isResourceEvent(){
			switch(this){
			case StartDocument:
			case EndDocument:
			case StartContainer:
			case EndContainer:
			case StartTextFlow:
			case EndTextFlow:
			case StartDataPart:
			case EndDataPart:
			case StartReference:
			case EndReference:
				return true;
			default:
				return false;
			}
		}
		
		public boolean isStartEvent(){
			switch(this){
			case StartDocument:
			case StartContainer:
			case StartTextFlow:
			case StartDataPart:
			case StartReference:
			case StartProperties:
			case StartChildren:
			case StartTextFlowContent:
			case StartContainerFragment:
			case StartTextFragment:
			case StartResourceFragment:
			case StartAnnotation:
				return true;
			default:
				return false;
			}
		}
		
		public boolean isEndEvent(){
			switch(this){
			case EndDocument:
			case EndContainer:
			case EndTextFlow:
			case EndDataPart:
			case EndReference:
			case EndProperties:
			case EndChildren:
			case EndTextFlowContent:
			case EndContainerFragment:
			case EndTextFragment:
			case EndResourceFragment:
			case EndAnnotation:
				return true;
			default:
				return false;
			}
		}
		
		public EventType getEndEvent(){
			switch(this){
			case StartDocument:
				return EndDocument;
			case StartContainer:
			    return EndContainer;
			case StartTextFlow:
			    return EndTextFlow;
			case StartDataPart:
			    return EndDataPart;
			case StartReference:
			    return EndReference;
			case StartProperties:
			    return EndProperties;
			case StartChildren:
			    return EndChildren;
			case StartTextFlowContent:
			    return EndTextFlowContent;
			case StartContainerFragment:
			    return EndContainerFragment;
			case StartTextFragment:
			    return EndTextFragment;
			case StartResourceFragment:
			    return EndResourceFragment;
			case StartAnnotation:
				return EndAnnotation;
			}
			throw new RuntimeException("Invalid startEvent");
		}
		
		public boolean isIsolatedEvent(){
			switch(this){
			case AnnotationData:
				return true;
			default:
				return false;
			}
		}
		
		
	}
	
