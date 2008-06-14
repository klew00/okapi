package net.sf.okapi.filters.xliff;

import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public class OutputFilter implements IOutputFilter {
	
	private Resource         res;
	private OutputStream     output;
	private String           targetLang;

	public void close () {
		// Nothing to do
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		targetLang = targetLanguage;
	}

	public void endContainer (IGroupResource resourceContainer) {
	}

	private void buildContent (Element elem,
		IContainer content)
	{
		// Remove existing content
		Node node = elem.getFirstChild();
		while ( node != null ) {
			elem.removeChild(node);
			node = elem.getFirstChild();
		}

		// Set new nodes
		Document doc = elem.getOwnerDocument();
		List<IFragment> fragList = content.getFragments();
		for ( IFragment frag : fragList ) {
			if ( frag.isText() ) {
				elem.appendChild(doc.createTextNode(
					frag.toString()));
			}
			else { // Re-use the original code
				// We need to use importNode() for case where target
				// is created from the source item.
				elem.appendChild(doc.importNode(
					(Node)((CodeFragment)frag).getData(), true));
			}
		}
	}
	
	public void endExtractionItem (IExtractionItem item) {
		if ( res.srcElem != null ) {
			// Create a target element if needed
			boolean newTarget = (res.trgElem == null);
			if  ( newTarget ) {
				res.trgElem = res.doc.createElement("target");
				res.trgElem.setAttribute("xml:lang", targetLang);
				//TODO: insert after source instead of append at the end of source parent
				res.srcElem.getParentNode().appendChild(res.trgElem);
				res.srcElem.getParentNode().appendChild(res.doc.createTextNode("\n"));
			}
			// Set the target element
			//TODO: cases for existing translation
			if ( item.isTranslatable() ) {
				if ( item.hasTarget() ) {
					buildContent(res.trgElem, item.getTarget());
				}
				else {
					buildContent(res.trgElem, item.getSource());
				}
			}
			else if ( newTarget || !res.trgElem.hasChildNodes() ) {
				// Fill new target with original text
				Node node = res.srcElem.getFirstChild();
				while ( node != null ) {
					res.trgElem.appendChild(node.cloneNode(true));
					node = node.getNextSibling();
				}
			}
		}
	}

	public void endResource (IDocumentResource resource) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(res.doc), new StreamResult(output));
		}
		catch ( TransformerConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( TransformerException e ) {
			throw new RuntimeException(e);
		}
	}

	public void startContainer (IGroupResource resource) {
	}

	public void startExtractionItem (IExtractionItem item) {
	}

	public void startResource (IDocumentResource resource) {
		res = (Resource)resource;
	}

    public void skeletonContainer (ISkeletonResource resource) {
    }
    
}
