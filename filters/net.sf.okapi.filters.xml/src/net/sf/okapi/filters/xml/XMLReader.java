package net.sf.okapi.filters.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

import org.w3c.dom.Node;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.w3c.its.ITraversal;

public class XMLReader {

	public static final int       RESULT_ENDINPUT          = 0;
	public static final int       RESULT_STARTGROUP        = 1;
	public static final int       RESULT_ENDGROUP          = 2;
	public static final int       RESULT_STARTTRANSUNIT    = 3;
	public static final int       RESULT_ENDTRANSUNIT      = 4;

	protected Resource       resource;
	
	private boolean          needNewItem;
	private IExtractionItem  item;
	private IContainer       content;
	private int              codeID;
	private Node             node;
	private ITSEngine        itsProc;
	
	
	public XMLReader () {
	}
	
	public void open (InputStream input,
			String inputName)
	{
		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			fact.setValidating(false);
			resource = new Resource();
			resource.setName(inputName);
			resource.doc = fact.newDocumentBuilder().parse(input);
			node = resource.doc.getDocumentElement();
			applyITSRules();
			itsProc.startTraversal();
			content = new Container();
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public IExtractionItem getItem () {
		return item;
	}

	public int read () {
		if ( needNewItem ) {
			createNewItem();
			needNewItem = false;
		}

		while ( true ) {
			if ( (node = itsProc.nextNode()) == null ) {
				return RESULT_ENDINPUT; // Document is done
			}
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				if ( itsProc.backTracking() ) {
					// Was it an in-line element?
					switch ( itsProc.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						content.append(new CodeFragment(IContainer.CODE_CLOSING, codeID,
							node.getNodeValue()));
						break;
					default:
						//TODO: Nested case
						if ( !content.isEmpty() ) {
							return RESULT_ENDTRANSUNIT;
						}
						break;
					}
				}
				else { // We start a new element
					switch ( itsProc.getWithinText() ) {
					case ITraversal.WITHINTEXT_YES:
						content.append(new CodeFragment(IContainer.CODE_OPENING, ++codeID,
							node.getNodeValue()));
						break;
					//case itsProc.WITHINTEXT_NESTED:
						//TODO
					default:
						// Finish previous item if needed
						if ( !content.isEmpty() ) {
							needNewItem = true;
							return RESULT_ENDTRANSUNIT;
						}
						break;
					}
					// IF there is nothing to send, then start new item now
					createNewItem();
					return RESULT_STARTTRANSUNIT;
				}
				//break;
			case Node.TEXT_NODE:
				if ( itsProc.translate() ) {
					content.append(node.getNodeValue());
				}
				else {
					content.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
						node.getNodeValue()));
				}
				break;
			}
		}
	}

	
	private void createNewItem () {
		item = new ExtractionItem();
		item.setType(node.getLocalName());
		content = item.getContent();
		codeID = 0;
	}
	
	private void applyITSRules () {
		itsProc = new ITSEngine(resource.doc, resource.getName());
		
		// Add any external rules file(s)
		//TODO: Get the info from the parameters
		
		// Apply the all rules (external and internal)
		itsProc.applyRules(ITSEngine.DC_LANGINFO | ITSEngine.DC_TRANSLATE
			| ITSEngine.DC_WITHINTEXT);
	}
	
}
