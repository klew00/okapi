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

package net.sf.okapi.steps.bomconversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.RawDocument;

public class BOMConversionStep implements IPipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private final byte[] BOM_UTF8 = {(byte)0xEF,(byte)0xBB,(byte)0xBF};
	private final byte[] BOM_UTF16BE = {(byte)0xFE,(byte)0xFF};
	private final byte[] BOM_UTF16LE = {(byte)0xFF,(byte)0xFE};

	private Parameters params;
	private byte[] buffer;

	public void destroy () {
		// Nothing to do
	}

	public String getDescription () {
		return "Add or remove BOM to a document.";
	}

	public String getName () {
		return "BOM Conversion";
	}

	public IParameters getParameters () {
		return params;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case RAW_DOCUMENT:
			processRawDocument(event);
			break;
		}
		return event;
	}

	public boolean hasNext () {
		return false;
	}

	public void postprocess () {
		// Nothing to do
	}

	public void preprocess () {
		// Nothing to do
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}
 
	public void processRawDocument (Event event) {
		RawDocument rawDoc;
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			rawDoc = (RawDocument)event.getResource();
			if ( rawDoc.getInputCharSequence() != null ) {
				// Nothing to do
				//TODO: Check if this is the appropriate behavior
				return;
			}
			if ( rawDoc.getInputURI() != null ) {
				input = new FileInputStream(new File(rawDoc.getInputURI()));
			}
			else if ( rawDoc.getInputStream() != null ) {
				// Try to cast, in cast it's a FileInputStream
				try {
					input = (FileInputStream)rawDoc.getInputStream();
				}
				catch ( ClassCastException e ) {
					throw new OkapiBadFilterInputException("RawDocument has no incompatible type of InputStream.");
				}
			}
			else {
				// Change this exception to more generic (not just filter)
				throw new OkapiBadFilterInputException("RawDocument has no input defined.");
			}
			
			// Open the output
			File tmpOut;
			try {
				tmpOut = File.createTempFile("okptmp_", ".bom");
			}
			catch ( Throwable e ) {
				throw new OkapiIOException("Cannot create temporary output.", e);
			}
			output = new FileOutputStream(tmpOut);
			
			// Reset the start of the buffer
			for ( int i=0; i<5; i++ ) buffer[i] = 0;
			// And read the 4 initial bytes
			int len = input.read(buffer, 0, 4);
			
			// Process the initial buffer
			if ( len == -1 ) {
				// Do nothing yet
			}
			else if ( len == 0 ) { // Empty file
				if ( !params.removeBOM ) { // Add the BOM
					// Let's make that empty file a UTF-8 file
					output.write(BOM_UTF8);
				}
			}
			else { // Non-empty file
				int n = hasBOM(buffer, len);
				if ( n > 0 ) { // A BOM is present
					if ( params.removeBOM ) {
						if (( n == 3 ) || ( params.alsoNonUTF8 )) {
							// Skip it, output the remaining bytes
							output.write(buffer, n, len-n);
						}
						else {
							// Keep the BOM
							output.write(buffer, 0, len);
						}
					}
					else { // Add the BOM: It's there, just write the buffer 
						output.write(buffer, 0, len);
					}
				}
				else { // No BOM present
					if ( !params.removeBOM ) { // If we add, do it
						String enc = rawDoc.getEncoding().toLowerCase();
						if ( enc.equals("utf-16") || enc.equals("utf-16le") ) {
							output.write(BOM_UTF16LE);
							logger.info("Added UTF-16LE BOM");
						}
						else if ( enc.equals("utf-16be") ) {
							output.write(BOM_UTF16BE);
							logger.info("Added UTF-16BE BOM");
						}
						else if ( enc.equals("utf-8") ) {
							output.write(BOM_UTF8);
							logger.info("Added UTF-8 BOM");
						}
						else { // Cannot add to un-supported encodings
							logger.warning(String.format("Cannot add a BOM to a document in %s.", enc));
						}
					}
					// Then write the buffer we checked
					output.write(buffer, 0, len);
				}
			}
			
			// Now copy the remaining of the file
			while ( (len = input.read(buffer)) > 0 ) {
				output.write(buffer, 0, len);
			}
			
			// Set the new raw-document URI
			// Other info stays the same
			rawDoc.setInputURI(tmpOut.toURI());
		}
		catch ( IOException e ) {
			throw new OkapiIOException("IO error while converting.", e);
		}
		finally {
			try { // Close the files
				if ( output != null ) {
					output.close();
					output = null;
				}
				if ( input != null ) {
					input.close();
					input = null;
				}
			}
			catch ( IOException e ) {
				throw new OkapiIOException("IO error while closing.", e);
			}
		}
	}

	/**
	 * Checks for BOM presence
	 * @param buffer The buffer to check.
	 * @param length The number of usable bytes in the buffer.
	 * @return 0 if there is no BOM, or the number of bytes used by
	 * the BOM if it is present.
	 */
	private int hasBOM (byte[] buffer,
		int length)
	{
		if ( length > 1 ) {
			// Check for UTF-16
			if (( buffer[0] == (byte)0xFE )
				&& ( buffer[1] == (byte)0xFF )) {
				// UTF-16BE
				logger.info("UTF-16BE detected");
				return 2;
			}
			else if (( buffer[0] == (byte)0xFF )
				&& ( buffer[1] == (byte)0xFE )) {
				// UTF-16LE
				logger.info("UTF-16LE detected");
				return 2;
			}
			// Check for UTF-8
			if ( length > 2 ) {
				if (( buffer[0] == (byte)0xEF )
					&& ( buffer[1] == (byte)0xBB )
					&& ( buffer[2] == (byte)0xBF )) {
					// UTF-8
					logger.info("UTF-8 detected");
					return 3;
				}
				// Check for UTF-32
				if ( length > 3) {
					if (( buffer[0] == (byte)0xFF )
						&& ( buffer[1] == (byte)0xFE )
						&& ( buffer[2] == (byte)0x00 )
						&& ( buffer[3] == (byte)0x00 )) {
						// UTF-32LE
						logger.info("UTF-32LE detected");
						return 4;
					}
					else if (( buffer[0] == (byte)0x00 )
						&& ( buffer[1] == (byte)0x00 )
						&& ( buffer[2] == (byte)0xFE )
						&& ( buffer[3] == (byte)0xFF )) {
						// UTF-32BE
						logger.info("UTF-32BE detected");
						return 4;
					}
				}
			}
		}
		return 0;
	}

}
