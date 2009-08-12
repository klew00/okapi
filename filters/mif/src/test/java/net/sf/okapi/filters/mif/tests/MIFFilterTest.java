/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert; 

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.mif.MIFFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

public class MIFFilterTest {
	
	private MIFFilter filter;
	
	@Before
	public void setUp() {
		filter = new MIFFilter();
	}

	@Test
	public void testDefaultInfo () {
		//Not using parameters yet: assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void runTest () {
		FilterTestDriver testDriver;
				
		try {
			testDriver = new FilterTestDriver();
			testDriver.setShowSkeleton(false);
			testDriver.setDisplayLevel(0);
			URL url = MIFFilterTest.class.getResource("/Test01.mif");
			filter.open(new RawDocument(new URI(url.toString()), "UTF-8", "en"));
			if ( !testDriver.process(filter) ) Assert.fail();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
}
