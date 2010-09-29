/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.steps.xmlvalidation;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

@UsingParameters(Parameters.class)
public class XMLValidationStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private XMLInputFactory xmlInputFact;
//	private Schema schema;
//	private Validator validator;
//	private InputSource source;
	private String currentFileDir;
	private Parameters params;
	
	public XMLValidationStep () {
		params = new Parameters();
	}
	
	@Override
	public void destroy () {
		// Make available to GC
	}
	
	public String getDescription () {
		return "Validate XML documents."
			+ " Expects: raw XML document. Sends back: raw XML document.";
	}

	public String getName () {
		return "XML Validation";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
 
	@Override
	protected Event handleStartBatch (Event event) {
		
		xmlInputFact = XMLInputFactory.newInstance();
		//xmlInputFact.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		if(params.isValidate()){
			logger.info("Validating using XML Schema: "+params.getSchemaPath());
		}
		
		return event;
	}
	
	
	@Override
	protected Event handleRawDocument (Event event) {
		
		RawDocument rawDoc = (RawDocument)event.getResource();
		
		//--this is used to locate relative dtds--
		currentFileDir = Util.getDirectoryName(rawDoc.getInputURI().getPath());
		
		// Create the input source
		// Use the stream, so the encoding auto-detection can be done
		Source xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getStream());
		
		//--Checking for well-formedness--
		try {
			XMLStreamReader reader  = xmlInputFact.createXMLStreamReader(xmlInput);
			while(reader.hasNext()) {
				reader.next();
			}
			reader.close();

		} catch (XMLStreamException e) {
			logger.severe("Well-Formedness Error" +
					"\nLine: "+ e.getLocation().getLineNumber() +
					"\nColumn: "+ e.getLocation().getColumnNumber() +
					"\nOffset: "+ e.getLocation().getCharacterOffset() +
					"\n"+ e.getMessage().substring(e.getMessage().indexOf("Message:")));
			return event;
		}
		

		if(params.isValidate()){
			
			xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getStream());
			
			//--validating against schema--

			
			try {
				  SAXParserFactory factory = SAXParserFactory.newInstance();
				  factory.setNamespaceAware( true);
				  factory.setValidating( true);

				  SAXParser parser = factory.newSAXParser();

				  if(params.getValidationType() == Parameters.VALIDATIONTYPE_DTD){
					  
				  }else if(params.getValidationType() == Parameters.VALIDATIONTYPE_SCHEMA){
					  
					  parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
					                      "http://www.w3.org/2001/XMLSchema");
					  if(params.getSchemaPath().length() > 0){
						  parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaSource", new File(params.getSchemaPath()));						  
					  }
				  }

				  XMLReader reader = parser.getXMLReader();

				  reader.setErrorHandler(new ErrorHandler(){

					public void error(SAXParseException e) throws SAXException {
						logger.severe("Validation Error" +
								"\nLine: "+ e.getLineNumber() +
								"\nColumn: "+ e.getColumnNumber() +
								"\n"+ e.getMessage()+"\n");
						throw new SAXException("Error encountered");
					}

					public void fatalError(SAXParseException e) throws SAXException {
						logger.severe("Validation Fatal Error" +
								"\nLine: "+ e.getLineNumber() +
								"\nColumn: "+ e.getColumnNumber() +
								"\n"+ e.getMessage()+"\n");
						throw new SAXException("Fatal Error encountered");
					}

					public void warning(SAXParseException e) throws SAXException {
						logger.severe("Validation Warning" +
								"\nLine: "+ e.getLineNumber() +
								"\nColumn: "+ e.getColumnNumber() +
								"\n"+ e.getMessage()+"\n");
						throw new SAXException("Warning encountered");
					}
					  
				  });
				  reader.setEntityResolver(new DTDResolver(currentFileDir));
				  reader.parse( new InputSource(rawDoc.getStream()));

				} catch ( ParserConfigurationException e) {
					logger.severe(e.getMessage());
					return event;
				} catch ( SAXException e) {
					logger.severe(e.getMessage());
					return event;
				} catch ( IOException e) {
				  logger.severe(e.getMessage());
				  return event;
				}
		}
		
		return event;
	}
}

class DTDResolver implements EntityResolver {
	
	String currentFileDir;
	
    public DTDResolver(String currentFileDir) {
    	super();
    	this.currentFileDir = currentFileDir;
	}

	public InputSource resolveEntity(String publicID, String systemID) throws SAXException {

		//--check if the default dtd-location file is resolved--
    	File file = new File(systemID);
    	if (file.exists()){
	    	return null;
	    }
	    
    	//--check for dtd relative to the xml file--
	    file = new File(currentFileDir+systemID.substring(systemID.lastIndexOf("/")));
	    if (file.exists()){
	    	return new InputSource(currentFileDir+systemID.substring(systemID.lastIndexOf("/")));
	    }
        
        return null;
    }
}