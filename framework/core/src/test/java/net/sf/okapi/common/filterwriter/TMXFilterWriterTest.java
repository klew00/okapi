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

package net.sf.okapi.common.filterwriter;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.filters.DummyFilter;
import net.sf.okapi.common.resource.RawDocument;

import org.junit.Before;
import org.junit.Test;

public class TMXFilterWriterTest {
	
	private DummyFilter filter;
	
	@Before
	public void setUp() {
		filter = new DummyFilter();
	}

	@Test
	public void testSimpleOutput () {
		String result = rewrite(getEvents(null, "en", "fr"), "fr");
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<tmx version=\"1.4\"><header creationtool=\"unknown\" creationtoolversion=\"unknown\" segtype=\"TODO\" o-tmf=\"TODO\" adminlang=\"en\" srclang=\"en\" datatype=\"TODO\"></header><body>"
			+ "<tu tuid=\"autoID1\">"
			+ "<tuv xml:lang=\"en\"><seg>Source text</seg></tuv>"
			+ "<tuv xml:lang=\"fr\"><seg>Target text</seg></tuv>"
			+ "</tu>"
			+ "<tu tuid=\"autoID2\">"
			+ "<tuv xml:lang=\"en\"><seg>Source text 2</seg></tuv>"
			+ "</tu>"
			+ "</body>"
			+ "</tmx>";
		assertEquals(expected, result.replaceAll("[\\r\\n]", ""));
	}
		
	private ArrayList<Event> getEvents(String snippet,
		String srcLang,
		String trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument((CharSequence)null, srcLang, trgLang));
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
		TMXFilterWriter writer = new TMXFilterWriter();
		writer.setOptions(trgLang, null);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writer.setOutput(output);
		for (Event event : list) {
			writer.handleEvent(event);
		}
		writer.close();
		return output.toString();
	}

}
