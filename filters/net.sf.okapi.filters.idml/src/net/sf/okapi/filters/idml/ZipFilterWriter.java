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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.writer.GenericFilterWriter;

public class ZipFilterWriter implements IFilterWriter {

	private String outputPath;
	private ZipFile zipOriginal;
	private ZipOutputStream zipOut;
	private byte[] buffer;
	private String outLang;
	private ZipEntry subDocEntry;
	private IFilterWriter subDocWriter;
	private File tempFile;
	
	public void close () {
		if ( zipOut != null ) {
			try {
				zipOut.close();
				buffer = null;
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			finally {
				zipOut = null;
			}
		}
	}

	public String getName () {
		return "ZipFilterWriter";
	}

	public IParameters getParameters () {
		// TODO Auto-generated method stub
		return null;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case START:
			processStart();
			break;
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case DOCUMENT_PART:
			processDocumentPart(event);
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		case START_SUBDOCUMENT:
			processStartSubDocument((StartSubDocument)event.getResource());
			break;
		case END_SUBDOCUMENT:
			processEndSubDocument((Ending)event.getResource());
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case START_GROUP:
			processStartGroup((StartGroup)event.getResource());
			break;
		case END_GROUP:
			processEndGroup((Ending)event.getResource());
			break;
		case CANCELED:
		case FINISHED:
			break;
		}
		return event;
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		outLang = language;
	}

	public void setOutput (String path) {
		outputPath = path;
	}

	public void setOutput (OutputStream output) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this class.");
	}

	public void setParameters (IParameters params) {
		// TODO Auto-generated method stub
	}

	private void processStart () {
		buffer = new byte[2048];
	}
	
	private void processStartDocument (StartDocument res) {
		try {
			ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
			zipOriginal = skel.getOriginal();
			Util.createDirectories(outputPath);
			zipOut = new ZipOutputStream(new FileOutputStream(outputPath));
		}
		catch ( FileNotFoundException e ) {
    		throw new RuntimeException(e);
		}
	}
	
	private void processEndDocument () {
		close();
	}
	
	private void processDocumentPart (FilterEvent event) {
		// Treat top-level ZipSkeleton events
		DocumentPart res = (DocumentPart)event.getResource();
		if ( res.getSkeleton() instanceof ZipSkeleton ) {
			ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
			ZipEntry entry = skel.getEntry();
			// Copy the entry data
			try {
				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				InputStream input = zipOriginal.getInputStream(entry); 
				int len;
				while ( (len = input.read(buffer)) > 0 ) {
					zipOut.write(buffer, 0, len);
				}
				input.close();
				zipOut.closeEntry();
			}
			catch ( IOException e ) {
				throw new RuntimeException(e);
			}
		}
		else { // Otherwise it's a normal skeleton event
			subDocWriter.handleEvent(event);
		}
	}

	private void processStartSubDocument (StartSubDocument res) {
		ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
		subDocEntry = skel.getEntry();

		// Set the temporary path and create it
		try {
			tempFile = File.createTempFile("zfwTmp", null);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		
		// Instantiate the filter writer for that entry
		//TODO: replace this by transparent call
		subDocWriter = new GenericFilterWriter(new GenericSkeletonWriter());
		subDocWriter.setOptions(outLang, "UTF-8");
		subDocWriter.setOutput(tempFile.getAbsolutePath());
		subDocWriter.handleEvent(new FilterEvent(FilterEventType.START));
		
		StartDocument sd = new StartDocument("sd");
		sd.setSkeleton(res.getSkeleton());
		subDocWriter.handleEvent(new FilterEvent(FilterEventType.START_DOCUMENT, sd));
	}
	
	private void processEndSubDocument (Ending res) {
		try {
			// Finish writing the sub-document
			subDocWriter.handleEvent(new FilterEvent(FilterEventType.END_DOCUMENT, res));
			subDocWriter.handleEvent(new FilterEvent(FilterEventType.FINISHED));
			subDocWriter.close();

			// Create the new entry from the temporary output file
			zipOut.putNextEntry(new ZipEntry(subDocEntry.getName()));
			InputStream input = new FileInputStream(tempFile); 
			int len;
			while ( (len = input.read(buffer)) > 0 ) {
				zipOut.write(buffer, 0, len);
			}
			input.close();
			zipOut.closeEntry();
			// Delete the temporary file
			tempFile.delete();
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private void processTextUnit (TextUnit tu) {
		//TODO
	}

	private void processStartGroup (StartGroup res) {
		//TODO
	}

	private void processEndGroup (Ending res) {
		//TODO
	}
	
}
