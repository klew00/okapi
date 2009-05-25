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
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * <code>PlainTextFilter</code> extracts lines of input text, separated by line terminators.
 * The filter is aware of the following line terminators:
 * <ul><li>Carriage return character followed immediately by a newline character ("\r\n")
 * <li>Newline (line feed) character ("\n")
 * <li>Stand-alone carriage return character ("\r")</ul><p> 
 */

public class PlainTextFilter implements IFilter {	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private IParameters params;			// Plain Text Filter's parameters
	private BufferedReader reader;
	private boolean canceled;
	private String encoding;
	private TextUnit tuRes;
	private LinkedList<Event> queue;
	private String lineRead;
	private String lineToProcess = "";
	private int lineNumber = 0;
	private TextProcessingResult lastResult;
	private int tuId;
	private int otherId;
	private String lineBreak;
	private int parseState = 0;
	private GenericSkeleton skel;
	private GenericSkeleton activeSkeleton = null; // Skeleton of the last event in the queue
	private String docName;
	private String srcLang;
	private boolean hasUTF8BOM;
	private String filterName;
	private String mimeType;
	private boolean reading = false;
	
	private boolean preserveWS = true;
	private boolean useCodeFinder = false;
	private InlineCodeFinder codeFinder = null;
	
// Commons	
	public void cancel() {
		canceled = true;
	}

	public void close() {
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
	
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			mimeType,
			getClass().getName(),
			"Table Filter",
			"Table-like files such as tab-delimited, CSV, fixed-width columns, etc."));
		return list;
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
			BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input, encoding);
			encoding = detector.getEncoding();
			if (encoding == null) throw new OkapiBadFilterInputException("Encoding cannot be detected, probably empty input.");
			
			hasUTF8BOM = detector.hasUtf8Bom();
			lineBreak = detector.getNewlineType().toString();
			detector = null; // Free/close
			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
			bis.detectEncoding(); // Needed to skip over the BOM
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

	public void setParameters(IParameters params) {
		this.params = params;
	}
	
	private void commonOpen (Reader inputReader) {
		parseState = 1;
		canceled = false;

		// Open the input reader from the provided reader
		reader = new BufferedReader(inputReader);
		try {
			reader.mark(1024); // Mark line start (needed for 1-line files, reader.ready() is false before first read)
		} 
		catch (IOException e) {
		} 
		
		// Initialize variables
		tuId = 0;
		otherId = 0;
		lineNumber = 0;
		lineToProcess = "";
		lastResult = TextProcessingResult.NONE;		
		preserveWS = true;
				
		if (params instanceof Parameters) {
			Parameters prm = (Parameters)params;
			
			preserveWS = prm.preserveWS;
			
			useCodeFinder = prm.useCodeFinder;
			codeFinder = prm.codeFinder;
			
			if (codeFinder == null) useCodeFinder = false;
			if (useCodeFinder) {
				codeFinder.addRule(prm.regularExpressionForEmbeddedMarkup);
				codeFinder.compile();						
			}
		}
		
		queue = new LinkedList<Event>();
		
		init(); // Initialize the filter with implementation-specific parameters (protected method)
		reading = true; // Activate input reading
		
		// Send start event
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLanguage(srcLang);
		startDoc.setFilterParameters(params);
		startDoc.setFilter(this);
		startDoc.setLineBreak(lineBreak);
		startDoc.setType(getMimeType());
		startDoc.setMimeType(getMimeType());
		startDoc.setSkeleton(new GenericSkeleton());
		
		sendEvent(EventType.START_DOCUMENT, startDoc);				
	}
			
