package com.googlecode.okapi.filter.odf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;

import com.googlecode.okapi.events.ContainerEvent;
import com.googlecode.okapi.events.ContainerFragmentEvent;
import com.googlecode.okapi.events.DataPartEvent;
import com.googlecode.okapi.events.DocumentEvent;
import com.googlecode.okapi.events.Event;
import com.googlecode.okapi.events.EventFactory;
import com.googlecode.okapi.events.IDocumentPartEvent;
import com.googlecode.okapi.events.ResourceFragmentEvent;
import com.googlecode.okapi.events.TextFlowEvent;
import com.googlecode.okapi.events.TextFragmentEvent;
import com.googlecode.okapi.pipeline.PipelineDriver;
import com.googlecode.okapi.pipeline.event.BaseDocumentParser;
import com.googlecode.okapi.pipeline.event.EventWriter;
import com.googlecode.okapi.resource.DocumentId;
import com.googlecode.okapi.resource.DocumentManager;
import com.googlecode.okapi.resource.DomEventFactory;
import com.googlecode.okapi.resource.ResourceFactoryImpl;

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

	private enum State{Flow,Container, None};
	
	private State state = State.None;
	
	public ContentXmlParser(EventFactory factory, InputStream inputStream) {
		super(factory);
		this.inputStream = inputStream;

		setupOdfRules();
		
		XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
		xmlInputFactory.configureForRoundTripping();
		try {
			reader = xmlInputFactory.createXMLStreamReader(inputStream);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DocumentEvent docEvent = getEventFactory().createStartDocumentEvent();
		docEvent.setName("blah");
		// StaX doesn't seem to report the START_DOCUMENT event.
		addEvent(docEvent);
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
		addEndEvent(); // Document
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
		if(state == State.Flow){
			TextFragmentEvent textFragment = getEventFactory().createStartTextFragmentEvent();
			textFragment.setContent(reader.getText());
			addEvent(textFragment);
		}
		else if(state == State.Container){
			DataPartEvent dataPart = getEventFactory().createStartDataPartEvent();
			dataPart.setStructuralFeature(FEATURE_XML_CHARACTERS);
			addEvent(dataPart);
		}
		else{
			throw new RuntimeException("Programming error");
		}
	}

	private void handleCData() {
		
		if(state == State.Flow){
			TextFragmentEvent textFragment = getEventFactory().createStartTextFragmentEvent();

			textFragment.setContent(reader.getText());
			addEvent(textFragment);

			DataPartEvent dataPart = getEventFactory().createStartDataPartEvent();
			dataPart.setStructuralFeature(FEATURE_XML_CDATA);
			addEvent(dataPart);
			addEndEvent(); // TextFragment
			
		}
		else if(state == State.Container){
			DataPartEvent dataPart = getEventFactory().createStartDataPartEvent();
			dataPart.setStructuralFeature(FEATURE_XML_CDATA);
			addEvent(dataPart);
		}
		else{
			throw new RuntimeException("Programming error");
		}
	}

	private void addAttributes(){
		addEvent( getEventFactory().createStartPropertiesEvent() );
		for(int i=0; i<reader.getAttributeCount();i++){
			QName attName = reader.getAttributeName(i);
			
			IDocumentPartEvent attribute;
			
			if(translatableAttributes.contains(attName)){
				attribute = getEventFactory().createStartTextFlowEvent();
				attribute.setName(reader.getAttributeName(i).toString());
				attribute.setStructuralFeature(FEATURE_XML_ATTRIBUTE);
				
				TextFlowEvent textFlow = (TextFlowEvent) attribute;
				addEvent(textFlow);
				addEvent( getEventFactory().createStartTextFlowContentEvent() );
				
				TextFragmentEvent fragment = getEventFactory().createStartTextFragmentEvent();
				fragment.setContent(reader.getAttributeValue(i));
				addEvent(fragment);
				
				addEndEvent(); // TextFlowContent
				addEndEvent(); // TextFlow
			}
			else{
				attribute = getEventFactory().createStartDataPartEvent();
				attribute.setName(reader.getAttributeName(i).toString());
				attribute.setStructuralFeature(FEATURE_XML_ATTRIBUTE);
				addEvent(attribute);
			}
		}
		addEndEvent(); // Properties
	}

	private void handleStartElementInTextFlow(){
		QName name = reader.getName();
		boolean isInline = inlineElements.contains(name);

		if(isInline){
			ContainerFragmentEvent containerFragment = getEventFactory().createStartContainerFragmentEvent();

			addEvent(containerFragment);

			DataPartEvent dataPart = getEventFactory().createStartDataPartEvent();
			addAttributes();
			addEvent( getEventFactory().createStartTextFlowContentEvent() );
		}
		else{
			ResourceFragmentEvent resourceFragment = getEventFactory().createStartResourceFragmentEvent();
			
			addEvent(resourceFragment);
			
			ContainerEvent containerPart = getEventFactory().createStartContainerEvent();
			addAttributes();
		}
	}
	
	private void handleStartElementOutsideTextFlow(){
		QName name = reader.getName();
		boolean isTextFlow = textFlowContainerElements.contains(name);
		
		if(isTextFlow){
			TextFlowEvent flow = getEventFactory().createStartTextFlowEvent();
			flow.setName(name.toString());
			flow.setStructuralFeature(FEATURE_XML_ELEMENT);
			flow.setSemanticFeature("paragraph");
			
			addEvent(flow);
			addAttributes();
			addEvent( getEventFactory().createStartTextFlowContentEvent() );
		}
		else{
			if(inlineElements.contains(name)){
				// warning
			}
			ContainerEvent container = getEventFactory().createStartContainerEvent();
			container.setName(name.toString());
			container.setStructuralFeature(FEATURE_XML_ELEMENT);
			addEvent(container);
			
			addAttributes();
		}
	}
	
	private void handleStartElement(){
		if(state == State.Flow){
			handleStartElementInTextFlow();
		}
		else{
			handleStartElementOutsideTextFlow();
			
		}
		hasProcessedEvent = true;
	}
	
	private void handleEndElement(){
		
		if(state == State.Flow){
			addEvent( getEventFactory().createEndTextFlowContentEvent() );
		}
		
		addEndEvent();
		state = State.None;
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
					
		EventFactory factory = new DomEventFactory(
				new ResourceFactoryImpl( new DocumentId("doc-id.xml")));
		ByteArrayInputStream bs = new ByteArrayInputStream(documentString.getBytes());
		ContentXmlParser parser = new ContentXmlParser(factory,bs);
		
		PipelineDriver<Event> driver = new PipelineDriver<Event>();
		driver.setInput(parser);
		driver.addStep(new EventWriter(System.out));
		
		driver.run();
	}

}
