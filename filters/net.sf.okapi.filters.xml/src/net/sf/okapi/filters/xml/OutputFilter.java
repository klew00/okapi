package net.sf.okapi.filters.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IFragment;
import net.sf.okapi.common.resource.IDocumentResource;
import net.sf.okapi.common.resource.IGroupResource;
import net.sf.okapi.common.resource.ISkeletonResource;

public class OutputFilter implements IOutputFilter {
	
	private Resource                        res;
	private OutputStream                    output;
	private DocumentBuilderFactory          fact;
	private DocumentBuilder                 docBuilder;
	private HashMap<String, IContainer>     subItems;
	private final Logger                    logger = LoggerFactory.getLogger("net.sf.okapi.logging");


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
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
	}

	public void endContainer (IGroupResource resource) {
	}

	private void buildContent (Node node,
		String content)
	{
		// Remove the part of the existing content that is
		// marked with 'toDel' user-data flag
		boolean foundNodeToDelete = false;
		Node beforeNode = null;
		Node deleteNode = null;
		Node currentNode = node.getFirstChild();
		while ( currentNode != null ) {
			if ( currentNode.getUserData("toDel") == null ) {
				if ( foundNodeToDelete ) { // Deletion done, stop here
					beforeNode = currentNode;
					break;
				}
				else { // No deletion done yet, keep looking for the first one
					currentNode = currentNode.getNextSibling();
				}
			}
			else { // Do the deletion, set the flag
				deleteNode = currentNode;
				currentNode = currentNode.getNextSibling();
				node.removeChild(deleteNode);
				foundNodeToDelete = true;
			}
		}

		Document doc = node.getOwnerDocument();
		DocumentFragment df = parseXMLString(doc, content);

		if ( df.hasChildNodes() ) {
			// If beforeNode is null, insertBefore does an append(),
			// which is what we need.
			node.insertBefore(df.removeChild(df.getFirstChild()), beforeNode);
			while ( df.hasChildNodes() ) {
				node.appendChild(df.removeChild(df.getFirstChild()));
			}
		}
	}
	
	public void endExtractionItem (IExtractionItem item) {
		// Set the target text
		IExtractionItem current = item.getFirstItem();
		do {
			if ( current.isTranslatable() ) {
				if ( current.hasTarget() ) {
					// Attribute case: Simply set the value of the node passed by the data
					//TODO: Redo the merging for attr Node attr = (Node)current.getData();
					String siFlag = current.getProperty("subItem");
					if ( siFlag != null ) {
						if ( siFlag.startsWith(XMLReader.ILMARKER) ) {
							// This is an item extracted from an in-line code
							// We have to wait the parent to be merged to set it
							subItems.put(current.getID(), current.getTarget());
						}
						else { // Not in in-line code, set it now
							Element elem = (Element)res.srcNode;
							Attr attr = elem.getAttributeNode(siFlag);
							if ( attr != null ) attr.setNodeValue(current.getTarget().toString());
							else {
								logger.warn(String.format("Cannot found mergeable attribute '%s' in item id='%s'",
									siFlag, current.getID()));
							}
						}
					}
					else {
						String tmp = makeXMLString(current.getTarget());
						// Merge items extracted from in-line codes if there are any
						if ( subItems.size() > 0 ) {
							Iterator<String> iter = subItems.keySet().iterator();
							while ( iter.hasNext() ) {
								String id = iter.next();
								String mark = XMLReader.ILMARKER + id;
								IContainer cont = subItems.get(id);
								if ( tmp.indexOf(mark) > -1 ) {
									//TODO: We risk double-escape if the sub-item has in-line code
									tmp = tmp.replace(mark, Util.escapeToXML(
										cont.toString(), 3, false));
								}
								else {
									// Warning: marker not found for sub-item
									logger.warn(String.format("Cannot found marker for sub-item id='%s' in item id='%s'",
										id, current.getID()));
								}
							}
							subItems.clear();
						}
						// Merge the content
						buildContent(res.srcNode, tmp);
					}
				}
			}
		}
		while ( (current = item.getNextItem()) != null );
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
		subItems = new HashMap<String, IContainer>();
	}

    public void skeletonContainer (ISkeletonResource resource) {
    }
    
	private String makeXMLString (IContainer item) {
		StringBuilder tmp = new StringBuilder();
		List<IFragment> fragList = item.getFragments();
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
		} catch ( SAXException e ) {
			// A parsing error occurred. Invalid XML
			logger.error("XML syntax error: " + e.getLocalizedMessage(), e);
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
    }
	
}
