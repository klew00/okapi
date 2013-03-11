package net.sf.okapi.filters.openxml;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;

import java.net.URL;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Miscellaneous OOXML tests.
 */
public class OpenXMLTest {
	private LocaleId locENUS = LocaleId.fromString("en-us");
	
	/**
	 * Test to ensure the filter can handle an OOXML package in
	 * which the [Content Types].xml document does not appear 
	 * as the first entry in the ZIP archive.
	 * @throws Exception 
	 */
	@Test
	public void testReorderedZipPackage() throws Exception {
		OpenXMLFilter filter = new OpenXMLFilter();
		URL url = getClass().getResource("/reordered-zip.docx");
		RawDocument doc = new RawDocument(url.toURI(),"UTF-8", locENUS);
		ArrayList<Event> events = getEvents(filter, doc);
		ITextUnit tu = FilterTestDriver.getTextUnit(events, 1);
		assertNotNull(tu);
		assertEquals("Untitled document.docx", tu.getSource().toString());
		tu = FilterTestDriver.getTextUnit(events, 2);
		assertEquals("<w:r><w:rPr><w:rtl w:val=\"0\"/></w:rPr><w:t xml:space=\"preserve\">This is a test.</w:t></w:r>",
					 tu.getSource().toString());
	}
	
	private ArrayList<Event> getEvents(OpenXMLFilter filter, RawDocument doc) {
        ArrayList<Event> list = new ArrayList<Event>();
        filter.open(doc, false, true);
        while (filter.hasNext()) {
            Event event = filter.next();
            list.add(event);
        }
        filter.close();
        return list;
    }
}
