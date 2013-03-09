/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.transifex;

import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.lib.transifex.TransifexClient;

/**
 * Implementation of {@link IFilterWriter} for Transifex project.
 */
public class TransifexFilterWriter implements IFilterWriter {

	private IFilterWriter writer;
	private LocaleId trgLoc;
	FilterWriterAnnotation ann;
	private TransifexClient cli;
	private String outputPath;
	
	@Override
	public void cancel () {
		if ( writer != null ) {
			writer.cancel();
		}
	}

	@Override
	public void close () {
		if ( writer != null ) {
			writer.close();
		}
	}

	@Override
	public EncoderManager getEncoderManager () {
		if ( writer == null ) return null;
		else return writer.getEncoderManager();
	}

	@Override
	public String getName () {
		return "TransifexFilterWriter";
	}

	@Override
	public IParameters getParameters () {
		// Not used
		return null;
	}

	@Override
	public ISkeletonWriter getSkeletonWriter () {
		if ( writer == null ) return null;
		else return writer.getSkeletonWriter();
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			return processStartDocument(event);
		case END_DOCUMENT:
			return processEndDocument(event);
		}
		event = writer.handleEvent(event);
		return event;
	}

	@Override
	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		trgLoc = locale;
		// Encoding is hard-coded
	}

	/**
	 * Does nothing: A Transifex project always re-write onto itself. 
	 */
	@Override
	public void setOutput (String path) {
		// Nothing to do
	}

	/**
	 * Does nothing: A Transifex project always re-write onto itself. 
	 */
	@Override
	public void setOutput (OutputStream output) {
		// Nothing to do
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	private Event processStartDocument (Event event) {
		StartDocument sd = event.getStartDocument();
		// the filter writer is not the original one
		// we need to reset it here, so it uses the proper one
		ann = sd.getAnnotation(FilterWriterAnnotation.class);
		if ( ann == null ) {
			throw new OkapiBadFilterInputException("Missing filter-writer annotation.");
		}
		writer = ann.getFilterWriter();
		writer.setOptions(trgLoc, "UTF-8");
		outputPath = sd.getName()+".out";
		writer.setOutput(outputPath);
		return writer.handleEvent(event);
	}

	private Event processEndDocument (Event event) {
		// Process the event
		event = writer.handleEvent(event);
		// The original file should be re-constructed now
		// let's push it back to Transifex

		// Set the client if not done yet
		if ( cli == null ) {
			cli = new TransifexClient(ann.getProject().getHost());
			cli.setCredentials(ann.getProject().getUser(), ann.getProject().getPassword());
			cli.setProject(ann.getProject().getProjectId());
		}
		
		// Push the updated file
		String[] res = cli.putTargetResource(outputPath, trgLoc,
			ann.getResourceInfo().getId(), ann.getResourceInfo().getName());
		if ( res[0] == null ) {
			throw new OkapiIOException("Error uploading file.\n"+res[1]);
		}
		return event;
	}
}
