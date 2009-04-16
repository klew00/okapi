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

package net.sf.okapi.filters.rtf.tests;

import java.io.InputStream;

import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.rtf.RTFFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RTFFilterTest {

	@Before
	public void setUp() {
	}

	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		RTFFilter filter = null;		
		try {
			filter = new RTFFilter();
			InputStream input = RTFFilterTest.class.getResourceAsStream("/Test01.rtf");
			filter.open(new RawDocument(input, "windows-1252", "en", "fr"));
			process1(filter);
			filter.close();
			
			input = RTFFilterTest.class.getResourceAsStream("/Test01.rtf");
			filter.open(new RawDocument(input, "windows-1252", "en", "fr"));
			process2(filter);
			filter.close();

			input = RTFFilterTest.class.getResourceAsStream("/Test01.rtf");
			filter.open(new RawDocument(input, "windows-1252", "en", "fr"));
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
	
	private void process1 (RTFFilter filter) {
		System.out.println("===== 1 ===========================================");
		StringBuilder buf = new StringBuilder();
		while ( filter.getTextUntil(buf, -1, 0) == 0 ) {
			System.out.println(buf.toString());
		}
	}

	private void process2 (RTFFilter filter) {
		System.out.println("===== 2 ===========================================");
		TextUnit tu = new TextUnit("testid"); 
		while ( filter.getSegment(tu) ) {
			System.out.println("S="+tu.toString());
			if ( tu.hasTarget("fr") ) {
				System.out.println("T="+tu.getTargetContent("fr").toString());
			}
		}
	}

}
