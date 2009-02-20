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

package net.sf.okapi.common.writer;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * This class implements the filter writer interface for filters that use the
 * GenericSkeleton skeleton.
 */
public class GenericFilterWriter implements IFilterWriter {

	private ISkeletonWriter skelWriter;
	private OutputStream output;
	private String outputPath;
	private OutputStreamWriter writer;
	private String language;
	private String encoding;
	private EncoderManager encoderManager;
	
	public GenericFilterWriter (ISkeletonWriter skelWriter) {
		this.skelWriter = skelWriter;
		encoderManager = new EncoderManager();
	}
	
	public void close () {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
				//TODO: do we need to close the underlying stream???
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "GenericFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		try {
			switch ( event.getEventType() ) {
			case START:
				processStart();
				break;
			case FINISHED:
				processFinished();
				break;
			case START_DOCUMENT:
				processStartDocument((StartDocument)event.getResource());
				break;
			case END_DOCUMENT:
				processEndDocument((Ending)event.getResource());
				close();
				break;
			case START_SUBDOCUMENT:
				processStartSubDocument((StartSubDocument)event.getResource());
				break;
			case END_SUBDOCUMENT:
				processEndSubDocument((Ending)event.getResource());
				break;
			case START_GROUP:
				processStartGroup((StartGroup)event.getResource());
				break;
			case END_GROUP:
				processEndGroup((Ending)event.getResource());
				break;
			case TEXT_UNIT:
				processTextUnit((TextUnit)event.getResource());
				break;
			case DOCUMENT_PART:
				processDocumentPart((DocumentPart)event.getResource());
				break;
			}
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		return event;
	}

	private void processStart () {
		skelWriter.processStart(language, encoding, null, encoderManager);
	}

	private void processFinished () {
		skelWriter.processFinished();
		close();
	}
	
	private void processStartDocument(StartDocument resource) throws IOException {
		// Create the output
		createWriter(resource);
		writer.write(skelWriter.processStartDocument(resource));
	}

	private void processEndDocument(Ending resource) throws IOException {
		writer.write(skelWriter.processEndDocument(resource));
	}

	private void processStartSubDocument (StartSubDocument resource) throws IOException {
		writer.write(skelWriter.processStartSubDocument(resource));
	}

	private void processEndSubDocument (Ending resource) throws IOException {
		writer.write(skelWriter.processEndSubDocument(resource));
	}

	private void processStartGroup (StartGroup resource) throws IOException {
		writer.write(skelWriter.processStartGroup(resource));
	}

	private void processEndGroup (Ending resource) throws IOException {
		writer.write(skelWriter.processEndGroup(resource));
	}

	private void processTextUnit (TextUnit resource) throws IOException {
		writer.write(skelWriter.processTextUnit(resource));
	}

	private void processDocumentPart (DocumentPart resource) throws IOException {
		writer.write(skelWriter.processDocumentPart(resource));
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
		this.encoding = defaultEncoding;
	}

	public void setOutput (String path) {
		close(); // Make sure previous is closed
		this.outputPath = path;
	}

	public void setOutput (OutputStream output) {
		close(); // Make sure previous is closed
		this.output = output; // then assign the new stream
	}

	public void setParameters (IParameters params) {
	}

	private void createWriter (StartDocument resource) {
		try {
			// Create the output writer from the provided stream
			// or from the path if there is no stream provided
			if ( output == null ) {
				output = new BufferedOutputStream(new FileOutputStream(outputPath));
			}
			
			// Get the encoding of the original document
			String originalEnc = resource.getEncoding();
			// If it's undefined, assume it's the default of the system
			if ( originalEnc == null ) {
				originalEnc = Charset.defaultCharset().name();
			}
			// Check if the output encoding is defined
			if ( encoding == null ) {
				// if not: Fall back on the encoding of the original
				encoding = originalEnc;
			}
			// Create the output
			writer = new OutputStreamWriter(output, encoding);
			// Set default UTF-8 BOM usage
			boolean useUTF8BOM = false; //TODO: use platform default
			// Check if the output encoding is UTF-8
			if ( "utf-8".equalsIgnoreCase(encoding) ) {
				// If the original was UTF-8 too
				if ( "utf-8".equalsIgnoreCase(originalEnc) ) {
					// Check whether it had a BOM or not
					useUTF8BOM = resource.hasUTF8BOM();
				}
			}
			// Write out the BOM if needed
			Util.writeBOMIfNeeded(writer, useUTF8BOM, encoding);
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}

}
