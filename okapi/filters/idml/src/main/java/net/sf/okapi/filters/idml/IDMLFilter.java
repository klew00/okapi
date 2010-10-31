/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.idml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

@UsingParameters(Parameters.class)
public class IDMLFilter implements IFilter {

	private final static String MIMETYPE = "application/vnd.adobe.indesign-idml-package";
	private final static String DOCID = "sd";
	
	private final static String ENDID = "end";
	private final static String SPREADTYPE = "spread";
	private final static String STORYTYPE = "story";
	
	final private DocumentBuilder docBuilder;
	private URI docURI;
	private LinkedList<Event> queue;
	private LocaleId srcLoc;
	private Parameters params;
	private EncoderManager encoderManager;
	private HashMap<String, ZipEntry> stories;
	private LinkedHashMap<String, ArrayList<String>> spreads;
	private Iterator<String> storyIter;
	private Iterator<String> spreadIter;
	private ZipFile zipFile;
	private IdGenerator spreadIdGen;
	private IdGenerator storyIdGen;
	private int spreadStack;
	private int nodeCount;
	private String tuIdPrefix;
	private Stack<IDMLContext> ctx;

	public IDMLFilter () {
		try {
			params = new Parameters();
			DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
			docFact.setValidating(false);
			docBuilder = docFact.newDocumentBuilder();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error initializing.\n"+e.getMessage(), e);
		}
	}

	@Override
	public void cancel () {
		// TODO
	}

	@Override
	public void close () {
		if ( zipFile != null ) {
			try {
				zipFile.close();
			}
			catch ( IOException e ) {
				// Swallow it
			}
			zipFile = null;
		}
	}

	@Override
	public ISkeletonWriter createSkeletonWriter () {
		return null; // There is no corresponding skeleton writer
	}
	
	@Override
	public IFilterWriter createFilterWriter () {
		return new IDMLFilterWriter();
	}

