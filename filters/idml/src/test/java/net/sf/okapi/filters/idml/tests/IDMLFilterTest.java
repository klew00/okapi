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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.filters.idml.IDMLFilter;

import org.junit.Before;
import org.junit.Test;

public class IDMLFilterTest {

	private IDMLFilter filter;
	private String root;

	@Before
	public void setUp() {
		filter = new IDMLFilter();
		URL url = IDMLFilterTest.class.getResource("/Test01.idml");
		root = Util.getDirectoryName(url.getPath()) + File.separator;
	}

	@Test
	public void testDefaultInfo () {
		assertNotNull(filter.getParameters());
		assertNotNull(filter.getName());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size()>0);
	}

	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root+"Test01.idml", null),
			"UTF-8", "en", "en"));
	}
	
	@Test
	public void testDoubleExtraction () {
		// Read all files in the data directory
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.idml", null));
		list.add(new InputDocument(root+"helloworld-1.idml", null));
		list.add(new InputDocument(root+"helloworld-2.idml", null));
		list.add(new InputDocument(root+"ConditionalText.idml", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", "en", "en", "output"));
	}

}
