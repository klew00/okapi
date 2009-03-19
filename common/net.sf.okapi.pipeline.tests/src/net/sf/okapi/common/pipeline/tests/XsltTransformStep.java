package net.sf.okapi.common.pipeline.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.FileResource;

public class XsltTransformStep extends BasePipelineStep {
	private InputStream xstlInputstream;
	
	public XsltTransformStep(InputStream xstlInputstream) {
		this.xstlInputstream = xstlInputstream;
	}

	public String getName() {
		return "XSLT Processing Step";
	}

	@Override
	protected void handleFileResource(Event event) {
		
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

		// get the input xml and xslt streams
		InputStream XmlInput = ((FileResource)event.getResource()).getInputStream(); 
				
		Source xmlSource = new StreamSource(XmlInput);
		Source xsltSource = new StreamSource(xstlInputstream);

		Result result = new StreamResult(tempStream);

		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans;
		try {
			trans = transFact.newTransformer(xsltSource);
			// do the transformation
			trans.transform(xmlSource, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

		ByteArrayInputStream transformedInput = new ByteArrayInputStream(tempStream.toByteArray());
			
		// overwrite our event to the new transformed content	
		event = new Event(EventType.FILE_RESOURCE, new FileResource(transformedInput, "UTF-8", "text/xml", "en"));
	}
}
