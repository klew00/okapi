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

import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.filters.idml.IDMLFilter;

import org.junit.Assert;
import org.junit.Test;

public class IDMLFilterTest {

	@Test
	public void runTest () {
		IDMLFilter filter = null;		
		try {
			filter = new IDMLFilter();
			filter.setOptions("en", "UTF-8", true);
			//URL url = IDMLFilterTest.class.getResource("/helloworld-1.idml");
			//URL url = IDMLFilterTest.class.getResource("/private/100_101_CentreofGalaxy.idml");
			URL url = IDMLFilterTest.class.getResource("/private/iPhone_2_A_MASTER.idml");
			filter.open(new URI(url.toString()));
			
			IFilterWriter writer = filter.createFilterWriter();
			writer.setOptions("en", "UTF-8");
			writer.setOutput("output.idml");
			
			while ( filter.hasNext() ) {
				writer.handleEvent(filter.next());
			}
			writer.close();
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
