package net.sf.okapi.filters.rtf;

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
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.filters.rtf.RTFFilter;

public class RtfTestUtils {

	public static String[] getTestFiles() throws URISyntaxException {
		// read all files in the test rtf data directory
		URL url = RtfFullFileTest.class.getResource("/AddComments.rtf");
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".rtf");
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
				assertTrue(event.getResource() instanceof StartSubfilter || event.getResource() instanceof EndSubfilter);
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
		RTFFilter filter = new RTFFilter();
		InputStream htmlStream = RtfFullFileTest.class.getResourceAsStream("/" + file);
		filter.open(new RawDocument(htmlStream, "UTF-8", LocaleId.fromString("en")));
		try {
			while (filter.hasNext()) {
				Event event = filter.next();
				if (event.getEventType() == EventType.TEXT_UNIT) {
					assertTrue(event.getResource() instanceof ITextUnit);
				} else if (event.getEventType() == EventType.DOCUMENT_PART) {
					assertTrue(event.getResource() instanceof DocumentPart);
				} else if (event.getEventType() == EventType.START_GROUP || event.getEventType() == EventType.END_GROUP) {
					assertTrue(event.getResource() instanceof StartGroup || event.getResource() instanceof Ending);
				} else if (event.getEventType() == EventType.START_SUBFILTER || event.getEventType() == EventType.END_GROUP) {
					assertTrue(event.getResource() instanceof StartSubfilter || event.getResource() instanceof EndSubfilter);
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
			filter.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
