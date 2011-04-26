/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.rtf;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.rtf.RTFFilter;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericContent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RTFFilterTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private GenericContent fmt;

	@Before
	public void setUp () {
		fmt = new GenericContent();
	}
	
	@Test
	public void testBasicProcessing () {
		FilterTestDriver testDriver = new FilterTestDriver();
		RTFFilter filter = null;		
		try {
			filter = new RTFFilter();
			InputStream input = RTFFilterTest.class.getResourceAsStream("/Test01.rtf");
			filter.open(new RawDocument(input, "windows-1252", locEN, locFR));
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
	
	@Test
	public void testSimpleTU () {
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents("Test01.rtf", locEN, locFR), 1);
		assertNotNull(tu);
		assertEquals("Text (to) translate.", tu.getSource().toString());
		TextContainer tc = tu.getTarget(locFR);
		assertNotNull(tc);
		assertEquals("Texte \u00e0 traduire.", tc.toString());

		tu = FilterTestDriver.getTextUnit(getEvents("Test01.rtf", locEN, locFR), 2);
		assertNotNull(tu);
		assertEquals("[Text with <1>bold</1>.]", fmt.printSegmentedContent(tu.getSource(), true));
		tc = tu.getTarget(locFR);
		assertNotNull(tc);
		assertEquals("[Texte avec du <1>gras</1>.]", fmt.printSegmentedContent(tc, true));
	}
	
	private ArrayList<Event> getEvents (String file, LocaleId srcLoc, LocaleId trgLoc) {
		IFilter filter = null;
		ArrayList<Event> list = new ArrayList<Event>();
		try {
			filter = new RTFFilter();
			InputStream input = RTFFilterTest.class.getResourceAsStream("/"+file);
			filter.open(new RawDocument(input, "windows-1252", srcLoc, trgLoc));
			while ( filter.hasNext() ) {
				list.add(filter.next());
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail("Exception occured: "+e.getLocalizedMessage());
		}
		finally {
			if ( filter != null ) filter.close();
		}
		return list;
	}	

}
