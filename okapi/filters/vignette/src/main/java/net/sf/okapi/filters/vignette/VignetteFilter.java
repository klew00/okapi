/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.vignette;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.BaseSubFilterAdapter;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterState;
import net.sf.okapi.common.filters.FilterState.FILTER_STATE;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.abstractmarkup.AbstractMarkupFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implements the IFilter interface for Vignette export/import content.
 */
@UsingParameters(Parameters.class)
public class VignetteFilter implements IFilter {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private final String STARTBLOCK = "<importContentInstance>";
	private final String ENDBLOCK = "</importContentInstance>";
	
	private Parameters params;
	private String lineBreak;
	private int tuId;
	private IdGenerator subDocId;
	private IdGenerator groupId;
	private int otherId;
	private LinkedList<Event> queue;
	private boolean hasNext;
	private EncoderManager encoderManager;
	private BufferedReader reader;
	private BaseSubFilterAdapter subFilter;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private DocumentBuilder docBuilder;
	private String[] partsNames;
	private String[] partsConfigurations;
	private Hashtable<String, String[]> docs;
	private String inputText;
	private int current; // Current position
	private boolean preprocessing;
	private TemporaryStore store;
	private File storeFile;
	private int counter;
	private IFilterConfigurationMapper fcMapper;
	private String currentVFullPath;
	private List<String> listOfPaths;
	private String rootId;
	private boolean monolingual;
	
	public VignetteFilter () {
		params = new Parameters();
		DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
		Fact.setValidating(false);
		try {
			docBuilder = Fact.newDocumentBuilder();
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException("Error creating document builder.", e);
		}
	}
	
	@Override
	public void cancel () {
		// TODO: Support cancel
	}

