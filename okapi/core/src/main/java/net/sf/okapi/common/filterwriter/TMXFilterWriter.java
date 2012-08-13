/*===========================================================================
//Copyright (C) 2009-2011 by the Okapi Framework contributors
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
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Implementation of {@link IFilterWriter} for TMX. This class is not
 * designed to be used with the TMX Filter, but as a standalone writer that
 * can be driven by filter events.
 */
public class TMXFilterWriter implements IFilterWriter {

	private TMXWriter writer;
	private OutputStream outputStream;
	private String outputPath;
	private LocaleId locale;
	private boolean canceled;
	private String segType;
	private Parameters params;
	
	public TMXFilterWriter() {
		this.params = new Parameters();
	}

	public TMXFilterWriter(TMXWriter writer) {
		this.params = new Parameters();
		this.writer = writer;
	}
	
	public void setSegType (String segType) {
		this.segType = segType;
	}
	
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

	public EncoderManager getEncoderManager () {
		return null;
	}
	
	@Override
	public ISkeletonWriter getSkeletonWriter () {
		return null;
	}

	public IParameters getParameters () {
		return params;
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
	 * @param locale output locale.
	 * @param defaultEncoding this argument is ignored for this writer: the output is always UTF-8.
	 */
	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		this.locale = locale;
		// encoding is ignore: we always use UTF-8
	}

	public void setOutput (String path) {
		outputPath = path;
	}

	public void setOutput (OutputStream output) {
		outputStream = output;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
	
	private void processStartDocument (Event event) {
		try {
			StartDocument sd = (StartDocument)event.getResource();
			// Create the output
			if ( outputStream == null ) {	
				if (writer == null) {
					writer = new TMXWriter(outputPath);
				} else {
					writer.setPath(outputPath);
				}
			}
			else if ( outputStream != null ) {
				if (writer == null) {
					writer = new TMXWriter(new XMLWriter(
							new OutputStreamWriter(outputStream, "UTF-8")));
				} else {
					writer.setXmlWriter(new XMLWriter(
							new OutputStreamWriter(outputStream, "UTF-8")));
				}
			}
			
			writer.setWriteAllPropertiesAsAttributes(params.isWriteAllPropertiesAsAttributes());		
			
			writer.writeStartDocument(sd.getLocale(), locale,
				null, null, segType, "unknown", "text");
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error writing the header.", e);
		}
	}

	private void processEndDocument () {
		close();
	}

	private void processTextUnit (Event event) {
		writer.writeTUFull(event.getTextUnit());
	}
}
