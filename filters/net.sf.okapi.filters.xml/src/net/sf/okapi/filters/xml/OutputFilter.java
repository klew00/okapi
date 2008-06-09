package net.sf.okapi.filters.xml;

import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
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

	public void close () {
		// Nothing to do
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		targetLang = targetLanguage;
	}

	public void endContainer (IResourceContainer resourceContainer) {
	}

	private void buildContent (Node node,
		IExtractionItem item)
	{
		// Remove existing content
		Node tmpNode = node.getFirstChild();
		while ( tmpNode != null ) {
			node.removeChild(tmpNode);
			tmpNode = node.getFirstChild();
		}

		// Set new nodes
		Document doc = node.getOwnerDocument();
		List<IFragment> fragList = item.getContent().getFragments();
		for ( IFragment frag : fragList ) {
			if ( frag.isText() ) {
				node.appendChild(doc.createTextNode(
					frag.toString()));
			}
			else { // Re-use the original code
				// We need to use importNode() for case where target
				// is created from the source item.
				node.appendChild(doc.importNode(
					(Node)((CodeFragment)frag).extraData, true));
			}
		}
	}
	
	public void endExtractionItem (IExtractionItem item) {
		// Set the target text
		//TODO: children!
		if ( item.isTranslatable() ) {
			if ( item.hasTarget() ) {
				buildContent(res.srcNode, item.getTarget());
			}
			// Else: no modification
		}
	}

	public void endResource (IResource resource) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(res.doc), new StreamResult(output));
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public void startContainer (IResourceContainer resourceContainer) {
	}

	public void startExtractionItem (IExtractionItem item) {
	}

	public void startResource (IResource resource) {
		res = (Resource)resource;
	}

}
