package example03;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

public class XsltTransformStep extends BasePipelineStep {
	private InputStream xstlInputstream;
	private boolean done;
	
	public XsltTransformStep(InputStream xstlInputstream) {
		done = false;
		this.xstlInputstream = xstlInputstream;
	}

	public String getName() {
		return "XSLT Processing Step";
	}

	/**
	 * handler takes a 
	 */
	@Override
	protected void handleRawDocument(Event event) {
		
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

		// get the input xml and xslt streams
		Reader XmlInput = ((RawDocument)event.getResource()).getReader(); 
				
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
		event.setResource(new RawDocument(transformedInput, "UTF-8", "en"));
		
		// this step is done generating events
		done = true;
	}

	public boolean hasNext() {
		return !done;
	}
}
