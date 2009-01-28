package net.sf.okapi.filters.html.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HtmlSnippetsTest {
	private HtmlFilter htmlFilter;
	
	@Before
	public void setUp() throws Exception {
		htmlFilter = new HtmlFilter();	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMETATag1() {
		String snippet = "<meta http-equiv=\"keywords\" content=\"one,two,three\"/>";				
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}

	@Test
	public void testPWithAttributes() {
		String snippet = "<p title='my title' dir='rtl'>Text of p</p>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}

	@Test
	public void testComplexEmptyElement() {
		String snippet = "<elem wr-prop1='wr-value1' ro-prop1='ro-value1' wr-prop2='wr-value2' text='text'/>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}

	@Test
	public void testPWithInlines() {
		String snippet = "<p>Before <b>bold</b> <a href=\"there\"/> after.</p>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}

	@Test
	public void testMETATag2() {
		String snippet = "<meta http-equiv=\"Content-Language\" content=\"en\"/>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}
	
	@Test
	public void testPWithInlines2() {
		String snippet = "<p>Before <img href=\"img.png\" alt=\"text\"/> after.</p>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}
	
	@Test
	public void testTableGroups() {
		String snippet = "<table id=\"100\"><tr><td>text</td></tr></table>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}
	
	@Test
	public void testGroupInPara() {
		String snippet = "<p>Text before list:"
			 + "<ul>"
			 + "<li>Text of item 1</li>"
			 + "<li>Text of item 2</li>"
			 + "</ul>"
			 + "and text after the list.</p>";
		assertEquals(generateOutput(getEvents(snippet), snippet), snippet);
	}
	
	private ArrayList<FilterEvent> getEvents(String snippet) {
		ArrayList<FilterEvent> list = new ArrayList<FilterEvent>();
		htmlFilter.open(snippet);	
		while (htmlFilter.hasNext()) {
			FilterEvent event = htmlFilter.next();
			list.add(event);
		}
		htmlFilter.close();
		return list;
	}

	private String generateOutput(ArrayList<FilterEvent> list, String original) {
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		writer.processStart("en", "utf-8", null, new EncoderManager());
		for (FilterEvent event : list) {
			switch (event.getEventType()) {
			case TEXT_UNIT:
				TextUnit tu = (TextUnit) event.getResource();
				GenericSkeleton skl = (GenericSkeleton) tu.getSkeleton();
				if (skl != null) {
					System.out.println("TU:skl=" + skl.toString());
				} else {
					System.out.println("TU:skl=None");
				}
				System.out.println("  :txt=" + tu.toString());
				tmp.append(writer.processTextUnit(tu));
				break;
			case DOCUMENT_PART:
				DocumentPart dp = (DocumentPart) event.getResource();
				skl = (GenericSkeleton) dp.getSkeleton();
				if (skl != null) {
					System.out.println("DP:skl=" + skl.toString());
				} else {
					System.out.println("DP:skl=None");
				}
				tmp.append(writer.processDocumentPart(dp));
				break;
			case START_GROUP:
				StartGroup startGroup = (StartGroup) event.getResource();
				skl = (GenericSkeleton) startGroup.getSkeleton();
				if (skl != null) {
					System.out.println("SG:skl=" + skl.toString());
				} else {
					System.out.println("SG:skl=None");
				}
				tmp.append(writer.processStartGroup(startGroup));
				break;
			case END_GROUP:
				Ending ending = (Ending) event.getResource();
				skl = (GenericSkeleton) ending.getSkeleton();
				if (skl != null) {
					System.out.println("EG:skl=" + skl.toString());
				} else {
					System.out.println("EG:skl=None");
				}
				tmp.append(writer.processEndGroup(ending));
				break;
			}
		}
		System.out.println("out: " + tmp.toString());
		System.out.println("-----");
		return tmp.toString();
	}
}
