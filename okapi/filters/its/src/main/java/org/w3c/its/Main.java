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
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.filters.its.html5.HTML5Filter;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Main entry point for generating test files for ITS.
 */
public class Main {

	public static final String DC_TRANSLATE = "translate";
	public static final String DC_LOCALIZATIONNOTE = "locnote";
	public static final String DC_TERMINOLOGY = "terminology";
	public static final String DC_DIRECTIONALITY = "dir";
	public static final String DC_LANGUAGEINFORMATION = "lang";
	public static final String DC_WITHINTEXT = "withintext";
	public static final String DC_DOMAIN = "domain";
	public static final String DC_LOCALEFILTER = "localefilter";
	public static final String DC_EXTERNALRESOURCE = "externalresource";
	public static final String DC_TARGETPOINTER = "targetpointer";
	public static final String DC_IDVALUE = "idvalue";
	public static final String DC_PRESERVESPACE = "preservespace";
	public static final String DC_LOCQUALITYISSUE = "locqualityissue";
	public static final String DC_STORAGESIZE = "storagesize";

	public static void main (String[] args) {
 
		PrintWriter writer = null;
		
		try {
			System.out.println("ITSTest - Test File Geneator for XML+ITS and HTML5+ITS");
			
			File inputFile = null;
			File outputFile = null;
			File rulesFile = null;
			String dc = "translate";
			boolean isHTML5 = false;
			
			for ( int i=0; i<args.length; i++ ) {
				String arg = args[i];
				if ( arg.equals("-r") ) { // External rule file
					i++; rulesFile = new File(args[i]);
				}
				else if ( arg.equals("-dc") ) { // Data category
					i++; dc = args[i].toLowerCase();
				}
				else if ( arg.equals("-?") ) {
					showUsage();
					return;
				}
				else if ( arg.equals("-l") ) {
					System.out.println(DC_TRANSLATE
						+ "\n" + DC_LOCALIZATIONNOTE
						+ "\n" + DC_TERMINOLOGY
						+ "\n" + DC_DIRECTIONALITY
						+ "\n" + DC_LANGUAGEINFORMATION
						+ "\n" + DC_WITHINTEXT
						+ "\n" + DC_DOMAIN
						+ "\n" + DC_LOCALEFILTER
						+ "\n" + DC_EXTERNALRESOURCE
						+ "\n" + DC_TARGETPOINTER
						+ "\n" + DC_IDVALUE
						+ "\n" + DC_PRESERVESPACE
						+ "\n" + DC_LOCQUALITYISSUE
						+ "\n" + DC_STORAGESIZE
					);
					return;
				}
				else {
					if ( inputFile == null ) {
						inputFile = new File(args[i]);
						isHTML5 = Util.getExtension(args[i]).toLowerCase().startsWith(".htm");
					}
					else {
						outputFile = new File(args[i]);
					}
				}
			}
			
			if ( inputFile == null ) {
				showUsage();
				return;
			}
			
			// Default output
			if ( outputFile == null ) {
				//String ext = Util.getExtension(inputFile.getAbsolutePath());
				String name = inputFile.getAbsolutePath();
				int n = name.lastIndexOf('.');
				if ( n > -1 ) name = name.substring(0, n);
				name += "output";
				name += ".txt";
				outputFile = new File(name);
			}
			
			// Trace
			System.out.println("   input: " + inputFile.getAbsolutePath());
			System.out.println("  output: " + outputFile.getAbsolutePath());
			if ( rulesFile != null ) {
				System.out.print("   rules: " + rulesFile.getAbsolutePath());
			}
			
			Util.createDirectories(outputFile.getAbsolutePath());
			writer = new PrintWriter(outputFile.getAbsolutePath(), "UTF-8");
			
			// Read the document
			Document doc;
			if ( isHTML5 ) {
				HtmlDocumentBuilder docBuilder = new HtmlDocumentBuilder();
				doc = docBuilder.parse(inputFile);
			}
			else {
				DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
				fact.setNamespaceAware(true);
				fact.setValidating(false);
				doc = fact.newDocumentBuilder().parse(inputFile);
			}
			
			// Applies the rules
			ITraversal trav = applyITSRules(doc, inputFile, rulesFile, isHTML5);

			String path = null;
			Stack<Integer> stack = new Stack<Integer>();
			stack.push(1);
			int prevCount = 0;
			
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
						prevCount = stack.pop();
					}
					else {
						Element element = (Element)node;
						// Get the previous sibling element (if any)
						Node prev = element;
						do {
							prev = prev.getPreviousSibling();
						}						
						while (( prev != null ) && 
								(( prev.getNodeType() != Node.ELEMENT_NODE ) || 
								(( prev.getNodeType() == Node.ELEMENT_NODE )  && ( !prev.getNodeName().equals(element.getNodeName())))));

						// If it's the same kind of element, we increment the counter
						if (( prev != null ) && prev.getNodeName().equals(element.getNodeName()) ) { 
							stack.push(prevCount+1);
						}
						else {
							stack.push(1);
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
							
							ArrayList<String> list = new ArrayList<String>();
							for ( int i=0; i<map.getLength(); i++ ) {
								list.add(((Attr)map.item(i)).getNodeName());
							}
							Collections.sort(list);
							
							for ( String attrName : list ) {
								Attr attr = (Attr)map.getNamedItem(attrName);
								if ( attr.getNodeName().startsWith("xmlns:") ) continue; // Skip NS declarations
								// gather and output the values for the attribute
								output(writer, dc, path+"/@"+attr.getNodeName(), trav, attr);
							}
						}
						
						// Empty elements:
						if ( !element.hasChildNodes() ) {
							int n = path.lastIndexOf('/');
							if ( n > -1 ) path = path.substring(0, n);
							prevCount = stack.pop();
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
				writer.flush();
				writer.close();
			}
		}
	}