	@Override
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MimeTypeMapper.XML_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		}
		return encoderManager;
	}

	@Override
	public String getName () {
		return "okf_idml";
	}

	@Override
	public String getDisplayName () {
		return "IDML Filter (ALPHA)";
	}

	@Override
	public String getMimeType () {
		return MIMETYPE;
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"IDML",
			"Adobe InDesign IDML documents"));
		return list;
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public boolean hasNext () {
		return ( queue != null );
	}

	@Override
	public Event next () {
		if ( queue == null ) return null;
		if ( queue.size() > 0 ) {
			return queue.poll();
		}

		// Get the next event
		read();
		// End process if needed
		if ( queue.size() == 0 ) {
			queue = null; // No more
			Ending ending = new Ending("ed");
			return new Event(EventType.END_DOCUMENT, ending);
		}
		// Else, return the next event that was read
		return queue.poll();
	}

	@Override
	public void open (RawDocument input) {
		open(input, true);
	}
	
	@Override
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		queue = null;
		close();
		docURI = input.getInputURI();
		if ( docURI == null ) {
			throw new OkapiBadFilterInputException("This filter supports only URI input.");
		}
		srcLoc = input.getSourceLocale();
		spreadIdGen = new IdGenerator(null, "spr");
		storyIdGen = new IdGenerator(null, "sto");
		ctx = new Stack<IDMLContext>();

		// Gather the spreads
		gatherStories();

		// Add the start document event
		StartDocument sd = new StartDocument(DOCID);
		sd.setEncoding("UTF-8", false);
		sd.setName(docURI.getPath());
		sd.setLocale(srcLoc);
		sd.setMimeType(MIMETYPE);
		sd.setLineBreak("\n");
		sd.setFilterParameters(params);
		sd.setFilterWriter(createFilterWriter());
		// Add the skeleton
		sd.setSkeleton(new IDMLSkeleton(zipFile));
		// Create the start document event
		queue = new LinkedList<Event>();
		queue.add(new Event(EventType.START_DOCUMENT, sd));
		
		// Point to the first spread
		if ( spreads.size() > 0 ) {
			spreadIter = spreads.keySet().iterator();
			if ( spreadIter.hasNext() ) {
				String spreadName = spreadIter.next();
				storyIter = spreads.get(spreadName).iterator();
				StartGroup sg = new StartGroup(DOCID, spreadIdGen.createId());
				queue.add(new Event(EventType.START_GROUP, sg));
				sg.setName(spreadName);
				sg.setType(SPREADTYPE);
				spreadStack++;
			}
		}
	}
	
	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// TODO (if needed)
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private void read () {
		if ( spreadIter == null ) return; // No content
		while ( true ) {
			// Check for next story in the current spread
			if ( storyIter.hasNext() ) {
				// At least one more story to process
				processStory(storyIter.next());
				return;
			}
			
			// Otherwise: close the previous spread if needed
			if ( spreadStack > 0 ) {
				Ending ending = new Ending(spreadIdGen.getLastId()+ENDID);
				queue.add(new Event(EventType.END_GROUP, ending));
				spreadStack--;
			}
			
			// Then try the next spread
			if ( spreadIter.hasNext() ) {
				String spreadName = spreadIter.next();
				storyIter = spreads.get(spreadName).iterator();
				StartGroup sg = new StartGroup(DOCID, spreadIdGen.createId());
				sg.setName(spreadName);
				sg.setType(SPREADTYPE);
				queue.add(new Event(EventType.START_GROUP, sg));
				spreadStack++;
			}	
			else {
				// Else: nothing else
				break;
			}
		}
	}
	
	/**
	 * Gathers all the stories to process, for each spread.
	 */
	private void gatherStories () {
		spreadIter = null;
		storyIter = null;
		spreads = new LinkedHashMap<String, ArrayList<String>>();
		stories = new HashMap<String, ZipEntry>();
		try {
			zipFile = new ZipFile(new File(docURI));
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while( entries.hasMoreElements() ) {
				ZipEntry entry = entries.nextElement();
				if ( entry.getName().endsWith(".xml") ) {
					if ( entry.getName().startsWith("Spreads/") ) {
						// Gather stories from the spread
						processSpread(entry);
					}
					else if ( entry.getName().startsWith("Stories/") ) {
						// Add the entry to the lookup list
						String storyId = Util.getFilename(entry.getName(), false);
						int p = storyId.indexOf('_');
						if ( p > -1 ) storyId = storyId.substring(p+1);
						stories.put(storyId, entry);
					}
				}
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error while gathering stories.", e);
		}
	}

	/**
	 * Gather all the stories used in this spread.
	 * @param entry the zip entry for the spread.
	 * @return the total number of stories in the given spread.
	 */
	private int processSpread (ZipEntry entry)
		throws SAXException, IOException, ParserConfigurationException
	{
		ArrayList<String> storyList = new ArrayList<String>();
		Document doc = docBuilder.parse(zipFile.getInputStream(entry));

		String name = entry.getName();
		
		NodeList list = doc.getElementsByTagName("TextFrame");
		for ( int i=0; i<list.getLength(); i++ ) {
			Element tf = (Element)list.item(i);
			String tmp = tf.getAttribute("ParentStory");
			if ( Util.isEmpty(tmp) ) {
				throw new IOException("Missing value for parentStory.");
			}
			// Add the the story to the lookup list
			storyList.add(tmp);
		}
		
		// Add the stories for this spread to the overall list of stories to process
		spreads.put(name, storyList);
		// Return the number of stories in this spread
		return storyList.size();
	}

	private void processStory (String storyId) {
		ZipEntry entry = stories.get(storyId);
		if ( entry == null ) {
			throw new OkapiIOException("No story entry found for "+storyId);
		}
		try {
			// Read the document in memory
			Document doc = docBuilder.parse(zipFile.getInputStream(entry));
			
			// Real story id
			
			// Start the story group
			StartGroup sg = new StartGroup(spreadIdGen.getLastId(), storyIdGen.createId());
			sg.setName(storyId);
			sg.setType(STORYTYPE);
			sg.setSkeleton(new IDMLSkeleton(entry, doc));
			queue.add(new Event(EventType.START_GROUP, sg));
			
			// Prepare for traversal
			DocumentTraversal dt = (DocumentTraversal)doc;
			// Note: some TreeWalker implementations seem to always send text nodes, no matter
			// what the 'whatToShow' parameter says here. So we do include text nodes.
			TreeWalker tw = dt.createTreeWalker(doc.getDocumentElement(),
				NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT,
				null, true);
			nodeCount = 0;
			tuIdPrefix = storyId+"-";
			// Traverse the story
			processNodes((Element)tw.getRoot());
			
			// End the story group
			Ending ending = new Ending(storyIdGen.getLastId()+ENDID);
			queue.add(new Event(EventType.END_GROUP, ending));
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error processing story file.\n"+e.getMessage(), e);
		}
	}
	
	private void processNodes (Node node) {
		while ( node != null ) {
			
			switch ( node.getNodeType() ) {
			case Node.TEXT_NODE:
				if ( !ctx.isEmpty() ) {
					ctx.peek().addCode(node);
				}
				node = node.getNextSibling();
				continue;
			}
			
			// Else: it's an element
			Element elem = (Element)node;
			nodeCount++;
			String name = elem.getNodeName();
			
			// Process before the children
			if ( name.equals("Content") ) {
				ctx.peek().addContent(elem);
				node = elem.getNextSibling();
				continue;
			}
			else if ( name.equals("ParagraphStyleRange") ) {
				ctx.push(new IDMLContext(node, nodeCount));
			}
			else {
				if ( !ctx.isEmpty() ) {
					ctx.peek().addStartTag(elem);
				}
			}
		
			// Process the children (if any)
			if ( elem.hasChildNodes() ) {
				processNodes(elem.getFirstChild());
			}
			
			// When coming back from the children
			if ( name.equals("ParagraphStyleRange") ) {
				// Trigger the text unit (and pop the current context)
				ctx.pop().addToQueue(queue, tuIdPrefix);
			}
			else {
				if ( !ctx.isEmpty() ) {
					ctx.peek().addEndTag(elem);
				}
			}
			
			// Then move on to the next sibling
			node = elem.getNextSibling();
		}
	}
	
}
