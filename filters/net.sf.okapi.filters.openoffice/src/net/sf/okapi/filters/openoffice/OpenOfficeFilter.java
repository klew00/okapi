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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * This class implements the IFilter interface for Open-Office.org documents
 * (ODT, ODP, ODS, and ODG files). It expects the ZIP files as input, and calls the
 * ODFFilter as needed to process the embedded documents.
 */
public class OpenOfficeFilter implements IFilter {

	private enum StateType {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}

	private ODFFilter odf;
	private ZipFile zipFile;
	private StateType nextAction = StateType.DONE;
	private URL inputUrl;
	private Enumeration<? extends ZipEntry> entries;
	private int subDocId;
	private LinkedList<FilterEvent> queue;
	
	public OpenOfficeFilter () {
		odf = new ODFFilter();
	}
	
	public void cancel () {
		odf.cancel();
	}

	public void close () {
		try {
			odf.close();
			if ( zipFile != null ) {
				zipFile.close();
				zipFile = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "OpenOfficeFilter";
	}

	public IParameters getParameters () {
		return odf.getParameters();
	}

	public boolean hasNext () {
		return ((( queue != null ) && ( !queue.isEmpty() )) || ( nextAction != StateType.DONE ));
	}

	public FilterEvent next () {
		try {
			// Return queue content first
			if ( !queue.isEmpty() ) {
				return queue.poll();
			}
			// Else: get the next event
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
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (InputStream input) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}
	
	public void open (URL inputUrl) {
		this.inputUrl = inputUrl;
		queue = new LinkedList<FilterEvent>();
		queue.add(new FilterEvent(FilterEventType.START));
		nextAction = StateType.OPENZIP;
	}

	public void open (CharSequence inputText) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}

	public void setOptions (String language,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(language, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		//TODO: set vars
	}
	
	public void setParameters (IParameters params) {
		odf.setParameters((Parameters)params);
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	private FilterEvent openZipFile () throws IOException {
		try {
			zipFile = new ZipFile(new File(inputUrl.toURI()));
			entries = zipFile.entries();
			subDocId = 0;
			nextAction = StateType.NEXTINZIP;
			StartDocument startDoc = new StartDocument("sd");
			startDoc.setName(inputUrl.getPath());
			return new FilterEvent(FilterEventType.START_DOCUMENT, startDoc);
		}
		catch ( ZipException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( URISyntaxException e ) {
			throw new RuntimeException(e);
		}
	}

	private FilterEvent nextInZipFile () throws IOException {
		// Find the relevant zip entries and process them
		ZipEntry entry;
		while( entries.hasMoreElements() ) {
			entry = entries.nextElement();
			if ( entry.getName().equals("content.xml")
				|| entry.getName().equals("meta.xml")
				|| entry.getName().equals("styles.xml") )
			{
				return openSubDocument(entry);
			}
			else continue;
		}

		// No more sub-documents: end of the ZIP document
		close();
		queue.add(new FilterEvent(FilterEventType.FINISHED));
		Ending ending = new Ending("ed");
		nextAction = StateType.DONE;
		return new FilterEvent(FilterEventType.END_DOCUMENT, ending);
	}

	private FilterEvent openSubDocument (ZipEntry zipEntry) throws IOException {
		// Start of the sub-document
		// Get the input stream
		odf.open(new BufferedInputStream(zipFile.getInputStream(zipEntry)));
		nextAction = StateType.NEXTINSUBDOC;
		return nextInSubDocument();
	}
	
	private FilterEvent nextInSubDocument () throws IOException {
		while ( odf.hasNext() ) {
			FilterEvent event = odf.next();
			switch ( (FilterEventType)event.getEventType() ) {
			case START:
			case FINISHED:
				// Skip those event, they are send by the caller
				continue;
			
			case START_DOCUMENT:
				// Change the start-document into a start-sub-document
				StartSubDocument subDoc = new StartSubDocument(String.valueOf(++subDocId)); 
				StartDocument startDoc = (StartDocument)event.getResource();
				if ( startDoc != null ) {
					subDoc.setName(startDoc.getName());
					subDoc.setType(startDoc.getType());
					subDoc.setMimeType(startDoc.getMimeType());
					subDoc.setSkeleton(startDoc.getSkeleton());
				}
				return new FilterEvent(FilterEventType.START_SUBDOCUMENT, subDoc);

			case END_DOCUMENT:
				// Change the end-document into an end-sub-document
				return new FilterEvent(FilterEventType.END_SUBDOCUMENT,
					(Ending)event.getResource());
			
			default: // Just pass on the filter's event and data
				return event;
			}
		}

		// Send the end sub-document event
		odf.close();
		nextAction = StateType.NEXTINZIP;
		return nextInZipFile();
	}

}
