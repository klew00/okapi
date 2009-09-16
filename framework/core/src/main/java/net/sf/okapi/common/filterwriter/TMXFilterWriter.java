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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Implementation of {@link IFilterWriter} for TMX. This class is not
 * designed to be used with the TMX Filter, but as a standalone writer that
 * can be driven by filter events.
 */
public class TMXFilterWriter implements IFilterWriter {

	private TMXWriter writer;
	private OutputStream outputStream;
	private String outputPath;
	private String language;
	private boolean canceled;

	public void cancel () {
		close();
		canceled = true;
	}

	public void close () {
		if ( writer == null ) return;
		writer.writeEndDocument();
		writer.close();
		writer = null;
	}

	public String getName () {
		return "TMXFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		if ( canceled ) {
			return new Event(EventType.CANCELED);
		}
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument(event);
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		case TEXT_UNIT:
			processTextUnit(event);
			break;
		}
		return event;
	}

	/**
	 * Sets the options for this writer.
	 * @param language code of the output language.
	 * @param defaultEncoding this argument is ignored for this writer: the output is always UTF-8.
	 */
	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
		// encoding is ignore: we always use UTF-8
	}

	public void setOutput (String path) {
		outputPath = path;
	}

	public void setOutput (OutputStream output) {
		outputStream = output;
	}

	public void setParameters (IParameters params) {
		// No parameters for now
	}

	private void processStartDocument (Event event) {
		try {
			StartDocument sd = (StartDocument)event.getResource();
			// Create the output
			if ( outputStream == null ) {
				writer = new TMXWriter(outputPath);
			}
			else if ( outputStream != null ) {
				writer = new TMXWriter(new XMLWriter(
					new OutputStreamWriter(outputStream, "UTF-8")));
			}
			writer.writeStartDocument(sd.getLanguage(), language, null, null, "TODO", "TODO", "TODO");
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error writing the header.", e);
		}
	}

	private void processEndDocument () {
		close();
	}

	private void processTextUnit (Event event) {
		writer.writeTUFull((TextUnit)event.getResource());
	}

}
