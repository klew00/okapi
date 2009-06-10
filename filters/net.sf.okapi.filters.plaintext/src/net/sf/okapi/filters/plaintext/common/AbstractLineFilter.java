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

package net.sf.okapi.filters.plaintext.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Base class for the filters which input is processed line-upon-line. Provides low-level skeleton, events, 
 * and configuration handling mechanisms.  
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class AbstractLineFilter extends AbstractFilter {

	public static final String LINE_NUMBER = "line_number";	
	
	private final Logger logger = Logger.getLogger(getClass().getName());	
	private BufferedReader reader;
	private boolean canceled;
	private String encoding;
	
	private LinkedList<Event> queue;
	private String lineRead;
	private long lineNumber = 0;
	private TextProcessingResult lastResult;
	private int tuId;
	private int otherId;
	private String lineBreak;
	private int parseState = 0;	
	private String docName;
	private String srcLang;
	private boolean hasUTF8BOM;	
	
	public AbstractLineFilter() {
		
		super();		
	}
	
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
	 * Called by the filter every time a new input is being open.
	 * Used to update parameters and filter-specific variables.
	 */
	protected void filter_init() {
		// To be implemented in descendant classes
	}
	
	/**
	 * Called by the filter for every line read from the input
	 * @param lineContainer
	 * @return
	 */
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
		// To be overridden in descendant classes
		
		return TextProcessingResult.REJECTED;		
	}

	/**
	 * Called by the filter when there are no input lines (the input has been read).
	 * Used to control implementation-specific internal buffers.
	 * 
	 * @param lastChance True if there are no events in the queue, and if the method will not produce events, the filter will be finished.
	 */
	protected void filter_idle(boolean lastChance) {
		// To be implemented in descendant classes
	}
	
	/**
	 * Called once the filter has finished processing of the input.
	 * Use this method to do final clean-up. Do not use it for actions, that can create events, otherwise those events won't be sent.
	 * Use filter_exec() and filter_idle() for actual processing and event generation.
	 */
	protected void filter_done() {
		// To be implemented in descendant classes
	}
	
	protected Object filter_notify(String notification, Object info) {
		// Can be overridden in descendant classes, but will the call of superclass filter_notify()
		
		if (notification == Notification.FILTER_LINE_BEFORE_PROCESSING) {
			
			if (lastResult == TextProcessingResult.DELAYED_DECISION) 
				addLineBreak();
			
			return null;
		}
		
		return null;				
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
					
		encoding = input.getEncoding();
		srcLang = input.getSourceLanguage();
		hasUTF8BOM = input.hasUtf8Bom();
		lineBreak = input.getNewLineType();
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}
		
		commonOpen(input.getReader());
	}
		
	private void commonOpen (Reader inputReader) {
		
		parseState = 1;
		canceled = false;

		// Open the input reader from the provided reader
		
		if (inputReader instanceof BufferedReader ) {
			reader = (BufferedReader) inputReader;
		}
		else {
			reader = new BufferedReader(inputReader);
		}
				
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
		
		// Send start event
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLanguage(srcLang);
		startDoc.setFilterParameters(getParameters());
		startDoc.setLineBreak(lineBreak);
		startDoc.setType(getMimeType());
		startDoc.setMimeType(getMimeType());
		startDoc.setSkeleton(new GenericSkeleton());
		
		sendEvent(EventType.START_DOCUMENT, startDoc);				
	}

	protected final void logMessage (Level level, String text) {
		
		logger.log(level, String.format("Line %d: %s", lineNumber, text));
	}
	
	private Event getQueueEvent() {
		
		Event event = queue.poll();

		return event;		
	}
	
	public Event next() {
				
		// Cancel if requested
		if (canceled) {
			parseState = 0;
			queue.clear();
			
			sendEvent(EventType.CANCELED, null);
			return getQueueEvent(); 
		}
		
		// At least one event should always remain in the queue to provide a skeleton
		if (queue.size() > 1) { // 1Y
			return getQueueEvent();				
		}
		else { // 1N
			try {
				
				while (true) {
					if (getNextLine()) { // Sets lineRead // 2Y
						// 3 
						// Process read line
					
						switch (lastResult) { // Result of previous processing
						
						case REJECTED:
						case ACCEPTED:						
							// Add line break to skeleton
							addLineBreak();					
							break;
							
						case DELAYED_DECISION:
							break;
						}
						
						TextContainer lineContainer = new TextContainer(lineRead);
							
						lineContainer.setProperty(new Property(LINE_NUMBER, String.valueOf(lineNumber), true)); // Prop of source, not TU
						lastResult = filter_exec(lineContainer); // Can modify text and codes of lineContainer  
						
						switch (lastResult) { 
						
							case REJECTED:
								// Add the whole line to the skeleton
								GenericSkeleton skel = getActiveSkeleton();
								if (skel == null) break;
								if (lineContainer == null) {
									lastResult = TextProcessingResult.NONE;
									break;
								}
								skel.append(lineContainer.toString());						
								break;
								
							case ACCEPTED:						
								break;
								
							case DELAYED_DECISION:
								break;
						}						
						
						if (queue.size() > 1) { // 4Y
							return getQueueEvent();
						}				
						else { // 4N
							continue; 
						}
					} 
					else { // 2N
						break;
					}	
				}		
						
						if (queue.size() > 1) { // 10Y							
							return getQueueEvent();
						}
						else { // 10N							
							// 5
							filter_idle(queue.size() == 0);
							
							// 6
							if (queue.size() > 0) { // 6Y							
								return getQueueEvent();
							}
							else { // 6N
								filter_done(); // 7
																
									// The ending event
									Ending ending = new Ending(String.valueOf(++otherId));
									GenericSkeleton skel = new GenericSkeleton();
									ending.setSkeleton(skel);
									
									String tail = getFileTail();					
									if (!Util.isEmpty(tail)) 
										if (skel != null) skel.append(tail);
									
									parseState = 0;
									return new Event(EventType.END_DOCUMENT, ending);
							}
						}
					}
				
			catch ( IOException e ) {
				throw new OkapiIOException(e);
			}
		} 
		
	}
	
	protected final boolean sendEvent(EventType eventType, IResource res) {
		
		return sendEvent(-1, eventType, res);
	}
	
	protected final GenericSkeleton getActiveSkeleton() {
		// Return a skeleton of the last event in the queue
		
		if (queue == null) return null;
		if (Util.isEmpty(queue)) return null;
		
		Event event = queue.getLast();
		if (event == null) return null;

		IResource res = event.getResource();
		if (res == null) return null;
		
		ISkeleton skel = res.getSkeleton();
		
		if (skel == null) {
			
		// Force skeleton
			skel = new GenericSkeleton();
			res.setSkeleton(skel);
		}
			
		if (!(skel instanceof GenericSkeleton)) return null; 
		
		return (GenericSkeleton) skel;
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

	protected final void addLineBreak() {

		GenericSkeleton skel = getActiveSkeleton();
		
		if (skel != null && lineNumber > 1) 
			skel.append(lineBreak);
	}
}
