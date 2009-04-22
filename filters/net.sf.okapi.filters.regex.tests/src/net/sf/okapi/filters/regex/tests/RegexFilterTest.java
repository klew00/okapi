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

package net.sf.okapi.filters.regex.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.RegexFilter;
import net.sf.okapi.filters.regex.Rule;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexFilterTest {
	
	private RegexFilter filter;
	
	@Before
	public void setUp() {
		filter = new RegexFilter();
	}

	@Test
	public void runTest () {
		try {
			FilterTestDriver testDriver = new FilterTestDriver();
			testDriver.setDisplayLevel(0);
			testDriver.setShowSkeleton(true);
			
			IParameters params = new Parameters();
			URL paramsUrl = RegexFilterTest.class.getResource("/okf_regex@StringInfo.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			InputStream input = RegexFilterTest.class.getResourceAsStream("/Test01_stringinfo_en.info");
			filter.open(new RawDocument(input, "UTF-8", "en"));
			if ( !testDriver.process(filter) ) Assert.fail();
			filter.close();
			
			paramsUrl = RegexFilterTest.class.getResource("/okf_regex@SRT.fprm");
			params.load(paramsUrl.getPath(), false);
			filter.setParameters(params);
			input = RegexFilterTest.class.getResourceAsStream("/Test01_srt_en.srt");
			filter.open(new RawDocument(input, "UTF-8", "en"));
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
	public void testSimpleRule () {
		String snippet = "test1=\"text1\"\ntest2=\"text2\"\n";
		Parameters params = new Parameters();
		Rule rule = new Rule();
		rule.setRuleType(Rule.RULETYPE_STRING);
		rule.setExpression("=(.+)$");
		rule.setSourceGroup(1);
		params.rules.add(rule);
		filter.setParameters(params);
		// Process
		ArrayList<Event> list = getEvents(snippet);
		TextUnit tu = FilterTestDriver.getTextUnit(list, 1);
		assertNotNull(tu);
		assertEquals("text1", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(list, 2);
		assertNotNull(tu);
		assertEquals("text2", tu.getSource().toString());
	}

	private ArrayList<Event> getEvents(String snippet) {
		ArrayList<Event> list = new ArrayList<Event>();
		filter.open(new RawDocument(snippet, "en"));
		while (filter.hasNext()) {
			Event event = filter.next();
			list.add(event);
		}
		filter.close();
		return list;
	}

}
