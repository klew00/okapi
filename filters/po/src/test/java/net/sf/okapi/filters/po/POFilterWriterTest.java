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

package net.sf.okapi.filters.po;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.filters.po.POFilterWriter;

import org.junit.Before;
import org.junit.Test;

public class POFilterWriterTest {
	
	private POFilter filter;
	
	@Before
	public void setUp() {
		filter = new POFilter();
	}

	@Test
	public void testSrcSimpleOutput () {
		String snippet = ""
			+ "msgid \"Text 1\"\r"
			+ "msgstr \"\"\r";
		String result = rewrite(getEvents(snippet, "en", "fr"), "fr");
		assertEquals(snippet, result);
	}
		
	@Test
	public void testSrcTrgSimpleOutput () {
		String snippet = ""
			+ "msgid \"Text 1\"\r"
			+ "msgstr \"Texte 1\"\r";
		String result = rewrite(getEvents(snippet, "en", "fr"), "fr");
		assertEquals(snippet, result);
	}
		
	private ArrayList<Event> getEvents(String snippet,
		String srcLang,
		String trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, srcLang, trgLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

	private String rewrite (ArrayList<Event> list,
		String trgLang)
	{
		POFilterWriter writer = new POFilterWriter();
		writer.setOptions(trgLang, "UTF-8");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writer.setOutput(output);
		for (Event event : list) {
			writer.handleEvent(event);
		}
		writer.close();
		return output.toString();
	}

}
