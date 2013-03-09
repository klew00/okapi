/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.railsyaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.LocaleId;

import org.junit.Before;
import org.junit.Test;

public class RailsYmlFilterTest {
	
	private RailsYamlFilter filter;
	private String root;

	@Before
	public void setUp() {
		filter = new RailsYamlFilter();
		root = TestUtil.getParentDir(this.getClass(), "/Test01.yml");
	}

	@Test
	public void testDefaultInfo() {
		assertNotNull(filter.getName());
		assertNotNull(filter.getDisplayName());
		assertNotNull(filter.getParameters());
		List<FilterConfiguration> list = filter.getConfigurations();
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	@Test
	public void testStartDocument () {
		assertTrue("Problem in StartDocument", FilterTestDriver.testStartDocument(filter,
			new InputDocument(root + "Test01.yml", null),
			"UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));
	}

	@Test
	public void testSimpleYaml() {
		String snippet = "config:\n  title: \"My Rails Website\"";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, null), 1);
		assertNotNull(tu);
		assertEquals("My Rails Website", tu.getSource().toString());
		assertEquals("config.title", tu.getName());
	}

	@Test
	public void testSimplePlaceholders() {
		String snippet = "config:\n  title: \"My {{count}} Rails Website\"";
		ITextUnit tu = FilterTestDriver.getTextUnit(getEvents(snippet, null), 1);
		assertNotNull(tu);
		assertEquals("{{count}}", tu.getSource().getFirstContent().getCode(0).toString());
		assertEquals("My {{count}} Rails Website", tu.getSource().toString());
		assertEquals("config.title", tu.getName());
	}

	@Test
	public void testDoubleExtraction() throws URISyntaxException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();
		list.add(new InputDocument(root + "Test01.yml", null));
		list.add(new InputDocument(root + "Test02.yml", null));
		list.add(new InputDocument(root + "Test03.yml", null));
		RoundTripComparison rtc = new RoundTripComparison();
		assertTrue(rtc.executeCompare(filter, list, "UTF-8", LocaleId.ENGLISH,
				LocaleId.ENGLISH));
	}

	@Test
	public void testOpenTwiceWithString() {
		RawDocument rawDoc = new RawDocument("config:\n  title: \"My Rails Website\"", LocaleId.ENGLISH);
		filter.open(rawDoc);
		filter.open(rawDoc);
		filter.close();
	}

	private ArrayList<Event> getEvents(String snippet, IParameters params) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, LocaleId.ENGLISH));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}
}
