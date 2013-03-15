/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

import java.io.File;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.openoffice.OpenOfficeFilter;
import net.sf.okapi.filters.properties.PropertiesFilter;

public class Main {
	
	public static void main (String[] args) {
		try {
			// Create the filter (based on the extension of the input)
			IFilter filter = null;
			if ( args[0].endsWith(".properties") ) {
				filter = new PropertiesFilter();
			}
			else if ( args[0].endsWith(".odt") ) {
				filter = new OpenOfficeFilter();
			}
			// Open the document to process
			filter.open(new RawDocument(new File(args[0]).toURI(), "UTF-8", new LocaleId("en")));
			
			// Create a formatter to display text unit more prettily.
			GenericContent fmt = new GenericContent();
			
			// process the input document
			while ( filter.hasNext() ) {
				Event event = filter.next();
				if ( event.getEventType() == EventType.TEXT_UNIT ) {
					// Format and print out each text unit
					// We can use getFirstPartContent() because nothing is segmented
					fmt.setContent((event.getTextUnit()).getSource().getFirstContent());
					System.out.println(fmt.toString());
				}
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
}
