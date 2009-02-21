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

package net.sf.okapi.filters.openoffice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * This class implements the IFilter interface for Open-Office.org documents
 * (ODT, ODP, ODS, and ODG files). It expects the ZIP files as input, and calls the
 * ODFFilter as needed to process the embedded documents.
 */
public class OpenOfficeFilter implements IFilter {

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
	private LinkedList<FilterEvent> queue;
	private String srcLang;
	private ODFFilter filter;

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
			throw new RuntimeException(e);
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

	public IParameters getParameters () {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext () {
		return ((( queue != null ) && ( !queue.isEmpty() )) || ( nextAction != NextAction.DONE ));
	}

	public FilterEvent next () {
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
			throw new RuntimeException("Invalid next() call.");
		}
	}

	public void open (InputStream input) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}

	public void open (CharSequence inputText) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}

	public void open (URI inputURI) {
		close();
		docURI = inputURI;
		nextAction = NextAction.OPENZIP;
		queue = new LinkedList<FilterEvent>();
		filter = new ODFFilter();
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
	}

	public void setParameters (IParameters params) {
		// TODO Auto-generated method stub
	}

	private FilterEvent openZipFile () {
		try {
			zipFile = new ZipFile(new File(docURI));
			entries = zipFile.entries();
			subDocId = 0;
			nextAction = NextAction.NEXTINZIP;
			
			StartDocument startDoc = new StartDocument(docId);
			startDoc.setName(docURI.getPath());
			startDoc.setLanguage(srcLang);
			startDoc.setMimeType(MIMETYPE);
			ZipSkeleton skel = new ZipSkeleton(zipFile);
			queue.add(new FilterEvent(FilterEventType.START_DOCUMENT, startDoc, skel));
			
			return new FilterEvent(FilterEventType.START);
		}
		catch ( ZipException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private FilterEvent nextInZipFile () {
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
				ZipSkeleton skel = new ZipSkeleton(entry);
				return new FilterEvent(FilterEventType.DOCUMENT_PART, dp, skel);
			}
		}

		// No more sub-documents: end of the ZIP document
		close();
		queue.add(new FilterEvent(FilterEventType.FINISHED));
		Ending ending = new Ending("ed");
		return new FilterEvent(FilterEventType.END_DOCUMENT, ending);
	}
	
	private FilterEvent openSubDocument () {
		filter.close();
		filter.setOptions(srcLang, "UTF-8", true);
		FilterEvent event;
		try {
			filter.open(zipFile.getInputStream(entry));
			filter.next(); // START
			event = filter.next(); // START_DOCUMENT
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		// Change the START_DOCUMENT event to START_SUBDOCUMENT
		StartSubDocument sd = new StartSubDocument(docId, String.valueOf(++subDocId));
		sd.setName(entry.getName());
		nextAction = NextAction.NEXTINSUBDOC;
		ZipSkeleton skel = new ZipSkeleton(
			(GenericSkeleton)event.getResource().getSkeleton(), entry);
		return new FilterEvent(FilterEventType.START_SUBDOCUMENT, sd, skel);
	}
	
	private FilterEvent nextInSubDocument () {
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case END_DOCUMENT:
				// Read the FINISHED event
				filter.next();
				// Change the END_DOCUMENT to END_SUBDOCUMENT
				Ending ending = new Ending(String.valueOf(subDocId));
				nextAction = NextAction.NEXTINZIP;
				ZipSkeleton skel = new ZipSkeleton(
					(GenericSkeleton)event.getResource().getSkeleton(), entry);
				return new FilterEvent(FilterEventType.END_SUBDOCUMENT, ending, skel);
			
			default: // Else: just pass the event through
				return event;
			}
		}
		return null; // Should not get here
	}

}
