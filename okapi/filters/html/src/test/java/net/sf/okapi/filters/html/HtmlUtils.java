package net.sf.okapi.filters.html;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.filters.html.HtmlFilter;

public class HtmlUtils {

	public static String[] getHtmlTestFiles() throws URISyntaxException {
		// read all files in the test html directory
		URL url = HtmlFullFileTest.class.getResource("/simpleTest.html");
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".html") || name.endsWith(".htm");
			}
		};
		return dir.list(filter);
	}

	public static void printEvents(ArrayList<Event> events) {
		for (Event event : events) {
			if (event.getEventType() == EventType.TEXT_UNIT) {
				assertTrue(event.getResource() instanceof ITextUnit);
			} else if (event.getEventType() == EventType.DOCUMENT_PART) {
				assertTrue(event.getResource() instanceof DocumentPart);
			} else if (event.getEventType() == EventType.START_GROUP || event.getEventType() == EventType.END_GROUP) {
				assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
			} else if (event.getEventType() == EventType.START_SUBFILTER || event.getEventType() == EventType.END_SUBFILTER) {
				assertTrue(event.getResource() instanceof StartSubfilter || event.getResource() instanceof Ending);
			}
			System.out.print(event.getEventType().toString() + ": ");
			if (event.getResource() != null) {
				if (event.getResource() instanceof DocumentPart) {
					System.out.println(((DocumentPart) event.getResource()).getSourcePropertyNames());
				} else {
					System.out.println(event.getResource().toString());
				}
				if (event.getResource().getSkeleton() != null) {
					System.out.println("\tSkeleton: " + event.getResource().getSkeleton().toString());
				}
			}
		}
	}

	public static void printEvents(String file) {
		HtmlFilter htmlFilter = new HtmlFilter();
		InputStream htmlStream = HtmlFullFileTest.class.getResourceAsStream("/" + file);
		htmlFilter.open(new RawDocument(htmlStream, "UTF-8", LocaleId.fromString("en")));
		try {
			while (htmlFilter.hasNext()) {
				Event event = htmlFilter.next();
				if (event.getEventType() == EventType.TEXT_UNIT) {
					assertTrue(event.getResource() instanceof ITextUnit);
				} else if (event.getEventType() == EventType.DOCUMENT_PART) {
					assertTrue(event.getResource() instanceof DocumentPart);
				} else if (event.getEventType() == EventType.START_GROUP || event.getEventType() == EventType.END_GROUP) {
					assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
				} else if (event.getEventType() == EventType.START_SUBFILTER || event.getEventType() == EventType.END_SUBFILTER) {
					assertTrue(event.getResource() instanceof StartSubfilter || event.getResource() instanceof Ending);
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
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
