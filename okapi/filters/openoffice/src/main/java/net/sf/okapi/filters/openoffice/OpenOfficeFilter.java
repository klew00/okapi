/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.openoffice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ZipSkeleton;

/**
 * This class implements the IFilter interface for Open-Office.org documents
 * (ODT, ODP, ODS, and ODG files). It expects the ZIP files as input, and calls the
 * ODFFilter as needed to process the embedded documents.
 */
@UsingParameters(Parameters.class)
public class OpenOfficeFilter implements IFilter {

	private enum NextAction {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}

	private final String docId = "sd";
	
	private ZipFile zipFile;
	private ZipEntry entry;
	private NextAction nextAction;
	private URI docURI;
	private Enumeration<? extends ZipEntry> entries;
	private int subDocId;
	private LinkedList<Event> queue;
	private LocaleId srcLoc;
	private ODFFilter filter;
	private Parameters params;
	private String internalMimeType;
	private EncoderManager encoderManager;

	public OpenOfficeFilter () {
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
		return new ZipFilterWriter(getEncoderManager());
	}

	public String getName () {
		return "okf_openoffice";
	}

	public String getDisplayName () {
		return "OpenOffice.org Filter (BETA)";
	}

	public String getMimeType () {
		return MimeTypeMapper.OPENOFFICE_MIME_TYPE;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.OPENOFFICE_MIME_TYPE,
			getClass().getName(),
			"OpenOffice.org Documents",
			"OpenOffice.org ODT, ODS, ODP, ODG, OTT, OTS, OTP, OTG documents",
			null,
			".odt;.ods;.odg;.odp;.ott;.ots;.otp;.otg;"));
		return list;
	}
	
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MimeTypeMapper.ODF_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		}
		return encoderManager;
	}
	
	public IParameters getParameters () {
		// Should be the same as the internal ODF filter already 
		return params;
	}

	public boolean hasNext () {
		return ((( queue != null ) && ( !queue.isEmpty() ))
			|| ( nextAction != NextAction.DONE ));
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
		filter = new ODFFilter();
		filter.setParameters(params);
		
		srcLoc = input.getSourceLocale();
	}
	
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
		if ( filter != null ) filter.setParameters(params);
	}

	private void getInternalMimeType () {
		try {
			ZipEntry entry;
			internalMimeType = "";
			while( entries.hasMoreElements() ) {
				entry = entries.nextElement();
				if ( entry.getName().equals("mimetype") ) {
					BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
					internalMimeType = br.readLine();
					internalMimeType = internalMimeType.trim();
					br.close();
				}
				return;
			}
		}
		catch (IOException e) {
			throw new OkapiIOException("Error opening mimetype file.", e);
		}
		finally {
			entries = zipFile.entries();
		}
	}
	
	private Event openZipFile () {
		try {
			zipFile = new ZipFile(new File(docURI));
			entries = zipFile.entries();
			subDocId = 0;
			nextAction = NextAction.NEXTINZIP;
			// Initialize the internalMime type
			getInternalMimeType();
			
			StartDocument startDoc = new StartDocument(docId);
			startDoc.setName(docURI.getPath());
			startDoc.setLocale(srcLoc);
			startDoc.setMimeType(MimeTypeMapper.OPENOFFICE_MIME_TYPE);
			startDoc.setFilterParameters(params);
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setLineBreak("\n"); // forced
			startDoc.setEncoding("UTF-8", false); // Forced
			ZipSkeleton skel = new ZipSkeleton(zipFile, null);
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
			if ( entry.getName().equals("content.xml")
				|| entry.getName().equals("meta.xml")
				|| entry.getName().equals("styles.xml") )
			{
				return openSubDocument();
			}
			else {
				DocumentPart dp = new DocumentPart(entry.getName(), false);
				ZipSkeleton skel = new ZipSkeleton(zipFile, entry);
				return new Event(EventType.DOCUMENT_PART, dp, skel);
			}
		}

		// No more sub-documents: end of the ZIP document
		close();
		Ending ending = new Ending("ed");
		return new Event(EventType.END_DOCUMENT, ending);
	}
	
	private Event openSubDocument () {
		filter.close();
		Event event;
		try {
			filter.open(new RawDocument(zipFile.getInputStream(entry), "UTF-8", srcLoc));
			event = filter.next(); // START_DOCUMENT
			filter.setContainerMimeType(internalMimeType);
		}
		catch (IOException e) {
			throw new OkapiIOException("Error opening internal file.", e);
		}
		
		// Change the START_DOCUMENT event to START_SUBDOCUMENT
		StartSubDocument sd = new StartSubDocument(docId, String.valueOf(++subDocId));
		sd.setName(docURI.getPath() + "/" + entry.getName()); // Use '/'
		nextAction = NextAction.NEXTINSUBDOC;
		ZipSkeleton skel = new ZipSkeleton(
			(GenericSkeleton)event.getResource().getSkeleton(), zipFile, entry);
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
					(GenericSkeleton)event.getResource().getSkeleton(), zipFile, entry);
				return new Event(EventType.END_SUBDOCUMENT, ending, skel);
			
			default: // Else: just pass the event through
				return event;
			}
		}
		return null; // Should not get here
	}

}
