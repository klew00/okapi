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

package net.sf.okapi.steps.formatconversion;

import java.io.File;
import java.io.OutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

public class PensieveFilterWriter implements IFilterWriter {

	private ITmWriter writer;
	private String directory;
	
	public void cancel () {
		// TODO Auto-generated method stub
	}

	public void close () {
		// TODO Auto-generated method stub
	}

	public String getName () {
		return "PensieveFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			handleStartDocument(event);
			break;
		}
		return event;
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		// TODO Auto-generated method stub
	}

	public void setOutput (String path) {
		directory = path; // We assume it is a directory
	}

	public void setOutput (OutputStream output) {
		throw new OkapiIllegalFilterOperationException("Output type not supported.");
	}

	public void setParameters (IParameters params) {
		// TODO Auto-generated method stub
	}

	private void handleStartDocument (Event event) {
		Util.createDirectories(directory+File.separator);
		writer = TmWriterFactory.createFileBasedTmWriter(directory);
	}

}
