/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package org.w3c.its;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Main {

	public static void main (String[] args) {
		XMLWriter writer = null;
		try {
			System.out.println("ITSTest");
			
			File inputFile;
			if ( args.length > 0 ) inputFile = new File(args[0]);
			else inputFile = new File("inputFile.xml"); 
			System.out.println("   input: " + inputFile.getAbsolutePath());
			
			File outputFile;
			if ( args.length > 1 ) outputFile = new File(args[1]);
			else {
				outputFile = new File(Util.getDirectoryName(inputFile.getAbsolutePath())
					+ File.separator + "nodelist-with-its-information.xml");
			}
			System.out.println("  output: " + outputFile.getAbsolutePath());

			File rulesFile = null;
			if ( args.length > 2 ) rulesFile = new File(args[2]);
			System.out.print("   rules: ");
			if ( rulesFile == null ) System.out.print("No external rules file will be used.");
			else System.out.println(rulesFile.getAbsolutePath());
			
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			fact.setValidating(false);
			
			writer = new XMLWriter(outputFile.getAbsolutePath());
			writer.setLineBreak("\n");
			writer.writeStartDocument();
			writer.writeStartElement("nodeList");
			writer.writeAttributeString("xmlns:its", ITSEngine.ITS_NS_URI);
			writer.writeAttributeString("xmlns:datc", "http://example.com/datacats");
			writer.writeStartElement("nodeList");
			writer.writeAttributeString("datacat", "TODO");
			
			Document doc = fact.newDocumentBuilder().parse(inputFile);
			ITraversal trav = applyITSRules(doc, inputFile, rulesFile);

			String path = "";
			
			// Process the document
			trav.startTraversal();
			Node node;
			while ( (node = trav.nextNode()) != null ) {
				switch ( node.getNodeType() ) {
				case Node.ELEMENT_NODE:
					// Use !backTracking() to get to the elements only once
					// and to include the empty elements (for attributes).
					if ( trav.backTracking() ) {
						int n = path.lastIndexOf('/');
						if ( n > -1 ) path = path.substring(0, n);
					}
					else {
						Element element = (Element)node;
						path += "/"+element.getLocalName();
						
						writer.writeStartElement("node");
						writer.writeAttributeString("path", path);
						writer.writeAttributeString("outputType", "TODO");
						writer.writeStartElement("output");
						
						writer.writeEndElement(); // output
						writer.writeEndElement(); // node

						if ( element.hasAttributes() ) {
							NamedNodeMap map = element.getAttributes();
							for ( int i=0; i<map.getLength(); i++ ) {
								Attr attr = (Attr)map.item(i);
								writer.writeStartElement("node");
								writer.writeAttributeString("path", path+"/@"+attr.getName());
								writer.writeAttributeString("outputType", "TODO");
								writer.writeStartElement("output");
								
								writer.writeEndElement(); // output
								writer.writeEndElement(); // node
							}
						}
					}
					break; // End switch
				}
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( writer != null ) {
				writer.writeEndElement(); // nodeList for datacat
				writer.writeEndElement(); // root nodeList
				writer.close();
			}
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
	
//	private static boolean hasTextChild (Node element) {
//		NodeList nl = element.getChildNodes();
//		for ( int i=0; i<nl.getLength(); i++ ) {
//			if ( nl.item(i).getNodeType() == Node.TEXT_NODE ) return true;
//		}
//		return false;
//	}
//	
//	private static void saveDocument (Document doc,
//		File outputFile)
//		throws TransformerFactoryConfigurationError, TransformerException, FileNotFoundException
//	{
//		Transformer trans = TransformerFactory.newInstance().newTransformer();
//		FileOutputStream output = new FileOutputStream(outputFile);
//		trans.transform(new DOMSource(doc), new StreamResult(output));
//	}

}
