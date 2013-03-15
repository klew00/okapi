/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.packages.xliff;

import java.io.File;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.xliff.XLIFFFilter;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	XLIFFFilter reader;
	Event event;
	
	public void closeDocument () {
		if ( reader != null ) {
			reader.close();
			reader = null;
		}
	}

	public ITextUnit getItem () {
		return event.getTextUnit();
	}

	public void openDocument (String path,
		LocaleId sourceLanguage,
		LocaleId targetLanguage) {
		try {
			closeDocument();
			reader = new XLIFFFilter();
			// Encoding is not really used so we can hard-code
			File f = new File(path);
			RawDocument res = new RawDocument(f.toURI(), "UTF-8", sourceLanguage, targetLanguage);
			reader.open(res, false);
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean readItem () {
		while ( reader.hasNext() ) {
			event = reader.next();
			if ( event.getEventType() == EventType.TEXT_UNIT ) {
				return true;
			}
		}
		return false;
	}

}
