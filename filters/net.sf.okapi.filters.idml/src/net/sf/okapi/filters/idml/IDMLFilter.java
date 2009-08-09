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

package net.sf.okapi.filters.idml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ZipSkeleton;

public class IDMLFilter implements IFilter {

	private enum NextAction {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}

	private final String MIMETYPE = "application/vnd.adobe.indesign-idml-package";
	private final String docId = "sd";
	
	private ZipFile zipFile;
	private ZipEntry entry;
	private NextAction nextAction;
	private URI docURI;
	private Enumeration<? extends ZipEntry> entries;
	private int subDocId;
	private LinkedList<Event> queue;
	private String srcLang;
	private IDMLContentFilter filter;
	private Parameters params;

	public IDMLFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		// TODO Auto-generated method stub
	}

	public void close () {
		try {
			nextAction = NextAction.DONE;
			if ( zipFile != null ) {
				zipFile.close();
				zipFile = null;
			}
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	public ISkeletonWriter createSkeletonWriter () {
		return null; // There is no corresponding skeleton writer
	}
	
	public IFilterWriter createFilterWriter () {
		return new ZipFilterWriter();
	}

	public String getName () {
		return "okf_idml";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"IDML",
			"Adobe InDesign IDML documents"));
		return list;
	}
	
	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return ((( queue != null ) && ( !queue.isEmpty() )) || ( nextAction != NextAction.DONE ));
	}

	public Event next () {
		// Send remaining event from the queue first
		if ( queue.size() > 0 ) {
			return queue.poll();
		}
		
		// When the queue is empty: process next action
		switch ( nextAction ) {
		case OPENZIP:
			return openZipFile();
		case NEXTINZIP:
			return nextInZipFile();
		case NEXTINSUBDOC:
			return nextInSubDocument();
		default:
			throw new OkapiIllegalFilterOperationException("Invalid next() call.");
		}
	}

	public void open (RawDocument input) {
		open(input, true);
	}
	
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		close();
		docURI = input.getInputURI();
		if ( docURI == null ) {
			throw new OkapiBadFilterInputException("This filter supports only URI input.");
		}
		nextAction = NextAction.OPENZIP;
		queue = new LinkedList<Event>();
		filter = new IDMLContentFilter();
		filter.setParameters(params);

		srcLang = input.getSourceLanguage();
	}
	
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
		if ( filter != null ) filter.setParameters(params);
	}

	private Event openZipFile () {
		try {
			zipFile = new ZipFile(new File(docURI));
			entries = zipFile.entries();
			subDocId = 0;
			nextAction = NextAction.NEXTINZIP;
			
			StartDocument startDoc = new StartDocument(docId);
			startDoc.setEncoding("UTF-8", false); // Default
			startDoc.setName(docURI.getPath());
			startDoc.setLanguage(srcLang);
			startDoc.setMimeType(MIMETYPE);
			startDoc.setLineBreak("\n");
			startDoc.setFilterParameters(params);
			startDoc.setFilterWriter(createFilterWriter());
			ZipSkeleton skel = new ZipSkeleton(zipFile);
			return new Event(EventType.START_DOCUMENT, startDoc, skel);
		}
		catch ( ZipException e ) {
			throw new OkapiIOException(e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}
	
	private Event nextInZipFile () {
		while( entries.hasMoreElements() ) {
			entry = entries.nextElement();
			if ( entry.getName().startsWith("Stories/")
				&& entry.getName().endsWith(".xml") ) {
				return openSubDocument();
			}
			else {
				DocumentPart dp = new DocumentPart(entry.getName(), false);
				ZipSkeleton skel = new ZipSkeleton(entry);
				return new Event(EventType.DOCUMENT_PART, dp, skel);
			}
		}

		// No more sub-documents: end of the ZIP document
		close();
		Ending ending = new Ending("ed");
		return new Event(EventType.END_DOCUMENT, ending);
	}
	
	private Event openSubDocument () {
		filter.close(); // Make sure the previous is closed
		Event event;
		try {
			filter.open(new RawDocument(zipFile.getInputStream(entry), "UTF-8", srcLang));
			event = filter.next(); // START_DOCUMENT
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		// Change the START_DOCUMENT event to START_SUBDOCUMENT
		StartSubDocument sd = new StartSubDocument(docId, String.valueOf(++subDocId));
		sd.setName(docURI.getPath() + "/" + entry.getName()); // Use '/'
		nextAction = NextAction.NEXTINSUBDOC;
		ZipSkeleton skel = new ZipSkeleton(
			(GenericSkeleton)event.getResource().getSkeleton(), entry);
		return new Event(EventType.START_SUBDOCUMENT, sd, skel);
	}
	
	private Event nextInSubDocument () {
		Event event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case END_DOCUMENT:
				// Change the END_DOCUMENT to END_SUBDOCUMENT
				Ending ending = new Ending(String.valueOf(subDocId));
				nextAction = NextAction.NEXTINZIP;
				ZipSkeleton skel = new ZipSkeleton(
					(GenericSkeleton)event.getResource().getSkeleton(), entry);
				return new Event(EventType.END_SUBDOCUMENT, ending, skel);
			
			default: // Else: just pass the event through
				return event;
			}
		}
		return null; // Should not get here
	}

}
