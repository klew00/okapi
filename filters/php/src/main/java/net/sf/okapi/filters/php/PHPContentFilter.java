/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.php;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
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
 * Implements the IFilter interface for PHP content. This filter is
 * expected to be called from a parent filter that processed the container.
 */
public class PHPContentFilter implements IFilter {

	private static final String MIMETYPE = "x-application/php";
	
	private static final int STRTYPE_SINGLEQUOTED = 0;
	private static final int STRTYPE_DOUBLEQUOTED = 1;
	private static final int STRTYPE_HEREDOC = 2;
	private static final int STRTYPE_NOWDOC = 3;
	
	private Parameters params;
	private String srcLang;
	private String lineBreak;
	private String inputText;
	private int tuId;
	private int otherId;
	private LinkedList<Event> queue;
	private boolean hasNext;
	private int current;
	private int startSkl;
	private int startStr;
	private int endStr;
	
	public PHPContentFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		// TODO: Support cancel
	}

	public void close () {
		// Nothing to do
		hasNext = false;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"PHP Content Default",
			"Default PHP Content configuration."));
		return list;
	}

	public String getDisplayName () {
		return "PHP Content Filter (ALPHA)";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public String getName () {
		return "okf_phpcontent";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return hasNext;
	}

	public Event next () {
		if ( !hasNext ) return null;
		if ( queue.size() == 0 ) {
			parse();
		}
		Event event = queue.poll();
		if ( event.getEventType() == EventType.END_DOCUMENT ) {
			hasNext = false;
		}
		return event;
	}

	public void open (RawDocument input) {
		open(input, true);
	}

	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		close(); // Just in case resources need to be freed
		
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
		detector.detectAndRemoveBom();
		input.setEncoding(detector.getEncoding());
		String encoding = input.getEncoding();
		
		BufferedReader reader = null;		
		try {
			reader = new BufferedReader(new InputStreamReader(detector.getInputStream(), encoding));
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiUnsupportedEncodingException(
				String.format("The encoding '%s' is not supported.", encoding), e);
		}
		srcLang = input.getSourceLanguage();
		lineBreak = detector.getNewlineType().toString();
		boolean hasUTF8BOM = detector.hasUtf8Bom();
		String docName = null;
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}
		
		StringBuilder tmp = new StringBuilder();
		char[] buf = new char[2048];
		int count = 0;
		try {
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}
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
		
		// Set the input string
		inputText = tmp.toString().replace(lineBreak, "\n");
		current = -1;
		tuId = 0;
		otherId = 0;
		// Compile code finder rules
		if ( params.useCodeFinder ) {
			params.codeFinder.compile();
		}

		// Set the start event
		queue = new LinkedList<Event>();
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLanguage(srcLang);
		startDoc.setLineBreak(lineBreak);
		startDoc.setFilterParameters(getParameters());
		startDoc.setFilterWriter(createFilterWriter());
		startDoc.setType(MIMETYPE);
		startDoc.setMimeType(MIMETYPE);
		startDoc.setMultilingual(false);
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));
		hasNext = true;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private void parse () {
		int prevState = 0;
		int state = 0;
		StringBuilder buf = null;
		StringBuilder possibleEndKey = null;
		String endKey = null;
		int blockType = STRTYPE_HEREDOC;
		char ch = 0x0;
		char pch = 0x0;
		
		if ( current < 0 ) startSkl = 0;
		else startSkl = current;
		endStr = -1;
		
		while ( true ) {
			if ( current+1 >= inputText.length() ) {
				// End of input
				Ending ending = new Ending(String.valueOf(++otherId));
				if ( startSkl < inputText.length() ) {
					GenericSkeleton skl = new GenericSkeleton(inputText.substring(startSkl).replace("\n", lineBreak));
					ending.setSkeleton(skl);
				}
				queue.add(new Event(EventType.END_DOCUMENT, ending));
				return;
			}
			
			if ( state == 0 ) {
				// In state 0: remember the last non-whitespace chararcter
				if ( !Character.isWhitespace(ch) ) pch = ch;
			}
			ch = inputText.charAt(++current);
			
			switch ( state ) {
			case 0:
				switch ( ch ) {
				case '/':
					prevState = state;
					state = 1; // After '/'
					continue;
				case '\\': // Escape prefix
					prevState = state;
					state = 4;
					continue;
				case '<': // Test for heredoc/nowdoc
					if ( inputText.length() > current+2) {
						if (( inputText.charAt(current+1) == '<' ) 
							&& ( inputText.charAt(current+1) == '<' )) {
							// Gets the keyword
							current+=2;
							buf = new StringBuilder();
							state = 6;
							continue;
						}
						// Else: fall thru
					}
					// Else: not a heredoc
					continue;
				case '\'': // Single-quoted string
					prevState = state;
					state = 9;
					startStr = current;
					buf = new StringBuilder();
					continue;
				case '"': // Double-quoted string
					prevState = state;
					state = 10;
					startStr = current;
					buf = new StringBuilder();
					continue;
				}
				continue;
				
			case 1: // After initial '/'
				if ( ch == '/' ) {
					state = 2; // Wait for EOL/EOS
					continue;
				}
				if ( ch == '*' ) {
					state = 3; // wait for slash+star
					continue;
				}
				// Else: Was a normal '/'
				state = prevState;
				current--;
				continue;
				
			case 2: // In single-line comment, wait for EOL/EOS
				if ( ch == '\n' ) {
					state = prevState;
					continue;
				}
				continue;
				
			case 3: // In multi-line comment, wait for star+slash
				if ( ch == '*' ) {
					state = 5;
				}
				continue;
				
			case 4: // After backslash for escape
				state = prevState;
				continue;
				
			case 5: // After '*', expect slash (from multi-line comment)
				if ( ch == '/' ) {
					state = prevState;
					continue;
				}
				// Else: 
				state = 3; // Go back to comment
				current--;
				continue;
				
			case 6: // After <<<, getting the heredoc key
				if ( Character.isWhitespace(ch) ) {
					// End of key
					if ( buf.toString().startsWith("'") ) {
						blockType = STRTYPE_NOWDOC;
						endKey  = Util.trimEnd(Util.trimStart(buf.toString(), "'"), "'");
					}
					else if ( buf.toString().startsWith("\"") ) {
						blockType = STRTYPE_HEREDOC;
						endKey  = Util.trimEnd(Util.trimStart(buf.toString(), "\""), "\"");
					}
					else {
						blockType = STRTYPE_HEREDOC;
						endKey = buf.toString();
					}
					// Change state to wait for the end of heredoc/nowdoc
					state = 7;
					startStr = current;
					buf = new StringBuilder();
					continue;
				}
				else {
					buf.append(ch);
				}
				continue;
				
			case 7: // Inside a heredoc/nowdoc entry, wait for linebreak
				if ( ch == '\n' ) {
					endStr = current;
					possibleEndKey = new StringBuilder();
					state = 8;
				}
				else {
					buf.append(ch);
				}
				continue;
				
			case 8: // Parsing the end-key for the heredoc/nowdoc entry
				switch ( ch ) {
				case '\n':
					if ( possibleEndKey.length() > 0 ) { // End of key
						if ( processString(buf.toString(), pch, blockType) ) {
							return;
						}
						else {
							state = prevState;
							continue;
						}
					}
					// Else: Sequential line-breaks
					buf.append("\n"); // Append the previous
					endStr = current; // Reset possible ending point
					// and stay in this state
					break;
				case ';':
					if ( possibleEndKey.length() > 0 ) { // End of key
						if ( processString(buf.toString(), pch, blockType) ) {
							return;
						}
						else {
							state = prevState;
							continue;
						}
					}
					// Else: fall thru (';' in string and back to previous state)
				default:
					possibleEndKey.append(ch);
					if ( !endKey.startsWith(possibleEndKey.toString()) ) {
						// Not the end key, just part of the text
						state = 7; // back to inside heredoc entry
						// Don't forget the initial linebreak of case 7
						buf.append("\n"+possibleEndKey);
					}
				}
				continue;
				
			case 9: // Inside a single-quoted string, wait for closing single quote
				if ( ch == '\'' ) {
					// End of string
					endStr = current;
					if ( processString(buf.toString(), pch, STRTYPE_SINGLEQUOTED) ) {
						return;
					}
					else {
						state = prevState;
						continue;
					}
				}
				else if ( ch == '\\' ) {
					if ( inputText.length() > current+1 ) {
						buf.append('\\');
						buf.append(inputText.charAt(++current));
					}
					else {
						throw new OkapiIllegalFilterOperationException("Unexpected end.");
					}
				}
				else {
					buf.append(ch);
				}
				continue;

			case 10: // Inside a double-quoted string, wait for closing double quote
				if ( ch == '"' ) {
					// End of string
					endStr = current;
					if ( processString(buf.toString(), pch, STRTYPE_DOUBLEQUOTED) ) {
						return;
					}
					else {
						state = prevState;
						continue;
					}
				}
				else if ( ch == '\\' ) {
					if ( inputText.length() > current+1 ) {
						buf.append('\\');
						buf.append(inputText.charAt(++current));
					}
					else {
						throw new OkapiIllegalFilterOperationException("Unexpected end.");
					}
				}
				else {
					buf.append(ch);
				}
				continue;
			}
		}
	}

	// Returns true if we have an event to send
	private boolean processString (String text,
		char lastNonWSChar,
		int stringType)
	{
		// Do not extract strings used as array key
		if ( lastNonWSChar == '[' ) return false;
		
		TextUnit tu = new TextUnit(null, text);

		boolean preserveWS = false;
		switch ( stringType ) {
		case STRTYPE_HEREDOC:
			preserveWS = true;
			tu.setType("x-heredoc");
			break;
		case STRTYPE_NOWDOC:
			preserveWS = true;
			tu.setType("x-nowdoc");
			break;
		case STRTYPE_SINGLEQUOTED:
			tu.setType("x-singlequoted");
			break;
		case STRTYPE_DOUBLEQUOTED:
			tu.setType("x-doublequoted");
			break;
		}
		
		if ( params.useCodeFinder ) {
			params.codeFinder.process(tu.getSourceContent());
		}
		
		if ( !tu.getSource().hasText(false) ) return false; // Nothing to localize 

		tu.setId(String.valueOf(++tuId));
		tu.setPreserveWhitespaces(preserveWS);
		GenericSkeleton skl = new GenericSkeleton();
		tu.setSkeleton(skl);
		
		if ( startStr > startSkl ) {
			skl.add(inputText.substring(startSkl, startStr+1).replace("\n", lineBreak));
		}
		skl.addContentPlaceholder(tu);
		if ( endStr < current ) {
			skl.add(inputText.substring(endStr, current).replace("\n", lineBreak));
		}
		queue.add(new Event(EventType.TEXT_UNIT, tu));
		return true;
	}

}
