package com.googlecode.okapi.dom.pipeline;

import java.io.PrintStream;

import com.googlecode.okapi.pipeline.AbstractPipelineStep;
import com.googlecode.okapi.pipeline.IPullParser;

public class DomEventWriter extends AbstractPipelineStep<DocumentEvent>{

	private PrintStream writer;
	
	public DomEventWriter(PrintStream writer) {
		this.writer = writer;
	}
	
	public DomEventWriter(IPullParser<DocumentEvent> input, PrintStream writer) {
		super(input);
		this.writer = writer;
	}
	
	@Override
	public void handleEvent(DocumentEvent event) {
		writer.println(event.name());
		switch(event){
		case Container:
		case DataPart:
		case Document:
		case Reference:
		case TextFlow:
		}
	}
	
	public static void main(String[] args) {
		
	}

}
