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
						System.out.println(String.format(" Attr %s",
							attrs.item(i).getNodeName()));
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

	}
}
