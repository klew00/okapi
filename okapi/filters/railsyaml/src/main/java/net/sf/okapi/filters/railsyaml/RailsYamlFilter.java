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

package net.sf.okapi.filters.railsyaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

/**
 * @author PerkinsGW
 */
public class RailsYamlFilter extends AbstractFilter {

	private static final int RESULT_END     = 0;
	private static final int RESULT_ENTRY    = 1;
	private static final int RESULT_DATA    = 2;

	private static final int PARSE_STOP = 0;
	private static final int PARSE_CONTINUE = 1;
	
	private static final int ITEM_STRING = 0;
	private static final int ITEM_SKELETON = 1;
	private static final int ITEM_COMMENT = 2;
	private static final int ITEM_CONTAINER = 3;
	private static final int ITEM_NONE = 4;

	private static final String YAML_INDENTATION_REGEX = "( *)(.*)";
	private static final Pattern YAML_INDENTATION_PATTERN = Pattern.compile(YAML_INDENTATION_REGEX);
	private static final int YAML_INDENTATION_INDEX = 1;
	
	private static final String YAML_STRING_REGEX = "( *)(.*)(: *\")(.*)(\".*)";
	private static final Pattern YAML_STRING_PATTERN = Pattern.compile(YAML_STRING_REGEX);
	private static final int YAML_STRING_INDEX_KEY = 2;
	private static final int YAML_STRING_INDEX_TU = 4;

	private static final String YAML_MIME_TYPE = "text/x-yaml";
	
	private YamlEventBuilder eventBuilder;

	private int parseState;
	private BufferedReader reader;
	private int tuid;
	private ITextUnit tuEntry;
	private GenericSkeleton skel;
	private String key = "";
	private int indentation;
	private Stack<KeyPair> keyStack;
	private boolean isUtf8Encoding = false;
	private boolean isUtf8Bom = false;
	private Parameters params;
	
	private class KeyPair {
		public int indent;
		public String key;
		public KeyPair(int i, String k) {
			indent = i;
			key = k;
		}
	}
	
	public RailsYamlFilter () {		
		setMimeType(YAML_MIME_TYPE);
		setFilterWriter(createFilterWriter());
		
		setName("okf_railsyaml");
		setDisplayName("Ruby on Rails YAML Filter");
		addConfiguration(new FilterConfiguration(getName(), 
			YAML_MIME_TYPE, 
			getClass().getName(),
			"Ruby on Rails YAML", "Ruby on Rails YAML files",
			null, ".yml;"));
		
		parseState = PARSE_STOP;
		indentation = 0;
		keyStack = new Stack<KeyPair>();
		params = new Parameters();
	}

	@Override
	protected boolean isUtf8Bom() {
		return isUtf8Bom;
	}

	@Override
	protected boolean isUtf8Encoding() {
		return isUtf8Encoding;
	}

