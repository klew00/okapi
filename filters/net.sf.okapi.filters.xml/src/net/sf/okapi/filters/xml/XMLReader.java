package net.sf.okapi.filters.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

import org.w3c.dom.Node;
import org.w3c.its.ITSEngine;

public class XMLReader {

	public static final int       RESULT_ENDINPUT          = 0;
	public static final int       RESULT_STARTGROUP        = 1;
	public static final int       RESULT_ENDGROUP          = 2;
	public static final int       RESULT_STARTTRANSUNIT    = 3;
	public static final int       RESULT_ENDTRANSUNIT      = 4;

	protected Resource       resource;
	
	private IExtractionItem  sourceItem;
	private IExtractionItem  targetItem;
	private int              lastResult;
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
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public IExtractionItem getSourceItem () {
		return sourceItem;
	}

	public IExtractionItem getTargetItem () {
		return targetItem;
	}
	
	private void resetItems () {
		sourceItem = new ExtractionItem();
		targetItem = null;
	}

	public int read () {
		boolean append = false;
		IContainer cont = null;
		int codeID = 0;
		while ( true ) {
			if ( (node = itsProc.nextNode()) == null ) {
				return (lastResult = RESULT_ENDINPUT); // Document is done
			}
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				if ( !itsProc.translate() ) continue;
				if ( itsProc.backTracking() ) {
					return (lastResult = RESULT_ENDTRANSUNIT);
				}
				else {
					resetItems();
					cont = sourceItem.getContent();
					sourceItem.setName(node.getLocalName());
					append = true;
					return (lastResult = RESULT_STARTTRANSUNIT);
				}
				//break;
			case Node.TEXT_NODE:
				if ( !append ) continue;
				if ( itsProc.translate() ) {
					cont.append(node.getNodeValue());
				}
				else {
					cont.append(new CodeFragment(IContainer.CODE_ISOLATED, ++codeID,
						node.getNodeValue()));
				}
			}
		}
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
