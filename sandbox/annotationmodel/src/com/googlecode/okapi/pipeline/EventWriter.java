package com.googlecode.okapi.pipeline;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.googlecode.okapi.filter.zip.ZipDocumentParser;
import com.googlecode.okapi.pipeline.ResourceEvent.ResourceEventType;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.DocumentImpl;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Reference;
import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceFactory;
import com.googlecode.okapi.resource.ResourceFactoryImpl;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public class EventWriter extends AbstractPipelineStep{

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
	public void handleEvent(ResourceEvent event) {
		if(event.type.isStartEvent()){
			writeIndentation();
			stack++;
		}
		else if(event.type.isEndEvent()){
			stack--;
			writeIndentation();
		}
		out.append(event.type.name());

		
		
		if(event.hasValue()){
			out.append('(');
			
			if(event.type.isStartEvent()){
				Resource<?> r = (Resource<?>) event.value;
				out.append(r.getId().get());
				
				if(event.value instanceof DocumentPart){
					DocumentPart part = (DocumentPart) event.value;
					if(part.getName() != null){
						out.append(", ");
						out.append("name: '");
						out.append(part.getName());
						out.append("'");
					}
					if(part.getStructuralFeature() != null){
						out.append(", ");
						out.append("structural-feature: '");
						out.append(part.getStructuralFeature());
						out.append("'");
					}
					if(part.getSemanticFeature() != null){
						out.append(", ");
						out.append("semantic-feature: '");
						out.append(part.getSemanticFeature());
						out.append("'");
					}
				}
			}
			else if(event.type.isEndEvent()){
				Resource<?> r = (Resource<?>) event.value;
				out.append(r.getId().get());
			}
			
			switch(event.type){
			case Annotation:
				break;
			case EndDocument:
				break;
			case StartProperties:
			case EndProperties:
			case StartTextFlowContent:
			case EndTextFlowContent:
			case StartChildren:
			case EndChildren:
				break;
			case EndContainer:
			case EndDataPart:
			case EndReference:
			case EndTextFlow:
				DocumentPart part = (DocumentPart) event.value;
				break;
			case EndContainerFragment:
			case EndResourceFragment:
				break;
			case EndTextFragment:
				TextFragment textFragment1 = (TextFragment) event.value;
				out.append("'");
				String content = textFragment1.getContent().replace("\n", "\\n");
				out.append(content);
				out.append("'");
				break;
			case StartDocument:
				Document doc = (Document) event.value;
				out.append(", name:'");
				out.append(doc.getName());
				out.append("'");
				break;
			case StartTextFlow:
				TextFlow textFlow = (TextFlow) event.value;
				break;
			case StartContainer:
				Container container = (Container) event.value;
				out.append(", name:'");
				out.append(container.getName());
				out.append("'");
				break;
			case StartDataPart:
				DataPart dataPart = (DataPart) event.value;
				break;
			case StartReference:
				Reference ref = (Reference) event.value;
				break;
			case StartResourceFragment:
				ResourceFragment resourceFragment = (ResourceFragment) event.value;
				break;
			case StartContainerFragment:
				ContainerFragment containerFragment = (ContainerFragment) event.value;
				break;
			case StartTextFragment:
				TextFragment textFragment = (TextFragment) event.value;
				break;
			}
			out.append(')');
		}
		out.println();
		
	}

	private void writeIndentation(){
		for(int i=0;i<stack*INDENT;i++){
			out.append(' ');
		}
	}
	
	public static void main(String[] args) {
		String inputFile = "/home/asgeirf/jdev/netbeans-6.5beta/mobility8/sources/netbeans_databindingme-src.zip";
		Document doc = new DocumentImpl(new DocumentId("xyz"));
		ResourceFactory factory = new ResourceFactoryImpl();
		DocumentManager docManager = new DocumentManager(doc, factory);
		ZipDocumentParser input;
		try{
			input = new ZipDocumentParser(docManager, inputFile);
			EventWriter writer = new EventWriter(null, System.out);
			PipelineDriver driver = new PipelineDriver();
			driver.addStep(writer);
			driver.setInput(input);
			driver.run();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