	private static void showUsage () {
		System.out.println("Usage: <inputFile>[ <outputFile>][ <options>]");
		System.out.println("Where the options are:");
		System.out.println(" -? shows this help");
		System.out.println(" -r <ruleFile> : associates the input with an ITS rule file");
		System.out.println(" -l : lists all the avaibale data categories to use with -dc");
		System.out.println(" -dc <data-category> : sets the data category to process (default=translate)");
	}
	
	private static void output (PrintWriter writer,
		String dc,
		String path,
		ITraversal trav,
		Attr attr)
	{
		// Path
		writer.print(path);

		// Values
		String out1 = null;
		if ( dc.equals(DC_TRANSLATE) ) {
			out1 = (trav.getTranslate(attr) ? "yes" : "no");
			writer.print(String.format("\tits:translate=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_LOCALIZATIONNOTE) ) {
			out1 = trav.getLocNote(attr);
			if ( out1 != null ) {
				writer.print(String.format("\tits:locNote=\"%s\"", escape(out1)));
				out1 = trav.getLocNoteType(attr);
				writer.print(String.format("\tits:locNoteType=\"%s\"", escape(out1)));
			}
		}
		else if ( dc.equals(DC_TERMINOLOGY) ) {
			out1 = (trav.getTerm(attr) ? "yes" : "no");
			if ( out1 != null ) writer.print(String.format("\tits:term=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getTermInfo(attr);
			if ( out1 != null ) writer.print(String.format("\tits:termInfo=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_DIRECTIONALITY) ) {
			int dir = trav.getDirectionality(attr);
			switch ( dir ) {
			case ITraversal.DIR_LRO: out1 = "lro"; break;
			case ITraversal.DIR_LTR: out1 = "ltr"; break;
			case ITraversal.DIR_RLO: out1 = "rlo"; break;
			case ITraversal.DIR_RTL: out1 = "rtl"; break;
			}
			writer.print(String.format("\tits:dir=\"%s\"",
				escape(out1)));
		}
		else if ( dc.equals(DC_LANGUAGEINFORMATION) ) {
			out1 = trav.getLanguage();
			if ( out1 != null ) writer.print(String.format("\tits:lang=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_WITHINTEXT) ) {
			if ( attr != null ) return;
			int wt = trav.getWithinText();
			switch ( wt ) {
			case ITraversal.WITHINTEXT_NESTED: out1 = "nested"; break;
			case ITraversal.WITHINTEXT_NO: out1 = "no"; break;
			case ITraversal.WITHINTEXT_YES: out1 = "yes"; break;
			}
			writer.print(String.format("\tits:withinText=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_DOMAIN) ) {
			out1 = trav.getDomains(attr);
			if ( out1 != null ) writer.print(String.format("\tits:domains=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_LOCALEFILTER) ) {
			out1 = trav.getLocaleFilter();
			if ( out1 != null ) writer.print(String.format("\tits:localeFilterList=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_EXTERNALRESOURCE) ) {
			out1 = trav.getExternalResourceRef(attr);
			if ( out1 != null ) writer.print(String.format("\tits:externalResource=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_TARGETPOINTER) ) {
			out1 = trav.getTargetPointer(attr);
			if ( out1 != null ) writer.print(String.format("\tits:targetPointer=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_IDVALUE) ) {
			out1 = trav.getIdValue(attr);
			if ( out1 != null ) writer.print(String.format("\tits:idValue=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_PRESERVESPACE) ) {
			out1 = (trav.preserveWS() ? "preserve" : "default");
			if ( out1 != null ) writer.print(String.format("\tits:preserveSpace=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_LOCQUALITYISSUE) ) {
			//TODO attributes
			out1 = trav.getLocQualityIssuesRef();
			if ( out1 != null ) writer.print(String.format("\tits:locQualityIssuesRef=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getLocQualityIssueType();
			if ( out1 != null ) writer.print(String.format("\tits:locQualityIssueType=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getLocQualityIssueComment();
			if ( out1 != null ) writer.print(String.format("\tits:locQualityIssueComment=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getLocQualityIssueSeverity();
			if ( out1 != null ) writer.print(String.format("\tits:locQualityIssueSeverity=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getLocQualityIssueProfileRef();
			if ( out1 != null ) writer.print(String.format("\tits:locQualityIssueProfileRef=\"%s\"", escape(out1)));
		}
		else if ( dc.equals(DC_STORAGESIZE) ) {
			out1 = trav.getStorageSize(attr);
			if ( out1 != null ) writer.print(String.format("\tits:storageSize=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getStorageEncoding(attr);
			if ( out1 != null ) writer.print(String.format("\tits:storageEncoding=\"%s\"", escape(out1)));
			writer.print("\t");
			out1 = trav.getLineBreakType(attr);
			if ( out1 != null ) writer.print(String.format("\tits:lineBreakType=\"%s\"", escape(out1)));
		}
		
		writer.print("\n");
	}
	
	private static String escape (String text) {
		text = text.replace("\n", "\\n");
		text = text.replace("\t", "\\t");
		return Util.escapeToXML(text, 3, false, null);
	}
	
	private static ITraversal applyITSRules (Document doc,
		File inputFile,
		File rulesFile,
		boolean isHTML5)
	{
		// Create the ITS engine
		ITSEngine itsEng = new ITSEngine(doc, inputFile.toURI(), isHTML5, null);
		
		// For HTML5: load the default rules
		if ( isHTML5 ) {
			URL url = HTML5Filter.class.getResource("default.fprm");
			try {
				itsEng.addExternalRules(url.toURI());
			}
			catch ( URISyntaxException e ) {
				throw new OkapiBadFilterParametersException("Cannot load default parameters.");
			}
		}

		// Add any external rules file(s)
		if ( rulesFile != null ) {
			itsEng.addExternalRules(rulesFile.toURI());
		}
		
		// Load the linked rules for HTML
		if ( isHTML5 ) {
			HTML5Filter.loadLinkedRules(doc, inputFile.toURI(), itsEng);
		}
		
		// Apply the all rules (external and internal) to the document
		itsEng.applyRules(ITSEngine.DC_ALL);
		
		return itsEng;
	}
	
}