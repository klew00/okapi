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

package net.sf.okapi.filters.vignette;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class VignetteFilterTest {
	
	private VignetteFilter filter;
	private String root;
	private GenericContent fmt;
	private LocaleId locEN = LocaleId.fromString("en");

	@Before
	public void setUp() {
		filter = new VignetteFilter();
		root = TestUtil.getParentDir(this.getClass(), "/Test01.xml");
		fmt = new GenericContent();
	}

	@Test
	public void testDefine () {
		assertTrue(filter.getParameters()!=null);
	}
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root+"Test01.xml", null));
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", locEN, locEN, ""));
	}
	
//	private ArrayList<Event> getEvents(String snippet) {
//		return getEvents(snippet, null);
//	}
//	
//	private ArrayList<Event> getEvents(String snippet, Parameters params) {
//		ArrayList<Event> list = new ArrayList<Event>();
//		filter.open(new RawDocument(snippet, locEN));
//		
//		if ( params == null ) filter.getParameters().reset();
//		else filter.setParameters(params);
//		
//		while (filter.hasNext()) {
//			Event event = filter.next();
//			list.add(event);
//		}
//		filter.close();
//		return list;
//	}

}
