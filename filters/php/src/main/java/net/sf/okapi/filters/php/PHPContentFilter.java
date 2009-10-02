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
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Implements the IFilter interface for PHP files.
 */
public class PHPContentFilter implements IFilter {

	private static final String MIMETYPE = "x-application/php";
	
	private Parameters params;
	private String srcLang;
	private String lineBreak;
	private String inputText;
	private int tuId;
	private int otherId;
	private LinkedList<Event> queue;
	private boolean hasNext;
	private int current;
	
	public PHPContentFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		// TODO Auto-generated method stub
	}

	public void close () {
		// Nothing to do
		hasNext = false;
	}

	public IFilterWriter createFilterWriter () {
		// TODO Auto-generated method stub
		return null;
	}

	public ISkeletonWriter createSkeletonWriter () {
		// TODO Auto-generated method stub
		return null;
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
		return "PHP Content Filter";
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
		
		//TODO: Optimize this with a better 'readToEnd()'
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
		StringBuilder heredocKey = null;
		
		while ( true ) {
			if ( current+1 >= inputText.length() ) {
				// End of input
				Ending ending = new Ending(String.valueOf(++otherId));
				queue.add(new Event(EventType.END_DOCUMENT, ending));
				return;
			}
			
			char ch = inputText.charAt(++current);
			
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
				case '<': // Test for heredoc
					if ( inputText.length() > current+2) {
						if (( inputText.charAt(current+1) == '<' ) 
							&& ( inputText.charAt(current+1) == '<' )) {
							// Gets the keyword
							current+=2;
							heredocKey = new StringBuilder();
							state = 6;
							continue;
						}
						// Else: fall thru
					}
					// Else: not a heredoc
					continue;
				case '\'': // Single-quoted string
					prevState = state;
					state = 8;
					buf = new StringBuilder();
					continue;
				case '"': // Double-quoted string
					prevState = state;
					state = 9;
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
				
			case 5: // After '*', expect (from multi-line comment)
				if ( ch == '/' ) {
					state = prevState;
					continue;
				}
				// Else: 
				state = 4; // Go back to comment
				current--;
				continue;
				
			case 6: // after <<<, getting the heredoc key
				if ( Character.isWhitespace(ch) ) {
					// End of key
					state = 7; // wait for the end of heredoc
					continue;
				}
				else {
					heredocKey.append(ch);
				}
				continue;
				
			case 7: // End of end of heredoc (linebreak+key+';' or line break)
				if ( ch == '\n' ) {
					//TODO
				}
				//TODO
				continue;
				
			case 8: // Inside a single-quoted string, wait for closing single quote
				if ( ch == '\'' ) {
					// End of string
					processString(buf.toString());
					return;
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

			case 9: // Inside a double-quoted string, wait for closing double quote
				if ( ch == '"' ) {
					// End of string
					processString(buf.toString());
					return;
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

	private void processString (String text) {
		TextUnit tu = new TextUnit(String.valueOf(++tuId), text);
		queue.add(new Event(EventType.TEXT_UNIT, tu));
	}

}
