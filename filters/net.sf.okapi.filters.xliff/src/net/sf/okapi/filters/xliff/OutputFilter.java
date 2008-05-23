package net.sf.okapi.filters.xliff;

import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;
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

	public void endContainer (IResourceContainer resourceContainer) {
	}

	private void buildContent (Element elem,
		IExtractionItem item)
	{
		// Remove existing content
		Node node = elem.getFirstChild();
		while ( node != null ) {
			elem.removeChild(node);
			node = elem.getFirstChild();
		}

		// Set new nodes
		List<IFragment> fragList = item.getContent().getFragments();
		for ( IFragment frag : fragList ) {
			if ( frag.isText() ) {
				elem.appendChild(elem.getOwnerDocument().createTextNode(
					frag.toString()));
			}
			else { // Re-use the original code
				elem.appendChild(((Node)((CodeFragment)frag).extraData).cloneNode(true));
			}
		}
	}
	
	public void endExtractionItem (IExtractionItem sourceItem,
		IExtractionItem targetItem) {
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
			if ( sourceItem.isTranslatable() ) {
				buildContent(res.trgElem, sourceItem);
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

	public void endResource (IResource resource) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
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
