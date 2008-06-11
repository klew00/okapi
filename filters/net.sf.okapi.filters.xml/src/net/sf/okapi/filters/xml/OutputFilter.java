package net.sf.okapi.filters.xml;

import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class OutputFilter implements IOutputFilter {
	
	private Resource                   res;
	private OutputStream               output;
	private DocumentBuilderFactory     fact;
	private DocumentBuilder            docBuilder;
	private ArrayList<IExtractionItem> subItems;


	public void close () {
		// Nothing to do
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage)
	{
		try {
			this.output = output;
			fact = DocumentBuilderFactory.newInstance();
			docBuilder = fact.newDocumentBuilder();
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public void endContainer (IResourceContainer resourceContainer) {
	}

	private void buildContent (Node node,
		IExtractionItem item,
		String content)
	{
		// Remove existing content
		Node tmpNode = node.getFirstChild();
		while ( tmpNode != null ) {
			node.removeChild(tmpNode);
			tmpNode = node.getFirstChild();
		}

		Document doc = node.getOwnerDocument();
		DocumentFragment df = parseXMLString(doc, content);
		
		while ( df.hasChildNodes() ) {
			node.appendChild(df.removeChild(df.getFirstChild()));
        }
	}
	
	public void endExtractionItem (IExtractionItem item) {
		// Set the target text
		IExtractionItem current = item.getFirstItem();
		do {
			if ( current.isTranslatable() ) {
				if ( current.hasTarget() ) {
					// Attribute case: Simply set the value of the node passed by the data
					Node attr = (Node)current.getData();
					if ( attr != null ) {
						if ( attr.getNodeValue().startsWith(XMLReader.ILMARKER) ) {
							// This is an item extracted from an in-line code
							// We have to wait the parent to be merged to set it
							current.getTarget().setID(current.getID()); // Copy ID
							subItems.add(current.getTarget());
						}
						else { // Not in in-line code, set it now
							attr.setNodeValue(current.getTarget().getContent().toString());
						}
					}
					else {
						String tmp = makeXMLString(current.getTarget());
						// Merge items extracted from in-line codes if there are any
						if ( subItems.size() > 0 ) {
							for ( IExtractionItem subItem : subItems ) {
								String mark = XMLReader.ILMARKER + subItem.getID();
								if ( tmp.indexOf(mark) > -1 ) {
									//TODO: We risk double-escape if the sub-item has in-line code
									tmp = tmp.replace(mark, Util.escapeToXML(
										subItem.toString(),3,false));
								}
								else {
									//TODO: Warning: marker not found for sub-item
								}
							}
							subItems.clear();
						}
						// Merge the content
						buildContent(res.srcNode, current.getTarget(), tmp);
					}
				}
			}
		}
		while ( (current = item.getNextItem()) != null );
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
		subItems = new ArrayList<IExtractionItem>();
	}

	private String makeXMLString (IExtractionItem item) {
		StringBuilder tmp = new StringBuilder();
		List<IFragment> fragList = item.getContent().getFragments();
		for ( IFragment frag : fragList ) {
			if ( frag.isText() ) {
				tmp.append(Util.escapeToXML(frag.toString(), 0, false));
			}
			else { // Re-use the original code
				// Attribute values should be escaped correctly already
				//TODO: What about escape for code frags that are text not attrinute?
				tmp.append(frag.toString());
			}
		}
		return tmp.toString();
	}
	
	private DocumentFragment parseXMLString (Document doc,
		String fragment)
	{
		// Make sure we have boundaries
		fragment = "<F>"+fragment+"</F>";
		try {
			// Parse the fragment
			Document tmpDoc = docBuilder.parse(
				new InputSource(new StringReader(fragment)));
			// Import the nodes of the new document into the destination
			// document so that they will be compatible with the it
			Node node = doc.importNode(tmpDoc.getDocumentElement(), true);
			// Create the document fragment node to hold the new nodes
			DocumentFragment docfrag = doc.createDocumentFragment();
            // Move the nodes into the fragment
			while ( node.hasChildNodes() ) {
				docfrag.appendChild(node.removeChild(node.getFirstChild()));
            }
			return docfrag;
		//} catch ( SAXException e ) {
			// A parsing error occurred. Invalid XML
			//TODO: Log
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
    }
	
}
