/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.transtable;

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
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class TransTableFilter implements IFilter {

	private static String MIMETYPE = "text/x-transtable";
	
	private BufferedReader reader = null;
	private boolean canceled;
	private String encoding;
	private long line;
	private IdGenerator otherId;
	private String docName;
	private LinkedList<Event> queue;
	private LocaleId trgLoc;
	private String lineBreak;
	private boolean hasUTF8BOM;
	private EncoderManager encoderManager;
	private boolean firstLineDone;
	private boolean hasNext;
	
	public TransTableFilter () {
	}

	public void cancel () {
		canceled = true;
	}

	public void close () {
		if ( reader != null ) {
			try {
				reader.close();
			}
			catch ( IOException e ) {
				// Let it go
			}
			reader = null;
		}
	}

	public String getName () {
		return "okf_transtable";
	}
	
	public String getDisplayName () {
		return "TransTable Filter";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"Translation Table Default",
			"Default TransTable configuration."));
		return list;
	}

	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setAllKnownMappings();
		}
		return encoderManager;
	}
	
	public IParameters getParameters () {
		return null;
	}

	public boolean hasNext () {
		return hasNext;
	}

	public Event next () {
		try {
			// Cancel if requested
			if ( canceled ) {
				queue.clear();
				queue.add(new Event(EventType.CANCELED));
			}
			
			// Process queue if it's not empty yet
			if ( queue.size() > 0 ) {
				return queue.poll();
			}
			
			// Next entry in the table
			String tmp;
			while ( true ) {
				try {
					tmp = reader.readLine();
				}
				catch ( IOException e ) {
					throw new OkapiIOException("Error reading the table.", e);
				}
				// Process the line
				if ( tmp == null ) {
					// End of the file
					hasNext = false;
					Ending ending = new Ending(otherId.createId());
					return new Event(EventType.END_DOCUMENT, ending);
				}
				else {
					line++;
					// Check for empty or white-spaces-only lines (allowed)
					if ( tmp.trim().length() == 0 ) {
						continue;
					}
					
					if ( !firstLineDone ) {
						firstLineDone = true;
						// First entry is just information
						String[] parts = tmp.split("\t");
						// Source locale
						
						// Target locale
						trgLoc = LocaleId.fromString(parts[2]);
						continue;
					}
					// Parse the line
					String[] parts = tmp.split("\t");
					if ( parts.length < 2 ) {
						throw new OkapiIOException(String.format("Not enough fields in line %d.", line));
					}

					String id = parseCrumbs(unescape(parts[0]));
					ITextUnit tu = new TextUnit(id);
					
					// Source
					tmp = unescape(parts[1]);
					tu.setSourceContent(new TextFragment(tmp));
					
					// Process the target if there is one
					if ( parts.length > 2 ) {
						tmp = unescape(parts[2]);
						tu.setTargetContent(trgLoc, new TextFragment(tmp));
					}
					
					return new Event(EventType.TEXT_UNIT, tu);
				}
			}

		}
		catch ( Throwable e ) {
			throw new OkapiIOException(String.format("Error parsing the table in line %d.", line));
		}
	}
	
	public void open (RawDocument input) {
		open(input, true);
	}
	
	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
	}

	public void setParameters (IParameters params) {
		// Nothing to do
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter(), getEncoderManager());
	}

	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		close(); // Just in case resources need to be freed
	
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
		detector.detectAndRemoveBom();
		input.setEncoding(detector.getEncoding());
		encoding = input.getEncoding();
		
		reader = null;		
		try {
			reader = new BufferedReader(new InputStreamReader(detector.getInputStream(), encoding));
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiUnsupportedEncodingException(
				String.format("The encoding '%s' is not supported.", encoding), e);
		}
		trgLoc = input.getTargetLocale();
		hasUTF8BOM = detector.hasUtf8Bom();
		lineBreak = detector.getNewlineType().toString();
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}
		
		otherId = new IdGenerator(null);
		firstLineDone = false;
		line = 0;
		hasNext = true;
		
		// Set the start event
		queue = new LinkedList<Event>();
		StartDocument startDoc = new StartDocument(otherId.createId());
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLocale(input.getSourceLocale());
		startDoc.setLineBreak(lineBreak);
		startDoc.setFilterParameters(getParameters());
		startDoc.setFilterWriter(createFilterWriter());
		startDoc.setType(MIMETYPE);
		startDoc.setMimeType(MIMETYPE);
		startDoc.setMultilingual(true);
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));
	}
	
	private String unescape (String text) {
		String tmp = text;
		if ( tmp.startsWith("\"") ) {
			tmp = tmp.substring(1);
		}
		if ( tmp.endsWith("\"") ) {
			tmp = tmp.substring(0, tmp.length()-1);
		}
		tmp = tmp.replace("\\t", "\t");
		tmp = tmp.replace("\\n", "\n");
		return tmp;
	}

	private String parseCrumbs (String text) {
		// Check if it is a crumbs-string or not
		if ( !text.startsWith(TransTableWriter.CRUMBS_PREFIX) ) {
			// Not an expected pattern
			throw new OkapiIOException(String.format("Error in ID pattern ('%s'", text));
		}

		// Get the text unit id
		int n = text.indexOf(TransTableWriter.TEXTUNIT_CRUMB);
		if ( n == -1 ) {
			throw new OkapiIOException(String.format("Error in ID pattern ('%s'", text));
		}
		String tmp = text.substring(n+TransTableWriter.TEXTUNIT_CRUMB.length()).trim();
		if ( tmp.isEmpty() ) {
			// Something is not right
			throw new OkapiIOException(String.format("Error in ID pattern ('%s'", text));
		}
		return tmp;
	}
	
}
