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

package net.sf.okapi.filters.vignette;

import java.io.IOException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class VignetteFilterWriter extends GenericFilterWriter {

	public VignetteFilterWriter (ISkeletonWriter skelWriter,
		EncoderManager encoderManager)
	{
		super(skelWriter, encoderManager);
	}

	@Override
	public Event handleEvent (Event event) {
		try {
			// Enclose the output of the sub-filters into a CDATA block
			switch ( event.getEventType() ) {
			case START_GROUP:
				if ( event.getResource().getAnnotation(SubFilterAnnotation.class) != null ) {
					writer.write("<![CDATA[");
				}
				break;
			case END_GROUP:
				if ( event.getResource().getAnnotation(SubFilterAnnotation.class) != null ) {
					writer.write("]]>");
				}
				break;
			}
			super.handleEvent(event);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		return event;
	}

	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		this.locale = locale;
		// Force UTF-8
		this.encoding = "UTF-8";
	}

}
