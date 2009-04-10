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

package net.sf.okapi.filters.po.tests;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class POFilterTest {
	
	private POFilter filter;
	
	@Before
	public void setUp() {
		filter = new POFilter();
	}

	@Test
	public void testOuputOptionLine_JustFormat () {
		String snippet = "#, c-format\n"
			+ "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr");
		assertEquals(result, snippet);
	}
		
	@Test
	public void testOuputOptionLine_FormatFuzzy () {
		String snippet = "#, c-format, fuzzy\n"
			+ "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr");
		assertEquals(result, snippet);
	}
		
	@Test
	public void testOuputOptionLine_FuzyFormat () {
		String snippet = "#, fuzzy, c-format\n"
			+ "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr");
		assertEquals(result, snippet);
	}

	@Test
	public void testOuputOptionLine_StuffFuzyFormat () {
		String snippet = "#, x-stuff, fuzzy, c-format\n"
			+ "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		String result = FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr");
		assertEquals(result, snippet);
	}
	
	@Test
	public void testOuputSimpleEntry () {
		String snippet = "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		String expect = "msgid \"Text 1\"\n"
			+ "msgstr \"Texte 1\"\n";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr"));
	}
	
	@Test
	public void testOuputAddTranslation () {
		String snippet = "msgid \"Text 1\"\n"
			+ "msgstr \"\"\n";
		String expect = "msgid \"Text 1\"\n"
			+ "msgstr \"Text 1\"\n";
		assertEquals(expect, FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr"));
	}
	
	@Test
	public void testTUEmptyIDEntry () {
		String snippet = "msgid \"\"\n"
			+ "msgstr \"Some stuff\"\n";
		assertEquals(null, FilterTestDriver.getTextUnit(getEvents(snippet, "en", "fr"), 1));
	}
	
	@Test
	public void testTUCompleteEntry () {
		String snippet = "#, fuzzy\n"
			+ "#. Comment\n"
			+ "#: Reference\n"
			+ "# Translator note\n"
			+ "#| Context\n"
			+ "msgid \"Source\"\n"
			+ "msgstr \"Target\"\n";
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, "en", "fr"), 1);

		assertNotNull(tu);
		assertEquals("Source", tu.getSource().toString());
		assertEquals("Target", tu.getTarget("fr").toString());

		assertTrue(tu.hasTargetProperty("fr", Property.APPROVED));
		Property prop = tu.getTargetProperty("fr", Property.APPROVED);
		assertEquals("no", prop.getValue());
		assertFalse(prop.isReadOnly());
		
		assertTrue(tu.hasProperty(Property.NOTE));
		prop = tu.getProperty(Property.NOTE);
		assertEquals("Comment", prop.getValue());
		assertTrue(prop.isReadOnly());
		
		assertTrue(tu.hasProperty("references"));
		prop = tu.getProperty("references");
		assertEquals("Reference", prop.getValue());
		assertTrue(prop.isReadOnly());

		assertTrue(tu.hasProperty("transnote"));
		prop = tu.getProperty("transnote");
		assertEquals("Translator note", prop.getValue());
		assertTrue(prop.isReadOnly());
	}
	
	@Test
	public void testTUPluralEntry_DefaultGroup () {
		StartGroup sg = FilterTestDriver.getGroup(getEvents(makePluralEntry(), "en", "fr"), 1);
		assertNotNull(sg);
		assertEquals("x-gettext-plurals", sg.getType());
	}

	@Test
	public void testTUPluralEntry_DefaultSingular () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(makePluralEntry(), "en", "fr"), 1);
		assertNotNull(tu);
		assertEquals("untranslated-singular", tu.getSource().toString());
		assertFalse(tu.hasTarget("fr"));
	}

	@Test
	public void testTUPluralEntry_DefaultPlural () {
		TextUnit tu = FilterTestDriver.getTextUnit(getEvents(makePluralEntry(), "en", "fr"), 2);
		assertNotNull(tu);
		assertEquals("untranslated-plural", tu.getSource().toString());
		assertFalse(tu.hasTarget("fr"));
	}
	
	@Test
	public void testOuputPluralEntry () {
		String snippet = makePluralEntry();
		String result = FilterTestDriver.generateOutput(getEvents(snippet, "en", "fr"), snippet, "fr");
		String expected = "msgid \"untranslated-singular\"\n"
			+ "msgid_plural \"untranslated-plural\"\n"
			+ "msgstr[0] \"untranslated-singular\"\n"
			+ "msgstr[1] \"untranslated-plural\"\n";
		assertEquals(expected, result);
	}
		
	@Test
	public void testOuputExternalFile () {
		POFilter filter = null;		
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			//testDriver.setShowSkeleton(true);
			//testDriver.setDisplayLevel(3);
			filter = new POFilter();
			InputStream input = POFilterTest.class.getResourceAsStream("/Test01.po");
			filter.open(new InputResource(input, "UTF-8", "en", "fr"));
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
	
	private ArrayList<Event> getEvents(String snippet,
		String srcLang,
		String trgLang)
	{
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new InputResource(snippet, srcLang, trgLang));
		while ( filter.hasNext() ) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

	private String makePluralEntry () {
		return "msgid \"untranslated-singular\"\n"
			+ "msgid_plural \"untranslated-plural\"\n"
			+ "msgstr[0] \"\"\n"
			+ "msgstr[1] \"\"\n";
	}

}
