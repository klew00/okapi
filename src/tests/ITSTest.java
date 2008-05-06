package tests;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.its.IProcessor;
import net.sf.okapi.its.ITSProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ITSTest {

	public static void main (String[] args)
		throws Exception
	{
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		dbFact.setNamespaceAware(true);
		dbFact.setValidating(false);
		
		String inputPath = "C:\\Tmp\\BorneoTests\\XML\\Test01.xml";
		Document doc = dbFact.newDocumentBuilder().parse(new File(inputPath));
		
		ITSProcessor proc = new ITSProcessor(doc, inputPath);
		proc.applyRules(IProcessor.DC_ALL);
		
		traverse(proc, doc.getDocumentElement());
		
		/*
		proc.startTraversal();
		Node node;
		while ( proc.nextNode() ) {
			node = proc.getNode();
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				System.out.println(String.format("Element %s, tr=%s",
					node.getNodeName(), (proc.translate() ? "yes" : "no")));
				if ( node.hasAttributes() ){
					NamedNodeMap attrs = node.getAttributes();
					for ( int i=0; i<attrs.getLength(); i++ ) {
						System.out.println(String.format(" Attr %s tr=%s",
							attrs.item(i).getNodeName(),
							(proc.translate(attrs.item(i).getNodeName()) ? "yes" : "no")));
					}
				}
				break;
			case Node.TEXT_NODE:
				System.out.println(String.format("Text tr=%s '%s'",
					(proc.translate() ? "yes" : "no"),
					node.getTextContent()));
				break;
			}
		}
		*/
	}
	
	static void traverse (ITSProcessor proc,
		Node node) {
		if ( node == null ) return;
		
		switch ( node.getNodeType() ) {
		case Node.ELEMENT_NODE:
			System.out.print("<"+node.getNodeName());
			if ( node.hasAttributes() ) {
				NamedNodeMap attrs = node.getAttributes();
				for ( int i=0; i<attrs.getLength(); i++ ) {
					System.out.print(" "+attrs.item(i).getNodeName() + "=\""
						+ attrs.item(i).getNodeName() + "\"");
				}
			}
			System.out.print(">");
			break;
		case Node.TEXT_NODE:
			if ( proc.translate() ) 
				System.out.print("[["+node.getTextContent()+"]]");
			else
				System.out.print(node.getTextContent());
			break;
		}
		
		if ( node.hasChildNodes() ) {
			traverse(proc, node.getFirstChild());
		}
		if ( node.getNextSibling() != null ) {
			traverse(proc, node.getNextSibling());
		}
		
		switch ( node.getNodeType() ) {
		case Node.ELEMENT_NODE:
			System.out.print("</"+node.getNodeName()+">");
			break;
		}
		
	}
}
