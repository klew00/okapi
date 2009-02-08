/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.w3c.its.ITraversal;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class XMLFilter implements IFilter {

	private String docName;
	private String encoding;
	private String srcLang;
	private Document doc;
	private ITraversal trav;
	private LinkedList<FilterEvent> queue;
	private int tuId;
	private int otherId;
	private int parseState;
	
	public void cancel () {
		queue.clear();
		queue.add(new FilterEvent(FilterEventType.CANCELED));
	}

	public void close () {
	}

	public ISkeletonWriter createSkeletonWriter () {
		return new GenericSkeletonWriter();
	}

	public String getName () {
		return "okf_xml";
	}

	public IParameters getParameters () {
		return null;
	}

	public boolean hasNext () {
		return (queue != null);
	}

	public FilterEvent next () {
		if ( queue == null ) return null;
		// Process queue if it's not empty yet
		if ( queue.size() > 0 ) {
			return queue.poll();
		}

		// Process the next item, filling the queue
		if ( parseState != 1 ) {
			process();
			// Send next event after processing, if there is one
			if ( queue.size() > 0 ) {
				return queue.poll();
			}
		}

		// Else: we are done
		queue = null;
		return new FilterEvent(FilterEventType.FINISHED, null);
	}

	public void open (InputStream input) {
		commonOpen(0, input);
	}

	public void open (CharSequence inputText) {
		encoding = "UTF-16";
		InputSource is = new InputSource(new StringReader(inputText.toString()));
		commonOpen(2, is);
	}

	public void open (URL inputURL) {
		try {
			docName = inputURL.getPath();
			commonOpen(0, inputURL.openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
		encoding = defaultEncoding;
	}

	public void setParameters (IParameters params) {
	}

	/**
	 * Shared open method for the public open() calls.
	 * @param type Indicates the type of obj: 0=InputStream, 1=File, 2=InputSource.
	 * @param obj The object to read.
	 */
	private void commonOpen (int type,
		Object obj)
	{
		close();
		// Initializes the variables
		tuId = 0;
		otherId = 0;
		parseState = 0;
		//lineBreak = System.getProperty("line.separator"); //TODO: Auto-detection of line-break type

		// Create the document builder
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		fact.setNamespaceAware(true);
		fact.setValidating(false);
		
		// Load the document
		try {
			switch ( type ) {
			case 0: // InputStream
				doc = fact.newDocumentBuilder().parse((InputStream)obj);
				break;
			case 1: // File
				doc = fact.newDocumentBuilder().parse((File)obj);
				break;
			case 2: // InputSource
				doc = fact.newDocumentBuilder().parse((InputSource)obj);
				break;
			}
		}
		catch ( SAXException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
		
		// Create the ITS engine
		ITSEngine itsEng;
		try {
			itsEng = new ITSEngine(doc, new URI("http://test")); //doc.getDocumentURI()));
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		// Apply the all rules (external and internal) to the document
		itsEng.applyRules(IProcessor.DC_TRANSLATE | IProcessor.DC_LANGINFO 
			| IProcessor.DC_LOCNOTE | IProcessor.DC_WITHINTEXT);
		
		trav = itsEng;
		trav.startTraversal();

		// Set the start event
		queue = new LinkedList<FilterEvent>();
		queue.add(new FilterEvent(FilterEventType.START));

		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		String realEnc = doc.getInputEncoding();
		if ( realEnc != null ) encoding = realEnc;
		startDoc.setEncoding(encoding);
		startDoc.setLanguage(srcLang);
		//TODO: startDoc.setFilterParameters(params);
		startDoc.setType("text/xml");
		startDoc.setMimeType("text/xml");
		queue.add(new FilterEvent(FilterEventType.START_DOCUMENT, startDoc));
	}

	private void process () {
		Node node;
		while ( true ) {
			node = trav.nextNode();
			if ( node == null ) { // No more node: we stop
				Ending ending = new Ending(String.valueOf(++otherId));
				//ending.setSkeleton(skel);
				queue.add(new FilterEvent(FilterEventType.END_DOCUMENT, ending));
				parseState = 1;
				return;
			}
			
			// Else: valid node
			switch ( node.getNodeType() ) {
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				TextUnit tu = new TextUnit(String.valueOf(++tuId));
				tu.setSourceContent(new TextFragment(node.getNodeValue()));
				queue.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));
				return;
			}
		}
	}

}