	@Override
	public void close () {
		try {
			if(reader != null) {
				reader.close();
				reader = null;
			}
			parseState = PARSE_STOP;
		}
		catch(IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public boolean hasNext () {
		return (parseState  != PARSE_STOP);
	}

	@Override
	public Event next () {
		while (getEventBuilder().hasQueuedEvents()) {
			return getEventBuilder().next();
		}
		
		int result;
		boolean resetBuffer = true;
		do {
			switch( result = readEntry(resetBuffer) ) {
			case RESULT_DATA:
				resetBuffer = false;
				break;
			case RESULT_ENTRY:
				// TODO Add the TextUnit event
				Event e = new Event(EventType.TEXT_UNIT, 
					getEventBuilder().postProcessTextUnit(tuEntry));
				getEventBuilder().addFilterEvent(e);
				resetBuffer = true;
				break;
			default:
				resetBuffer = true;
				break;
			}

			if (getEventBuilder().hasQueuedEvents()) {
				break;
			}

		} while (result != RESULT_END);
		
		if(parseState == PARSE_STOP) {
			getEventBuilder().flushRemainingTempEvents();
			getEventBuilder().addFilterEvent(createEndFilterEvent());
		}

		return getEventBuilder().next();
	}

	private int readEntry(boolean resetBuffer) {
		if(resetBuffer) {
			skel = new GenericSkeleton();
		}
		
		String line;
		try {
			line = reader.readLine();
			
			if(line == null) {
				parseState = PARSE_STOP;
				return RESULT_END;
			}
			
			switch(parseLine(line)) {
			case ITEM_COMMENT:
//				skel.append(line);
				getEventBuilder().addDocumentPart(line + getNewlineType());
				return RESULT_DATA;
			case ITEM_CONTAINER:
				getEventBuilder().addDocumentPart(line + getNewlineType());
//				skel.append(line);
				return RESULT_DATA;
			case ITEM_STRING:
				return RESULT_ENTRY;
			case ITEM_SKELETON:
				getEventBuilder().addDocumentPart(line + getNewlineType());
//				skel.append(line);
				return RESULT_DATA;
			case ITEM_NONE:
				getEventBuilder().addDocumentPart(line + getNewlineType());
				return RESULT_DATA;
			}
		}
		catch(IOException e) {
			throw new OkapiIOException(e);
		}
		return RESULT_END;
	}

	private int parseLine(String line) {
		String tmpKey = "";

		String trimLine = line.trim();
		
		if ( trimLine.length() == 0 ) {
			return ITEM_SKELETON;
		}
		if ( trimLine.startsWith("#") ) {
			return ITEM_COMMENT;
		}
		
		indentation = getIndentation(line);

		int colonIndex = trimLine.indexOf(':');
		if(colonIndex == (trimLine.length() - 1) ) {
			// Remove any keys that aren't our parents
			while( !keyStack.empty() && (indentation <= keyStack.peek().indent) ) {
				keyStack.pop();
			}
			keyStack.push(new KeyPair(indentation, trimLine.substring(0, colonIndex)));
			key = generateKey();
			return ITEM_CONTAINER;
		}

		key = generateKey();

		Matcher matcher = YAML_STRING_PATTERN.matcher(line);
		if(matcher.matches()) {
			// Remove any keys that aren't our parents
			while( !keyStack.empty() && (keyStack.peek().indent >= indentation) ) {
				keyStack.pop();
			}
			key = generateKey();
			tmpKey = line.substring(matcher.start(YAML_STRING_INDEX_KEY), matcher.end(YAML_STRING_INDEX_KEY)).trim();

			tuEntry = new TextUnit(String.valueOf(++tuid), matcher.group(YAML_STRING_INDEX_TU));
			tuEntry.setName(key + tmpKey);
			tuEntry.setMimeType(getMimeType());
			tuEntry.setPreserveWhitespaces(true);
			skel.append(line.substring(0, matcher.start(YAML_STRING_INDEX_TU)));
			skel.addContentPlaceholder(tuEntry, null);
			skel.append(matcher.group(YAML_STRING_INDEX_TU + 1));
			// TODO Generalize the line break character
			skel.append(getNewlineType());
			tuEntry.setSkeleton(skel);
			Property propIndentation = new Property("indentation", String.valueOf(indentation));
			tuEntry.setProperty(propIndentation);
			if ( params.useCodeFinder ) {
				params.codeFinder.process(tuEntry.getSource().getFirstContent());
			}

//			System.out.println(tuEntry.getName() + "=" + tuEntry.toString());
			return ITEM_STRING;
		}
		
		return ITEM_NONE;
	}

	private int getIndentation(String line) {
		Matcher m = YAML_INDENTATION_PATTERN.matcher(line);
		if(m.matches()) {
			return m.group(YAML_INDENTATION_INDEX).length();
		}
		else {
			return 0;
		}
	}
	
	private String generateKey() {
		String key = "";
		for ( KeyPair kp : keyStack ) {
			key += kp.key + ".";
		}
		return key;
	}

	@Override
	public void open (RawDocument input) {
		open(input, true);
	}

	@Override
	public void open (RawDocument input, boolean generateSkeleton) {
		close();
		
		// Set the parseState to 
		parseState = PARSE_CONTINUE;
		tuid = 0;
		// Compile code finder rules
		if ( params.useCodeFinder ) {
			params.codeFinder.compile();
		}
		
		// Handle the encoding
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
		detector.detectAndRemoveBom();
		setNewlineType(detector.getNewlineType().toString());
		input.setEncoding(detector.getEncoding());
		String encoding = input.getEncoding();
		isUtf8Bom = detector.hasUtf8Bom();
		isUtf8Encoding = detector.hasUtf8Encoding();
		if ( input.getInputURI() != null ) {
			setDocumentName(input.getInputURI().getPath());
		}
		
		
		setOptions(input.getSourceLocale(), input.getTargetLocale(), encoding, generateSkeleton);
		
		// Get a Reader on the RawDocument
		try {
			reader = new BufferedReader(new InputStreamReader(detector.getInputStream(), encoding));
		}
		catch(UnsupportedEncodingException e) {
			throw new OkapiUnsupportedEncodingException(String.format("The encoding %s is not supported.", encoding), e);
		}
		
		// Start the EventBuilder
		// create EventBuilder with document name as rootId
		if (eventBuilder == null) {
			eventBuilder = new YamlEventBuilder(getDocumentName(), isSubFilter());
		} else {
			eventBuilder.reset(getDocumentName(), isSubFilter());
		}
		
		getEventBuilder().addFilterEvent(createStartFilterEvent());
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
	
	private YamlEventBuilder getEventBuilder () {
		return eventBuilder;
	}

}
