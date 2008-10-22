package com.googlecode.okapi.filter.odf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import com.googlecode.okapi.pipeline.EventWriter;
import com.googlecode.okapi.pipeline.PipelineDriver;
import com.googlecode.okapi.resource.Container;
import com.googlecode.okapi.resource.DataPart;
import com.googlecode.okapi.resource.Document;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.DocumentImpl;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.DocumentPart;
import com.googlecode.okapi.resource.Resource;
import com.googlecode.okapi.resource.ResourceFactory;
import com.googlecode.okapi.resource.ResourceFactoryImpl;
import com.googlecode.okapi.resource.TextFlow;
import com.googlecode.okapi.resource.TextFlowProvider;
import com.googlecode.okapi.resource.builder.BaseDocumentParser;
import com.googlecode.okapi.resource.textflow.ContainerFragment;
import com.googlecode.okapi.resource.textflow.ResourceFragment;
import com.googlecode.okapi.resource.textflow.TextFragment;

public class ContentXmlParser extends BaseDocumentParser{
	
	public static final String FEATURE_XML_ELEMENT = "xml-element";
	public static final String FEATURE_XML_ATTRIBUTE = "xml-attribute";
	public static final String FEATURE_XML_CDATA = "xml-cdata";
	public static final String FEATURE_XML_CHARACTERS = "xml-characters";
	
	protected static final String NSURI_TEXT = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
	protected static final String NSURI_XLINK = "http://www.w3.org/1999/xlink";

	protected Set<QName> textFlowContainerElements;
	protected Set<QName> inlineElements;
	protected Set<QName> translatableAttributes;
	
	private InputStream inputStream;
	private XMLStreamReader reader;

	Stack<Resource<?>> resources;
	
	private Resource<?> getCurrentElement(){
		return resources.isEmpty() ? null : resources.peek();
	}
	
