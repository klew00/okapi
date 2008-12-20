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

package net.sf.okapi.filters.xliff.tests;

import java.net.URL;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.junit.Assert;
import org.junit.Test;

public class XLIFFFilterTest {

	@Test
	public void runTest () {
		XLIFFFilter filter = null;		
		try {
			filter = new XLIFFFilter();
			filter.setOptions("en", "es", "UTF-8", true);
			URL url = XLIFFFilterTest.class.getResource("/SF-12-Test01.xlf");
			filter.open(url);
			process(filter);
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	private void process (IFilter filter) {
		System.out.println("================================================");
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START:
				System.out.println("---Start");
				break;
			case FINISHED:
				System.out.println("---Finished");
				break;
			case START_DOCUMENT:
				System.out.println("---Start Document");
				break;
			case END_DOCUMENT:
				System.out.println("---End Document");
				break;
			case START_SUBDOCUMENT:
				System.out.println("---Start Sub Document");
				break;
			case END_SUBDOCUMENT:
				System.out.println("---End Sub Document");
				break;
			case START_GROUP:
				System.out.println("---Start Group");
				break;
			case END_GROUP:
				System.out.println("---End Group");
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				printResource((INameable)event.getResource());
				System.out.println("["+event.getResource().toString()+"]");
				break;
			}
		}
	}
	
	private void printResource (INameable res) {
		System.out.println("  id="+res.getId());
		System.out.println("  name="+res.getName());
		System.out.println("  type="+res.getType());
		System.out.println("  mimeType="+res.getMimeType());
	}
}
