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
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Main {

	public static final String DC_TRANSLATE = "translate";
	public static final String DC_IDVALUE = "idvalue";

	public static void main (String[] args) {
 
	XMLWriter writer = null;
		try {
			System.out.println("ITSTest");
			
			File inputFile = null;
			File outputFile = null;
			File rulesFile = null;
			String dc = "translate";
			
			if ( args.length == 0 ) inputFile = new File("inputFile.xml");
			else for ( int i=0; i<args.length; i++ ) {
				String arg = args[i];
				if ( arg.equals("-r") ) { // External rule file
					i++; rulesFile = new File(args[i]);
				}
				else if ( arg.equals("-dc") ) { // Data category
					i++; dc = args[i];
				}
				else {
					if ( inputFile == null ) {
						inputFile = new File(args[i]);
					}
					else {
						outputFile = new File(args[i]);
					}
				}
			}
			
			// Default output
			if ( outputFile == null ) {
				String ext = Util.getExtension(inputFile.getAbsolutePath());
				String name = inputFile.getAbsolutePath();
				int n = name.lastIndexOf('.');
				if ( n > -1 ) name = name.substring(0, n);
				name += "output";
				name += ext;
				outputFile = new File(name);
			}
			
			// Trace
			System.out.println("   input: " + inputFile.getAbsolutePath());
			System.out.println("  output: " + outputFile.getAbsolutePath());
			if ( rulesFile != null ) {
				System.out.print("   rules: " + rulesFile.getAbsolutePath());
			}
			
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
			writer.writeAttributeString("datacat", dc);
			
			Document doc = fact.newDocumentBuilder().parse(inputFile);
			ITraversal trav = applyITSRules(doc, inputFile, rulesFile);

			String path = null;
			Stack<Integer> stack = new Stack<Integer>();
			stack.push(1);
			
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
						stack.pop();
					}
					else {
						Element element = (Element)node;
						
						// Get the previous sibling element (if any)
						Node prev = element;
						do {
							prev = prev.getPreviousSibling();
						}
						while (( prev != null ) && ( prev.getNodeType() != node.ELEMENT_NODE ));

						// If it's the same kind of element, we increment the counter
						if (( prev != null ) && prev.getNodeName().equals(element.getNodeName()) ) { 
							stack.push(stack.peek()+1);
						}
						else {
							stack.push(1);
						}
						
						// If it's a sibling element, remove the other sibling before appending
						if ( prev != null ) {
							int n = path.lastIndexOf('/');
							if ( n > -1 ) path = path.substring(0, n);
						}
						if ( element == doc.getDocumentElement() ) {
							path = "/"+element.getNodeName();
						}
						else {
							path += String.format("/%s[%d]", element.getNodeName(), stack.peek());
						}

						// Gather and output the values for the element
						output(writer, dc, path, trav, null);

						if ( element.hasAttributes() ) {
							NamedNodeMap map = element.getAttributes();
							for ( int i=0; i<map.getLength(); i++ ) {
								Attr attr = (Attr)map.item(i);
								if ( attr.getNodeName().startsWith("xmlns:") ) continue; // Skip NS declarations
								// gather and output the values for the attribute
								output(writer, dc, path+"/@"+attr.getNodeName(), trav, attr);
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

	private static void output (XMLWriter writer,
		String dc,
		String path,
		ITraversal trav,
		Attr attr)
	{
		// Get the values for the give data category
		String outType = "no-value";
		String out1 = null;
		if ( dc.equals(DC_IDVALUE) ) {
			out1 = trav.getIdValue();
			if ( out1 != null ) outType = "cannot-detect";
		}
		else if ( dc.equals(DC_TRANSLATE) ) {
			if ( attr != null ) out1 = (trav.translate(attr) ? "yes" : "no");
			else out1 = (trav.translate() ? "yes" : "no");
		}

		writer.writeStartElement("node");
		writer.writeAttributeString("path", path);
		writer.writeAttributeString("outputType", outType);
		writer.writeStartElement("output");
		
		if ( dc.equals(DC_IDVALUE) ) {
			if ( out1 != null ) writer.writeAttributeString("idValue", out1);
		}
		else if ( dc.equals(DC_TRANSLATE) ) {
			if ( out1 != null ) writer.writeAttributeString("translate", out1);
		}
		
		writer.writeEndElement(); // output
		writer.writeEndElement(); // node
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
