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

package net.sf.okapi.common.filterwriter;

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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ZipSkeleton;

/**
 * Implements the {@link IFilterWriter} interface for filters that handle formats made of
 * a ZIP package with embedded extractable documents, such as IDML or
 * OpenOffice.org files (ODT, ODS, ODP, etc.)
 */
public class ZipFilterWriter implements IFilterWriter {

	private String outputPath;
	private ZipFile zipOriginal;
	private ZipOutputStream zipOut;
	private byte[] buffer;
	private LocaleId outLoc;
	private String entryName;
	private IFilterWriter subDocWriter;
	private File tempFile;
	private File tempZip;
	private EncoderManager encoderManager;
	private int subDocLevel;

	public ZipFilterWriter (EncoderManager encoderManager) {
		this.encoderManager = encoderManager;
	}
	
	public void cancel () {
		//TODO: implement cancel()
		zipOriginal = null;
	}
	
	public void close () {
		zipOriginal = null;
		if ( zipOut == null ) return;
		IOException err = null;
		InputStream orig = null;
		OutputStream dest = null;
		try {
			// Close the output
			zipOut.close();
			zipOut = null;

			// If it was in a temporary file, copy it over the existing one
			// If the IFilter.close() is called before IFilterWriter.close()
			// this should allow to overwrite the input.
			if ( tempZip != null ) {
				dest = new FileOutputStream(outputPath);
				orig = new FileInputStream(tempZip); 
				int len;
				while ( (len = orig.read(buffer)) > 0 ) {
					dest.write(buffer, 0, len);
				}
			}
			buffer = null;
		}
		catch ( IOException e ) {
			err = e;
		}
		finally {
			// Make sure we close both files
			if ( dest != null ) {
				try {
					dest.close();
				}
				catch ( IOException e ) {
					err = e;
				}
				dest = null;
			}
			if ( orig != null ) {
				try {
					orig.close();
				} catch ( IOException e ) {
					err = e;
				}
				orig = null;
				if ( err != null ) throw new RuntimeException(err);
				else {
					if ( tempZip != null ) {
						tempZip.delete();
						tempZip = null;
					}
				}
			}
		}
	}

	public String getName () {
		return "ZipFilterWriter";
	}

	public EncoderManager getEncoderManager () {
		return encoderManager;
	}
	
	@Override
	public ISkeletonWriter getSkeletonWriter () {
		return null;
	}

	public IParameters getParameters () {
		return null; // Not used
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			subDocLevel = 0;
			processStartDocument((StartDocument)event.getResource());			
			break;
		case DOCUMENT_PART:
			processDocumentPart(event);
			break;
		case END_DOCUMENT:
			processEndDocument();
			subDocLevel = 0;
			break;
		case START_SUBDOCUMENT:
			if (subDocLevel == 0)
				processStartSubDocument((StartSubDocument)event.getResource());
			else
				subDocWriter.handleEvent(event);
			subDocLevel++;
			break;
		case END_SUBDOCUMENT:
			subDocLevel--;
			if (subDocLevel == 0)
				processEndSubDocument((Ending)event.getResource());
			else
				subDocWriter.handleEvent(event);
			break;
		case TEXT_UNIT:
		case START_GROUP:
		case END_GROUP:
		case START_SUBFILTER:
		case END_SUBFILTER:
			subDocWriter.handleEvent(event);
			break;
		case CANCELED:
			break;
		}
		return event;
	}

	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		outLoc = locale;
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
		// Not used
	}

	private void processStartDocument (StartDocument res) {
		try {
			buffer = new byte[2048];
			zipOriginal = null;
			
			ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
			if (skel != null)
				zipOriginal = skel.getOriginal();
			
			tempZip = null;			
			// Create the output stream from the path provided
			boolean useTemp = false;
			File f = new File(outputPath);
			if ( f.exists() ) {
				// If the file exists, try to remove
				useTemp = !f.delete();
			}
			if ( useTemp ) {
				// Use a temporary output if we can overwrite for now
				// If it's the input file, IFilter.close() will free it before we
				// call close() here (that is if IFilter.close() is called correctly!)
				tempZip = File.createTempFile("zfwTmpZip", null);
				zipOut = new ZipOutputStream(new FileOutputStream(tempZip.getAbsolutePath()));
			}
			else { // Make sure the directory exists
				Util.createDirectories(outputPath);
				zipOut = new ZipOutputStream(new FileOutputStream(outputPath));
			}
		}
		catch ( FileNotFoundException e ) {
    		throw new RuntimeException(e);
		}
		catch ( IOException e ) {
    		throw new RuntimeException(e);
		}
	}
	
	private void processEndDocument () {
		close();
	}
	
	private void processDocumentPart (Event event) {
		// Treat top-level ZipSkeleton events
		DocumentPart res = (DocumentPart)event.getResource();
		if ( res.getSkeleton() instanceof ZipSkeleton ) {
			ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
			ZipFile original = skel.getOriginal();
			if (original == null)
				original = zipOriginal;
			ZipEntry entry = skel.getEntry();
			// Copy the entry data
			try {
				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				InputStream input = original.getInputStream(entry); 
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

	protected ISkeletonWriter createSubDocumentSkeletonWriter (StartSubDocument res) {
        return new GenericSkeletonWriter();
	}
	
	protected IFilterWriter createSubDocumentFilterWriter (StartSubDocument res) {
        IFilterWriter writer = new GenericFilterWriter(createSubDocumentSkeletonWriter(res), getEncoderManager());
        writer.setOptions(outLoc, "UTF-8");        
        return writer;
	}
	
	protected StartDocument convertToStartDocument(StartSubDocument res) {
		StartDocument sd = new StartDocument("sd");
		sd.setLineBreak("\n");
		sd.setSkeleton(res.getSkeleton());
		return sd;
	}
	
	private void processStartSubDocument (StartSubDocument res) {
		ZipSkeleton skel = (ZipSkeleton)res.getSkeleton();
		ZipEntry entry = skel.getEntry();
		if (entry != null)
			entryName = entry.getName();

		// Set the temporary path and create it
		try {
			tempFile = File.createTempFile("zfwTmp", null);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		
		// Instantiate the filter writer for that entry if not set from outside with setSubDocWriter()
		if (subDocWriter == null) {
			subDocWriter = createSubDocumentFilterWriter(res);
		}			
		subDocWriter.setOutput(tempFile.getAbsolutePath());
				
		StartDocument sd = convertToStartDocument(res);
		subDocWriter.handleEvent(new Event(EventType.START_DOCUMENT, sd));
	}
	
	private void processEndSubDocument (Ending res) {
		try {
			// Finish writing the sub-document
			subDocWriter.handleEvent(new Event(EventType.END_DOCUMENT, res));
			subDocWriter.close();
			
			// Reset subDocWriter, next sub-document might require a different writer
			// (a default writer will be created if not set from outside)
			subDocWriter = null;

			// Create the new entry from the temporary output file
			zipOut.putNextEntry(new ZipEntry(entryName));
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

	public IFilterWriter getSubDocWriter() {
		return subDocWriter;
	}

	public void setSubDocWriter(IFilterWriter subDocWriter) {
		this.subDocWriter = subDocWriter;
		subDocWriter.setOptions(outLoc, "UTF-8");
	}
}
