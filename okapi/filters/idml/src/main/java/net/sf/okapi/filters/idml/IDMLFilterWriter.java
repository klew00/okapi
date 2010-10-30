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

package net.sf.okapi.filters.idml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;

public class IDMLFilterWriter implements IFilterWriter {

	private String outputPath;
	private LocaleId trgLoc;
	private ZipFile zipOriginal;
	private ZipOutputStream zipOutStream;
	private File tempFile;
	private byte[] buffer;
	private Transformer xformer;
	private ZipEntry entry;
	private Document doc;
	private int group;
	
	public IDMLFilterWriter () {
        try {
			xformer = TransformerFactory.newInstance().newTransformer();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error creating IDMLFilterWriter.\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public void cancel () {
		// TODO
	}

	@Override
	public void close () {
		try {
			zipOriginal = null;
			if ( zipOutStream == null ) return; // Was closed already
			
			// Close the output
			zipOutStream.close();
			zipOutStream = null;
			buffer = null;

			// If it was in a temporary file, copy it over the existing one
			// If the IFilter.close() is called before IFilterWriter.close()
			// this should allow to overwrite the input.
			if ( tempFile != null ) {
				Util.copy(new FileInputStream(tempFile), outputPath);
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error closing IDML outpiut.\n"+e.getMessage(), e);
		}
	}

	@Override
	public EncoderManager getEncoderManager () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName () {
		return "IDMLFilterWriter";
	}

	@Override
	public IParameters getParameters () {
		return null; // Not used
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument(event.getStartDocument());
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		case START_GROUP:
			processStartGroup(event.getStartGroup());
			break;
		case END_GROUP:
			processEndGroup(event.getEndGroup());
			break;
		case TEXT_UNIT:
			processTextUnit(event.getTextUnit());
			break;
		}
		return event;
	}

	@Override
	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		trgLoc = locale;
		// Ignore encoding. We always use UTF-8
	}

	@Override
	public void setOutput (String path) {
		outputPath = path;
	}

	@Override
	public void setOutput (OutputStream output) {
		// Not supported for this filter
		throw new UnsupportedOperationException("setOutput(OutputStream) is not supported for this class.");
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	private void processStartDocument (StartDocument res) {
		try {
			// Get the original ZIP file
			// This will be used throughout the writting
			IDMLSkeleton skel = (IDMLSkeleton)res.getSkeleton();
			zipOriginal = skel.getOriginal();
			group = 0;
		
			// Create the output stream from the path provided
			tempFile = null;			
			boolean useTemp = false;
			File f = new File(outputPath);
			if ( f.exists() ) {
				// If the file exists, try to remove
				useTemp = !f.delete();
			}
			if ( useTemp ) {
				// Use a temporary output if we cannot overwrite for now
				// If it's the input file, IFilter.close() will free it before we
				// call close() here (that is if IFilter.close() is called correctly!)
				tempFile = File.createTempFile("idmlTmpZip", null);
				zipOutStream = new ZipOutputStream(new FileOutputStream(tempFile.getAbsolutePath()));
			}
			else { // Make sure the directory exists
				Util.createDirectories(outputPath);
				zipOutStream = new ZipOutputStream(new FileOutputStream(outputPath));
			}
			
			// Create buffer for transfer
			buffer = new byte[2048];
			
			// Copy all entries of the original ZIP file into the output,
			// except for the stories entries.
			Enumeration<? extends ZipEntry> entries = zipOriginal.entries();
			while( entries.hasMoreElements() ) {
				ZipEntry entry = entries.nextElement();
				if ( entry.getName().endsWith(".xml") ) {
					if ( entry.getName().startsWith("Stories/") ) {
						continue; // Not yet
					}
				}
				// Else: copy the entry into the output ZIP file
				zipOutStream.putNextEntry(new ZipEntry(entry.getName()));
				InputStream input = zipOriginal.getInputStream(entry); 
				int len;
				while ( (len = input.read(buffer)) > 0 ) {
					zipOutStream.write(buffer, 0, len);
				}
				input.close();
				zipOutStream.closeEntry();
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error creating output IDML.\n"+e.getMessage(), e);
		}
	}

	private void processEndDocument () {
		close();
	}

	private void processTextUnit (TextUnit tu) {
		//TODO
		
	}
	
	private void processStartGroup (StartGroup res) {
		group++;
		IDMLSkeleton skel = (IDMLSkeleton)res.getSkeleton();
		if ( skel == null ) return; // Not a story group
		// Store the entry data to process the text units
		// Everything will be written at the end group.
		entry = skel.getEntry();
		doc = skel.getDocument();
	}
	
	private void processEndGroup (Ending ending) {
		group--;
		try {
			if ( group != 1 ) {
				// Not a story group
				return;
			}
			// This is where we output the modified story
			// Prepare the DOM document for writing
	        Source source = new DOMSource(doc);
	        // Prepare the output file
			zipOutStream.putNextEntry(new ZipEntry(entry.getName()));
	        Result result = new StreamResult(zipOutStream);
	        // Write the DOM document to the file
	        xformer.transform(source, result);
			zipOutStream.closeEntry();
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error writing out the story.\n"+e.getMessage(), e);
		}
		catch ( TransformerConfigurationException e ) {
			throw new OkapiIOException("Error transforming the story output.\n"+e.getMessage(), e);
		}
		catch ( TransformerFactoryConfigurationError e ) {
			throw new OkapiIOException("Transform configuration error.\n"+e.getMessage(), e);
		}
		catch ( TransformerException e ) {
			throw new OkapiIOException("Transformation error.\n"+e.getMessage(), e);
		}
	}

}