// Specifics
	public PlainTextFilter() {
		setName("okf_plaintext");
		setMimeType(MimeTypeMapper.PLAIN_TEXT_MIME_TYPE);		
		setParameters(new Parameters());	// Plain Text Filter parameters			
	}
	
	protected void init() {
		// To be overridden in descendant classes 
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
					
				case COMBINE_WITH_NEXT:
					lineToProcess += "\n";
					break;
				}
				
				if (Util.isEmpty(lineToProcess)) lineToProcess = lineRead;
				
				lastResult = processLine(lineToProcess); // Modifies lineToProcess
				switch (lastResult) { 
				
					case REJECTED:
						// Add the whole line to skeleton
						if (activeSkeleton != null) activeSkeleton.append(lineToProcess);
						lineToProcess = "";
						continue;
						
					case ACCEPTED:						
						lineToProcess = "";
						break;
						
					case COMBINE_WITH_NEXT:
						lineToProcess += lineRead;
						continue;
				}
								
				// Return first event in the queue 
				if (queue.size() > 0) {
					return queue.poll();				
				}
				else
					activeSkeleton = null;
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
		
	private String trimAndUnescape(String line, GenericSkeleton skel) {
		// Can be overridden in descendant classes
		
		if (Util.isEmpty(line)) return line;
		
		String tmp = Util.trimStart(line, "\t\r\n \f");
	
		if ((skel != null) && (tmp.length() < line.length())) 
			skel.add(line.substring(0, line.length() - tmp.length()));
		
		if (tmp == null) return null;
		if (tmp != "") tmp = unescape(tmp);
		
		return tmp;
	}
	
	protected boolean checkTU(String tu) {

		return (!Util.isEmpty(tu));		
	}
	
	protected TextProcessingResult processLine(String line) {
		
		skel = new GenericSkeleton();		
		String tu = trimAndUnescape(line, skel);
		
		if (!checkTU(tu)) return TextProcessingResult.REJECTED;
											
		tuRes = new TextUnit(String.valueOf(++tuId), tu);
		tuRes.setName(tuRes.getId());
		tuRes.setMimeType(getMimeType()); 		
		tuRes.setSkeleton(skel);		
		skel.addContentPlaceholder(tuRes);
		
		tuRes.setPreserveWhitespaces(preserveWS);
		
		if (useCodeFinder) 
			codeFinder.process(tuRes.getSourceContent());
								
		sendEvent(EventType.TEXT_UNIT, tuRes);
		
		return TextProcessingResult.ACCEPTED;
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

	/**
	 * Unescapes slash-u+HHHH characters in a string.
	 * @param text The string to convert.
	 * @return The converted string.
	 */
	//TODO: Deal with escape ctrl, etc. \n should be converted
	private String unescape (String text) {
		final String INVALID_UESCAPE = "Invalid Unicode escape sequence '%s'";
		
		if (text == null) return text;
		if (text == "") return text;
		
		if ( text.indexOf('\\') == -1 ) return text;
		
		StringBuilder tmpText = new StringBuilder();
		for ( int i = 0; i < text.length(); i++ ) {
			if ( text.charAt(i) == '\\' ) {
				switch ( text.charAt(i+1) ) {
				case 'u':
					if ( i+5 < text.length() ) {
						try {
							int nTmp = Integer.parseInt(text.substring(i+2, i+6), 16);
							tmpText.append((char)nTmp);
						}
						catch ( Exception e ) {
							logMessage(Level.WARNING,
								String.format(INVALID_UESCAPE, text.substring(i+2, i+6)));
						}
						i += 5;
						continue;
					}
					else {
						logMessage(Level.WARNING,
							String.format(INVALID_UESCAPE, text.substring(i+2)));
					}
					break;
				case '\\':
					tmpText.append("\\\\");
					i++;
					continue;
				case 'n':
					tmpText.append("\n");
					i++;
					continue;
				case 't':
					tmpText.append("\t");
					i++;
					continue;
				}
			}
			else tmpText.append(text.charAt(i));
		}
		return tmpText.toString();
	}

	private void logMessage (Level level, String text) {
		logger.log(level, String.format("Line %d: %s", lineNumber, text));
	}
		
}	
	