	public ContentXmlParser(DocumentManager documentManager, InputStream inputStream) {
		super(documentManager);
		this.inputStream = inputStream;
		resources = new Stack<Resource<?>>();

		setupOdfRules();
		
		XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
		xmlInputFactory.configureForRoundTripping();
		try {
			reader = xmlInputFactory.createXMLStreamReader(inputStream);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// StaX doesn't seem to report the START_DOCUMENT event.
		addStartDocumentEvent();
	}
	
	private void setupOdfRules(){
		textFlowContainerElements = new HashSet<QName>();
		inlineElements = new HashSet<QName>();
		translatableAttributes = new HashSet<QName>();

		// TODO
		// this is just a mock-example with hard-coded rules
		// we should really generalize this parser and create
		// configuration for odf based on ITS or Jim's Groovy configuration
		
		// add extract rules
		textFlowContainerElements.add( new QName(NSURI_TEXT, "p") );
		textFlowContainerElements.add( new QName(NSURI_TEXT, "h") );

		// add inline rules 
		inlineElements.add( new QName(NSURI_TEXT, "span") );

		// <text:a> office:title
		
		
		// add translatable attributes
		
		// special characters
		// whitespace: <text:s text:c='1'/>
		// <text:tab/>
		// <text:line-break/>
		

	}
	
	private boolean hasProcessedEvent = false;
	
	@Override
	protected void cacheNextEvent() {
		try {
			while(!hasProcessedEvent){
				if(reader.hasNext()){
					switch(reader.next()){
					case XMLStreamReader.START_ELEMENT:
						System.err.println("START_ELEMENT: " +reader.getName());
						handleStartElement();
						break;
					case XMLStreamReader.END_ELEMENT:
						System.err.println("END_ELEMENT");
						handleEndElement();
						break;
					case XMLStreamReader.CDATA:
						System.err.println("CDATA");
						handleCData();
						break;
					case XMLStreamReader.CHARACTERS:
						System.err.println("CHARACTERS");
						handleCharacters();
						break;
					case XMLStreamReader.COMMENT:
						System.err.println("COMMENT");
						handleComment();
						break;
					case XMLStreamReader.DTD:
						System.err.println("DTD");
						handleDTD();
						break;
					case XMLStreamReader.ENTITY_DECLARATION:
						System.err.println("ENTITY_DECLARATION");
						handleEntityDeclaration();
						break;
					case XMLStreamReader.ENTITY_REFERENCE:
						System.err.println("ENTITY_REFERENCE");
						handleEntityReference();
						break;
					case XMLStreamReader.NAMESPACE:
						System.err.println("NAMESPACE");
						handleNamespace();
						break;
					case XMLStreamReader.NOTATION_DECLARATION:
						System.err.println("NOTATION_DECLARATION");
						handleNotationDeclaration();
						break;
					case XMLStreamReader.PROCESSING_INSTRUCTION:
						System.err.println("PROCESSING_INSTRUCTION");
						handleProcessingInstruction();
						break;
					case XMLStreamReader.SPACE:
						System.err.println("SPACE");
						handleSpace();
						break;
					case XMLStreamReader.END_DOCUMENT:
						System.err.println("END_DOCUMENT");
						handleEndDocument();
						break;
					case XMLStreamReader.ATTRIBUTE:	
					case XMLStreamReader.START_DOCUMENT:
					default:
						throw new RuntimeException("programming error");
					}
				
				}
				else{
					setEndOfDocument();
					break;
				}
			}
			hasProcessedEvent = false;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void handleEndDocument() {
		addEndDocumentEvent();
		setEndOfDocument();
		hasProcessedEvent = true;
	}

	private void handleSpace() {
		// TODO Auto-generated method stub
	}

	private void handleProcessingInstruction() {
		// TODO Auto-generated method stub
	}

	private void handleNotationDeclaration() {
		// TODO Auto-generated method stub
	}

	private void handleNamespace() {
		// TODO Auto-generated method stub
	}

	private void handleEntityReference() {
		// TODO Auto-generated method stub
	}

	private void handleEntityDeclaration() {
		// TODO Auto-generated method stub
	}

	private void handleDTD() {
		// TODO Auto-generated method stub
	}

	private void handleComment() {
		// TODO Auto-generated method stub
	}

	private void handleCharacters() {
		Resource<?> currentElement = getCurrentElement();
		if(currentElement instanceof TextFlowProvider){
			TextFlowProvider textFlowProvider = (TextFlowProvider) currentElement;
			
			TextFragment textFragment = getResourceFactory().createTextFragment();
			textFragment.setContent(reader.getText());
			textFlowProvider.getFlow().add(textFragment);
			
			addTextFragmentEvent(textFragment);
		}
		else if(currentElement instanceof Container){
			Container container = (Container) currentElement;
			DataPart dataPart = getResourceFactory().createDataPart();
			dataPart.setStructuralFeature(FEATURE_XML_CHARACTERS);
			container.getParts().add(dataPart.getId());
			addDataPartEvent(dataPart);
		}
		else{
			System.err.println(currentElement);
			throw new RuntimeException("Programming error");
		}
	}

	private void handleCData() {
		Resource<?> currentElement = getCurrentElement();
		
		if(currentElement instanceof TextFlowProvider){
			TextFlowProvider textFlow = (TextFlowProvider) currentElement;
			
			TextFragment textFragment = getResourceFactory().createTextFragment();

			textFragment.setContent(reader.getText());
			textFlow.getFlow().add(textFragment);
			
			
			addStartTextFragmentEvent(textFragment);

			DataPart dataPart = getResourceFactory().createDataPart();
			dataPart.setStructuralFeature(FEATURE_XML_CDATA);
			textFragment.setPart(dataPart.getId());
			
			addDataPartEvent(dataPart);
			
			addEndTextFragmentEvent();
			
		}
		else if(currentElement instanceof Container){
			Container container = (Container) currentElement;
			DataPart dataPart = getResourceFactory().createDataPart();
			dataPart.setStructuralFeature(FEATURE_XML_CDATA);
			container.getParts().add(dataPart.getId());
			addDataPartEvent(dataPart);
		}
		else{
			throw new RuntimeException("Programming error");
		}
	}

	private void addAttributes(DocumentPart part){
		addStartPropertiesEvent();
		for(int i=0; i<reader.getAttributeCount();i++){
			QName attName = reader.getAttributeName(i);
			
			DocumentPart attribute;
			
			if(translatableAttributes.contains(attName)){
				attribute = getResourceFactory().createTextFlow();
				attribute.setName(reader.getAttributeName(i).toString());
				attribute.setStructuralFeature(FEATURE_XML_ATTRIBUTE);
				
				TextFlow textFlow = (TextFlow) attribute;
				addStartTextFlowEvent(textFlow);
				addStartTextFlowContentEvent();
				
				TextFragment fragment = getResourceFactory().createTextFragment();
				fragment.setContent(reader.getAttributeValue(i));
				addTextFragmentEvent(fragment);
				
				addEndTextFlowContentEvent();
				addEndTextFlowEvent();
			}
			else{
				attribute = getResourceFactory().createDataPart();
				attribute.setName(reader.getAttributeName(i).toString());
				attribute.setStructuralFeature(FEATURE_XML_ATTRIBUTE);
				addDataPartEvent((DataPart)attribute);
			}
			part.getProperties().add(attribute.getId());
		}
		addEndPropertiesEvent();
	}

	private void handleStartElementInTextFlow(){
		QName name = reader.getName();
		boolean isInline = inlineElements.contains(name);

		TextFlowProvider current = (TextFlowProvider) getCurrentElement();
		
		if(isInline){
			ContainerFragment containerFragment = getResourceFactory().createContainerFragment();
			resources.push(containerFragment);
			
			current.getFlow().add(containerFragment);

			addStartContainerFragmentEvent(containerFragment);

			DataPart dataPart = getResourceFactory().createDataPart();
			containerFragment.setPart(dataPart.getId());
			addAttributes(dataPart);

			addStartTextFlowContentEvent();
		}
		else{
			ResourceFragment resourceFragment = getResourceFactory().createResourceFragment();
			//resources.push(resourceFragment);
			
			current.getFlow().add(resourceFragment);
			
			addStartResourceFragmentEvent(resourceFragment);
			
			Container containerPart = getResourceFactory().createContainer();
			resourceFragment.setPart(containerPart.getId());
			addAttributes(containerPart);
			resources.push(containerPart);
		}
	}
	
	private void handleStartElementOutsideTextFlow(){
		QName name = reader.getName();
		boolean isTextFlow = textFlowContainerElements.contains(name);
		
		if(isTextFlow){
			TextFlow flow = getResourceFactory().createTextFlow();
			flow.setName(name.toString());
			flow.setStructuralFeature(FEATURE_XML_ELEMENT);
			flow.setSemanticFeature("paragraph");
			
			addStartTextFlowEvent(flow);
			addAttributes(flow);
			
			resources.push(flow);
			addStartTextFlowContentEvent();
		}
		else{
			if(inlineElements.contains(name)){
				// warning
			}
			Container container = getResourceFactory().createContainer();
			container.setName(name.toString());
			container.setStructuralFeature(FEATURE_XML_ELEMENT);
			addStartContainerEvent(container);
			
			addAttributes(container);
			
			resources.push(container);
		}
	}
	
	private void handleStartElement(){
		Resource<?> currentElement = getCurrentElement();
		
		if(currentElement instanceof TextFlowProvider){
			handleStartElementInTextFlow();
		}
		else{
			handleStartElementOutsideTextFlow();
			
		}
		hasProcessedEvent = true;
	}
	
	private void handleEndElement(){
		Resource<?> currentElement = getCurrentElement();
		
		if(currentElement instanceof TextFlowProvider){
			addEndTextFlowContentEvent();
		}
		
		addEndEvent();
		resources.pop();
		hasProcessedEvent = true;
	}
	
	@Override
	public void close() {
		try{
			inputStream.close();
		}
		catch(IOException e){}
		super.close();
	}
	
	
	public static void main(String[] args) {
		String documentString =  
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<!DOCTYPE foo [ <!ENTITY greeting \"hello\"> ]>"+
			"<text:p xmlns:text=\""+ NSURI_TEXT + "\" text:style-name=\"Standard\">\n" +
			"  Paragraph with footnote here\n" + 
			"  <text:note xmlns:xx=\"urn:x\" xx:s='x' text:id=\"ftn1\" text:note-class=\"footnote\">\n" +
			"    <text:note-citation>1</text:note-citation>\n" + 
			"    <text:note-body>\n" +
			"      <text:p text:style-name=\"Footnote\">\n" +
			"        Text of the \n" +
			"        <text:span text:style-name=\"T1\">automatic</text:span>\n" + 
			"        footnote.\n" +
			"      </text:p>\n" +
			"      <text:p text:style-name=\"Footnote\">\n" +
			"        Second paragraph of the <![CDATA[footnote]]>\n" +
			"      </text:p>\n" +
			"    </text:note-body>\n" +
			"  </text:note>\n" +
			"</text:p>\n";
					
		Document doc = new DocumentImpl(new DocumentId("xyz"));
		ResourceFactory factory = new ResourceFactoryImpl();
		DocumentManager manager = new DocumentManager(doc, factory);
		ByteArrayInputStream bs = new ByteArrayInputStream(documentString.getBytes());
		ContentXmlParser parser = new ContentXmlParser(manager,bs);
		
		PipelineDriver driver = new PipelineDriver();
		driver.setInput(parser);
		driver.addStep(new EventWriter(System.out));
		
		driver.run();
	}

}