	@Override
	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			if ( store != null ) {
				store.close();
				// Nullify and delete if not in pre-processing mode
				// In other words, not after the first pass.
				if ( !preprocessing ) {
					store = null;
					storeFile.delete();
				}
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error when closing.", e);
		}
		// Nothing to do
		hasNext = false;
	}

	@Override
	public ISkeletonWriter createSkeletonWriter() {
		return new VignetteSkeletonWriter();
	}

	@Override
	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter(), getEncoderManager());
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.XML_MIME_TYPE,
			getClass().getName(),
			"Vignette Export/Import Content",
			"Default Vignette Export/Import Content configuration."));
		return list;
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
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	@Override
	public String getDisplayName () {
		return "Vignette Filter";
	}

	@Override
	public String getMimeType () {
		return MimeTypeMapper.XML_MIME_TYPE;
	}

	@Override
	public String getName () {
		return "okf_vignette";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public boolean hasNext () {
		return hasNext;
	}

	@Override
	public Event next () {
		try {
			if ( !hasNext ) return null;
			if ( queue.size() == 0 ) {
				processBlock();
			}
			Event event = queue.poll();
			if ( event.getEventType() == EventType.END_DOCUMENT ) {
				hasNext = false;
				if ( !preprocessing ) {
					generateListOfPaths();
				}
			}
			return event;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error reading the input.", e);
		}
	}

	@Override
	public void open (RawDocument input) {
		open(input, true);
	}

	@Override
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		monolingual = params.getMonolingual();
		if ( monolingual ) {
			logger.info("- Monolingual processing");
		}
		else {
			logger.info("- Pre-processing pass");
		}

		partsNames = params.getPartsNamesAsList();
		partsConfigurations = params.getPartsConfigurationsAsList();
		if ( !params.checkData() ) {
			throw new RuntimeException("Invalid parts description in the parameters.");
		}
		docs = new Hashtable<String, String[]>();
		
		trgLoc = input.getTargetLocale();
		if ( trgLoc == null ) {
			throw new RuntimeException("You must specify a target locale.");
		}
		listOfPaths = new ArrayList<String>();
		
		// Just one pass for monolingual mode
		if ( monolingual ) {
			preprocessing = false;
			internalOpen(input);
			return;
		}
		
		//--- Else: Normal bilingual mode with two passes
		
		store = new TemporaryStore();
		try {
			storeFile = File.createTempFile("vgnflt_", null);
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error creating temporary store.", e);
		}
		store.create(storeFile);
		storeFile.deleteOnExit(); // just in case
		counter = 0;

		// First pass: pre-processing
		// Get the list of the usable documents
		// and create the temporary store
		preprocessing = true;
		internalOpen(input);
		while ( hasNext() ) {
			next();
		}
		// Close (before changing to not-pre-processing mode)
		close();
		
		// Check if we have things to extract
		int toExtract = 0;
		for ( String sourceId : docs.keySet() ) {
			String[] data = docs.get(sourceId);
			if ( Util.isEmpty(data[0]) ) {
				// No source
				if ( !Util.isEmpty(data[1]) ) {
					// No source but target exists
					logger.warning(String.format(
						"Entry '%s': No corresponding source entry exists for the target '%s'",
						data[1], trgLoc.toPOSIXLocaleId()));
				}
			}
			else { // Source exists
				if ( Util.isEmpty(data[1]) ) {
					// Source exists but not the target
					logger.warning(String.format(
						"Entry '%s': No entry exists for the target '%s'",
						data[0], trgLoc.toPOSIXLocaleId()));
				}
				else {
					// Both source and target exists
					toExtract++;
				}
			}
		}
		if ( toExtract <= 0 ) {
			logger.warning("There are no entries to extract");
		}

		// Second pass: extraction
		logger.info("- Processing pass");
		preprocessing = false;
		internalOpen(input);
		store.openForRead(storeFile);		
		// Now, the caller will be calling next()
	}
	
	private void internalOpen (RawDocument input) {
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(
			input.getStream(), "UTF-8"); // UTF-8 for encoding
		detector.detectAndRemoveBom();
		input.setEncoding(detector.getEncoding());
		String encoding = input.getEncoding();
		
		try {
			reader = new BufferedReader(new InputStreamReader(detector.getInputStream(),
				encoding));
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiUnsupportedEncodingException(
				String.format("The encoding '%s' is not supported.", encoding), e);
		}
		lineBreak = detector.getNewlineType().toString();
		boolean hasUTF8BOM = detector.hasUtf8Bom();
		String docName = null;
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}
		
		//TODO: We may have to work with buffered block to handle very large files
		readAllData();
		
		if ( docName != null ) rootId = docName;
		else rootId = IdGenerator.DEFAULT_ROOT_ID;

		tuId = 0;
		subDocId = new IdGenerator(rootId, IdGenerator.START_SUBDOCUMENT);
		groupId = new IdGenerator(rootId, IdGenerator.START_GROUP);
		otherId = 0;
		
		// Set the start event
		queue = new LinkedList<Event>();
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		srcLoc = input.getSourceLocale();
		startDoc.setLocale(srcLoc);
		startDoc.setLineBreak(lineBreak);
		startDoc.setFilterParameters(getParameters());
		startDoc.setFilterWriter(createFilterWriter());
		startDoc.setType(MimeTypeMapper.XML_MIME_TYPE);
		startDoc.setMimeType(MimeTypeMapper.XML_MIME_TYPE);
		startDoc.setMultilingual(false);
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));
		hasNext = true;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private void readAllData () {
		StringBuilder tmp = new StringBuilder();
		char[] buf = new char[2048];
		int count = 0;
		try {
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}
			
			inputText = tmp.toString().replace(lineBreak, "\n");
			current = 0;
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error reading the input.", e);
		}
		finally {
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch ( IOException e ) {
					throw new OkapiIOException("Error closing the input.", e);
				}
			}
		}
	}

	private void updateDocumentList (String sourceId,
		boolean isSource)
	{
		if ( !docs.containsKey(sourceId) ) {
			docs.put(sourceId, new String[2]);
		}
		String[] data = docs.get(sourceId);
		// Source is at index 0, target at index 1
		// We just place the sourceId there for both
		if ( isSource ) data[0] = sourceId;
		else data[1] = sourceId;
	}

	private void processBlock ()
		throws SAXException, IOException
	{
		while ( true ) {
			// Look for the next block
			int start = inputText.indexOf(STARTBLOCK, current);
	
			// No more block: end of the document
			if ( start == -1 ) {
				// From current to end: to skeleton
				Ending ending = new Ending(String.valueOf(++otherId));
				ending.setSkeleton(new GenericSkeleton(
					inputText.substring(current).replace("\n", lineBreak)));
				queue.add(new Event(EventType.END_DOCUMENT, ending));
				return;
			}
			
			// Start of block found: look for end of block
			int end = inputText.indexOf(ENDBLOCK, start);
			if ( end == -1 ) {
				throw new OkapiIOException("Cannot find end of block.");
			}
			
			// End of block found
			if ( preprocessing ) {
				counter++;
			}
			else {
				// Parts between current and start go to skeleton
				DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
				dp.setSkeleton(new GenericSkeleton(
					inputText.substring(current, start).replace("\n", lineBreak)));
				queue.add(new Event(EventType.DOCUMENT_PART, dp));
			}
			current = end+ENDBLOCK.length(); // For next time
	
			// Treat the content
			// Returns true when an event was found
			if ( processXMLBlock(start, current) ) {
				break; // Return
			}
		}
	}
	
	// Returns true if event was found
	private boolean processXMLBlock (int start,
		int end)
	{
		boolean eventFound = false;
		try {
			// Parse the block into a DOM-tree
			String content = inputText.substring(start, end);
			Document doc = docBuilder.parse(new InputSource(new StringReader(content)));
			
			// Get first 'contentInstance' element
			NodeList nodes = doc.getElementsByTagName("contentInstance");
			Element elem = (Element)nodes.item(0);
			currentVFullPath = elem.getAttribute("vcmLogicalPath") + "/" + elem.getAttribute("vcmName");

			// Get all 'attribute' elements in 'contentInstance'
			if ( monolingual ) {
				if ( processListForMonolingual(elem, content) ) eventFound = true;
			}
			else {
				if ( !preprocessing ) {
					logger.info("contentInstance vcmLogicalPath="+currentVFullPath);
				}
				if ( processList(elem, content) ) eventFound = true;
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException(
				String.format("XML parsing error in block starting at character %d.", start), e);
		}
		return eventFound;
	}
	
	
	private boolean processListForMonolingual (Element elem,
		String content) throws SAXException, IOException
	{
		// Parse the block into a DOM-tree
		String tmp = content;

		// Parse the source content
		Document oriDoc = docBuilder.parse(new InputSource(new StringReader(tmp)));
		// Get first 'contentInstance' element
		NodeList oriNodes = oriDoc.getElementsByTagName("contentInstance");
		elem = (Element)oriNodes.item(0);

		// Get all 'attribute' elements in 'contentInstance' 
		oriNodes = elem.getElementsByTagName("attribute");
		int last = 0;
		int[] pos;
		listOfPaths.add(currentVFullPath);
		
		// Start of sub-document
		StartSubDocument ssd = new StartSubDocument(subDocId.createId());
		ssd.setName(subDocId.toString());
		queue.add(new Event(EventType.START_SUBDOCUMENT, ssd));
		
		for ( int i=0; i<oriNodes.getLength(); i++ ) {
			Element tmpElem = (Element)oriNodes.item(i);
			String name = tmpElem.getAttribute("name");
			
			// See if the name is in the list of the parts to extract
			//TODO: We could have a faster way to detect if the name is listed and get j
			boolean found = false;
			int j;
			for ( j=0; j<partsNames.length; j++ ) {
				if ( name.equals(partsNames[j]) ) {
					found = true;
					break;
				}
			}
			if ( !found ) continue; // Not an attribute element to extract
			
			tmpElem = getFirstElement(tmpElem);
			String data = tmpElem.getTextContent();
			if ( Util.isEmpty(data) ) continue;
			
			// Get the range of the content in the target block
			pos = getRange(content, last, partsNames[j]);
			// Create the document part skeleton for the data before
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
			dp.setSkeleton(new GenericSkeleton(content.substring(last, pos[0]).replace("\n", lineBreak)));
			queue.add(new Event(EventType.DOCUMENT_PART, dp));
			last = pos[1]; // For next part
			// Create the event from the original block
			processContent(data, partsNames[j], partsConfigurations[j]);

		}
		
		// End of group, and attached the skeleton for the end too
		Ending ending = new Ending(String.valueOf(++otherId));
		ending.setSkeleton(new GenericSkeleton(content.substring(last).replace("\n", lineBreak)));			
		queue.add(new Event(EventType.END_SUBDOCUMENT, ending));
		return true;
	}

	private boolean processList (Element elem,
		String content) throws SAXException, IOException
	{
		String sourceId = null;
		String localeId = null;

		// Get all 'attribute' elements in 'contentInstance' 
		NodeList nodes = elem.getElementsByTagName("attribute");
		// Get info
		for ( int i=0; i<nodes.getLength(); i++ ) {
			elem = (Element)nodes.item(i);
			String name = elem.getAttribute("name");
			if ( name.equals(params.getLocaleId()) ) {
				localeId = getValueString(elem);
			}
			else if ( name.equals(params.getSourceId()) ) {
				sourceId = getValueString(elem);
			}
			if (( sourceId != null ) && ( localeId != null )) {
				break; // We are done
			}
		}
		
		// Skip block, if not all info is available
		if ( Util.isEmpty(localeId) || Util.isEmpty(sourceId) ) {
			// Warn during pre-processing, then treat as document part
			if ( preprocessing ) {
				logger.warning(String.format(
					"Entry with incomplete data at %s number %d\nlocale='%s' sourceId='%s'",
					STARTBLOCK, counter, localeId, sourceId));
				return false;
			}
			else {
				logger.warning("Missing data, this section is skipped.");
				DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
				dp.setSkeleton(new GenericSkeleton(content.replace("\n", lineBreak)));
				queue.add(new Event(EventType.DOCUMENT_PART, dp));
				return true;
			}
		}

		if ( preprocessing ) {
			if ( srcLoc.toPOSIXLocaleId().equals(localeId) ) {
				// For a source block: update the list, store the data and move on
				updateDocumentList(sourceId, true);
				store.writeBlock(sourceId, content);
				return false;
			}
			else if ( trgLoc.toPOSIXLocaleId().equals(localeId) ) {
				// For a target block: update the list and skip
				updateDocumentList(sourceId, false);
				return false;
			}
			else {
				// For other locales, just skip them
				return false;
			}
		}
		else {
			// Else, in extract mode: skip if not a target block
			boolean extract = true;
			if ( trgLoc.toPOSIXLocaleId().equals(localeId) ) {
				// If it's a target
				// Find its corresponding entry in the store
				String[] data = docs.get(sourceId);
				if ( data == null ) {
					extract = false;
				}
				else if ( Util.isEmpty(data[0]) ) {
					// No corresponding source was detected
					extract = false;
				}
			}
			else { // Not a target: skip
				extract = false;
			}
			
			logger.info(String.format("   LocaleId='%s', extract=%s, sourceId='%s'",
				localeId, (extract ? "Yes" : "No"), sourceId));

			// If we don't extract
			if ( !extract ) {
				// Just send as document part
				DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
				dp.setSkeleton(new GenericSkeleton(content.replace("\n", lineBreak)));
				queue.add(new Event(EventType.DOCUMENT_PART, dp));
				return true;
			}
			// Else: extract
		}
		
		// Find its corresponding entry in the store
		// Parse the block into a DOM-tree
		String tmp = findOriginalInStore(sourceId);
		if ( tmp == null ) {
			throw new OkapiIOException(String.format(
				"The sourceId attribute was not found ('%s').", sourceId));
		}

		// Parse the source content
		Document oriDoc = docBuilder.parse(new InputSource(new StringReader(tmp)));
		// Get first 'contentInstance' element
		NodeList oriNodes = oriDoc.getElementsByTagName("contentInstance");
		elem = (Element)oriNodes.item(0);

		// Get all 'attribute' elements in 'contentInstance' 
		oriNodes = elem.getElementsByTagName("attribute");

		int last = 0;
		int[] pos;
		
		listOfPaths.add(currentVFullPath);

		// Start of sub-document
		StartSubDocument ssd = new StartSubDocument(subDocId.createId());
		ssd.setName(sourceId);
		queue.add(new Event(EventType.START_SUBDOCUMENT, ssd));
		
		for ( int i=0; i<oriNodes.getLength(); i++ ) {
			Element tmpElem = (Element)oriNodes.item(i);
			String name = tmpElem.getAttribute("name");
			
			// See if the name is in the list of the parts to extract
			//TODO: We could have a faster way to detect if the name is listed and get j
			boolean found = false;
			int j;
			for ( j=0; j<partsNames.length; j++ ) {
				if ( name.equals(partsNames[j]) ) {
					found = true;
					break;
				}
			}
			if ( !found ) continue; // Not an attribute element to extract
			
			tmpElem = getFirstElement(tmpElem);
			String data = tmpElem.getTextContent();
			if ( Util.isEmpty(data) ) continue;
			
			// Get the range of the content in the target block
			pos = getRange(content, last, partsNames[j]);
			// Create the document part skeleton for the data before
			DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false);
			dp.setSkeleton(new GenericSkeleton(content.substring(last, pos[0]).replace("\n", lineBreak)));
			queue.add(new Event(EventType.DOCUMENT_PART, dp));
			last = pos[1]; // For next part
			// Create the event from the original block
			processContent(data, partsNames[j], partsConfigurations[j]);

		}
		
		// End of group, and attached the skeleton for the end too
		Ending ending = new Ending(String.valueOf(++otherId));
		ending.setSkeleton(new GenericSkeleton(content.substring(last).replace("\n", lineBreak)));			
		queue.add(new Event(EventType.END_SUBDOCUMENT, ending));
		return true;
	}

	private String findOriginalInStore (String sourceId) {
		boolean rewund = false;
		String stop = null;
		while ( true ) {
			String tmp[] = store.readNext();
			if ( tmp == null ) {
				if ( rewund ) return null; // Not found
				// Else: rewind the store
				store.close();
				store.openForRead(storeFile);
				rewund = true;
			}
			else {
				if ( tmp[0].equals(sourceId) ) {
					return tmp[1]; // Found
				}
				else {
					if ( stop != null ) {
						if ( tmp[0].equals(stop) ) {
							return null; // Stop here, not found
						}
						// Move to next
					}
					else {
						// Remember where to stop later
						stop = tmp[0];
					}
				}
			}
		}
	}
	
