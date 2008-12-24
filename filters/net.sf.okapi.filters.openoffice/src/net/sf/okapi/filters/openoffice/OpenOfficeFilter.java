/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.filters.openoffice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;

public class OpenOfficeFilter implements IFilter {

	private enum StateType {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}
	
	private InputStream input;
	private ODFFilter odf;
	private ZipFile zipFile;
	private StateType nextAction = StateType.DONE;
	private String inputPath;
	private Enumeration<? extends ZipEntry> entries;
	private FilterEvent event;
	private StartSubDocument startSubDoc;
	private int subDocId;

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
		return (nextAction != StateType.DONE);
	}

	public FilterEvent next () {
		try {
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
		//TODO
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

	private FilterEvent openZipFile () throws IOException {
		zipFile = new ZipFile(inputPath);
		entries = zipFile.entries();
		
		event = new FilterEvent(FilterEventType.START_DOCUMENT);
		nextAction = StateType.NEXTINZIP;
		return event;
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

		// No more sub-documents: end of the document
		close();
		event = new FilterEvent(FilterEventType.END_DOCUMENT, odf.resource);
		nextAction = StateType.DONE;
		return event;
	}

	private FilterEvent openSubDocument (ZipEntry zipEntry) throws IOException {
		// Start of the sub-document
		// Get the input stream
		input = new BufferedInputStream(zipFile.getInputStream(zipEntry));
		odf.open(input);
		subDocId = 0;
		// Send the start sub-document event
		startSubDoc = new StartSubDocument(null);
		startSubDoc.setName(zipEntry.getName());
		startSubDoc.setType(zipEntry.getName());
		event = new FilterEvent(FilterEventType.START_SUBDOCUMENT, startSubDoc);
		nextAction = StateType.NEXTINSUBDOC;
		return event;
	}
	
	private FilterEvent nextInSubDocument () throws IOException {
		FilterEvent event;
		while ( odf.hasNext() ) {
			event = odf.next();
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

		// Send the end sub-document even
		odf.close();
		nextAction = StateType.NEXTINZIP;
		return null;
	}

}
