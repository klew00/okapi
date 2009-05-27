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

package net.sf.okapi.filters.plaintext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.*;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public abstract class AbstractPlainTextFilter implements IFilter {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private IParameters params;
	private BufferedReader reader;
	private boolean canceled;
	private String encoding;
	
	private LinkedList<Event> queue;
	private String lineRead;
	// private String lineToProcess = "";	
	private int lineNumber = 0;
	private TextProcessingResult lastResult;
	private int tuId;
	private int otherId;
	private String lineBreak;
	private int parseState = 0;	
	private GenericSkeleton activeSkeleton = null; // Skeleton of the last event in the queue
	private String docName;
	private String srcLang;
	private boolean hasUTF8BOM;
	private String filterName;
	private String mimeType;
	private boolean reading = false;
		
	public void cancel() {
		
		canceled = true;
	}

	public final void close() {
		
		try {
			if (reader != null) {
				reader.close();
				reader = null;
				docName = null;
			}
			parseState = 0;
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	public IFilterWriter createFilterWriter() {
		
		return new GenericFilterWriter(createSkeletonWriter());
	}

	public ISkeletonWriter createSkeletonWriter() {
		
		return new GenericSkeletonWriter();
	}

	/**
	 * Sets the input document mime type.
	 * 
	 * @param mimeType
	 *            the new mime type
	 */
	protected void setMimeType(String mimeType) {
		
		this.mimeType = mimeType;
	}

	/**
	 * Gets the input document mime type.
	 * 
	 * @return the mime type
	 */

	public String getMimeType() {
		
		return mimeType;
	}
	
	protected void setName(String filterName) {
		
		this.filterName = filterName;
	}
	
	public String getName() {
		
		return filterName;
	}

	public IParameters getParameters() {
		
		return params;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			mimeType,
			getClass().getName(),
			"Plain Text Filter",
			"Text files; ANSI, Unicode, UTF-8, UTF-16 are supported."));
		return list;
	}
	
	protected void filter_init() {
		// To be implemented in descendant classes
	}
	
	protected void filter_done() {
		// To be implemented in descendant classes
	}
	
	public void setParameters(IParameters params) {
		
		this.params = params;
		//filter_init(params);
	}

	public boolean hasNext() {
		
		return (parseState > 0);
	}

	public void open (RawDocument input) {
		
		if (input == null) throw new OkapiBadFilterInputException("RawDocument is not defined in open(RawDocument).");
		
		open(input, true);
	}
	
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		if (input == null) throw new OkapiBadFilterInputException("RawDocument is not defined in open(RawDocument, boolean).");
			
		setOptions(input.getSourceLanguage(), input.getTargetLanguage(),
			input.getEncoding(), generateSkeleton);
		if (input.getInputURI() != null) {
			open(input.getInputURI());
		}
		else if (input.getInputCharSequence() != null) {
			open(input.getInputCharSequence());
		}
		else if (input.getInputStream() != null) {
			open(input.getInputStream());
		}
		else {
			throw new OkapiBadFilterInputException("RawDocument has no input defined.");
		}
	}
	
	private void open (InputStream input) {
		
		try {
			close();
//			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input, encoding);
//			encoding = detector.getEncoding();
//			if (encoding == null) throw new OkapiBadFilterInputException("Encoding cannot be detected, probably empty input.");
//			
//			hasUTF8BOM = detector.hasUtf8Bom();
//			lineBreak = detector.getNewlineType().toString();
//			detector = null; // Free/close
//			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
//			bis.detectEncoding(); // Needed to skip over the BOM
			
			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
			encoding = bis.detectEncoding();
			hasUTF8BOM = bis.hasUTF8BOM();
			
			//reader = new InputStreamReader(bis, encoding);
			
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input, encoding);
			lineBreak = detector.getNewlineType().toString();
			
			commonOpen(new InputStreamReader(bis, encoding));
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	private void open (URI inputURI) {
		
		if (inputURI == null) throw new OkapiBadFilterInputException("URI is not defined in open(URI).");
		
		try {
			docName = inputURI.getPath();
			open(inputURI.toURL().openStream());
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	private void open (CharSequence inputText) {
		
		close();
		encoding = "UTF-16";
		hasUTF8BOM = false;
		lineBreak = BOMNewlineEncodingDetector.getNewlineType(inputText).toString();
		commonOpen(new StringReader(inputText.toString()));
	}
	
	private void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		//TODO: Implement boolean generateSkeleton
		encoding = defaultEncoding;
		srcLang = sourceLanguage;
	}

	protected final void throwParametersException(IParameters expected, IParameters current) {
		
		String expectedName = "";
		String currentName = "";
		
		if (!Util.isEmpty(expected) && !Util.isEmpty(expected.getClass())) {
			expectedName = expected.getClass().getSimpleName();
		}
		
		if (!Util.isEmpty(current) && !Util.isEmpty(current.getClass())) {
			currentName = current.getClass().getSimpleName();
		}
		 
		throw new OkapiBadFilterParametersException(String.format("Parameters of class <%s> expected, but are <%s>", expectedName, currentName));
	}
	
	protected final void checkParameters(IParameters params) {
		
		if (Util.isEmpty(params)) {
			throw new OkapiBadFilterParametersException("Empty filter parameters.");			
		}
	}
	
	
	private void commonOpen (Reader inputReader) {
		
		parseState = 1;
		canceled = false;

		// Open the input reader from the provided reader
		reader = new BufferedReader(inputReader);
		try {
			reader.mark(1024); // Mark line start (needed for 1-line files, reader.ready() returns false before the first read)
		} 
		catch (IOException e) {
		} 
		
		// Initialize variables
		tuId = 0;
		otherId = 0;
		lineNumber = 0;
		//lineToProcess = "";		
		lastResult = TextProcessingResult.NONE;
		
		queue = new LinkedList<Event>();
		
		filter_init(); // Initialize the filter with implementation-specific parameters (protected method)
		
		reading = true; // Activate input reading
		
		// Send start event
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLanguage(srcLang);
		startDoc.setFilterParameters(params);
		startDoc.setLineBreak(lineBreak);
		startDoc.setType(getMimeType());
		startDoc.setMimeType(getMimeType());
		startDoc.setSkeleton(new GenericSkeleton());
		
		sendEvent(EventType.START_DOCUMENT, startDoc);				
	}

	protected final void logMessage (Level level, String text) {
		
		logger.log(level, String.format("Line %d: %s", lineNumber, text));
	}

	public Event next() {
		
		// Cancel if requested
		if (canceled) {
			parseState = 0;
			queue.clear();
			
			sendEvent(EventType.CANCELED, null);
			return queue.poll(); 
		}
				
		try {
			while (reading) { 
				
				if (!getNextLine()) { // Sets lineRead
					
					filter_done();
					
					String tail = getFileTail();					
					if (!Util.isEmpty(tail)) 
						if (activeSkeleton != null) activeSkeleton.append(tail);
					
					reading = false;
					break;
				}
				
				switch (lastResult) { // Result of previous processing
				
				case REJECTED:
				case ACCEPTED:						
					// Add line break to skeleton
					if (activeSkeleton != null) activeSkeleton.append(lineBreak);					
					break;
					
				case DELAYED_DECISION:
					break;
				}
				
				TextContainer lineContainer = new TextContainer(lineRead);
				if (lineContainer == null) {
					lastResult = TextProcessingResult.NONE;
					reading = false;
					break;
				}
					
				lastResult = filter_exec(lineContainer); // Can modify text and codes of lineContainer  
				
				switch (lastResult) { 
				
					case REJECTED:
						// Add the whole line to skeleton
						if (activeSkeleton == null) continue;
						
						if (lineContainer == null) {
							lastResult = TextProcessingResult.NONE;
							continue;
						}
						
						activeSkeleton.append(lineContainer.toString());						
						continue;
						
					case ACCEPTED:						
						break;
						
					case DELAYED_DECISION:
						continue;
				}
								
				// Return first event in the queue 
				if (queue.size() > 0) {
					return queue.poll();				
				}
				else
					activeSkeleton = null; // No events left in the queue, no skeleton to add to
			}
			
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}	
				
		// Get last TU event with trailing skeleton of the file 
		if (queue.size() > 0) {
			return queue.poll();
		}
		
		// The ending event
		Ending ending = new Ending(String.valueOf(++otherId));
		ending.setSkeleton(new GenericSkeleton());
		parseState = 0;
		return new Event(EventType.END_DOCUMENT, ending);		
	}

	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
		// To be overridden in descendant classes
		
		return TextProcessingResult.REJECTED;		
	}
	
	protected final boolean sendEvent(EventType eventType, IResource res) {
		
		return sendEvent(-1, eventType, res);
	}
	
	protected final GenericSkeleton getActiveSkeleton() {
		
		return activeSkeleton;
	}
	
	protected final int getQueueSize() {
		
		return queue.size();
	}

	/**
	 * 
	 * @param index
	 * @param eventType
	 * @param res
	 */
	protected final boolean sendEvent(int index, EventType eventType, IResource res) {
		// Called from descendant classes if they want to put events in the queue themselves, not to be overridden
		
		Event event = new Event(eventType, res);
		if (event == null) return false;				
		
		int saveSize = queue.size();
		boolean result = false;
		
		if (res != null && Util.isEmpty(res.getId())) {
			if (event.getEventType() == EventType.TEXT_UNIT)			
				res.setId(String.valueOf(++tuId));
			else
				res.setId(String.valueOf(++otherId));
		}
		
		if (index == -1) {
			queue.add(event);
			result = (queue.size() > saveSize); // Is event added?
			
			if (res != null && res.getSkeleton() instanceof GenericSkeleton) 
				activeSkeleton = (GenericSkeleton) res.getSkeleton();
			
			return result;
		}		
		else {
			queue.add(index, event);
			result = (queue.size() > saveSize); // Is event added?
			
			return result;
		}
	}
		
	/**
	 * Gets the next line of the string or file input.
	 * @return True if there was a line to read,
	 * false if this is the end of the input.
	 */
	private boolean getNextLine() throws IOException {
		
		if (reader.markSupported())
			if (reader.ready())
				reader.mark(1024); // Mark line start
		
		lineRead = reader.readLine();
		if ( lineRead != null ) {
			lineNumber++;
		}
		return (lineRead != null);
	}
	
	private String getFileTail() throws IOException {
		
		if (!reader.markSupported()) return "";
		
		reader.reset(); // Seek the last marked position
		String st = reader.readLine();
		
		if (st == null) return "";
		
		reader.reset(); // Seek the last marked position
		
		char[] buf = new char[st.length() + 100];
		int count = reader.read(buf);
		
		if (count != -1)
			return (new String(buf)).substring(st.length(), count);
		else
			return "";
	}

	protected final String getLineBreak() {
		
		return lineBreak;
	}
}