//	private String getContent (NodeList nodes,
//		String partName)
//	{
//		for ( int i=0; i<nodes.getLength(); i++ ) {
//			Element elem = (Element)nodes.item(i);
//			String name = elem.getAttribute("name");
//			if ( name.equals(partName) ) {
//				elem = getFirstElement(elem);
//				return elem.getTextContent();
//			}
//		}
//		return null;
//	}
	
	// Returns from start of attribute element for the given part name,
	// 0=start of the attribute element
	// 1=first char of the data in the value-type element
	// 2=start of the closing value-type element
	// [attribute name='name'][valueObject]data[/valueObject][/attribute]
	//                                     ^=0 ^=1
	private int[] getRange (String content,
		int start,
		String partName)
	{
		int[] res = new int[3];
		res[0] = -1;
		
		String tmp = String.format("<attribute name=\"%s\">", partName);
		int n = content.indexOf(tmp, start);
		if ( n == -1 ) return res;
		n = content.indexOf("<", n+1); // Start of value-type element
		// Meta characters are escaped so we can just do this: 
		res[0] = content.indexOf(">", n)+1;
		res[1] = content.indexOf("<", res[0]);
		return res;
	}
	
	private String getValueString (Element parent) {
		return getFirstElement(parent, "valueString").getTextContent();
	}
	
	private void processContent (String data,
		String partName,
		String partConfiguration)
	{
		if ( partConfiguration.equals("default") ) {
			ITextUnit tu = new TextUnit(String.valueOf(++tuId));
			tu.setSourceContent(new TextFragment(data));
			tu.setMimeType(MimeTypeMapper.XML_MIME_TYPE);
			tu.setType("x-"+partName);
			queue.add(new Event(EventType.TEXT_UNIT, tu));
		}
		else {
			IFilter sf = fcMapper.createFilter(partConfiguration, subFilter);
			encoderManager.mergeMappings(sf.getEncoderManager());	
			subFilter = new BaseSubFilterAdapter(sf);
			
			groupId.createId(); // Create new Id for this group
			if ( subFilter.getFilter() instanceof AbstractMarkupFilter ) { // For IdGenerator try-out
				// The root id of is made of: rootId + subDocId + groupId
				FilterState state = new FilterState(FILTER_STATE.STANDALONE_TEXTUNIT, 
						subDocId.getLastId(), null, null); 
				subFilter.setState(state);
				subFilter.open(new RawDocument(data, srcLoc));
				while ( subFilter.hasNext() ) {
					queue.add(subFilter.next());
				}
				subFilter.close();
			}
			else {
				subFilter.open(new RawDocument(data, srcLoc));
					
				// Change the START_DOCUMENT to START_GROUP
				Event event = subFilter.next(); // START_DOCUMENT
				StartDocument sd = (StartDocument)event.getResource();
				StartSubfilter sg = new StartSubfilter(subDocId.getLastId(), groupId.getLastId()); // Group id already created
				sg.setType("x-"+partName);
				sg.setMimeType(sd.getMimeType());
				sg.setSkeleton(sd.getSkeleton());
				queue.add(new Event(EventType.START_GROUP, sg));
				
				while ( subFilter.hasNext() ) {
					event = subFilter.next();
					if ( event.getEventType() == EventType.END_DOCUMENT ) {
						break;
					}
					queue.add(event);
				}
				subFilter.close();
	
				// Change the END_DOCUMENT to END_GROUP
				EndSubfilter ending = new EndSubfilter(groupId.createId());
				ending.setSkeleton(event.getResource().getSkeleton());
				queue.add(new Event(EventType.END_GROUP, ending));
			}
		}
	}
	
	/**
	 * Gets the first element of a given name in the given parent element.
	 * The element must exist.
	 * @param parent the element where to element is a child. 
	 * @param name name of the element to get
	 * @return the first element of a given name in the given parent element.
	 */
	private Element getFirstElement (Element parent,
		String name)
	{
		NodeList nodes = parent.getElementsByTagName(name);
		return (Element)nodes.item(0);
	}

	/**
	 * Gets the first child element of a given parent.
	 * @param parent the parent.
	 * @return the first child element of a given parent or null.
	 */
	private Element getFirstElement (Element parent) {
		Node node = parent.getFirstChild();
		while ( true ) {
			if ( node == null ) {
				return null;
			}
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				return (Element)node;
			}
			node = node.getNextSibling();
		}
	}

	private void generateListOfPaths () {
		logger.info(String.format("\nNumber of parts to localize = %d", listOfPaths.size()));
		for ( String tmp : listOfPaths ) {
			logger.info(tmp);
		}
	}

	@Override
	public boolean isSubfilter() {
		// TODO Auto-generated method stub
		return false;
	}

}
