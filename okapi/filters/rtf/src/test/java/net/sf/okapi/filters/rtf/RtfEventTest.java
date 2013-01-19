/* Copyright (C) 2008 Jim Hargrave
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.rtf;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.rtf.RTFFilter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RtfEventTest {

	private RTFFilter filter;
	private LocaleId locEN = LocaleId.ENGLISH;

	@Before
	public void setUp() throws Exception {
		filter = new RTFFilter();	
	}
	
	@Test
	public void testStartDoc () {
		String snippet = "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fnil\\fcharset0 Courier New;}}"
			+ "\\uc1\\pard\\f0\\fs22 t\\b e\\b0 st\\par }";
		List<Event> list = getEvents(snippet);
		assertTrue(list.get(0).isStartDocument());
		StartDocument sd = list.get(0).getStartDocument();
		assertNotNull(sd.getFilterWriter());
	}
	
	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, locEN));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
}
