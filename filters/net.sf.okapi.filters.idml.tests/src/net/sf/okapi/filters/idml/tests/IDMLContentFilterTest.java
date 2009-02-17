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

package net.sf.okapi.filters.idml.tests;

import java.net.URI;
import java.net.URL;

import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.writer.GenericFilterWriter;
import net.sf.okapi.filters.idml.IDMLContentFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Test;

public class IDMLContentFilterTest {

	@Test
	public void runTest () {
		IDMLContentFilter filter = null;		
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			testDriver.setShowSkeleton(false);
			testDriver.setShowOnlyTextUnits(false);
			filter = new IDMLContentFilter();
			filter.setOptions("en", "UTF-8", true);
			URL url = IDMLContentFilterTest.class.getResource("/Story_story1.xml");
			filter.open(new URI(url.toString()));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();

			// Test a simple re-write
			filter.open(new URI(url.toString()));
			rewrite(filter);
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
	
	private void rewrite (IFilter filter) {
		GenericFilterWriter writer = null;
		try {
			writer = new GenericFilterWriter(filter.createSkeletonWriter());
			writer.setOptions("FR", "UTF-8");
			writer.setOutput("Story_story1.out.xml");
			while ( filter.hasNext() ) {
				writer.handleEvent(filter.next());
			}
		}
		finally {
			if ( writer != null ) writer.close();
		}
		
	}
	
}
