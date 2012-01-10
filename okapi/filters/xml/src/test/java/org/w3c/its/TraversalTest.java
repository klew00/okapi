/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

import net.sf.okapi.common.TestUtil;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TraversalTest {

	private String root = TestUtil.getParentDir(this.getClass(), "/input.xml");
	//private LocaleId locEN = LocaleId.fromString("en");
	private DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();

	@Before
	public void setUp() {
		fact.setNamespaceAware(true);
		fact.setValidating(false);
	}

	@Test
	public void testSimple () throws SAXException, IOException, ParserConfigurationException {
		Document doc = fact.newDocumentBuilder().parse(root + "/input.xml");
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml"), null);
		Element elem = getElement(trav, "p", 1);
		assertNotNull(elem);
		assertTrue(trav.translate());
		elem = getElement(trav, "term", 1);
		assertNotNull(elem);
		assertFalse(trav.translate());
	}

	@Test
	public void testTerm () throws SAXException, IOException, ParserConfigurationException {
		Document doc = fact.newDocumentBuilder().parse(root + "/input.xml");
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml"), null);
		Element elem = getElement(trav, "p", 1);
		assertNotNull(elem);
		assertFalse(trav.isTerm());
		elem = getElement(trav, "term", 1);
		assertNotNull(elem);
		assertTrue(trav.isTerm());
		// This is empty because ref in id(@ref) is not defined as IDType
		// So no text is detected
		assertEquals("", trav.getTermInfo());
	}

	@Test
	public void testXmlId () throws SAXException, IOException, ParserConfigurationException {
		Document doc = fact.newDocumentBuilder().parse(root + "/input.xml");
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml"), null);
		Element elem = getElement(trav, "gloss", 1);
		assertNotNull(elem);
		assertEquals("TDPV", trav.getIdValue());
	}

	@Test
	public void testWithinText () throws SAXException, IOException, ParserConfigurationException {
		Document doc = fact.newDocumentBuilder().parse(root + "/Translate1.xml");
		ITraversal trav = applyITSRules(doc, new File(root + "/Translate1.xml"), null);
		Element elem = getElement(trav, "verbatim", 1);
		assertNotNull(elem);
		assertFalse(trav.translate());
		assertTrue(trav.getWithinText()==ITraversal.WITHINTEXT_YES);
	}

	private static Element getElement (ITraversal trav,
		String name,
		int number)
	{
		trav.startTraversal();
		Node node;
		int count = 0;
		while ( (node = trav.nextNode()) != null ) {
			switch ( node.getNodeType() ) {
			case Node.ELEMENT_NODE:
				if ( !trav.backTracking() ) {
					if ( node.getNodeName().equals(name) ) {
						if ( ++count == number ) return (Element)node;
					}
				}
				break;
			}
		}
		return null;
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

}
