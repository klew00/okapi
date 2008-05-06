package net.sf.okapi.its;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ITSProcessor implements IProcessor  
{
	public static final String    ITS_NS_URI     = "http://www.w3.org/2005/11/its";
	public static final String    ITS_NS_PREFIX  = "its";
	public static final String    XLINK_NS_URI   = "http://www.w3.org/1999/xlink";
	
	static final String           FLAGNAME       = "\u00ff";
	static final String           FLAGSEP        = "|";

	static final int              FP_TRANSLATE        = 0;
	static final int              FP_DIRECTIONALITY   = 1;

	private DocumentBuilderFactory     dbFact; 
	private Document                   doc;
	private String                     docPath;
	private NSContextManager           nsContext;
	private XPathFactory               xpFact;
	private XPath                      xpath;
	private ArrayList<ITSRule>         rules;
	private Node                       node;
	private boolean                    startTraversal;
	private Stack<ITSTrace>            trace;
	
	public ITSProcessor (Document doc,
		String docPath) {
		this.doc = doc;
		this.docPath = docPath;
		node = null;
		rules = new ArrayList<ITSRule>();
		nsContext = new NSContextManager();
		nsContext.addNamespace(ITS_NS_PREFIX, ITS_NS_URI);
		xpFact = XPathFactory.newInstance();
		xpath = xpFact.newXPath();
		xpath.setNamespaceContext(nsContext);
		dbFact = DocumentBuilderFactory.newInstance();
		dbFact.setNamespaceAware(true);
		dbFact.setValidating(false);
	}

	public void addExternalRules (String docPath)
		throws Exception {
		try {
			Document rulesDoc = dbFact.newDocumentBuilder().parse(new File(docPath));
			addExternalRules(rulesDoc, docPath);
		}
		catch ( Exception E ) {
			throw E;
		}
	}

	public void addExternalRules (Document rulesDoc,
		String docPath)
		throws Exception {
		compileRules(rulesDoc, docPath, false);
	}
	
	private void compileRules (Document rulesDoc,
		String docPath,
		boolean isInternal)
		throws Exception {
		try {
			// Compile the namespaces
			XPathExpression expr = xpath.compile("//*[@selector]//namespace::*");
			NodeList NL = (NodeList)expr.evaluate(rulesDoc, XPathConstants.NODESET);
			for ( int i=0; i<NL.getLength(); i++ ) {
				String prefix = NL.item(i).getLocalName();
				if ( "xml".equals(prefix) ) continue; // Set by default
				String uri = NL.item(i).getNodeValue();
				nsContext.addNamespace(prefix, uri);
			}
			
			// Compile the rules
			// First: get the its:rules element(s)
			expr = xpath.compile("//"+ITS_NS_PREFIX+":rules");
			NL = (NodeList)expr.evaluate(rulesDoc, XPathConstants.NODESET);
			if ( NL.getLength() == 0 ) return; // Nothing to do
			
			// Process each its:rules element
			Element rulesElem;
			for ( int i=0; i<NL.getLength(); i++ ) {
				rulesElem = (Element)NL.item(i);
				//TODO: Check version

				//TODO: load linked rules
				// Check for link
				String href = rulesElem.getAttributeNS(XLINK_NS_URI, "href");
				if ( href.length() > 0 ) {
					//String id = null;
					int n = href.lastIndexOf('#');
					if ( n > -1 ) {
						//id = href.substring(n+1);
						href = href.substring(0, n);
					}

					// xlink:href allows the use of xml:base so we need to calculate it
					// The initial base is the folder of the current document
					File tmpFile = new File(docPath);
					String baseFolder = tmpFile.getPath();
					// Then we look for the last xml:base specified
					Node node = rulesElem;
					while ( node != null ) {
						if ( node.getNodeType() == Node.ELEMENT_NODE ) {
							//TODO: Relative path with ../../ constructs
							String xmlBase = ((Element)node).getAttribute("xml:base");
							if ( xmlBase.length() > 0 ) {
								if ( xmlBase.endsWith(File.separator) )
									xmlBase = xmlBase.substring(0, xmlBase.length()-1);
								if ( !baseFolder.startsWith(File.separator) )
									baseFolder = xmlBase + File.separator + baseFolder;
								else
									baseFolder = xmlBase + baseFolder;
							}
						}
						node = node.getParentNode(); // Back-track to parent
					}
					if ( baseFolder.length() > 0 ) {
						if ( baseFolder.endsWith(File.separator) )
							baseFolder = baseFolder.substring(0, baseFolder.length()-1);
						if ( !href.startsWith("/") ) href = baseFolder + File.separator + href;
						else href = baseFolder + href;
					}

					// Load the document and the rules
					loadLinkedRules(href, isInternal);
				}

				// Process each rule inside its:rules
				expr = xpath.compile("//"+ITS_NS_PREFIX+":*");
				NodeList NL2 = (NodeList)expr.evaluate(rulesElem, XPathConstants.NODESET);
				if ( NL2.getLength() == 0 ) break; // Nothing to do, move to next its:rules
				
				Element ruleElem;
				for ( int j=0; j<NL2.getLength(); j++ ) {
					ruleElem = (Element)NL2.item(j);
					if ( "translateRule".equals(ruleElem.getLocalName()) ) {
						compileTranslateRule(ruleElem, isInternal);
					}
					else if ( "irRule".equals(ruleElem.getLocalName()) ) {
						compileDirRule(ruleElem, isInternal);
					}
				}
			}
			
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	private void loadLinkedRules (String docPath,
		boolean isInternal)
		throws Exception {
		Document rulesDoc = dbFact.newDocumentBuilder().parse(new File(docPath));
		compileRules(rulesDoc, docPath, isInternal);
	}
	
	private void compileTranslateRule (Element elem,
		boolean isInternal)
		throws Exception {
			ITSRule rule = new ITSRule();
			rule.selector = elem.getAttribute("selector");
			String value = elem.getAttribute("translate");
			if ( "yes".equals(value) ) rule.translate = true;
			else if ( "yes".equals(value) ) rule.translate = false;
			else throw new Exception("Invalid value for 'translate'.");
			rule.ruleType = IProcessor.DC_TRANSLATE;
			rule.isInternal = isInternal;
			rules.add(rule);
		}

	private void compileDirRule (Element elem,
		boolean isInternal)
		throws Exception {
			ITSRule rule = new ITSRule();
			rule.selector = elem.getAttribute("selector");
			String value = elem.getAttribute("dir");
			if ( "ltr".equals(value) ) rule.dir = DIR_LTR;
			else if ( "rtl".equals(value) ) rule.dir = DIR_RTL;
			else if ( "lro".equals(value) ) rule.dir = DIR_LRO;
			else if ( "rlo".equals(value) ) rule.dir = DIR_RLO;
			else throw new Exception("Invalid value for 'dir'.");
			rule.ruleType = IProcessor.DC_DIRECTIONALITY;
			rule.isInternal = isInternal;
			rules.add(rule);
		}

	public void applyRules (int dataCategories)
		throws Exception {
		try {
			processGlobalRules(dataCategories);
			processLocalRules(dataCategories);
		}
		catch ( Exception E ) {
			throw E;
		}
	}

	private void removeFlag (Node node) {
		//TODO: Any possible optimization, instead of using recursive calls
		if ( node == null ) return;
		node.setUserData(FLAGNAME, null, null);
		if ( node.hasChildNodes() )
			removeFlag(node.getFirstChild());
		if ( node.getNextSibling() != null )
			removeFlag(node.getNextSibling());
	}
	
	public void disapplyRules () {
		removeFlag(doc.getDocumentElement());
	}

	public Node getNode () {
		return node;
	}
	
	public boolean nextNode () {
		// Check for start or end
		if ( startTraversal ) {
			startTraversal = false;
			// Set the initial trace with default behaviors
			ITSTrace startTrace = new ITSTrace();
			startTrace.translate = true;
			startTrace.isChildDone = false;
			trace.push(startTrace);
			node = doc.getDocumentElement();
			// Overwrite any default behaviors if needed
			return updateTraceData(node);
		}
		if ( node == null ) {
			return false;
		}

		while ( true ) {
			// Check for child
			if ( !trace.peek().isChildDone && node.hasChildNodes() ) {
				trace.peek().isChildDone = true;
				trace.push(new ITSTrace(trace.peek(), false));
				node = node.getFirstChild();
				return updateTraceData(node);
			}
			
			// Check for sibling
			if ( node.getNextSibling() != null ) {
				trace.pop(); // Remove previous sibling
				trace.push(new ITSTrace(trace.peek(), false));
				node = node.getNextSibling();
				return updateTraceData(node);
			}

			// Else: move back to parent
			trace.pop();
			node = node.getParentNode();
			if (( node == null ) || ( trace.empty() )) return false;
		}
	}
	
	/**
	 * Updates the trace stack.
	 * @param newNode Node to update 
	 * @return False if the node passed as parameter is null, true otherwise.
	 */
	private boolean updateTraceData (Node newNode) {
		// Check if the node is null
		if ( newNode == null ) return false;

		// Get the flag data
		String data = (String)newNode.getUserData(FLAGNAME);
		
		// If this node has no ITS flags, then we leave the current states
		// as they are. They have been set by inheritance.
		if ( data == null ) return true;
		
		// Otherwise: see if there are any flags to change
		if ( data.charAt(FP_TRANSLATE) != '?' )
			trace.peek().translate = (data.charAt(FP_TRANSLATE) == 'y');
		if ( data.charAt(FP_DIRECTIONALITY) != '?' ) {
			switch ( data.charAt(FP_DIRECTIONALITY) ) {
			case '0':
				trace.peek().dir = DIR_LTR;
				break;
			case '1':
				trace.peek().dir = DIR_RTL;
				break;
			case '2':
				trace.peek().dir = DIR_LRO;
				break;
			case '3':
				trace.peek().dir = DIR_LRO;
				break;
			}
		}
		return true;
	}

	public void startTraversal () {
		node = null;
		trace = new Stack<ITSTrace>();
		//trace.push(new ITSTrace(true)); // For root #document
		startTraversal = true;
	}
	
	private void clearInternalGlobalRules () {
		for ( int i=0; i<rules.size(); i++ ) {
			if ( rules.get(i).isInternal ) {
				rules.remove(i);
				i--;
			}
		}
	}

	private void processGlobalRules (int dataCategories)
		throws Exception
	{
		// Compile any internal global rules
		clearInternalGlobalRules();
		compileRules(doc, docPath, true);
		
		// Now apply the compiled rules
	    for ( ITSRule rule : rules ) {
	    	// Check if we should apply this type of rule
	    	if ( (dataCategories & rule.ruleType) == 0 ) continue;
	    	
	    	// Get the selected nodes for the rule
			XPathExpression expr = xpath.compile(rule.selector);
			NodeList NL = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			// Apply the rule specific action on the selected nodes
			for ( int i=0; i<NL.getLength(); i++ ) {
				switch ( rule.ruleType ) {
				case IProcessor.DC_TRANSLATE:
					setFlag(NL.item(i), FP_TRANSLATE,
						(rule.translate ? 'y' : 'n'));
					break;
				case IProcessor.DC_DIRECTIONALITY:
					setFlag(NL.item(i), FP_DIRECTIONALITY,
						String.format("%d", rule.dir).charAt(0));
					break;
				}
			}
	    }
		
	}
	
	private void processLocalRules (int dataCategories)
		throws XPathExpressionException, Exception
	{
		if ( (dataCategories & IProcessor.DC_TRANSLATE) > 0 ) {
			XPathExpression expr = xpath.compile("//*/@"+ITS_NS_PREFIX+":translate|//"+ITS_NS_PREFIX+":span/@translate");
			NodeList NL = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			Attr attr;
			for ( int i=0; i<NL.getLength(); i++ ) {
				attr = (Attr)NL.item(i);
				// Skip irrelevant nodes
				if ( ITS_NS_URI.equals(attr.getOwnerElement().getNamespaceURI())
					&& "translateRule".equals(attr.getOwnerElement().getLocalName()) ) continue;
				// Set the flag on the others
				//TODO: Validate values
				setFlag(attr.getOwnerElement(), FP_TRANSLATE,
					(attr.getValue().equals("yes") ? 'y' : 'n'));
			}
		}
		
		if ( (dataCategories & IProcessor.DC_DIRECTIONALITY) > 0 ) {
			XPathExpression expr = xpath.compile("//*/@"+ITS_NS_PREFIX+":dir|//"+ITS_NS_PREFIX+":span/@dir");
			NodeList NL = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			Attr attr;
			for ( int i=0; i<NL.getLength(); i++ ) {
				attr = (Attr)NL.item(i);
				// Skip irrelevant nodes
				if ( ITS_NS_URI.equals(attr.getOwnerElement().getNamespaceURI())
					&& "dirRule".equals(attr.getOwnerElement().getLocalName()) ) continue;
				// Set the flag on the others
				int n = DIR_LTR;
				if ( "rtl".equals(attr.getValue()) ) n = DIR_LTR; 
				else if ( "ltr".equals(attr.getValue()) ) n = DIR_RTL; 
				else if ( "rlo".equals(attr.getValue()) ) n = DIR_RLO; 
				else if ( "lro".equals(attr.getValue()) ) n = DIR_LRO;
				else throw new Exception("Invalid value for 'dir'."); 
				setFlag(attr.getOwnerElement(), FP_DIRECTIONALITY,
					String.format("%d", n).charAt(0));
			}
		}
	}

	private void setFlag (Node node,
		int position,
		char value)
	{
		StringBuilder data = new StringBuilder();
		if ( node.getUserData(FLAGNAME) == null )
			data.append("????"+FLAGSEP);
		else
			data.append((String)node.getUserData(FLAGNAME));
		data.setCharAt(position, value);
		node.setUserData(FLAGNAME, data.toString(), null);
	}

	public boolean translate () {
		return trace.peek().translate;
	}
	
	public boolean translate (String attrName) {
		//TODO
		return false;
	}
	
	public int getDirectionality () {
		return trace.peek().dir;
	}

	public int getDirectionality (String attrName) {
		//TODO
		return DIR_LTR;
	}
}
