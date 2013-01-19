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
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.filters.po.POWriter;

import org.junit.Before;
import org.junit.Test;

public class POWriterTest {
	
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locFR = LocaleId.fromString("fr");
	private POFilter filter;
	private String header = "# \nmsgid \"\"\nmsgstr \"\"\n"
		+ "\"Content-Type: text/plain; charset=UTF-8\\n\"\n"
		+ "\"Content-Transfer-Encoding: 8bit\\n\"\n"
		+ "\"Language: fr\\n\"\n"
		+ "\"Plural-Forms: nplurals=2; plural=(n>1);\\n\"\n\n";
	
	@Before
	public void setUp() {
		filter = new POFilter();
	}

	@Test
	public void testEscapes () {
		String snippet = ""
			+ "msgid \"'\"\\\"\r"
			+ "msgstr \"'\"\\\"\r\r";
		String expected = ""
			+ "msgid \"'\\\"\\\\\"\r"
			+ "msgstr \"'\\\"\\\\\"\r\r";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header.replace('\n', '\r')+expected, result);
	}

	@Test
	public void testEscapesAmongAlreadyEscaped () {
		String snippet = ""
			+ "msgid \"' \\\\ \" \\\\\\\"\r"
			+ "msgstr \"' \\\\ \" \\\\\\\"\r\r";
		String expected = ""
			+ "msgid \"' \\\\ \\\" \\\\\\\\\"\r"
			+ "msgstr \"' \\\\ \\\" \\\\\\\\\"\r\r";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header.replace('\n', '\r')+expected, result);
	}
		
	@Test
	public void testSrcSimpleOutput () {
		String snippet = ""
			+ "msgid \"Text 1\"\r"
			+ "msgstr \"\"\r\r";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header.replace('\n', '\r')+snippet, result);
	}
		
	@Test
	public void testSrcTrgSimpleOutput () {
		String snippet = ""
			+ "msgid \"Text 1\"\r"
			+ "msgstr \"Texte 1\"\r\r";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header.replace('\n', '\r')+snippet, result);
	}
	
	@Test
	public void testOutputWithLinesWithWrap () {
		String snippet = ""
			+ "msgid \"\"\n"
			+ "\"line1\\n\"\n"
			+ "\"line2\\n\"\n"
			+ "msgstr \"\"\n"
			+ "\"line1trans\\n\"\n"
			+ "\"line2trans\\n\"\n"
			+ "\"line3trans\"\n\n";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header+snippet, result);
	}
		
	@Test
	public void testOutputWithPlural () {
		String snippet = ""
			+ "msgid \"source singular\"\n"
			+ "msgid_plural \"source plural\"\n"
			+ "msgstr[0] \"target singular\"\n"
			+ "msgstr[1] \"target plural\"\n\n";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header+snippet, result);
	}
		
	@Test
	public void testOutputWithFuzzyPlural () {
		String snippet = ""
			+ "#, fuzzy\n"
			+ "msgid \"source singular\"\n"
			+ "msgid_plural \"source plural\"\n"
			+ "msgstr[0] \"target singular\"\n"
			+ "msgstr[1] \"target plural\"\n\n";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header+snippet, result);
	}
		
	@Test
	public void testOutputWithFuzzy () {
		String snippet = "#, fuzzy\n"
			+ "msgid \"source\"\n"
			+ "msgstr \"target\"\n\n";
		String result = rewrite(getEvents(snippet, locEN, locFR), locFR);
		assertEquals(header+snippet, result);
	}
		
	private ArrayList<Event> getEvents(String snippet,
		LocaleId srcLang,
		LocaleId trgLang)
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
		LocaleId trgLang)
	{
		POWriter writer = new POWriter();
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
