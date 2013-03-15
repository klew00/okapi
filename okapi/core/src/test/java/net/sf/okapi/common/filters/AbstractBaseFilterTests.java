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

package net.sf.okapi.common.filters;

import org.junit.Assert;
import org.junit.Test;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;

public class AbstractBaseFilterTests {

	private LocaleId locEN = LocaleId.fromString("en");
	
	@Test
	public void testMultilingual () {
		DummyBaseFilter filter = null;		
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			testDriver.setDisplayLevel(0);
			testDriver.setShowSkeleton(true);
			filter = new DummyBaseFilter();

			filter.open(new RawDocument("1", locEN, locEN));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();

			filter.open(new RawDocument("2", locEN, locEN));
			if ( !testDriver.process(filter) ) Assert.fail();
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
}
