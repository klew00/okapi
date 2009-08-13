package org.w3c.its;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test {

	public static void main (String[] args) {
		try {
			System.out.println("ITSTest version 1.6");
			
			File inputFile;
			if ( args.length > 0 ) inputFile = new File(args[0]);
			else inputFile = new File("input.xml"); 
			System.out.println("   input: " + inputFile.getAbsolutePath());
			
			File outputFile;
			if ( args.length > 1 ) outputFile = new File(args[1]);
			else outputFile = new File("output.xml");
			System.out.println("  output: " + outputFile.getAbsolutePath());

			File rulesFile = null;
			if ( args.length > 2 ) rulesFile = new File(args[2]);
			System.out.print("   rules: ");
			if ( rulesFile == null ) System.out.print("No external rules file will be used.");
			else System.out.println(rulesFile.getAbsolutePath());
			
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			fact.setValidating(false);
			Document doc = fact.newDocumentBuilder().parse(inputFile);
			ITraversal trav = applyITSRules(doc, inputFile, rulesFile);
			
			// Process the document:
			// Adds "+ TRANSLATION" too all elements that are translatable and have text
			// and to translatable attribute values.
			trav.startTraversal();
			Node node;
			while ( (node = trav.nextNode()) != null ) {
				switch ( node.getNodeType() ) {
				case Node.ELEMENT_NODE:
					// Use !backTracking() to get to the elements only once
					// and to include the empty elements (for attributes).
					if ( !trav.backTracking() ) {
						Element element = (Element)node;
						if ( trav.translate() && hasTextChild(node) ) { // Add only if there is text already
							element.appendChild(doc.createTextNode(" + TRANSLATION"));
						}
						if ( trav.isTerm() && hasTextChild(node) ) {
							element.appendChild(doc.createTextNode(" + TERM!"));
						}
						if ( element.hasAttributes() ) {
							NamedNodeMap map = element.getAttributes();
							Attr attr;
							for ( int i=0; i<map.getLength(); i++ ) {
								attr = (Attr)map.item(i);
								if ( trav.translate(attr) ) {
									attr.setValue(attr.getValue()+ " + TRANSLATION");
								}
								if ( trav.isTerm(attr) ) {
									attr.setValue(attr.getValue()+ " + TERM!");
								}
							}
						}
						// Show the language
						element.appendChild(doc.createTextNode(" + lang="+ trav.getLanguage()));
						// Show the idPointer
						element.appendChild(doc.createTextNode(" + idPointer="+ trav.getIdPointer()));
					}
					break;
				}
			}
			saveDocument(doc, outputFile);
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}

	private static ITraversal applyITSRules (Document doc,
		File inputFile,
		File rulesFile)
	{
		// Create the ITS engine
		ITSEngine itsEng = new ITSEngine(doc, inputFile.toURI());
		
		// Add any external rules file(s)
		if ( rulesFile != null ) {
			itsEng.addExternalRules(rulesFile.toURI());
		}
		
		// Apply the all rules (external and internal) to the document
		itsEng.applyRules(ITSEngine.DC_ALL);
		
		return itsEng;
	}
	
	private static boolean hasTextChild (Node element) {
		NodeList nl = element.getChildNodes();
		for ( int i=0; i<nl.getLength(); i++ ) {
			if ( nl.item(i).getNodeType() == Node.TEXT_NODE ) return true;
		}
		return false;
	}
	
	private static void saveDocument (Document doc,
		File outputFile)
		throws TransformerFactoryConfigurationError, TransformerException, FileNotFoundException
	{
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		FileOutputStream output = new FileOutputStream(outputFile);
		trans.transform(new DOMSource(doc), new StreamResult(output));
	}

}
