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

package net.sf.okapi.filters.html.tests;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.markupfilter.Parameters;
import net.sf.okapi.filters.tests.FilterTestDriver;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlEventTest {
	private HtmlFilter htmlFilter;

	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();
	}

	@Test
	public void testMETATag1() {
		String snippet = "<meta http-equiv=\"keywords\" content=\"one,two,three\"/>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu = new TextUnit("tu1", "one,two,three");
		skel.add("content=\"");
		skel.addContentPlaceholder(tu);
		skel.add("\"");
		tu.setIsReferent(true);
		tu.setName("content");
		tu.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));

		skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);
		skel.add("<meta http-equiv=\"keywords\" ");
		skel.addReference(tu);
		skel.add("/>");
		dp.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testPWithAttributes() {
		String snippet = "<p title='my title' dir='rtl'>Text of p</p>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu2 = new TextUnit("tu2", "my title");
		skel.add("title='");
		skel.addContentPlaceholder(tu2);
		skel.add("'");
		tu2.setIsReferent(true);
		tu2.setName("title");
		tu2.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu2));

		skel = new GenericSkeleton();
		TextUnit tu1 = new TextUnit("tu1", "Text of p");
		skel.add("<p ");
		skel.addReference(tu2);
		skel.add(" dir='");
		skel.addValuePlaceholder(tu1, "dir", null);		
		skel.add("'>");
		skel.addContentPlaceholder(tu1);
		skel.append("</p>");
		tu1.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu1));

		addEndEvents(events);
		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testComplexEmptyElement() {
		String snippet = "<dummy write=\"w\" readonly=\"ro\" trans=\"tu1\"/>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		// Build the input
		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu = new TextUnit("tu1", "tu1");
		skel.add("trans=\"");
		skel.addContentPlaceholder(tu);
		skel.add("\"");
		tu.setIsReferent(true);
		tu.setName("content");
		tu.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu));

		skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);
		skel.add("<dummy write=\"");
		dp.setSourceProperty(new Property("write", "w", false));
		skel.addValuePlaceholder(dp, "write", null);
		dp.setSourceProperty(new Property("readonly", "ro", true));
		skel.add("\" readonly=\"ro\" ");
		skel.addReference(tu);
		skel.add("/>");
		dp.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testPWithInlines() {
		String snippet = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		GenericSkeleton skel = new GenericSkeleton();
		DocumentPart dp1 = new DocumentPart("dp1", true);
		skel.add("<a href=\"");
		skel.addValuePlaceholder(dp1, "href", null);
		dp1.setSourceProperty(new Property("href", "there", false));
		skel.add("\"/>");
		dp1.setName("a");
		dp1.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp1));

		skel = new GenericSkeleton();
		TextUnit tu1 = new TextUnit("tu1", "Before ");
		TextFragment tf = tu1.getSourceContent();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" ");
		Code code = new Code(TagType.PLACEHOLDER, "a");
		code.appendReference("dp1");
		tf.append(code);
		tf.append(" after.");
		skel.add("<p>");
		skel.addContentPlaceholder(tu1);
		skel.append("</p>");
		tu1.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu1));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testMETATagWithLanguage() {
		String snippet = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		GenericSkeleton skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);
		skel.add("<meta http-equiv=\"Content-Language\" content=\"");
		skel.addValuePlaceholder(dp, "language", null);
		skel.add("\"/>");
		dp.setSourceProperty(new Property("language", "en", false));
		dp.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}
	
	@Test
	public void testMETATagWithEncoding() {
		String snippet = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-2022-JP\">";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		GenericSkeleton skel = new GenericSkeleton();
		DocumentPart dp = new DocumentPart("dp1", false);
		skel.add("<meta http-equiv=\"Content-Type\" content=\"");
		skel.add("text/html; charset=");
		skel.addValuePlaceholder(dp, "encoding", null);
		skel.add("\">");
		dp.setSourceProperty(new Property("encoding", "ISO-2022-JP", false));
		dp.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testPWithInlines2() {
		String snippet = "<p>Before <b>bold</b> <img href=\"there\" alt=\"text\"/> after.</p>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu2 = new TextUnit("tu2", "text");
		skel.add("alt=\"");
		skel.addContentPlaceholder(tu2);
		skel.add("\"");
		tu2.setIsReferent(true);
		tu2.setName("alt");
		tu2.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu2));

		skel = new GenericSkeleton();
		DocumentPart dp1 = new DocumentPart("dp1", true);
		skel.add("<img href=\"");
		dp1.setSourceProperty(new Property("href", "there", false));
		skel.addValuePlaceholder(dp1, "href", null);
		skel.add("\" ");
		skel.addReference(tu2);
		skel.add("/>");
		dp1.setIsReferent(true);
		dp1.setName("img");
		dp1.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp1));

		skel = new GenericSkeleton();
		TextUnit tu1 = new TextUnit("tu1", "Before ");
		TextFragment tf = tu1.getSourceContent();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" ");
		Code code = new Code(TagType.PLACEHOLDER, "a");
		code.appendReference("dp1");
		tf.append(code);
		tf.append(" after.");
		skel.add("<p>");
		skel.addContentPlaceholder(tu2);
		skel.append("</p>");
		tu1.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu1));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testTableGroups() {
		String snippet = "<table id=\"100\"><tr><td>text</td></tr></table>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);

		StartGroup g1 = new StartGroup("ssd0", "sg1");
		g1.setSkeleton(new GenericSkeleton("<table id=\"100\">"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g1));

		StartGroup g2 = new StartGroup("sg1", "sg2");
		g2.setSkeleton(new GenericSkeleton("<tr>"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g2));

		StartGroup g3 = new StartGroup("sg2", "sg3");
		g3.setSkeleton(new GenericSkeleton("<td>"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g3));

		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, new TextUnit("tu1", "text")));

		Ending e1 = new Ending("eg1");
		e1.setSkeleton(new GenericSkeleton("</td>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e1));

		Ending e2 = new Ending("eg2");
		e2.setSkeleton(new GenericSkeleton("</tr>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e2));

		Ending e3 = new Ending("eg3");
		e3.setSkeleton(new GenericSkeleton("</table>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e3));

		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	@Test
	public void testGroupInPara() {
		String snippet = "<p>Text before list:" + 
		"<ul>" + 
		"<li>Text of item 1</li>" + 
		"<li>Text of item 2</li>" + 
		"</ul>" + "and text after the list.</p>";
		ArrayList<FilterEvent> events = new ArrayList<FilterEvent>();

		addStartEvents(events);
		
		GenericSkeleton skel = new GenericSkeleton();
		TextUnit tu3 = new TextUnit("tu3", "Text before list:");
		
		// embedded groups
		StartGroup g1 = new StartGroup("tu3", "sg1");
		g1.setIsReferent(true);
		g1.setSkeleton(new GenericSkeleton("<ul>"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g1));

		StartGroup g2 = new StartGroup("sg1", "sg2");
		g2.setSkeleton(new GenericSkeleton("<li>"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g2));

		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, new TextUnit("tu1", "Text of item 1")));
		
		Ending e1 = new Ending("eg1");
		e1.setSkeleton(new GenericSkeleton("</li>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e1));

		StartGroup g3 = new StartGroup("sg1", "sg3");
		g3.setSkeleton(new GenericSkeleton("<li>"));
		events.add(new FilterEvent(FilterEventType.START_GROUP, g3));

		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, new TextUnit("tu2", "Text of item 2")));

		Ending e2 = new Ending("eg2");
		e2.setSkeleton(new GenericSkeleton("</li>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e2));

		Ending e3 = new Ending("eg3");
		e3.setSkeleton(new GenericSkeleton("</ul>"));
		events.add(new FilterEvent(FilterEventType.END_GROUP, e3));
		
		TextFragment tf = tu3.getSourceContent();
		Code c = new Code(TagType.PLACEHOLDER, "<ul>", TextFragment.makeRefMarker("sg1"));
		c.setHasReference(true);
		tf.append(c);
		
		tf.append("and text after the list.");
		skel.add("<p>");
		skel.addContentPlaceholder(tu3);
		skel.append("</p>");
		tu3.setSkeleton(skel);
		events.add(new FilterEvent(FilterEventType.TEXT_UNIT, tu3));
		
		addEndEvents(events);

		assertTrue(FilterTestDriver.compareEvents(events, getEvents(snippet)));
	}

	private ArrayList<FilterEvent> getEvents(String snippet) {
		ArrayList<FilterEvent> list = new ArrayList<FilterEvent>();
		htmlFilter.setParameters(new Parameters("/net/sf/okapi/filters/html/tests/testConfiguration1.yml"));
		htmlFilter.open(snippet);
		while (htmlFilter.hasNext()) {
			FilterEvent event = htmlFilter.next();
			list.add(event);
		}
		htmlFilter.close();
		return list;
	}

	private void addStartEvents(ArrayList<FilterEvent> events) {
		events.add(new FilterEvent(FilterEventType.START));
		events.add(new FilterEvent(FilterEventType.START_DOCUMENT));
	}

	private void addEndEvents(ArrayList<FilterEvent> events) {
		events.add(new FilterEvent(FilterEventType.END_DOCUMENT));
		events.add(new FilterEvent(FilterEventType.FINISHED));
	}

	// @Test
	public void printEvents() {
		htmlFilter = new HtmlFilter();
		InputStream htmlStream = HtmlEventTest.class.getResourceAsStream("/simpleSimpleTest.html");
		htmlFilter.setParameters(new Parameters("/net/sf/okapi/filters/html/tests/testConfiguration1.yml"));
		htmlFilter.open(htmlStream);
		while (htmlFilter.hasNext()) {
			FilterEvent event = htmlFilter.next();
			if (event.getEventType() == FilterEventType.TEXT_UNIT) {
				assertTrue(event.getResource() instanceof TextUnit);
			} else if (event.getEventType() == FilterEventType.DOCUMENT_PART) {
				assertTrue(event.getResource() instanceof DocumentPart);
			} else if (event.getEventType() == FilterEventType.START_GROUP
					|| event.getEventType() == FilterEventType.END_GROUP) {
				assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
			}
			System.out.print(event.getEventType().toString() + ": ");
			if (event.getResource() != null) {
				if (event.getResource() instanceof DocumentPart) {
					System.out.println(((DocumentPart) event.getResource()).getSourcePropertyNames());
				} else {
					System.out.println(event.getResource().toString());
				}
				if (event.getResource().getSkeleton() != null) {
					System.out.println("\tSkeketon: " + event.getResource().getSkeleton().toString());
				}
			}
		}

		htmlFilter.close();
	}
}
