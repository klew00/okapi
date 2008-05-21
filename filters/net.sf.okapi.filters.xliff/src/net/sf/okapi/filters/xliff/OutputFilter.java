package net.sf.okapi.filters.xliff;

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class OutputFilter implements IOutputFilter {
	
	private Resource         res;
	private OutputStream     output;
	private String           targetLang;

	public void close () throws Exception {
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		targetLang = targetLanguage;
	}

	public void endContainer (IResourceContainer resourceCntainer) {
	}

	public void endExtractionItem (IExtractionItem sourceItem,
		IExtractionItem targetItem) {
		if ( res.srcElem != null ) {
			//TODO: Handle inline codes, etc.
			String tmp = sourceItem.getContent().toString();
			
			// Create a target element if needed
			boolean newTarget = (res.trgElem == null); 
			if  ( newTarget ) {
				res.trgElem = res.doc.createElement("target");
				res.trgElem.setAttribute("xml:lang", targetLang);
				res.srcElem.getParentNode().appendChild(res.trgElem);
			}
			// Set the target element
			//TODO: cases for existing translation
			if ( sourceItem.isTranslatable() ) {
				res.trgElem.setTextContent(tmp);
			}
			else if ( newTarget ) {
				// Fill new target with original text
				res.trgElem.setTextContent(res.srcElem.getTextContent());
			}
		}
	}

	public void endResource (IResource resource) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(res.doc), new StreamResult(output));
		}
		catch ( Exception e ) {
			System.err.print(e.getLocalizedMessage());
		}
	}

	public void startContainer (IResourceContainer resourceContainer) {
	}

	public void startExtractionItem (IExtractionItem sourceItem,
		IExtractionItem targetItem) {
	}

	public void startResource (IResource resource) {
		res = (Resource)resource;
	}

}
