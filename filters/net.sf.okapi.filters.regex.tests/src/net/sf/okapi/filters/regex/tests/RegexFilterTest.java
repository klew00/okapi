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

package net.sf.okapi.filters.regex.tests;

import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.RegexFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexFilterTest {
	
	@Before
	public void setUp() {
	}

	@Test
	public void runTest () {
		RegexFilter filter = null;		
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			testDriver.setShowOnlyTextUnits(false);
			testDriver.setShowSkeleton(true);
			filter = new RegexFilter();
			IParameters params = new Parameters();

			URL paramsUrl = RegexFilterTest.class.getResource("/okf_regex@StringInfo.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			filter.setOptions("en", "UTF-8", true);
			InputStream input = RegexFilterTest.class.getResourceAsStream("/Test01_stringinfo_en.info");
			filter.open(input);
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			
			paramsUrl = RegexFilterTest.class.getResource("/okf_regex@SRT.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			filter.setOptions("en", "UTF-8", true);
			input = RegexFilterTest.class.getResourceAsStream("/Test01_srt_en.srt");
			filter.open(input);
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
