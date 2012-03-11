/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

@UsingParameters(Parameters.class)
public class XMLValidationStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private XMLInputFactory xmlInputFact;
	private String currentFileDir;
	private Parameters params;
	
	public XMLValidationStep () {
		params = new Parameters();
	}
	
	@Override
	public void destroy () {
		// Make available to GC
		xmlInputFact = null;
	}
	
	@Override
	public String getDescription () {
		return "Validate XML documents."
			+ " Expects: raw XML document. Sends back: raw XML document.";
	}

	@Override
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
		// Use the file path as systemId if possible
		Source xmlInput = null;
		if ( rawDoc.getInputURI() == null ) {
			xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getStream());
		}
		else {
			xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getStream(),
				rawDoc.getInputURI().getPath());
		}
		
		//--Checking for well-formedness--
		try {
			XMLStreamReader reader  = xmlInputFact.createXMLStreamReader(xmlInput);
			
			while(reader.hasNext()) {
				reader.next();
			}
			reader.close();

		}
		catch ( XMLStreamException e ) {
			logger.severe("Well-Formedness Error " +
				"Line: "+ e.getLocation().getLineNumber() +
				", Column: "+ e.getLocation().getColumnNumber() +
				", Offset: "+ e.getLocation().getCharacterOffset() +
				"\n"+ e.getMessage());
			return event;
		}
		
		if(params.isValidate()){			
			xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getStream());
			
			//--validating against schema--
			try {
				SchemaFactory factory = null;	
			    if(params.getValidationType() == Parameters.VALIDATIONTYPE_DTD){
			    	// JAXB doesn't seem to handle DTD very well, resort to SAX parser
			    	SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			    	saxFactory.setNamespaceAware(true);
			    	saxFactory.setValidating(true);

					SAXParser parser = saxFactory.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					reader.setErrorHandler(new ValidatingErrorHandler(logger));
					reader.setEntityResolver(new DTDResolver(currentFileDir));
					reader.parse(new InputSource(rawDoc.getStream()));
			    } else {
			    	// normal JAXB validation
					if(params.getValidationType() == Parameters.VALIDATIONTYPE_SCHEMA) {
						factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);    
					} else if(params.getValidationType() == Parameters.VALIDATIONTYPE_RELAXNG) {
						System.setProperty(SchemaFactory.class.getName() + ":" + 
				    			XMLConstants.RELAXNG_NS_URI, "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory");
						factory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
						if (params.getSchemaPath().length() <= 0) {
							throw new OkapiBadStepInputException("Please specify a valid RelaxNG schema path");
						}
					}			    
					URL schemaLocation = null;
					if (params.getSchemaPath().length() > 0) {
						try {
							// try true URL syntax first
							schemaLocation = new URL(params.getSchemaPath());
						} catch(MalformedURLException e) {
							// URL parse failed must be a local file path
							schemaLocation = new File(params.getSchemaPath()).toURI().toURL();
						}
					}
					         
				    Schema schema;
				    if (schemaLocation == null) {
				    	// the xml document specifies the schema internally
				    	// only works for W3C schemas
				    	schema = factory.newSchema();
				    } else {
				    	// user specified schema
				    	schema = factory.newSchema(schemaLocation);
				    }
				      
			        Validator validator = schema.newValidator();
			        validator.setErrorHandler(new ValidatingErrorHandler(logger));
				    validator.validate(xmlInput);
				}
			} catch (SAXException e) {
				logger.severe(e.getMessage());
				return event;
			} catch (IOException e) {
				logger.severe(e.getMessage());
				  return event;
			} catch (ParserConfigurationException e) {
				logger.severe(e.getMessage());
				return event;
			} 			
		}
		
		return event;
	}
}

class ValidatingErrorHandler implements ErrorHandler {
	Logger logger;
	
	public ValidatingErrorHandler(Logger logger) {
		this.logger = logger;
	}
	
	 @Override
	public void error(SAXParseException e) throws SAXException {
		logger.severe("Validation Error " +
				"Line: "+ e.getLineNumber() +
				", Column: "+ e.getColumnNumber() +
				"\n"+ e.getMessage()+"\n");
		throw new SAXException("Error encountered");
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		logger.severe("Validation Fatal Error " +
				"Line: "+ e.getLineNumber() +
				", Column: "+ e.getColumnNumber() +
				"\n"+ e.getMessage()+"\n");
		throw new SAXException("Fatal Error encountered");
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		logger.severe("Validation Warning " +
				"Line: "+ e.getLineNumber() +
				", Column: "+ e.getColumnNumber() +
				"\n"+ e.getMessage()+"\n");
		throw new SAXException("Warning encountered");
	}
}
	 
class DTDResolver implements EntityResolver {
	String currentFileDir;
	
    public DTDResolver(String currentFileDir) {
    	super();
    	this.currentFileDir = currentFileDir;
	}

	@Override
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