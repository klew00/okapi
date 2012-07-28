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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml").toURI(), null);
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
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml").toURI(), null);
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
		ITraversal trav = applyITSRules(doc, new File(root + "/input.xml").toURI(), null);
		Element elem = getElement(trav, "gloss", 1);
		assertNotNull(elem);
		assertEquals("TDPV", trav.getIdValue());
	}

	@Test
	public void testWithinText () throws SAXException, IOException, ParserConfigurationException {
		Document doc = fact.newDocumentBuilder().parse(root + "/Translate1.xml");
		ITraversal trav = applyITSRules(doc, new File(root + "/Translate1.xml").toURI(), null);
		Element elem = getElement(trav, "verbatim", 1);
		assertNotNull(elem);
		assertFalse(trav.translate());
		assertTrue(trav.getWithinText()==ITraversal.WITHINTEXT_YES);
	}

	@Test
	public void testDomainGlobal () throws SAXException, IOException, ParserConfigurationException {
		InputSource is = new InputSource(new StringReader("<doc>"
			+ "<i:rules xmlns:i='"+ITSEngine.ITS_NS_URI+"' version='2.0'>"
			+ "<i:domainRule selector='//doc' domainPointer='head/subject' "
			+ " domainMapping=\"dom1 finalDom1, 'dom2 val' 'final dom2'\"/>"
			+ "</i:rules>"
			+ "<head><subject>dom1</subject><subject>dom2 val</subject></head><p>text</p></doc>"));
		Document doc = fact.newDocumentBuilder().parse(is);
		ITraversal trav = applyITSRules(doc, null, null);
		Element elem = getElement(trav, "doc", 1);
		assertEquals("finalDom1, final dom2", trav.getDomain());
		elem = getElement(trav, "head", 1);
		assertEquals("finalDom1, final dom2", trav.getDomain());
		elem = getElement(trav, "p", 1);
		assertEquals("finalDom1, final dom2", trav.getDomain());
	}

	@Test
	public void testTargetPointerGlobal () throws SAXException, IOException, ParserConfigurationException {
		InputSource is = new InputSource(new StringReader("<doc>"
			+ "<i:rules xmlns:i='"+ITSEngine.ITS_NS_URI+"' version='2.0'>"
			+ "<i:targetPointerRule selector='//entry/src' targetPointer='../trg'/>"
			+ "</i:rules>"
			+ "<entry><src>source</src><trg></trg></entry></doc>"));
		Document doc = fact.newDocumentBuilder().parse(is);
		ITraversal trav = applyITSRules(doc, null, null);
		Element elem = getElement(trav, "entry", 1);
		assertNotNull(elem);
		assertTrue(trav.translate());
		assertEquals(null, trav.getTargetPointer());
		elem = getElement(trav, "src", 1);
		assertNotNull(elem);
		assertTrue(trav.translate());
		assertEquals("../trg", trav.getTargetPointer());
	}

	@Test
	public void testTargetPointerLocal () throws SAXException, IOException, ParserConfigurationException {
		InputSource is = new InputSource(new StringReader("<doc xmlns:i='"+ITSEngine.ITS_NS_URI+"' i:version='2.0'>"
			+ "<entry><src i:targetPointer='../trg'>source</src><trg></trg></entry></doc>"));
		Document doc = fact.newDocumentBuilder().parse(is);
		ITraversal trav = applyITSRules(doc, null, null);
		Element elem = getElement(trav, "entry", 1);
		assertNotNull(elem);
		assertTrue(trav.translate());
		assertEquals(null, trav.getTargetPointer());
		elem = getElement(trav, "src", 1);
		assertNotNull(elem);
		assertTrue(trav.translate());
		assertEquals("../trg", trav.getTargetPointer());
	}

	@Test
	public void testExternalResourceRefGlobal () throws SAXException, IOException, ParserConfigurationException {
		InputSource is = new InputSource(new StringReader("<doc>"
			+ "<i:rules xmlns:i='"+ITSEngine.ITS_NS_URI+"' version='2.0'>"
			+ "<i:externalResourcesRefRule selector='//video/@src' externalResourcesRefPointer='.' />"
			+ "<i:externalResourcesRefRule selector='//video/@poster' externalResourcesRefPointer='.' />"
			+ "</i:rules>"
			+ "<p>Text with <video src=\"http://www.example.com/v2.mp\" poster=\"video-image.png\" /></p></doc>"));
		Document doc = fact.newDocumentBuilder().parse(is);
		ITraversal trav = applyITSRules(doc, null, null);
		Element elem = getElement(trav, "video", 1);
		assertEquals("http://www.example.com/v2.mp", trav.getExternalResourcesRef(elem.getAttributeNode("src")));
		assertEquals("video-image.png", trav.getExternalResourcesRef(elem.getAttributeNode("poster")));
	}

	@Test
	public void testTranslateGlobal () throws SAXException, IOException, ParserConfigurationException {
		InputSource is = new InputSource(new StringReader("<doc>"
			+ "<i:rules xmlns:i='"+ITSEngine.ITS_NS_URI+"' version='2.0'>"
			+ "<i:translateRule selector='//par/@title' translate='yes' />"
			+ "<i:translateRule selector='//par/@alt' translate='yes' />"
			+ "</i:rules>"
			+ "<par title='title text' test='test' alt='alt text'>Text</par></doc>"));
		Document doc = fact.newDocumentBuilder().parse(is);
		ITraversal trav = applyITSRules(doc, null, null);
		Element elem = getElement(trav, "par", 1);
		assertTrue(trav.translate(elem.getAttributeNode("title")));
		assertTrue(trav.translate(elem.getAttributeNode("alt")));
		assertFalse(trav.translate(elem.getAttributeNode("test")));
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
		URI docURI,
		File rulesFile)
	{
		// Create the ITS engine
		ITSEngine itsEng = new ITSEngine(doc, docURI);
		// Add any external rules file(s)
		if ( rulesFile != null ) {
			itsEng.addExternalRules(rulesFile.toURI());
		}
		// Apply the all rules (external and internal) to the document
		itsEng.applyRules(ITSEngine.DC_ALL);
		return itsEng;
	}

}
