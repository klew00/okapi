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

package net.sf.okapi.filters.mosestext;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Implementation of the {@link IFilterWriter} interface for Moses Text files.
 * This class is not designed to be used with the Moses Text Filter, but as a 
 * standalone writer that can be driven by filter events. 
 */
public class MosesTextFilterWriter implements IFilterWriter {

	private OutputStream output;
	private String outputPath;
	private OutputStreamWriter writer;
	private LocaleId trgLoc;
	private XLIFFContent fmt;
	private String lineBreak;
	
	@Override
	public void cancel () {
	}

	@Override
	public void close () {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
			if ( output != null ) {
				output.close();
				output = null;
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public EncoderManager getEncoderManager () {
		return null; // Not used
	}

	@Override
	public String getName () {
		return "MosesTextFilterWriter";
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
			close();
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
		// Default encoding is ignored: we always use UTF-8 for Moses Text files
	}

	@Override
	public void setOutput (String path) {
		close(); // Make sure previous is closed
		this.outputPath = path;
	}

	@Override
	public void setOutput (OutputStream output) {
		close(); // Make sure previous is closed
		this.outputPath = null; // If we use the stream, we can't use the path
		this.output = output; // then assign the new stream
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	private void processStartDocument (StartDocument sd) {
		// Create the output file
		// If needed, create the output stream from the path provided
		try {
			if ( output == null ) {
				Util.createDirectories(outputPath);
				output = new BufferedOutputStream(new FileOutputStream(outputPath));
			}
			// Create the output
			writer = new OutputStreamWriter(output, "UTF-8");
		}
		catch ( FileNotFoundException e ) {
			throw new OkapiFileNotFoundException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiIOException(e);
		}
		// Initialize the variables
		fmt = new XLIFFContent();
		lineBreak = sd.getLineBreak();
	}
	
	private void processTextUnit (TextUnit tu) {
		try {
			TextContainer tc;
			if ( tu.hasTarget(trgLoc) ) {
				tc = tu.getTarget(trgLoc);
			}
			else { // Use the source
				tc = tu.getSource();
			}

			// Process by segments
			for ( Segment seg : tc.getSegments() ) {
				writer.write(fmt.setContent(seg.text).toString(0, false, false, false));
				writer.write(lineBreak);
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}

}
