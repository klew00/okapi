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

package net.sf.okapi.filters.mosestext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Implements the IFilter interface for Moses Text files.
 * One line per segment, normally in UTF-8. No text unit separator.
 */
public class MosesTextFilter implements IFilter {

	public static final String MOSESTEXT_MIME_TYPE = "text/x-mosestext";

	private BufferedReader reader;
	private String lineBreak;
	private Event event;
	private IdGenerator tuIdGen;
	private EncoderManager encoderManager;
	
	public MosesTextFilter () {
	}
	
	public void cancel () {
	}

	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( IOException e) {
			throw new OkapiIOException(e);
		}
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter(), getEncoderManager());
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MOSESTEXT_MIME_TYPE,
			getClass().getName(),
			"Moses Text Default",
			"Default Moses Text configuration.",
			null,
			".txt;"));
		return list;
	}

	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MOSESTEXT_MIME_TYPE, "net.sf.okapi.common.encoder.DefaultEncoder");
		}
		return encoderManager;
	}
	
	public String getDisplayName () {
		return "Moses Text Filter (BETA)";
	}

	public String getMimeType () {
		return MOSESTEXT_MIME_TYPE;
	}

	public String getName () {
		return "okf_mosestext";
	}

	public IParameters getParameters () {
		return null; // Not used
	}

	public boolean hasNext () {
		return (event != null);
	}

	public Event next () {
		// The current event is ready, now get the next one
		Event eventToSend = event;
		event = null; // Next one is reset to none

		// Stop the process after the end of document
		if ( eventToSend.getEventType() == EventType.END_DOCUMENT ) {
			return eventToSend;
		}
		
		// Else: compute the next event
		try {
			while ( true ) {
				String line = reader.readLine();
				if ( line == null ) {
					event = new Event(EventType.END_DOCUMENT, new Ending("ed"));
				}
				else {
					// Skip empty or space-only lines
					String tmp = line.trim();
					if ( tmp.isEmpty() ) continue;
					// Else: process the line
					event = processLine(line);
				}
				return eventToSend;
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
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
		
		try {
			reader = new BufferedReader(new InputStreamReader(detector.getInputStream(), encoding));
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
		
		tuIdGen = new IdGenerator(null);
		
		// Set the start event
		StartDocument startDoc = new StartDocument("sd");
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLocale(input.getSourceLocale());
		startDoc.setLineBreak(lineBreak);
		startDoc.setFilterParameters(getParameters());
		startDoc.setFilterWriter(createFilterWriter());
		startDoc.setType(MOSESTEXT_MIME_TYPE);
		startDoc.setMimeType(MOSESTEXT_MIME_TYPE);
		startDoc.setMultilingual(false);
		event = new Event(EventType.START_DOCUMENT, startDoc);
	}

	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// Not used
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	private Event processLine (String line) {
		// All is text for now
		//TODO: What about the line-breaks?
		
		TextUnit tu = new TextUnit(tuIdGen.createId());
		tu.setSourceContent(new TextFragment(line));
		tu.setPreserveWhitespaces(true);
		GenericSkeleton skel = new GenericSkeleton();
		skel.addContentPlaceholder(tu);
		skel.add(lineBreak);
		tu.setSkeleton(skel);
		return new Event(EventType.TEXT_UNIT, tu);
	}

}
