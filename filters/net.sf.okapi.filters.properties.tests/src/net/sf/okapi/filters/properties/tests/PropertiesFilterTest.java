/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.filters.properties.tests;

import java.io.InputStream;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.filters.properties.PropertiesFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PropertiesFilterTest {
	
	@Before
	public void setUp() {
	}

	@Test
	public void runTest () {
		PropertiesFilter filter = null;		
		try {
			filter = new PropertiesFilter();
			filter.setOptions("en", "UTF-8", true);
			InputStream input = PropertiesFilterTest.class.getResourceAsStream("/Test01.properties");
			filter.open(input);
			process(filter);
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured");
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	private void process (IFilter filter) {
		System.out.println("==================================================");
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				System.out.println("--- Start Document ---");
				break;
			case TEXT_UNIT:
				System.out.println("["+event.getResource().toString()+"]");
				break;
			case END_DOCUMENT:
				System.out.println("--- End Document ---");
				break;
			}
		}
	}

}
